package yugioh.modelo;

public class Card
{
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
