package unicsul.itinerario.tempoamigo.network;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import unicsul.itinerario.tempoamigo.dto.ClimaDTO;

public class ClimaApiClient {

    private static final String BASE_URL = "https://api.open-meteo.com/";

    private final ClimaApi climaApi;

    private ClimaApiClient(ClimaApi climaApi) {
        this.climaApi = climaApi;
    }

    public static ClimaApiClient criar(OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return new ClimaApiClient(retrofit.create(ClimaApi.class));
    }

    public CompletableFuture<ClimaDTO> buscarClima(double latitude, double longitude) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("latitude",  String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.putAll(parametrosPadrao());
        return climaApi.buscarClima(params);
    }

    private static Map<String, String> parametrosPadrao() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation,weather_code");
        params.put("hourly", "temperature_2m,precipitation_probability");
        params.put("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum");
        params.put("timezone", "America/Sao_Paulo");
        return params;
    }

    private interface ClimaApi {
        @GET("v1/forecast")
        CompletableFuture<ClimaDTO> buscarClima(
                @QueryMap Map<String, String> parametros
        );
    }
}