package yugioh.interfaz;

import yugioh.duelo.BattleListener;
import yugioh.modelo.Card;

import javax.swing.*;
import java.util.List;

//ventana principal del duelo, el diseño esta en VentanaDuelo.form
//implementa BattleListener para que la logica (Duel) le avise lo que pasa en cada ronda
public class VentanaDuelo extends JFrame implements BattleListener
{
    //componentes del .form, IntelliJ los crea solo por eso no llevan new
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

    //las 3 cartas de cada jugador en un arreglo para recorrerlas con un for
    private final PanelCarta[] cartasJugador;
    private final PanelCarta[] cartasMaquina;

    //la logica a la que se le avisan los clics
    private AccionesDuelo acciones;
    private boolean hayDuelo = false;
    private boolean dueloTerminado = false;
    private int ronda = 0;

    public VentanaDuelo()
    {
        super("Yu-Gi-Oh! Duel Lite");
        //el panel del .form es el contenido de la ventana
        setContentPane(panelPrincipal);
        cartasJugador = new PanelCarta[]{cartaJugador1, cartaJugador2, cartaJugador3};
        cartasMaquina = new PanelCarta[]{cartaMaquina1, cartaMaquina2, cartaMaquina3};
        for (int i = 0; i < 3; i++)
        {
            //la maquina elige sola, su boton queda desactivado
            cartasMaquina[i].ponerComoCartaRival();

            //ActionListener del boton Elegir carta, indice guarda el numero de la carta
            int indice = i;
            cartasJugador[i].getBotonElegir().addActionListener(e -> elegirCarta(indice));
        }
        //ActionListener del boton Iniciar duelo
        botonIniciar.addActionListener(e -> clicIniciar());
        //no se puede iniciar hasta que esten las 6 cartas
        botonIniciar.setEnabled(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        //centrar la ventana en la pantalla
        setLocationRelativeTo(null);
    }

    //Main nos pasa la logica que atiende los clics
    public void setAcciones(AccionesDuelo acciones)
    {
        this.acciones = acciones;
    }

    //pone las cartas en Cargando... y le pide a la logica que reparta
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

    //muestra las 3 cartas del jugador boca arriba
    public void mostrarCartasJugador(List<Card> cartas)
    {
        //invokeLater: los cambios a la ventana se hacen en el hilo de Swing
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasJugador[i].mostrarCarta(cartas.get(i), false);
            revisarSiSePuedeIniciar();
        });
    }

    //muestra las 3 cartas de la maquina ocultas hasta que se jueguen
    public void mostrarCartasMaquina(List<Card> cartas)
    {
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasMaquina[i].mostrarCarta(cartas.get(i), true);
            revisarSiSePuedeIniciar();
        });
    }

    //muestra el error en pantalla, en el log y en una ventana emergente
    public void mostrarError(String mensaje)
    {
        SwingUtilities.invokeLater(() ->
        {
            etiquetaEstado.setText(mensaje);
            escribirEnLog("ERROR: " + mensaje);
            //el boton queda como Reintentar para volver a pedir las cartas
            dueloTerminado = true;
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

    //clic en Iniciar duelo (o Nuevo duelo cuando ya termino)
    private void clicIniciar()
    {
        if (dueloTerminado)
        {
            //si ya termino se reparten cartas nuevas
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

    //clic en Elegir carta: pone la carta en la mesa y avisa a la logica
    private void elegirCarta(int indice)
    {
        boolean enDefensa = radioDefensa.isSelected();
        limpiarMesa();
        ponerEnMesa(mesaJugador, cartasJugador[indice]);
        etiquetaTu.setText(enDefensa ? "Tú (defensa)" : "Tú (ataque)");
        etiquetaEstado.setText("La máquina está eligiendo...");
        //mientras la logica responde no se puede elegir otra carta
        activarCartas(false);
        if (acciones != null) acciones.elegirCarta(indice, enDefensa);
    }

    //la logica avisa el resultado de una ronda
    @Override
    public void onTurn(String playerCard, String aiCard, String winner)
    {
        SwingUtilities.invokeLater(() ->
        {
            ronda++;
            //la carta de la maquina se voltea y se pone en la mesa
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
            //si el duelo sigue se puede elegir la siguiente carta
            if (hayDuelo)
            {
                etiquetaEstado.setText("Elige tu siguiente carta");
                activarCartas(true);
            }
        });
    }

    //la logica avisa que cambio el marcador
    @Override
    public void onScoreChanged(int playerScore, int aiScore)
    {
        SwingUtilities.invokeLater(() ->
        {
            etiquetaMarcador.setText("Tú " + playerScore + " - " + aiScore + " Máquina");
            escribirEnLog("   Marcador: Tú " + playerScore + " - " + aiScore + " Máquina");
        });
    }

    //la logica avisa quien gano el duelo
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

    //no se inicia el duelo hasta que los dos tengan sus 3 cartas
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

    //activa o desactiva los botones de las cartas y la posicion
    private void activarCartas(boolean activar)
    {
        for (PanelCarta carta : cartasJugador) carta.activarBoton(activar);
        radioAtaque.setEnabled(activar);
        radioDefensa.setEnabled(activar);
    }

    //pone la imagen de la carta en la mesa, si no hay imagen pone el nombre
    private void ponerEnMesa(JLabel espacio, PanelCarta carta)
    {
        ImageIcon imagen = carta.getImagenPequenia();
        espacio.setIcon(imagen);
        espacio.setText(imagen == null ? carta.getCarta().getNombre() : null);
    }

    //deja la mesa vacia
    private void limpiarMesa()
    {
        mesaJugador.setIcon(null);
        mesaJugador.setText("Tu carta");
        mesaMaquina.setIcon(null);
        mesaMaquina.setText("Carta rival");
        etiquetaTu.setText("Tú");
        etiquetaRival.setText("Máquina");
    }

    //busca la primera carta sin usar que tenga ese nombre
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
