package org.corpse.telling.death.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

import org.corpse.telling.death.CorpseAutopsy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpPetition {
	public static List<String> getPlayers(Properties p) {
        try {
        	String ip = p.getProperty("ip");
            URL url = new URL("http://" + ip.replace("\\", "") + "/players");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder respuesta = new StringBuilder();
            String linea;

            while ((linea = in.readLine()) != null) {
                respuesta.append(linea);
            }
            in.close();

            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(respuesta.toString(), tipoLista);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
	
	public static String ping(String ip, Properties p) {
		HttpURLConnection con = null;
        try {
            URL url = new URL("http://" + ip + "/ping");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder answer = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                answer.append(line);
            }
            in.close();
            p.setProperty("ip", ip);
            return answer.toString();

        } catch (IOException e) {
            return "ERROR: " + "http://" + ip + "/ping" + e.getMessage();
        }
    }
	
	public static String sendCommand(String command, Properties p) {
        try {
        	String ip = p.getProperty("ip");
        	System.out.println(ip);
            URL url = new URL("http://" + ip.replace("\\", "") + "/command");
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "text/plain");

            try (OutputStream os = con.getOutputStream()) {
                os.write(command.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return "Comando enviado correctamente: " + command;
            } else {
                return "Error en la petición: Código " + responseCode;
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
