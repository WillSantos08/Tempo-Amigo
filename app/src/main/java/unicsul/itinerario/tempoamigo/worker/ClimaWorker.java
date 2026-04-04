package unicsul.itinerario.tempoamigo.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import unicsul.itinerario.tempoamigo.factory.ClimaRepositoryFactory;
import unicsul.itinerario.tempoamigo.factory.ContatoEmergenciaFactory;
import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.Clima;
import unicsul.itinerario.tempoamigo.model.Localizacao;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;

public class ClimaWorker extends Worker {

    public static final String TAG = "ClimaWorker";

    public ClimaWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "=== Worker iniciado ===");
        try {
            Localizacao localizacao = obterLocalizacao();
            Clima clima = obterClima();
            List<Alerta> alertas = new AlertaClimaticoService(clima).verificarAlertas();
            Log.d(TAG, "Alertas encontrados: " + alertas.size());
            dispararNotificacaoSeNecessario(alertas, localizacao);
            return Result.success();
        } catch (TimeoutException e) {
            Log.e(TAG, "Timeout ao buscar localização/clima — tentará novamente", e);
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "Erro inesperado no worker", e);
            return Result.failure();
        }
    }

    private Localizacao obterLocalizacao() throws Exception {
        Log.d(TAG, "Buscando localização em background...");
        return new LocalizacaoClient(getApplicationContext())
                .obterLocalizacaoBackground()
                .thenApply(l -> new Localizacao(l.getLatitude(), l.getLongitude()))
                .get(30, TimeUnit.SECONDS);
    }

    private Clima obterClima() throws Exception {
        Log.d(TAG, "Buscando clima em background...");
        ClimaRepository repository = ClimaRepositoryFactory.criar(getApplicationContext());
        Clima clima = repository.buscarClimaPorLocalizacaoBackground().get(30, TimeUnit.SECONDS);
        Log.d(TAG, "Clima recebido: " + clima.getTemperatura() + "°C");
        return clima;
    }

    private void dispararNotificacaoSeNecessario(List<Alerta> alertas, Localizacao localizacao) {
        if (!NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
            Log.w(TAG, "Notificações desativadas pelo usuário — abortando");
            return;
        }

        if (alertas.isEmpty()) {
            Log.d(TAG, "Sem alertas — cancelando notificação anterior se existir");
            NotificationManagerCompat.from(getApplicationContext())
                    .cancel(NotificacaoService.NOTIFICACAO_ID);
            return;
        }

        Log.d(TAG, "Disparando notificação...");
        new ContatoEmergenciaFactory(getApplicationContext())
                .buscar()
                .thenAccept(contato ->
                        new NotificacaoService(getApplicationContext())
                                .notificarAlertas(alertas, contato, localizacao)
                );
        Log.d(TAG, "Notificação disparada com sucesso!");
    }
}