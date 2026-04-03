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

import unicsul.itinerario.tempoamigo.dto.ClimaDTO;
import unicsul.itinerario.tempoamigo.factory.ContatoEmergenciaFactory;
import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.Localizacao;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;
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

        LocalizacaoClient localizacaoClient = new LocalizacaoClient(getApplicationContext());
        ClimaRepository repository = new ClimaRepository(
                localizacaoClient,
                ClimaApiClient.criar()
        );

        try {
            Log.d(TAG, "Buscando localização e clima em background...");

            Localizacao localizacao = localizacaoClient
                    .obterLocalizacaoBackground()
                    .thenApply(l -> new Localizacao(l.getLatitude(), l.getLongitude()))
                    .get(30, TimeUnit.SECONDS);

            ClimaDTO clima = repository.buscarClimaPorLocalizacaoBackground().get(30, TimeUnit.SECONDS);
            Log.d(TAG, "Clima recebido: " + clima.current.temperature2m + "°C");

            List<Alerta> alertas = new AlertaClimaticoService(clima).verificarAlertas();
            Log.d(TAG, "Alertas encontrados: " + alertas.size());

            if (!NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
                Log.w(TAG, "Notificações desativadas pelo usuário — abortando");
                return Result.success();
            }

            if (!alertas.isEmpty()) {
                Log.d(TAG, "Disparando notificação...");
                new ContatoEmergenciaFactory(getApplicationContext())
                        .buscar()
                        .thenAccept(contato ->
                                new NotificacaoService(getApplicationContext())
                                        .notificarAlertas(alertas, contato, localizacao)
                        );
                Log.d(TAG, "Notificação disparada com sucesso!");
            } else {
                Log.d(TAG, "Sem alertas, nenhuma notificação enviada");
            }

            return Result.success();

        } catch (TimeoutException e) {
            Log.e(TAG, "Timeout ao buscar localização/clima — tentará novamente", e);
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "Erro inesperado no worker", e);
            return Result.failure();
        }
    }

    private String formatarAlerta(Alerta alerta) {
        switch (alerta.tipo) {
            case CALOR:
                return "🔴 CALOR EXTREMO: " + alerta.valor + "°C — Evite exposição ao sol e hidrate-se.";
            case FRIO:
                return "🔵 FRIO EXTREMO: " + alerta.valor + "°C — Agasalhe-se e evite ficar ao relento.";
            case UMIDADE_ALTA:
                return "💧 UMIDADE ALTA: " + alerta.valor + "% — Risco de doenças respiratórias.";
            case UMIDADE_BAIXA:
                return "🏜️ UMIDADE BAIXA: " + alerta.valor + "% — Hidrate-se e umidifique o ambiente.";
            case VENTO:
                return "🌪️ VENTANIA EXTREMA: " + alerta.valor + " km/h — Evite áreas abertas.";
            case CHUVA:
                return "🌧️ CHUVA EXTREMA em " + alerta.data + ": " + alerta.valor + "mm — Risco de alagamentos.";
            case PROBABILIDADE_CHUVA:
                return "⛈️ PROBABILIDADE DE CHUVA em " + alerta.data + ": " + (int) alerta.valor + "%";
            default:
                return "";
        }
    }
}