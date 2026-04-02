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

import unicsul.itinerario.tempoamigo.network.ClimaApiClient;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;

public class MainActivity extends AppCompatActivity {

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

        TextView textViewTemp = findViewById(R.id.textViewTemp);
        TextView textViewUmidade = findViewById(R.id.textViewUmidade);
        TextView textViewVento = findViewById(R.id.textViewVento);
        TextView textViewChuva = findViewById(R.id.textViewChuva);
        TextView textViewAlertas = findViewById(R.id.textViewAlertas);

        ClimaApiClient api = ClimaApiClient.criar();

        api.buscarClima(
                        -23.65572127630479,
                        -46.71783742784523
                ).thenAcceptAsync(clima -> {
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