package yugioh.interfaz;

import yugioh.modelo.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URI;

//una carta: imagen, nombre, ATK, DEF y boton Elegir carta
//el diseño esta en PanelCarta.form y la ventana lo usa 6 veces
public class PanelCarta
{
    //componentes del .form
    private JPanel panelCarta;
    private JLabel etiquetaImagen;
    private JLabel etiquetaNombre;
    private JLabel etiquetaAtk;
    private JLabel etiquetaDef;
    private JButton botonElegir;

    private Card carta;
    private Image imagen;
    //oculta = carta de la maquina que todavia no se juega
    private boolean oculta;
    private boolean usada;
    private boolean esRival;

    //mientras se piden las cartas a la API
    public void mostrarCargando()
    {
        carta = null;
        imagen = null;
        usada = false;
        etiquetaImagen.setIcon(null);
        etiquetaImagen.setText("Cargando...");
        etiquetaImagen.setEnabled(true);
        etiquetaNombre.setText("-");
        etiquetaAtk.setText("ATK -");
        etiquetaDef.setText("DEF -");
        botonElegir.setText(esRival ? "Carta rival" : "Elegir carta");
        botonElegir.setEnabled(false);
    }

    //si no se pudo cargar la carta
    public void mostrarError()
    {
        mostrarCargando();
        etiquetaImagen.setText("Error al cargar");
    }

    //pone la carta en el panel, si esta oculta no se ven sus datos
    public void mostrarCarta(Card nueva, boolean estaOculta)
    {
        mostrarCargando();
        carta = nueva;
        oculta = estaOculta;
        if (oculta)
        {
            etiquetaImagen.setText("Carta oculta");
            etiquetaNombre.setText("???");
            etiquetaAtk.setText("ATK ?");
            etiquetaDef.setText("DEF ?");
        }
        else
        {
            ponerDatos();
        }
        //la imagen se descarga aunque este oculta para tenerla lista al revelarla
        descargarImagen(nueva);
    }

    //descarga la imagen con SwingWorker para no congelar la ventana
    private void descargarImagen(Card cartaPedida)
    {
        new SwingWorker<Image, Void>()
        {
            //corre en otro hilo: aqui va lo lento (internet)
            @Override
            protected Image doInBackground() throws Exception
            {
                return ImageIO.read(URI.create(cartaPedida.getUrlImagen()).toURL());
            }

            //corre en el hilo de la ventana: aqui se muestra la imagen
            @Override
            protected void done()
            {
                //si cambiaron la carta mientras descargaba, se ignora
                if (carta != cartaPedida) return;
                try
                {
                    imagen = get();
                }
                catch (Exception e)
                {
                    //si falla solo la imagen, igual se ven los datos
                    imagen = null;
                }
                if (!oculta) ponerImagen();
            }
        }.execute();
    }

    //muestra la carta de la maquina cuando la juega
    public void revelar()
    {
        oculta = false;
        ponerDatos();
        ponerImagen();
    }

    //la carta ya se jugo: se pone gris y su boton se desactiva
    public void marcarUsada()
    {
        usada = true;
        etiquetaImagen.setEnabled(false);
        botonElegir.setText("Usada");
        botonElegir.setEnabled(false);
    }

    //las cartas de la maquina dejan el boton desactivado para que todas midan igual
    public void ponerComoCartaRival()
    {
        esRival = true;
        botonElegir.setText("Carta rival");
        botonElegir.setEnabled(false);
    }

    //activa el boton solo si la carta es del jugador y no se ha usado
    public void activarBoton(boolean activar)
    {
        botonElegir.setEnabled(activar && carta != null && !usada && !esRival);
    }

    //imagen pequeña de la carta para la mesa
    public ImageIcon getImagenPequenia()
    {
        if (imagen == null) return null;
        return new ImageIcon(imagen.getScaledInstance(80, 116, Image.SCALE_SMOOTH));
    }

    //escribe nombre, ATK y DEF
    private void ponerDatos()
    {
        etiquetaNombre.setText(carta.getNombre());
        //si el nombre es largo se ve completo al pasar el mouse
        etiquetaNombre.setToolTipText(carta.getNombre());
        etiquetaAtk.setText("ATK " + carta.getAtk());
        etiquetaDef.setText("DEF " + carta.getDef());
    }

    //pone la imagen descargada en el tamaño de la carta
    private void ponerImagen()
    {
        if (imagen != null)
        {
            etiquetaImagen.setIcon(new ImageIcon(imagen.getScaledInstance(110, 160, Image.SCALE_SMOOTH)));
            etiquetaImagen.setText(null);
        }
        else
        {
            etiquetaImagen.setText("Sin imagen");
        }
    }

    public Card getCarta() { return carta; }
    public boolean estaUsada() { return usada; }
    public JButton getBotonElegir() { return botonElegir; }
}
