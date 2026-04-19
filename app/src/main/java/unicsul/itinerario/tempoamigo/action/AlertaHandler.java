package unicsul.itinerario.tempoamigo.action;

import java.util.List;

import unicsul.itinerario.tempoamigo.factory.ContatoEmergenciaFactory;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.Localizacao;

public class AlertaHandler {

    private final ContatoEmergenciaFactory contatoEmergenciaFactory;
    private final List<Alerta> alertas;
    private final Localizacao localizacao;
    private final AcaoAlerta acao;

    public AlertaHandler(ContatoEmergenciaFactory contatoEmergenciaFactory,
                         List<Alerta> alertas,
                         Localizacao localizacao,
                         AcaoAlerta acao) {
        this.contatoEmergenciaFactory = contatoEmergenciaFactory;
        this.alertas = alertas;
        this.localizacao = localizacao;
        this.acao = acao;
    }

    public void executar() {
        if (alertas.isEmpty()) return;

        contatoEmergenciaFactory
                .buscar()
                .thenAccept(contato -> acao.executar(alertas, contato, localizacao));
    }
}