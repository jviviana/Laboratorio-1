package yugioh.modelo;

/**
 * MODELO de una carta Monster: una clase que solo guarda datos.
 * No sabe nada de internet ni de ventanas, solo representa "una carta".
 *
 * (El nombre "Card" y sus atributos los pide el taller: nombre, atk, def, imagen.)
 *
 * NOTA PARA LA PAREJA: la interfaz grafica solo usa los 4 getters de abajo.
 * Si la parte de logica necesita mas datos (tipo, nivel, id...), se pueden
 * agregar sin romper nada de la parte visual.
 */
public class Card
{
    // "final" = el valor se asigna UNA vez (en el constructor) y no cambia despues
    private final String nombre;
    private final int atk;
    private final int def;
    private final String urlImagen;

    //constructor: se ejecuta al hacer "new Card(...)"
    public Card(String nombre, int atk, int def, String urlImagen)
    {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.urlImagen = urlImagen;
    }

    // ---- getters: metodos para LEER los atributos privados desde otras clases ----
    public String getNombre() { return nombre; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public String getUrlImagen() { return urlImagen; }

    @Override
    public String toString()
    {
        return nombre + " (ATK " + atk + " / DEF " + def + ")";
    }
}
