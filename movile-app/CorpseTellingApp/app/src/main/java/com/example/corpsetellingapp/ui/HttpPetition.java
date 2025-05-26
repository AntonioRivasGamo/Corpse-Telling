package com.example.corpsetellingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.corpsetellingapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpPetition {

    private static final String PREFS_NAME = "ConnectionPrefs";

    public static void sendCommand(String command, Context context) {
        String serverIp = getServerIp(context);
        if (serverIp.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.error_no_server_ip), Toast.LENGTH_SHORT).show();
            return;
        }
        String urlString = "http://" + serverIp + "/command";
        new SendCommandTask(context, command).execute(urlString);
    }

    private static class SendCommandTask extends AsyncTask<String, Void, String> {
        private final Context context;
        private final String command;

        SendCommandTask(Context context, String command) {
            this.context = context.getApplicationContext();
            this.command = command;
        }

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "text/plain");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(command.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    return context.getString(R.string.command_sent_successfully, command);
                } else {
                    return context.getString(R.string.error_request_code, responseCode);
                }

            } catch (Exception e) {
                return context.getString(R.string.error_exception, e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

    public static void getPlayers(Context context, PlayersCallback callback) {
        String serverIp = getServerIp(context);
        if (serverIp.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.error_no_server_ip), Toast.LENGTH_SHORT).show();
            callback.onResult(List.of());
            return;
        }
        String urlString = "http://" + serverIp + "/players";
        new GetPlayersTask(callback).execute(urlString);
    }

    private static class GetPlayersTask extends AsyncTask<String, Void, List<String>> {
        private final PlayersCallback callback;

        GetPlayersTask(PlayersCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<String> doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
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

        @Override
        protected void onPostExecute(List<String> players) {
            callback.onResult(players);
        }
    }

    public interface PlayersCallback {
        void onResult(List<String> players);
    }

    private static String getServerIp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("server_ip", "");
    }

    public static void ping(String ip, Context context, PingCallback callback) {
        String urlString = "http://" + ip + "/ping";
        new PingTask(context, callback).execute(urlString);
    }

    private static class PingTask extends AsyncTask<String, Void, String> {
        private final Context context;
        private final PingCallback callback;

        PingTask(Context context, PingCallback callback) {
            this.context = context.getApplicationContext();
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder answer = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    answer.append(line);
                }
                in.close();
                return answer.toString();

            } catch (Exception e) {
                return "ERROR";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            callback.onResult(result);
        }
    }

    public interface PingCallback {
        void onResult(String response);
    }
}
