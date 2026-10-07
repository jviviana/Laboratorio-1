package yugioh.interfaz;

//lo que la ventana le avisa a la logica cuando el usuario hace clic
//es el camino contrario a BattleListener
public interface AccionesDuelo
{
    //pedir 3 cartas para cada jugador a la API
    void repartirCartas();

    //el usuario presiono Iniciar duelo
    void iniciarDuelo();

    //el usuario eligio una carta (0, 1 o 2) en ataque o en defensa
    void elegirCarta(int indice, boolean enDefensa);
}
