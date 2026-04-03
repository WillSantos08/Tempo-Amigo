package unicsul.itinerario.tempoamigo.model;

import java.util.List;
import java.util.stream.Collectors;

public class MensagemEmergencia {

    private final ContatoEmergencia contato;

    public MensagemEmergencia(ContatoEmergencia contato) {
        this.contato = contato;
    }

    public String getNumero() {
        return contato.numero;
    }

    public String formatar(List<Alerta> alertas) {
        String listaAlertas = alertas.stream()
                .map(Alerta::formatarParaNotificacao)
                .collect(Collectors.joining("\n"));

        return contato.mensagemInicial + "\n\n" + listaAlertas;
    }
}