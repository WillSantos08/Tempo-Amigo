package unicsul.itinerario.tempoamigo.action;

import android.content.Context;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;
import java.util.HashMap;
import java.util.Map;

public class AcaoAlertaRegistry {

    public static final String NOTIFICAR = "notificar";
    public static final String ABRIR_WHATSAPP = "abrirWhatsApp";

    private final Map<String, AcaoAlerta> acoes = new HashMap<>();

    public AcaoAlertaRegistry(Context context) {
        NotificacaoService notificacaoService = new NotificacaoService(context);
        acoes.put(NOTIFICAR, new AcaoNotificar(notificacaoService));
        acoes.put(ABRIR_WHATSAPP, new AcaoAbrirWhatsApp(context, notificacaoService));
    }

    public AcaoAlerta resolver(String chave) {
        AcaoAlerta acao = acoes.get(chave);
        if (acao == null) {
            throw new IllegalArgumentException("Ação não registrada: " + chave);
        }
        return acao;
    }
}