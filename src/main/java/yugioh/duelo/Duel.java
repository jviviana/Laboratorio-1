package yugioh.duelo;

import yugioh.modelo.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Duel
{
    //rondas que se necesitan para ganar el duelo (2 de 3)
    private static final int RONDAS_PARA_GANAR = 2;

    //a quien se le avisa
    private final BattleListener listener;
    private final Random azar = new Random();

    private List<Card> manoJugador;
    private List<Card> manoMaquina;

    private boolean[] usadasJugador;
    private int puntosJugador;
    private int puntosMaquina;
    private boolean turnoJugador;
    private boolean terminado;

    public Duel(BattleListener listener)
    {
        this.listener = listener;
    }

    //prepara el duelo con las 3 cartas de cada uno y sortea el turno inicial
    public void empezar(List<Card> cartasJugador, List<Card> cartasMaquina)
    {
        manoJugador = new ArrayList<>(cartasJugador);
        manoMaquina = new ArrayList<>(cartasMaquina);
        usadasJugador = new boolean[manoJugador.size()];
        puntosJugador = 0;
        puntosMaquina = 0;
        terminado = false;

        //el turno inicial se decide al azar
        turnoJugador = azar.nextBoolean();
    }

    //se llama cuando el jugador elige una carta  y su posicion
    public void jugarRonda(int indice, boolean enDefensa)
    {
        if (terminado || usadasJugador[indice])
        {
            return;
        }

        Card delJugador = manoJugador.get(indice);
        usadasJugador[indice] = true;

        //la maquina elige una carta al azar entre las que le quedan, y siempre ataca
        Card deLaMaquina = manoMaquina.remove(azar.nextInt(manoMaquina.size()));

        String ganador;
        if (!enDefensa)
        {
            //si los dos atacan, gana el ATK mayor
            ganador = comparar(delJugador.getAtk(), deLaMaquina.getAtk());
        }
        else
        {
            //el jugador defiende: el ATK de la maquina contra la DEF del jugador
            ganador = comparar(delJugador.getDef(), deLaMaquina.getAtk());
        }

        //cada ronda ganada suma 1 punto
        if (ganador.equals("Jugador"))
        {
            puntosJugador++;
        }
        else
        {
            puntosMaquina++;
        }

        //despues de cada ronda se avisa a la ventana
        listener.onTurn(delJugador.getNombre(), deLaMaquina.getNombre(), ganador);
        listener.onScoreChanged(puntosJugador, puntosMaquina);

        //gana el primero en llegar a 2
        if (puntosJugador == RONDAS_PARA_GANAR)
        {
            terminado = true;
            listener.onDuelEnded("Jugador");
        }
        else if (puntosMaquina == RONDAS_PARA_GANAR)
        {
            terminado = true;
            listener.onDuelEnded("Máquina");
        }

        //el turno pasa al otro
        turnoJugador = !turnoJugador;
    }


    //si empatan, gana quien tiene el turno
    private String comparar(int valorJugador, int valorMaquina)
    {
        if (valorJugador > valorMaquina)
        {
            return "Jugador";
        }
        if (valorMaquina > valorJugador)
        {
            return "Máquina";
        }
        return turnoJugador ? "Jugador" : "Máquina";
    }

    public boolean isTurnoJugador()
    {
        return turnoJugador;
    }

    public boolean isTerminado()
    {
        return terminado;
    }
}
