package unicsul.itinerario.tempoamigo.action;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;
import unicsul.itinerario.tempoamigo.model.Localizacao;
import unicsul.itinerario.tempoamigo.model.MensagemEmergencia;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;

public class AcaoAbrirWhatsApp implements AcaoAlerta {

    private final Context context;
    private final NotificacaoService notificacaoService;

    public AcaoAbrirWhatsApp(Context context, NotificacaoService notificacaoService) {
        this.context = context;
        this.notificacaoService = notificacaoService;
    }

    @Override
    public void executar(List<Alerta> alertas, ContatoEmergencia contato, Localizacao localizacao) {
        if (contato == null) return;

        Intent intent = notificacaoService.criarIntentWhatsApp(
                new MensagemEmergencia(contato, localizacao), alertas);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}