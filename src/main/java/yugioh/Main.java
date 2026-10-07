package yugioh;

import yugioh.interfaz.VentanaDuelo;
import yugioh.duelo.ControladorDuelo;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            VentanaDuelo ventana = new VentanaDuelo();

            ventana.setAcciones(new ControladorDuelo(ventana));
            ventana.setVisible(true);
            ventana.pedirCartas();
        });
    }
}
