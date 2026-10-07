package yugioh.interfaz;

import yugioh.duelo.BattleListener;
import yugioh.modelo.Card;

import javax.swing.*;
import java.util.List;

/**
 * VENTANA PRINCIPAL del duelo.
 *
 * EL DISEÑO esta en VentanaDuelo.form (se abre con el GUI Designer de IntelliJ):
 *   - arriba:  titulo y marcador
 *   - centro:  tus cartas (azul), la mesa de duelo y las cartas de la maquina (rojo)
 *   - abajo:   el log de batalla (JTextArea dentro de un JScrollPane, lo pide el taller)
 * Cada una de las 6 cartas es una copia de PanelCarta.form (en el diseñador se ven adentro).
 *
 * Esta clase solo se encarga de lo VISUAL:
 *   - cuando el usuario hace clic, le avisa a la logica con AccionesDuelo
 *   - cuando la logica tiene un resultado, nos avisa con BattleListener
 */
public class VentanaDuelo extends JFrame implements BattleListener
{
    // ---- componentes del .form (IntelliJ los crea solo, por eso no llevan "new") ----
    private JPanel panelPrincipal;
    private JPanel panelEncabezado;
    private JLabel etiquetaTitulo;
    private JLabel etiquetaMarcador;
    private JPanel panelJuego;
    private JPanel panelJugador;
    private PanelCarta cartaJugador1;
    private PanelCarta cartaJugador2;
    private PanelCarta cartaJugador3;
    private JPanel panelMesa;
    private JPanel panelSlots;
    private JLabel mesaJugador;
    private JLabel etiquetaVs;
    private JLabel mesaMaquina;
    private JLabel etiquetaTu;
    private JLabel etiquetaRival;
    private JLabel etiquetaResultado;
    private JLabel etiquetaPosicion;
    private JPanel panelPosicion;
    private JRadioButton radioAtaque;
    private JRadioButton radioDefensa;
    private JButton botonIniciar;
    private JLabel etiquetaEstado;
    private JPanel panelMaquina;
    private PanelCarta cartaMaquina1;
    private PanelCarta cartaMaquina2;
    private PanelCarta cartaMaquina3;
    private JScrollPane scrollLog;
    private JTextArea areaLog;

    // las 3 cartas de cada uno en un arreglo, para recorrerlas con un for
    private final PanelCarta[] cartasJugador;
    private final PanelCarta[] cartasMaquina;

    private AccionesDuelo acciones;        //la logica (la conecta Main)
    private boolean hayDuelo = false;      //true mientras se esta jugando
    private boolean dueloTerminado = false;
    private int ronda = 0;

    public VentanaDuelo()
    {
        super("Yu-Gi-Oh! Duel Lite"); //titulo de la ventana
        setContentPane(panelPrincipal); //el panel del .form es el contenido de la ventana

        cartasJugador = new PanelCarta[]{cartaJugador1, cartaJugador2, cartaJugador3};
        cartasMaquina = new PanelCarta[]{cartaMaquina1, cartaMaquina2, cartaMaquina3};

        for (int i = 0; i < 3; i++)
        {
            cartasMaquina[i].ponerComoCartaRival(); //la maquina elige sola, su boton queda desactivado

            // ActionListener del boton "Elegir carta" (lo pide el taller).
            // "indice" guarda el numero de la carta para usarlo dentro de la lambda.
            int indice = i;
            cartasJugador[i].getBotonElegir().addActionListener(e -> elegirCarta(indice));
        }

        // ActionListener del boton "Iniciar duelo" (lo pide el taller)
        botonIniciar.addActionListener(e -> clicIniciar());
        botonIniciar.setEnabled(false); //no se puede iniciar hasta tener las 6 cartas

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //al cerrar la ventana se cierra el programa
        pack();                       //ajusta el tamaño a lo que tiene adentro
        setLocationRelativeTo(null);  //centrar en la pantalla
    }

    // =====================================================================
    //  METODOS PARA LA LOGICA (los usa Main o la parte de logica)
    // =====================================================================

    public void setAcciones(AccionesDuelo acciones)
    {
        this.acciones = acciones;
    }

    //pone las cartas en "Cargando..." y le pide a la logica que reparta
    public void pedirCartas()
    {
        for (int i = 0; i < 3; i++)
        {
            cartasJugador[i].mostrarCargando();
            cartasMaquina[i].mostrarCargando();
        }
        dueloTerminado = false;
        limpiarMesa();
        etiquetaMarcador.setText("Tú 0 - 0 Máquina");
        etiquetaResultado.setText("Repartiendo cartas...");
        etiquetaEstado.setText("Cargando cartas desde YGOProDeck...");
        botonIniciar.setText("Iniciar duelo");
        botonIniciar.setEnabled(false);

        if (acciones != null) acciones.repartirCartas();
    }

    //la logica nos da las 3 cartas del jugador (se ven boca arriba)
    public void mostrarCartasJugador(List<Card> cartas)
    {
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasJugador[i].mostrarCarta(cartas.get(i), false);
            revisarSiSePuedeIniciar();
        });
    }

    //la logica nos da las 3 cartas de la maquina (ocultas hasta que se juegan)
    public void mostrarCartasMaquina(List<Card> cartas)
    {
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasMaquina[i].mostrarCarta(cartas.get(i), true);
            revisarSiSePuedeIniciar();
        });
    }

    //error visible (lo pide el taller): "No se pudo cargar la carta", "Error de red"...
    public void mostrarError(String mensaje)
    {
        SwingUtilities.invokeLater(() ->
        {
            etiquetaEstado.setText(mensaje);
            escribirEnLog("ERROR: " + mensaje);
            dueloTerminado = true;                //asi el boton sirve para volver a intentar
            botonIniciar.setText("Reintentar");
            botonIniciar.setEnabled(true);
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    //agrega una linea al log y baja el scroll hasta el final
    public void escribirEnLog(String linea)
    {
        areaLog.append(linea + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    // =====================================================================
    //  CLICS DEL USUARIO
    // =====================================================================

    private void clicIniciar()
    {
        // si el duelo ya termino, el boton dice "Nuevo duelo": se reparten cartas nuevas
        if (dueloTerminado)
        {
            pedirCartas();
            return;
        }

        hayDuelo = true;
        ronda = 0;
        areaLog.setText("");
        escribirEnLog("¡Comienza el duelo! Gana quien consiga 2 rondas.");
        etiquetaResultado.setText("Ronda 1");
        etiquetaEstado.setText("Elige una de tus cartas");
        botonIniciar.setEnabled(false);
        activarCartas(true);

        if (acciones != null) acciones.iniciarDuelo();
    }

    private void elegirCarta(int indice)
    {
        boolean enDefensa = radioDefensa.isSelected();

        // ponemos la carta del jugador en la mesa
        limpiarMesa();
        ponerEnMesa(mesaJugador, cartasJugador[indice]);
        etiquetaTu.setText(enDefensa ? "Tú (defensa)" : "Tú (ataque)");
        etiquetaEstado.setText("La máquina está eligiendo...");
        activarCartas(false); //mientras la logica responde, no se puede elegir otra

        if (acciones != null) acciones.elegirCarta(indice, enDefensa);
    }

    // =====================================================================
    //  METODOS DE BattleListener (los llama la LOGICA)
    //  Swing solo se puede tocar desde su propio hilo, por eso cada uno
    //  usa SwingUtilities.invokeLater(...): "hilo de Swing, haz esto tu".
    // =====================================================================

    @Override
    public void onTurn(String playerCard, String aiCard, String winner)
    {
        SwingUtilities.invokeLater(() ->
        {
            ronda++;

            // la carta de la maquina se voltea y va a la mesa
            PanelCarta deLaMaquina = buscarCarta(cartasMaquina, aiCard);
            if (deLaMaquina != null)
            {
                deLaMaquina.revelar();
                ponerEnMesa(mesaMaquina, deLaMaquina);
                deLaMaquina.marcarUsada();
            }
            PanelCarta delJugador = buscarCarta(cartasJugador, playerCard);
            if (delJugador != null) delJugador.marcarUsada();

            etiquetaResultado.setText("Ronda " + ronda + ": gana " + winner);
            escribirEnLog("Ronda " + ronda + ": Tú jugaste " + playerCard
                    + " | La máquina jugó " + aiCard + " -> Gana: " + winner);

            if (hayDuelo)
            {
                etiquetaEstado.setText("Elige tu siguiente carta");
                activarCartas(true);
            }
        });
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore)
    {
        SwingUtilities.invokeLater(() ->
        {
            etiquetaMarcador.setText("Tú " + playerScore + " - " + aiScore + " Máquina");
            escribirEnLog("   Marcador: Tú " + playerScore + " - " + aiScore + " Máquina");
        });
    }

    @Override
    public void onDuelEnded(String winner)
    {
        SwingUtilities.invokeLater(() ->
        {
            hayDuelo = false;
            dueloTerminado = true;
            activarCartas(false);
            escribirEnLog("*** ¡" + winner + " gana el duelo! ***");
            etiquetaResultado.setText("¡" + winner + " gana el duelo!");
            etiquetaEstado.setText("Pulsa Nuevo duelo para jugar otra vez");
            botonIniciar.setText("Nuevo duelo");
            botonIniciar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "¡" + winner + " gana el duelo!");
        });
    }

    // =====================================================================
    //  AYUDANTES
    // =====================================================================

    //validacion del taller: no iniciar hasta que los dos tengan sus 3 cartas
    private void revisarSiSePuedeIniciar()
    {
        for (int i = 0; i < 3; i++)
        {
            if (cartasJugador[i].getCarta() == null || cartasMaquina[i].getCarta() == null) return;
        }
        etiquetaResultado.setText("¡Cartas listas!");
        etiquetaEstado.setText("Pulsa Iniciar duelo");
        botonIniciar.setEnabled(true);
    }

    private void activarCartas(boolean activar)
    {
        for (PanelCarta carta : cartasJugador) carta.activarBoton(activar);
        radioAtaque.setEnabled(activar);
        radioDefensa.setEnabled(activar);
    }

    //pone la imagen de la carta en un espacio de la mesa (o su nombre si no hay imagen)
    private void ponerEnMesa(JLabel espacio, PanelCarta carta)
    {
        ImageIcon imagen = carta.getImagenPequenia();
        espacio.setIcon(imagen);
        espacio.setText(imagen == null ? carta.getCarta().getNombre() : null);
    }

    private void limpiarMesa()
    {
        mesaJugador.setIcon(null);
        mesaJugador.setText("Tu carta");
        mesaMaquina.setIcon(null);
        mesaMaquina.setText("Carta rival");
        etiquetaTu.setText("Tú");
        etiquetaRival.setText("Máquina");
    }

    //busca la primera carta SIN usar que tenga ese nombre
    private PanelCarta buscarCarta(PanelCarta[] mano, String nombre)
    {
        for (PanelCarta carta : mano)
        {
            if (carta.getCarta() != null && !carta.estaUsada() && carta.getCarta().getNombre().equals(nombre))
            {
                return carta;
            }
        }
        return null;
    }
}
