package yugioh.prueba;

import yugioh.interfaz.AccionesDuelo;
import yugioh.interfaz.VentanaDuelo;
import yugioh.modelo.Card;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * CLASE TEMPORAL, SOLO PARA PROBAR EL DISEÑO.
 *
 * Hace de "logica falsa": usa 6 cartas fijas (no llama a la API) y reglas muy simples,
 * para poder ver la ventana funcionando mientras la parte de logica esta en construccion.
 * Cuando esten YgoApiClient y Duel, esta clase (y el paquete "prueba") se puede borrar.
 */
public class DueloDePrueba implements AccionesDuelo
{
    private static final String URL = "https://images.ygoprodeck.com/images/cards_small/";

    // cartas Monster reales con su imagen de YGOProDeck
    private static final List<Card> CARTAS = Arrays.asList(
            new Card("Dark Magician", 2500, 2100, URL + "46986414.jpg"),
            new Card("Blue-Eyes White Dragon", 3000, 2500, URL + "89631139.jpg"),
            new Card("Red-Eyes Black Dragon", 2400, 2000, URL + "74677422.jpg"),
            new Card("Summoned Skull", 2500, 1200, URL + "70781052.jpg"),
            new Card("Celtic Guardian", 1400, 1200, URL + "91152256.jpg"),
            new Card("Gaia The Fierce Knight", 2300, 2100, URL + "6368038.jpg"));

    private final VentanaDuelo ventana;
    private final Random azar = new Random();

    private List<Card> manoJugador = new ArrayList<>();
    private List<Card> manoMaquina = new ArrayList<>();
    private int puntosJugador;
    private int puntosMaquina;

    public DueloDePrueba(VentanaDuelo ventana)
    {
        this.ventana = ventana;
    }

    @Override
    public void repartirCartas()
    {
        List<Card> mezcladas = new ArrayList<>(CARTAS);
        Collections.shuffle(mezcladas); //las revuelve al azar
        manoJugador = new ArrayList<>(mezcladas.subList(0, 3));
        manoMaquina = new ArrayList<>(mezcladas.subList(3, 6));

        // simulamos que la API tarda un poco (Timer de Swing, no congela la ventana)
        Timer espera = new Timer(800, e ->
        {
            ventana.mostrarCartasJugador(manoJugador);
            ventana.mostrarCartasMaquina(manoMaquina);
        });
        espera.setRepeats(false);
        espera.start();
    }

    @Override
    public void iniciarDuelo()
    {
        puntosJugador = 0;
        puntosMaquina = 0;
        ventana.escribirEnLog(azar.nextBoolean() ? "Empiezas tú." : "Empieza la máquina.");
    }

    @Override
    public void elegirCarta(int indice, boolean enDefensa)
    {
        Card delJugador = manoJugador.get(indice);
        Card deLaMaquina = manoMaquina.remove(azar.nextInt(manoMaquina.size()));

        // reglas simplificadas: la maquina siempre ataca
        int valorJugador = enDefensa ? delJugador.getDef() : delJugador.getAtk();
        boolean ganaJugador = valorJugador >= deLaMaquina.getAtk();
        if (ganaJugador) puntosJugador++; else puntosMaquina++;

        // pequeña pausa para que se vea la carta de la maquina "pensando"
        Timer espera = new Timer(700, e ->
        {
            ventana.onTurn(delJugador.getNombre(), deLaMaquina.getNombre(), ganaJugador ? "Jugador" : "Máquina");
            ventana.onScoreChanged(puntosJugador, puntosMaquina);
            if (puntosJugador == 2) ventana.onDuelEnded("Jugador");
            else if (puntosMaquina == 2) ventana.onDuelEnded("Máquina");
        });
        espera.setRepeats(false);
        espera.start();
    }
}
