package yugioh.modelo;

//modelo de una carta Monster: solo guarda sus datos
public class Card
{
    //final: los datos de la carta no cambian despues de crearla
    private final String nombre;
    private final int atk;
    private final int def;
    private final String urlImagen;

    public Card(String nombre, int atk, int def, String urlImagen)
    {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.urlImagen = urlImagen;
    }

    //getters para leer los datos desde otras clases
    public String getNombre() { return nombre; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public String getUrlImagen() { return urlImagen; }

    //texto de la carta, por ejemplo: Dark Magician (ATK 2500 / DEF 2100)
    @Override
    public String toString()
    {
        return nombre + " (ATK " + atk + " / DEF " + def + ")";
    }
}
