package unicsul.itinerario.tempoamigo.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import unicsul.itinerario.tempoamigo.dto.ClimaDTO;
import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;

public class ClimaWorker extends Worker {

    public ClimaWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        ClimaRepository repository = new ClimaRepository(
                new LocalizacaoClient(getApplicationContext()),
                ClimaApiClient.criar()
        );

        try {
            ClimaDTO clima = repository.buscarClimaPorLocalizacao().get();
            List<String> alertas = new AlertaClimaticoService(clima).verificarAlertas();

            if (!alertas.isEmpty()) {
                new NotificacaoService(getApplicationContext()).notificarAlertas(alertas);
            }

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}