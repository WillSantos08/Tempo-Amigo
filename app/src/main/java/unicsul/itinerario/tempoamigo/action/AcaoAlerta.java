package unicsul.itinerario.tempoamigo.action;

import java.util.List;

import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;
import unicsul.itinerario.tempoamigo.model.Localizacao;

public interface AcaoAlerta {
    void executar(List<Alerta> alertas, ContatoEmergencia contato, Localizacao localizacao);
}