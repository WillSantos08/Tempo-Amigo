package unicsul.itinerario.tempoamigo.repository;

import java.util.concurrent.CompletableFuture;

import unicsul.itinerario.tempoamigo.model.Clima;
import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.network.RetryUtil;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;

public class ClimaRepository {

    private static final int MAX_TENTATIVAS = 6;
    private static final long DELAY_INICIAL_MS = 250;

    private final LocalizacaoClient localizacaoClient;
    private final ClimaApiClient climaApiClient;

    public ClimaRepository(LocalizacaoClient localizacaoClient, ClimaApiClient climaApiClient) {
        this.localizacaoClient = localizacaoClient;
        this.climaApiClient = climaApiClient;
    }

    public CompletableFuture<Clima> buscarClimaPorLocalizacao() {
        return localizacaoClient.obterLocalizacao()
                .thenCompose(location ->
                        RetryUtil.executar(
                                () -> climaApiClient.buscarClima(
                                        location.getLatitude(),
                                        location.getLongitude()
                                ),
                                MAX_TENTATIVAS,
                                DELAY_INICIAL_MS
                        )
                );
    }

    public CompletableFuture<Clima> buscarClimaPorLocalizacaoBackground() {
        return localizacaoClient.obterLocalizacaoBackground()
                .thenCompose(location ->
                        RetryUtil.executar(
                                () -> climaApiClient.buscarClima(
                                        location.getLatitude(),
                                        location.getLongitude()
                                ),
                                MAX_TENTATIVAS,
                                DELAY_INICIAL_MS
                        )
                );
    }
}