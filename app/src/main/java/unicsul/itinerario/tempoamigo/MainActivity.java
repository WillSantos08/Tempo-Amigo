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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import unicsul.itinerario.tempoamigo.factory.ClimaRepositoryFactory;
import unicsul.itinerario.tempoamigo.location.PermissaoHelper;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class MainActivity extends AppCompatActivity {

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

        climaRepository = ClimaRepositoryFactory.criar(getApplicationContext());

        permissao = new PermissaoHelper(this);
        permissao.solicitar(() -> {
            atualizarClima();
            agendarWorker();
        });

        //TODO: Remover botão de teste
        findViewById(R.id.buttonTestar).setOnClickListener(v -> {
            OneTimeWorkRequest teste = new OneTimeWorkRequest.Builder(ClimaWorker.class).build();
            WorkManager.getInstance(this).enqueue(teste);
            Log.d("MainActivity", "Worker de teste enfileirado");
        });
    }

    private void agendarWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest trabalho = new PeriodicWorkRequest.Builder(
                ClimaWorker.class,
                15,
                TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                ClimaWorker.TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                trabalho
        );
    }

    private void atualizarClima() {
        TextView textViewTemp = findViewById(R.id.textViewTemp);
        TextView textViewUmidade = findViewById(R.id.textViewUmidade);
        TextView textViewVento = findViewById(R.id.textViewVento);
        TextView textViewChuva = findViewById(R.id.textViewChuva);
        TextView textViewAlertas = findViewById(R.id.textViewAlertas);

        climaRepository.buscarClimaPorLocalizacao()
                .thenAcceptAsync(clima -> {
                    textViewTemp.setText(clima.getTemperatura() + "°C");
                    textViewUmidade.setText("Umidade: " + clima.getUmidade() + "%");
                    textViewVento.setText("Vento: " + clima.getVelocidadeVento() + " km/h");
                    textViewChuva.setText("Chuva: " + clima.getPrecipitacaoAtual() + " mm");

                    List<Alerta> alertas = new AlertaClimaticoService(clima).verificarAlertas();

                    String textoAlertas = alertas.isEmpty()
                            ? "Nenhuma condição extrema detectada."
                            : alertas.stream()
                            .map(Alerta::formatarParaTela)
                            .collect(Collectors.joining("\n\n"));

                    textViewAlertas.setText(textoAlertas);
                }, mainThread::post)
                .exceptionally(erro -> {
                    Log.e("CLIMA", erro.getMessage());
                    return null;
                });
    }
}