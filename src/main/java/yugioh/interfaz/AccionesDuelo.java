package yugioh.interfaz;

/**
 * Lo que la VENTANA le pide a la LOGICA cuando el usuario hace clic.
 *
 * Es el camino contrario a BattleListener:
 *   BattleListener -> la logica le avisa a la ventana ("gano tal carta").
 *   AccionesDuelo  -> la ventana le avisa a la logica ("el usuario eligio la carta 2").
 *
 * Asi la ventana no necesita saber como se piden las cartas a la API
 * ni como se decide quien gana: solo llama a estos metodos.
 * La clase que conecta todo (Main) es la que escribe el codigo de cada uno.
 */
public interface AccionesDuelo
{
    /**
     * Pedir 3 cartas para cada jugador. Se llama al abrir la ventana y al
     * presionar "Nuevo duelo". Cuando la logica las tenga, debe llamar a
     * ventana.mostrarCartasJugador(...) y ventana.mostrarCartasMaquina(...).
     */
    void repartirCartas();

    /** El usuario presiono "Iniciar duelo" (solo se puede con las 6 cartas cargadas). */
    void iniciarDuelo();

    /**
     * El usuario presiono "Elegir carta" en una de sus cartas.
     * @param indice    posicion de la carta en su mano: 0, 1 o 2
     * @param enDefensa true si la eligio en posicion de defensa, false si en ataque
     */
    void elegirCarta(int indice, boolean enDefensa);
}
