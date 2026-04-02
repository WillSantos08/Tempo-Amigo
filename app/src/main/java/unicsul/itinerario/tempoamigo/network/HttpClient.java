package unicsul.itinerario.tempoamigo.network;

import android.os.Handler;
import android.os.Looper;

import okhttp3.OkHttpClient;

public class HttpClient {

    public static final Handler mainThread = new Handler(Looper.getMainLooper());

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    public static OkHttpClient getInstance() {
        return okHttpClient;
    }
}