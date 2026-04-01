package unicsul.itinerario.tempoamigo.network;

import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

public class HttpClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Handler mainThread = new Handler(Looper.getMainLooper());

    public interface OnSuccess<T> { void run(T result); }
    public interface OnError { void run(String error); }

    // ─── GET ──────────────────────────────────────────────────────────────────

    public static <T> void get(String url, Class<T> dtoClass, OnSuccess<T> onSuccess, OnError onError) {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainThread.post(() -> onError.run(e.getMessage() != null ? e.getMessage() : "Erro desconhecido"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    T dto = gson.fromJson(body.string(), dtoClass);
                    mainThread.post(() -> onSuccess.run(dto));
                } else {
                    mainThread.post(() -> onError.run("Erro HTTP: " + response.code()));
                }
            }
        });
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    public static <T> void post(String url, Object requestDto, Class<T> responseClass, OnSuccess<T> onSuccess, OnError onError) {
        RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainThread.post(() -> onError.run(e.getMessage() != null ? e.getMessage() : "Erro desconhecido"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    T dto = gson.fromJson(body.string(), responseClass);
                    mainThread.post(() -> onSuccess.run(dto));
                } else {
                    mainThread.post(() -> onError.run("Erro HTTP: " + response.code()));
                }
            }
        });
    }
}