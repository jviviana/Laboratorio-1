package yugioh.interfaz;

import yugioh.modelo.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class PanelCarta
{
    private JPanel panelCarta;
    private JLabel etiquetaImagen;
    private JLabel etiquetaNombre;
    private JLabel etiquetaAtk;
    private JLabel etiquetaDef;
    private JButton botonElegir;

    private Card carta;
    private Image imagen;
    private boolean oculta;
    private boolean usada;
    private boolean esRival;

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

    public void mostrarError()
    {
        mostrarCargando();
        etiquetaImagen.setText("Error al cargar");
    }

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
        descargarImagen(nueva);
    }
    private void descargarImagen(Card cartaPedida)
    {
        new SwingWorker<Image, Void>()
        {
            @Override
            protected Image doInBackground() throws Exception
            {
                return ImageIO.read(URI.create(cartaPedida.getUrlImagen()).toURL());
            }
            @Override
            protected void done()
            {
                if (carta != cartaPedida) return;
                try
                {
                    imagen = get();
                }
                catch (Exception e)
                {
                    imagen = null;
                }
                if (!oculta) ponerImagen();
            }
        }.execute();
    }
    public void revelar()
    {
        oculta = false;
        ponerDatos();
        ponerImagen();
    }
    public void marcarUsada()
    {
        usada = true;
        etiquetaImagen.setEnabled(false);
        botonElegir.setText("Usada");
        botonElegir.setEnabled(false);
    }
    public void ponerComoCartaRival()
    {
        esRival = true;
        botonElegir.setText("Carta rival");
        botonElegir.setEnabled(false);
    }
    public void activarBoton(boolean activar)
    {
        botonElegir.setEnabled(activar && carta != null && !usada && !esRival);
    }
    public ImageIcon getImagenPequenia()
    {
        if (imagen == null) return null;
        return new ImageIcon(imagen.getScaledInstance(80, 116, Image.SCALE_SMOOTH));
    }
    private void ponerDatos()
    {
        etiquetaNombre.setText(carta.getNombre());
        etiquetaNombre.setToolTipText(carta.getNombre());
        etiquetaAtk.setText("ATK " + carta.getAtk());
        etiquetaDef.setText("DEF " + carta.getDef());
    }
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
