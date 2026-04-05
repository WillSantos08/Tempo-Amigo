package unicsul.itinerario.tempoamigo.network.clima;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import unicsul.itinerario.tempoamigo.model.Clima;
import unicsul.itinerario.tempoamigo.network.HttpClient;

public class OpenMeteoApiClient implements ClimaApiClient {

    private static final String BASE_URL = "https://api.open-meteo.com/";

    private final OpenMeteoApi api;

    private OpenMeteoApiClient(OpenMeteoApi api) {
        this.api = api;
    }

    public static OpenMeteoApiClient criar() {
        return criar(HttpClient.getInstance());
    }

    public static OpenMeteoApiClient criar(OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return new OpenMeteoApiClient(retrofit.create(OpenMeteoApi.class));
    }

    @Override
    public CompletableFuture<Clima> buscarClima(double latitude, double longitude) {
        return api.buscarClima(parametros(latitude, longitude))
                .thenApply(ClimaMapper::fromOpenMeteo);
    }

    private Map<String, String> parametros(double latitude, double longitude) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation,weather_code");
        params.put("hourly", "temperature_2m,precipitation_probability");
        params.put("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum");
        params.put("timezone", "America/Sao_Paulo");
        return params;
    }

    private interface OpenMeteoApi {
        @GET("v1/forecast")
        CompletableFuture<OpenMeteoForecast> buscarClima(
                @QueryMap Map<String, String> parametros
        );
    }
}