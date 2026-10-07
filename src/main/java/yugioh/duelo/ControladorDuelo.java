package yugioh.duelo;

import org.json.JSONException;
import yugioh.api.YgoApiClient;
import yugioh.interfaz.AccionesDuelo;
import yugioh.interfaz.VentanaDuelo;
import yugioh.modelo.Card;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

//une la API, las reglas del duelo (Duel) y la ventana
//la ventana llama a estos metodos cuando el usuario pulsa los botones
public class ControladorDuelo implements AccionesDuelo
{
    private final VentanaDuelo ventana;
    private final YgoApiClient api = new YgoApiClient();
    //la ventana es el BattleListener: Duel le avisa lo que pasa en cada ronda
    private final Duel duelo;

    private List<Card> manoJugador;
    private List<Card> manoMaquina;

    public ControladorDuelo(VentanaDuelo ventana)
    {
        this.ventana = ventana;
        this.duelo = new Duel(ventana);
    }

    @Override
    public void repartirCartas()
    {
        //la consulta a la API corre en segundo plano con SwingWorker
        //para no congelar la ventana mientras llegan las cartas
        SwingWorker<Void, Void> cargador = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                //esto corre en otro hilo, no se toca la ventana aqui
                manoJugador = api.obtenerCartas(3);
                manoMaquina = api.obtenerCartas(3);
                return null;
            }

            @Override
            protected void done()
            {
                //esto corre otra vez en el hilo de la ventana
                try
                {
                    get();
                    ventana.mostrarCartasJugador(manoJugador);
                    ventana.mostrarCartasMaquina(manoMaquina);
                }
                catch (ExecutionException e)
                {
                    //el error que lanzo YgoApiClient viene dentro de e.getCause()
                    Throwable causa = e.getCause();
                    if (causa instanceof JSONException)
                    {
                        ventana.mostrarError("No se pudo cargar la carta");
                    }
                    else
                    {
                        ventana.mostrarError(causa.getMessage());
                    }
                }
                catch (InterruptedException e)
                {
                    ventana.mostrarError("Error de red");
                }
            }
        };
        cargador.execute();
    }

    @Override
    public void iniciarDuelo()
    {
        duelo.empezar(manoJugador, manoMaquina);
        ventana.escribirEnLog(duelo.isTurnoJugador() ? "Empiezas tú." : "Empieza la máquina.");
    }

    @Override
    public void elegirCarta(int indice, boolean enDefensa)
    {
        duelo.jugarRonda(indice, enDefensa);
    }
}
