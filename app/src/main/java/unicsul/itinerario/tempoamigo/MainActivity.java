package unicsul.itinerario.tempoamigo;

import static unicsul.itinerario.tempoamigo.network.HttpClient.mainThread;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.location.PermissaoHelper;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class MainActivity extends AppCompatActivity {

    private static final String WORKER_TAG = "clima_worker";

    private ClimaRepository climaRepository;
    private PermissaoHelper permissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        climaRepository = new ClimaRepository(
                new LocalizacaoClient(getApplicationContext()),
                ClimaApiClient.criar()
        );

        permissao = new PermissaoHelper(this);
        permissao.solicitar(() -> {
            atualizarClima();
            agendarWorker();
        });
    }

    private void agendarWorker() {
        PeriodicWorkRequest trabalho = new PeriodicWorkRequest.Builder(
                ClimaWorker.class,
                15, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                trabalho
        );
    }

    private void atualizarClima() {

        TextView textViewTemp    = findViewById(R.id.textViewTemp);
        TextView textViewUmidade = findViewById(R.id.textViewUmidade);
        TextView textViewVento   = findViewById(R.id.textViewVento);
        TextView textViewChuva   = findViewById(R.id.textViewChuva);
        TextView textViewAlertas = findViewById(R.id.textViewAlertas);

        climaRepository.buscarClimaPorLocalizacao()
                .thenAcceptAsync(clima -> {
                    textViewTemp.setText(clima.current.temperature2m + "°C");
                    textViewUmidade.setText("Umidade: " + clima.current.relativeHumidity2m + "%");
                    textViewVento.setText("Vento: " + clima.current.windSpeed10m + " km/h");
                    textViewChuva.setText("Chuva: " + clima.current.precipitation + " mm");

                    List<String> alertas = new AlertaClimaticoService(clima).verificarAlertas();
                    textViewAlertas.setText(String.join("\n", alertas));
                }, mainThread::post)
                .exceptionally(erro -> {
                    Log.e("CLIMA", erro.getMessage());
                    return null;
                });

        findViewById(R.id.buttonTestar).setOnClickListener(v -> {
            OneTimeWorkRequest teste = new OneTimeWorkRequest.Builder(ClimaWorker.class).build();
            WorkManager.getInstance(this).enqueue(teste);
        });
    }
}