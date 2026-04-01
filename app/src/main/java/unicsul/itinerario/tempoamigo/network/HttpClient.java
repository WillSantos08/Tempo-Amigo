package unicsul.itinerario.tempoamigo.network;

import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

public class HttpClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // ─── Callback genérico ────────────────────────────────────────────────────

    public interface HttpCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // ─── GET simples (retorna String) ─────────────────────────────────────────

    public static void get(String url, HttpCallback<String> callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("Erro HTTP: " + response.code());
                }
            }
        });
    }

    // ─── GET mapeado para DTO ─────────────────────────────────────────────────

    public static <T> void get(String url, Class<T> dtoClass, HttpCallback<T> callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    T dto = gson.fromJson(json, dtoClass);
                    callback.onSuccess(dto);
                } else {
                    callback.onError("Erro HTTP: " + response.code());
                }
            }
        });
    }

    // ─── POST simples (envia e retorna String) ────────────────────────────────

    public static void post(String url, String jsonBody, HttpCallback<String> callback) {
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("Erro HTTP: " + response.code());
                }
            }
        });
    }

    // ─── POST com DTO de entrada e saída ──────────────────────────────────────

    public static <T> void post(String url, Object requestDto, Class<T> responseClass, HttpCallback<T> callback) {
        String jsonBody = gson.toJson(requestDto);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    T dto = gson.fromJson(json, responseClass);
                    callback.onSuccess(dto);
                } else {
                    callback.onError("Erro HTTP: " + response.code());
                }
            }
        });
    }
}