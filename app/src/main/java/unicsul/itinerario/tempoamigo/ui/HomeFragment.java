package unicsul.itinerario.tempoamigo.ui;

import static unicsul.itinerario.tempoamigo.network.HttpClient.mainThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import unicsul.itinerario.tempoamigo.R;
import unicsul.itinerario.tempoamigo.action.AcaoAlertaRegistry;
import unicsul.itinerario.tempoamigo.factory.ClimaRepositoryFactory;
import unicsul.itinerario.tempoamigo.factory.ContatoEmergenciaFactory;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;
import unicsul.itinerario.tempoamigo.service.AlertaClimaticoService;
import unicsul.itinerario.tempoamigo.ui.util.ClimaVisualResolver;
import unicsul.itinerario.tempoamigo.worker.ClimaWorker;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

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
        configurarBotaoWhatsApp(view);
    }

    private void atualizarClima(View view) {
        ImageView imageViewClima = view.findViewById(R.id.imageViewClima);
        TextView textViewDescricaoClima = view.findViewById(R.id.textViewDescricaoClima);
        TextView textViewTemp = view.findViewById(R.id.textViewTemp);
        TextView textViewUmidade = view.findViewById(R.id.textViewUmidade);
        TextView textViewVento = view.findViewById(R.id.textViewVento);
        TextView textViewChuva = view.findViewById(R.id.textViewChuva);
        TextView textViewAlertas = view.findViewById(R.id.textViewAlertas);
        TextView labelAlerta = view.findViewById(R.id.textViewAlertaLabel);
        android.widget.ImageView imageViewAlertaIcon = view.findViewById(R.id.imageViewAlertaIcon);
        com.google.android.material.card.MaterialCardView cardAlertas = view.findViewById(R.id.cardAlertas);
        Button buttonWhatsApp = view.findViewById(R.id.buttonWhatsApp);

        climaRepository.buscarClimaPorLocalizacao()
                .thenAcceptAsync(clima -> {
                    if (!isAdded()) return;

                    String nomeDrawable = ClimaVisualResolver.resolverImagem(clima);
                    int resId = getResources().getIdentifier(nomeDrawable, "drawable", requireContext().getPackageName());
                    imageViewClima.setImageResource(resId);

                    textViewDescricaoClima.setText(ClimaVisualResolver.resolverDescricao(clima));

                    textViewTemp.setText(clima.getTemperatura() + "°C");
                    textViewUmidade.setText("Umid: " + clima.getUmidade() + "%");
                    textViewVento.setText("Vento: " + clima.getVelocidadeVento() + " km/h");
                    textViewChuva.setText("Chuva: " + clima.getPrecipitacaoAtual() + " mm");

                    List<Alerta> alertas = new AlertaClimaticoService(clima).verificarAlertas();

                    if (alertas.isEmpty()) {
                        cardAlertas.setVisibility(View.GONE);
                        buttonWhatsApp.setVisibility(View.GONE);
                        return;
                    }

                    String textoAlertas = alertas.stream()
                            .map(Alerta::formatarParaTela)
                            .collect(Collectors.joining("\n\n"));
                    textViewAlertas.setText(textoAlertas);

                    int cor = resolverCorSeveridade(alertas);
                    int corFundo = androidx.core.graphics.ColorUtils.setAlphaComponent(cor, 30);
                    cardAlertas.setStrokeColor(cor);
                    cardAlertas.setCardBackgroundColor(corFundo);
                    labelAlerta.setTextColor(cor);
                    imageViewAlertaIcon.setColorFilter(cor);

                    cardAlertas.setVisibility(View.VISIBLE);
                    buttonWhatsApp.setVisibility(View.VISIBLE);


                }, mainThread::post)
                .exceptionally(erro -> {
                    Log.e(TAG, erro.getMessage());
                    return null;
                });
    }

    private void configurarBotaoWhatsApp(View view) {
        view.findViewById(R.id.buttonWhatsApp).setOnClickListener(v -> {
            new ContatoEmergenciaFactory(requireContext())
                    .buscar()
                    .thenAcceptAsync(contato -> {
                        if (!isAdded()) return;

                        if (contato == null) {
                            Navigation.findNavController(view).navigate(R.id.action_home_to_edicao);
                        } else {
                            dispararWorker(AcaoAlertaRegistry.ABRIR_WHATSAPP);
                        }
                    }, requireActivity().getMainExecutor());
        });
    }

    private int resolverCorSeveridade(List<Alerta> alertas) {
        return alertas.stream()
                .max(Comparator.comparingInt(a -> a.severidade.ordinal()))
                .map(a -> requireContext().getColor(a.resolverCor()))
                .orElse(requireContext().getColor(R.color.notify_green));
    }

    private void dispararWorker(String acao) {
        if (!isAdded()) return;

        Data inputData = new Data.Builder()
                .putString(ClimaWorker.INPUT_ACAO, acao)
                .build();

        OneTimeWorkRequest trabalho = new OneTimeWorkRequest.Builder(ClimaWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(trabalho);
        Log.d(TAG, "Worker enfileirado com ação: " + acao);
    }
}