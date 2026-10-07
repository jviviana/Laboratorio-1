package yugioh;

import yugioh.interfaz.VentanaDuelo;
import yugioh.prueba.DueloDePrueba;

import javax.swing.*;

/**
 * PUNTO DE ENTRADA del programa: la primera clase que se ejecuta.
 */
public class Main
{
    public static void main(String[] args)
    {
        // Swing tiene UN hilo especial que dibuja la ventana y atiende los clics (el EDT).
        // Regla de Swing: las ventanas solo se crean y modifican desde ese hilo.
        // invokeLater(...) significa: "EDT, cuando puedas, ejecuta este codigo".
        SwingUtilities.invokeLater(() ->
        {
            VentanaDuelo ventana = new VentanaDuelo();

            // AQUI SE CONECTA LA LOGICA.
            // Por ahora usamos DueloDePrueba (cartas fijas) solo para ver el diseño.
            // Cuando esten YgoApiClient y Duel, se reemplaza esta linea por la clase
            // de logica que implemente AccionesDuelo y que use "ventana" como BattleListener.
            ventana.setAcciones(new DueloDePrueba(ventana));

            ventana.setVisible(true);
            ventana.pedirCartas(); //al abrir, se reparten las cartas
        });
    }
}
