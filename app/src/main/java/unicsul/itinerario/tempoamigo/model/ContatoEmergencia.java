package unicsul.itinerario.tempoamigo.model;

public class ContatoEmergencia {

    public final String numero;
    public final String nome;
    public final String mensagemInicial;

    public ContatoEmergencia(String numero, String nome, String mensagemInicial) {
        this.numero = numero;
        this.nome = nome;
        this.mensagemInicial = mensagemInicial;
    }
}