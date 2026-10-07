package yugioh.duelo;

//eventos del duelo: la logica los llama y la ventana los muestra
//asi la logica no depende de como esta hecha la ventana
public interface BattleListener
{
    //resultado de una ronda: carta del jugador, carta de la maquina y quien gano
    void onTurn(String playerCard, String aiCard, String winner);

    //el marcador cambio
    void onScoreChanged(int playerScore, int aiScore);

    //alguien llego a 2 rondas y gano el duelo
    void onDuelEnded(String winner);
}
