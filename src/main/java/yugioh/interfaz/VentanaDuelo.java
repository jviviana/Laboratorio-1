package yugioh.interfaz;

import yugioh.duelo.BattleListener;
import yugioh.modelo.Card;

import javax.swing.*;
import java.util.List;

public class VentanaDuelo extends JFrame implements BattleListener
{
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
    private final PanelCarta[] cartasJugador;
    private final PanelCarta[] cartasMaquina;
    private AccionesDuelo acciones;
    private boolean hayDuelo = false;
    private boolean dueloTerminado = false;
    private int ronda = 0;
    public VentanaDuelo()
    {
        super("Yu-Gi-Oh! Duel Lite");
        setContentPane(panelPrincipal);
        cartasJugador = new PanelCarta[]{cartaJugador1, cartaJugador2, cartaJugador3};
        cartasMaquina = new PanelCarta[]{cartaMaquina1, cartaMaquina2, cartaMaquina3};
        for (int i = 0; i < 3; i++)
        {
            cartasMaquina[i].ponerComoCartaRival();

            int indice = i;
            cartasJugador[i].getBotonElegir().addActionListener(e -> elegirCarta(indice));
        }
        botonIniciar.addActionListener(e -> clicIniciar());
        botonIniciar.setEnabled(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public void setAcciones(AccionesDuelo acciones)
    {
        this.acciones = acciones;
    }
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
    public void mostrarCartasJugador(List<Card> cartas)
    {
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasJugador[i].mostrarCarta(cartas.get(i), false);
            revisarSiSePuedeIniciar();
        });
    }
    public void mostrarCartasMaquina(List<Card> cartas)
    {
        SwingUtilities.invokeLater(() ->
        {
            for (int i = 0; i < 3; i++) cartasMaquina[i].mostrarCarta(cartas.get(i), true);
            revisarSiSePuedeIniciar();
        });
    }
    public void mostrarError(String mensaje)
    {
        SwingUtilities.invokeLater(() ->
        {
            etiquetaEstado.setText(mensaje);
            escribirEnLog("ERROR: " + mensaje);
            dueloTerminado = true;
            botonIniciar.setText("Reintentar");
            botonIniciar.setEnabled(true);
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    public void escribirEnLog(String linea)
    {
        areaLog.append(linea + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    private void clicIniciar()
    {
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
        limpiarMesa();
        ponerEnMesa(mesaJugador, cartasJugador[indice]);
        etiquetaTu.setText(enDefensa ? "Tú (defensa)" : "Tú (ataque)");
        etiquetaEstado.setText("La máquina está eligiendo...");
        activarCartas(false);
        if (acciones != null) acciones.elegirCarta(indice, enDefensa);
    }

    @Override
    public void onTurn(String playerCard, String aiCard, String winner)
    {
        SwingUtilities.invokeLater(() ->
        {
            ronda++;
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
