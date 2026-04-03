package unicsul.itinerario.tempoamigo.repository;

import java.util.concurrent.CompletableFuture;

import unicsul.itinerario.tempoamigo.dto.ClimaDTO;
import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;

public class ClimaRepository {

    private final LocalizacaoClient localizacaoClient;
    private final ClimaApiClient climaApiClient;

    public ClimaRepository(LocalizacaoClient localizacaoClient, ClimaApiClient climaApiClient) {
        this.localizacaoClient = localizacaoClient;
        this.climaApiClient = climaApiClient;
    }

    public CompletableFuture<ClimaDTO> buscarClimaPorLocalizacao() {
        return localizacaoClient.obterLocalizacao()
                .thenCompose(location -> climaApiClient.buscarClima(
                        location.getLatitude(),
                        location.getLongitude()
                ));
    }

    public CompletableFuture<ClimaDTO> buscarClimaPorLocalizacaoBackground() {
        return localizacaoClient.obterLocalizacaoBackground()
                .thenCompose(location -> climaApiClient.buscarClima(
                        location.getLatitude(),
                        location.getLongitude()
                ));
    }
}