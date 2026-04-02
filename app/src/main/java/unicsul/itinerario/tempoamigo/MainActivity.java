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

import java.util.List;

import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.location.PermissaoHelper;
import unicsul.itinerario.tempoamigo.network.clima.ClimaApiClient;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;

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

        climaRepository = new ClimaRepository(
                new LocalizacaoClient(getApplicationContext()),
                ClimaApiClient.criar()
        );

        permissao = new PermissaoHelper(this);
        permissao.solicitar(this::atualizarClima);
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
                    textViewUmidade.setText(clima.current.relativeHumidity2m + "%");
                    textViewVento.setText(clima.current.windSpeed10m + " km/h");
                    textViewChuva.setText(clima.current.precipitation + " mm");

                    List<String> alertas = new AlertaClimaticoService(clima).verificarAlertas();
                    textViewAlertas.setText(String.join("\n", alertas));
                }, mainThread::post)
                .exceptionally(erro -> {
                    Log.e("CLIMA", erro.getMessage());
                    return null;
                });
    }
}