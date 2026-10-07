package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main
{
    public void consultarCarta()
    {
        try
        {

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            //crear obj
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://db.ygoprodeck.com/api/v7/randomcard.php"))
                    .build();

            int cartasCargadas = 0;
            while (cartasCargadas < 3)
            {

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                //se verifica el codigo de respuesta, 200 para ejecucion exitosa
                if (response.statusCode() == 200)
                {
                    //Creamos el objeto JSON
                    JSONObject json = new JSONObject(response.body());


                    JSONArray data = json.getJSONArray("data");
                    JSONObject cartaJson = data.getJSONObject(0);


                    if (!cartaJson.getString("type").contains("Monster"))
                    {
                        continue;
                    }

                    cartasCargadas++;
                    System.out.println("Carta " + cartasCargadas);
                    System.out.println("Nombre: " + cartaJson.getString("name"));
                    System.out.println("Tipo: " + cartaJson.getString("type"));
                    System.out.println("ATK: " + cartaJson.optInt("atk", 0));
                    //los monstruos Link no tienen DEF, por eso se usa optInt
                    System.out.println("DEF: " + cartaJson.optInt("def", 0));

                    System.out.println("Imagen");

                    JSONArray imagesJson = cartaJson.getJSONArray("card_images");

                    JSONObject imageJson = imagesJson.getJSONObject(0);
                    System.out.println(imageJson.getString("image_url"));
                    System.out.println();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "No se pudo cargar la carta");
                    return;
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de red");
        }
    }

    //psvm
    public static void main(String[] args)
    {
        Main main = new Main();
        main.consultarCarta();
    }
}
