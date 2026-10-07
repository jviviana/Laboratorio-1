package yugioh.duelo;

/**
 * "BattleListener" = "Escuchador del duelo". (El nombre y los 3 metodos los exige el taller.)
 *
 * ¿Que es una INTERFACE?
 * Es una lista de metodos SIN codigo adentro, solo los nombres. Es como un
 * "contrato": cualquier clase que diga "implements BattleListener" esta
 * obligada a escribir el codigo de TODOS estos metodos.
 *
 * ¿Para que sirve aqui?
 * Duel (la logica) avisa lo que pasa llamando a estos metodos, pero NO sabe
 * quien lo escucha ni como lo va a mostrar. En nuestro caso quien escucha es
 * VentanaDuelo, que lo dibuja en pantalla. Eso es "desacoplar la UI de la logica".
 *
 * Es la MISMA idea que ActionListener en los botones: el boton no sabe que
 * hace tu codigo, solo llama a actionPerformed() cuando lo presionan.
 */
public interface BattleListener
{
    /**
     * Se llama al terminar cada ronda. "Turn" = turno.
     * @param playerCard nombre de la carta que jugo el jugador
     * @param aiCard     nombre de la carta que jugo la maquina
     * @param winner     quien gano la ronda (por ejemplo "Jugador" o "Máquina")
     */
    void onTurn(String playerCard, String aiCard, String winner);

    /**
     * Se llama cada vez que cambia el marcador. "ScoreChanged" = cambio el puntaje.
     * @param playerScore rondas ganadas por el jugador
     * @param aiScore     rondas ganadas por la maquina
     */
    void onScoreChanged(int playerScore, int aiScore);

    /**
     * Se llama cuando alguien llega a 2 rondas ganadas. "DuelEnded" = duelo terminado.
     * @param winner quien gano el duelo
     */
    void onDuelEnded(String winner);
}
