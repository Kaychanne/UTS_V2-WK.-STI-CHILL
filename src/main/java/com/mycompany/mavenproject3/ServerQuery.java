package com.mycompany.mavenproject3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.mavenproject3.transaction.Transaction;

public class ServerQuery {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Transaction.class, new TransactionDeserializer())
            .create();

    public static <T> T get(String dest, Type typeofT) throws Exception {
        HttpURLConnection conn = setupConnection(dest, "GET");

        String json;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            json = in.lines().collect(Collectors.joining());
        }

        T data = gson.fromJson(json, typeofT);
        return data;
    }

    public static void add(String dest, Object body) throws Exception {
        HttpURLConnection conn = setupConnection(dest, "POST");
        
        try (OutputStream os = conn.getOutputStream()) {
            String json = gson.toJson(body);
            os.write(json.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("Query tambah berhasil.");
        } else {
            System.out.println("Query tambah gagal. Kode: " + responseCode);
            printError(conn);
        }
    }

    public static void update(String dest, Object body, int id) throws Exception {
        HttpURLConnection conn = setupConnection(dest + "/" + id, "PUT");

        try (OutputStream os = conn.getOutputStream()) {
            String json = gson.toJson(body);
            os.write(json.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("Query ubah berhasil.");
        } else {
            System.out.println("Query ubah gagal. Kode: " + responseCode);
            printError(conn);
        }
    }

    public static void delete(String dest, int id) throws Exception {
        HttpURLConnection conn = setupConnection(dest + "/" + id, "DELETE");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 204) {
            System.out.println("Query delete berhasil.");
        } else {
            System.out.println("Query delete gagal. Kode: " + responseCode);
            printError(conn);
        }
    }

    @SuppressWarnings("deprecation")
    private static HttpURLConnection setupConnection(String url, String method) throws Exception {
        URL _url = new URL("http://localhost:4567/api/" + url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        conn.setRequestMethod(method);

        switch (method) {
            case "POST", "PUT" -> {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
            }
            default -> {
            }
        }

        return conn;
    }

    private static void printError(HttpURLConnection conn) throws Exception {
        @SuppressWarnings("StringBufferMayBeStringBuilder")
        StringBuffer response = new StringBuffer();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            String il;

            while ((il = in.readLine()) != null) {
                response.append(il);
            }
        }

        System.out.println(response.toString());
    }
}
