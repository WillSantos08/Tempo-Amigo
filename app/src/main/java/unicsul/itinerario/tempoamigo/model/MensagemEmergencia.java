package unicsul.itinerario.tempoamigo.model;

import java.util.List;
import java.util.stream.Collectors;

public class MensagemEmergencia {

    private final ContatoEmergencia contato;
    private final Localizacao localizacao;

    public MensagemEmergencia(ContatoEmergencia contato, Localizacao localizacao) {
        this.contato = contato;
        this.localizacao = localizacao;
    }

    public String getNumero() {
        return contato.numero;
    }

    private String linkMaps() {
        return "https://maps.google.com/?q=" + localizacao.latitude + "," + localizacao.longitude;
    }

    public String formatar(List<Alerta> alertas) {
        String listaAlertas = alertas.stream()
                .map(Alerta::formatarParaNotificacao)
                .collect(Collectors.joining("\n"));

        return "Olá " +  contato.nome + "!\n\n" + contato.mensagemInicial + "\n\n" + listaAlertas + "\n\n📍 Minha localização: " + linkMaps();
    }
}