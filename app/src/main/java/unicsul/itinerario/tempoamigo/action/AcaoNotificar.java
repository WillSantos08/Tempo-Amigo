package unicsul.itinerario.tempoamigo.action;

import java.util.List;

import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;
import unicsul.itinerario.tempoamigo.model.Localizacao;
import unicsul.itinerario.tempoamigo.service.NotificacaoService;

public class AcaoNotificar implements AcaoAlerta {

    private final NotificacaoService notificacaoService;

    public AcaoNotificar(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @Override
    public void executar(List<Alerta> alertas, ContatoEmergencia contato, Localizacao localizacao) {
        if (contato == null) {
            notificacaoService.notificarAlertasSemContato(alertas);
        } else {
            notificacaoService.notificarAlertas(alertas, contato, localizacao);
        }
    }
}