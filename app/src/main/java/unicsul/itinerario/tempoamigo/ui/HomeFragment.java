package unicsul.itinerario.tempoamigo.ui;

import static unicsul.itinerario.tempoamigo.network.HttpClient.mainThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.stream.Collectors;

import unicsul.itinerario.tempoamigo.R;
import unicsul.itinerario.tempoamigo.factory.ClimaRepositoryFactory;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class HomeFragment extends Fragment {

    private ClimaRepository climaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        climaRepository = ClimaRepositoryFactory.criar(requireContext());

        atualizarClima(view);

        //TODO: Remover botão de teste
        view.findViewById(R.id.buttonTestar).setOnClickListener(v -> {
            OneTimeWorkRequest teste = new OneTimeWorkRequest.Builder(ClimaWorker.class).build();
            WorkManager.getInstance(requireContext()).enqueue(teste);
            Log.d("ClimaFragment", "Worker de teste enfileirado");
        });
    }

    private void atualizarClima(View view) {
        TextView textViewTemp = view.findViewById(R.id.textViewTemp);
        TextView textViewUmidade = view.findViewById(R.id.textViewUmidade);
        TextView textViewVento = view.findViewById(R.id.textViewVento);
        TextView textViewChuva = view.findViewById(R.id.textViewChuva);
        TextView textViewAlertas = view.findViewById(R.id.textViewAlertas);

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