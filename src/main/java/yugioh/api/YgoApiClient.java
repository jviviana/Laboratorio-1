package yugioh.api;

import org.json.JSONArray;
import org.json.JSONObject;
import yugioh.modelo.Card;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class YgoApiClient
{
    public List<Card> obtenerCartas(int cantidad) throws IOException, InterruptedException
    {

        List<Card> cartas = new ArrayList<>();

        //se crea un cliente http para realizar la peticion

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        //se crea un objeto de tipo request para realizar la peticion
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://db.ygoprodeck.com/api/v7/randomcard.php"))
                .build();

        while (cartas.size() < cantidad)
        {
            //ejecutamos la solicitud
            HttpResponse<String> response;
            try
            {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException e)
            {
                //no hay internet o no se pudo conectar con la API
                throw new IOException("Error de red", e);
            }

            //se verifica el codigo de respuesta, 200 para ejecucion exitosa
            if (response.statusCode() != 200)
            {
                throw new IOException("No se pudo cargar la carta");
            }

            //Creamos el objeto JSON
            JSONObject json = new JSONObject(response.body());

            //la carta viene dentro del array "data"
            JSONObject cartaJson = json.getJSONArray("data").getJSONObject(0);

            //se valida que la carta sea tipo Monster, si no se vuelve a solicitar
            if (!cartaJson.getString("type").contains("Monster"))
            {
                continue;
            }

            //accedemos al primer objeto de imagen
            JSONArray imagesJson = cartaJson.getJSONArray("card_images");
            String urlImagen = imagesJson.getJSONObject(0).getString("image_url");

            //en vez de imprimir, se crea la carta y se agrega a la lista
            //los monstruos Link no tienen DEF, por eso se usa optInt
            Card carta = new Card(
                    cartaJson.getString("name"),
                    cartaJson.optInt("atk", 0),
                    cartaJson.optInt("def", 0),
                    urlImagen);
            cartas.add(carta);
        }

        return cartas;
    }

    //psvm para probar solo esta clase
    public static void main(String[] args) throws Exception
    {
        YgoApiClient api = new YgoApiClient();
        List<Card> cartas = api.obtenerCartas(3);

        for (Card carta : cartas)
        {
            System.out.println(carta + " -> " + carta.getUrlImagen());
        }
    }
}
