package yugioh;

import yugioh.interfaz.VentanaDuelo;
import yugioh.duelo.ControladorDuelo;

import javax.swing.*;

//punto de entrada del programa
public class Main
{
    public static void main(String[] args)
    {
        //la ventana se crea en el hilo de Swing
        SwingUtilities.invokeLater(() ->
        {
            VentanaDuelo ventana = new VentanaDuelo();

            //se conecta la logica con la ventana
            ventana.setAcciones(new ControladorDuelo(ventana));
            ventana.setVisible(true);
            //al abrir se piden las cartas a la API
            ventana.pedirCartas();
        });
    }
}
