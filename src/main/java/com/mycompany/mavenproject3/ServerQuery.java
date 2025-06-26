package com.mycompany.mavenproject3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.mavenproject3.product.Product;
import com.mycompany.mavenproject3.transaction.Transaction;

public class ServerQuery {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Transaction.class, new TransactionDeserializer())
            .create();

    @SuppressWarnings("deprecation")
    public static <T> T get(String dest, Type typeofT) throws Exception {
        URL url = new URL("http://localhost:4567/api/" + dest);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String json = in.lines().collect(Collectors.joining());
        in.close();

        T data = gson.fromJson(json, typeofT);

        return data;
    }

    @SuppressWarnings("deprecation")
    public static void add(String dest, Object body) throws Exception {
        String APIUrl = "http://localhost:4567/api/" + dest;
        URL url = new URL(APIUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        String json = gson.toJson(body);

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("Query tambah berhasil.");
        } else {
            System.out.println("Query tambah gagal. Kode: " + responseCode);

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

    @SuppressWarnings("deprecation")
    public static void update(String dest, Object body, int id) throws Exception {
        String APIUrl = "http://localhost:4567/api/" + dest + "/" + id;
        URL url = new URL(APIUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");

        String json = gson.toJson(body);

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("Query ubah berhasil.");
        } else {
            System.out.println("Query ubah gagal. Kode: " + responseCode);
        }
    }

    @SuppressWarnings("deprecation")
    public static void delete(String dest, int id) throws Exception {
        String APIUrl = "http://localhost:4567/api/" + dest + "/" + id;
        URL url = new URL(APIUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 204) {
            System.out.println("Query delete berhasil.");
        } else {
            System.out.println("Query delete gagal. Kode: " + responseCode);
        }
    }
}
