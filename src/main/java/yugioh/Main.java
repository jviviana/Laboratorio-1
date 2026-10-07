package yugioh;

import yugioh.interfaz.VentanaDuelo;
import yugioh.prueba.DueloDePrueba;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            VentanaDuelo ventana = new VentanaDuelo();

            ventana.setAcciones(new DueloDePrueba(ventana));
            ventana.setVisible(true);
            ventana.pedirCartas();
        });
    }
}
