package org.corpse.telling.death.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpPetition {
	public static List<String> getPlayers() {
        try {
        	Properties p = new Properties();
        	p.load(new FileInputStream(new File("config/conf.properties")));
        	String ip = new String(Files.readAllBytes(Paths.get(p.getProperty("fileIp"))), StandardCharsets.UTF_8);
            URL url = new URL("http://" + ip + "/players");
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
	
	public static String ping(String ip) {
		HttpURLConnection con = null;
        try {
        	Properties p = new Properties();
        	p.load(new FileInputStream(new File("config/conf.properties")));
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
            Files.write(
            	    Paths.get(p.getProperty("fileIp")),
            	    ip.getBytes(StandardCharsets.UTF_8),
            	    StandardOpenOption.CREATE,
            	    StandardOpenOption.TRUNCATE_EXISTING
            	);
            return answer.toString();

        } catch (IOException e) {
            return "ERROR";
        }
    }
	
	public static String sendCommand(String command) {
        try {
        	Properties p = new Properties();
        	p.load(new FileInputStream(new File("config/conf.properties")));
        	String ip = Files.readString(Paths.get(p.getProperty("fileIp")));
            URL url = new URL("http://" + ip + "/command");
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
