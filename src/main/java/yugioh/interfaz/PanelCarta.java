package yugioh.interfaz;

import yugioh.modelo.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URI;

/**
 * Diseño de UNA carta: imagen, nombre, ATK, DEF y el boton "Elegir carta".
 *
 * EL DISEÑO esta en PanelCarta.form (se abre con el GUI Designer de IntelliJ).
 * La ventana usa este mismo .form 6 veces (3 cartas del jugador y 3 de la maquina),
 * asi que si cambias algo aqui, cambian las 6 cartas.
 *
 * Los atributos de abajo tienen el MISMO nombre que el "binding" de cada
 * componente en el .form. IntelliJ los crea solo, por eso no llevan "new".
 */
public class PanelCarta
{
    // ---- componentes del .form ----
    private JPanel panelCarta;
    private JLabel etiquetaImagen;
    private JLabel etiquetaNombre;
    private JLabel etiquetaAtk;
    private JLabel etiquetaDef;
    private JButton botonElegir;

    private Card carta;          //la carta que se muestra (null si no hay ninguna)
    private Image imagen;        //la imagen descargada de la carta
    private boolean oculta;      //true = carta de la maquina que aun no se ha jugado
    private boolean usada;       //true = ya se jugo en una ronda
    private boolean esRival;     //true = carta de la maquina (su boton nunca se activa)

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

    /**
     * Pone una carta en el panel.
     * @param nueva       la carta
     * @param estaOculta  true para las cartas de la maquina: no se ven hasta que se juegan
     */
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

    /**
     * Descarga la imagen con un SwingWorker para NO congelar la ventana (lo pide el taller):
     *   doInBackground() -> se ejecuta en otro hilo: aqui va lo lento (internet)
     *   done()           -> se ejecuta en el hilo de Swing: aqui se actualiza la pantalla
     */
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
                if (carta != cartaPedida) return; //nos cambiaron la carta mientras descargaba
                try
                {
                    imagen = get(); //get() devuelve lo que retorno doInBackground()
                }
                catch (Exception e)
                {
                    imagen = null;  //si falla solo la imagen, igual se ven los datos
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
        etiquetaImagen.setEnabled(false); //un JLabel desactivado se ve gris
        botonElegir.setText("Usada");
        botonElegir.setEnabled(false);
    }

    // Las cartas de la maquina dejan su boton desactivado (la maquina elige sola).
    // No lo escondemos para que las 6 cartas tengan el mismo tamaño.
    public void ponerComoCartaRival()
    {
        esRival = true;
        botonElegir.setText("Carta rival");
        botonElegir.setEnabled(false);
    }

    //activa o desactiva el boton (nunca si la carta ya se uso)
    public void activarBoton(boolean activar)
    {
        botonElegir.setEnabled(activar && carta != null && !usada && !esRival);
    }

    //imagen pequeña de la carta para ponerla en la mesa
    public ImageIcon getImagenPequenia()
    {
        if (imagen == null) return null;
        return new ImageIcon(imagen.getScaledInstance(80, 116, Image.SCALE_SMOOTH));
    }

    private void ponerDatos()
    {
        etiquetaNombre.setText(carta.getNombre());
        etiquetaNombre.setToolTipText(carta.getNombre()); //si el nombre es largo, se ve completo al pasar el mouse
        etiquetaAtk.setText("ATK " + carta.getAtk());
        etiquetaDef.setText("DEF " + carta.getDef());
    }

    private void ponerImagen()
    {
        if (imagen != null)
        {
            // getScaledInstance cambia el tamaño de la imagen; SCALE_SMOOTH = que no se vea pixelada
            etiquetaImagen.setIcon(new ImageIcon(imagen.getScaledInstance(110, 160, Image.SCALE_SMOOTH)));
            etiquetaImagen.setText(null);
        }
        else
        {
            etiquetaImagen.setText("Sin imagen");
        }
    }

    // ---- getters ----
    public Card getCarta() { return carta; }
    public boolean estaUsada() { return usada; }
    public JButton getBotonElegir() { return botonElegir; }
}
