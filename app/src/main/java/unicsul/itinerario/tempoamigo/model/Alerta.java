package unicsul.itinerario.tempoamigo.model;

public class Alerta {

    public enum Tipo {
        CALOR, FRIO, UMIDADE_ALTA, UMIDADE_BAIXA, VENTO, CHUVA, PROBABILIDADE_CHUVA, TEMPESTADE, NEVE
    }

    public enum Severidade {
        ATENCAO, PERIGO, CRITICO
    }

    public final Tipo tipo;
    public final Severidade severidade;
    public final double valor;
    public final String data;

    public Alerta(Tipo tipo, Severidade severidade, double valor, String data) {
        this.tipo = tipo;
        this.severidade = severidade;
        this.valor = valor;
        this.data = data;
    }

    public Alerta(Tipo tipo, Severidade severidade, double valor) {
        this(tipo, severidade, valor, null);
    }

    public String formatarParaTela() {
        switch (tipo) {
            case CALOR:
                return "🔴 CALOR EXTREMO: " + valor + "°C — Evite exposição ao sol e hidrate-se.";
            case FRIO:
                return "🔵 FRIO EXTREMO: " + valor + "°C — Agasalhe-se e evite ficar ao relento.";
            case UMIDADE_ALTA:
                return "💧 UMIDADE ALTA: " + valor + "% — Risco de doenças respiratórias.";
            case UMIDADE_BAIXA:
                return "🏜️ UMIDADE BAIXA: " + valor + "% — Hidrate-se e umidifique o ambiente.";
            case VENTO:
                return "🌪️ VENTANIA EXTREMA: " + valor + " km/h — Evite áreas abertas e fique abrigado.";
            case CHUVA:
                return "🌧️ CHUVA EXTREMA em " + data + ": " + valor + "mm — Risco de alagamentos.";
            case PROBABILIDADE_CHUVA:
                return "⛈️ PROBABILIDADE DE CHUVA em " + data + ": " + (int) valor + "%";
            case TEMPESTADE:
                return severidade == Severidade.CRITICO
                        ? "⛈️ TEMPESTADE SEVERA — Risco de granizo. Procure abrigo imediatamente."
                        : "🌩️ TEMPESTADE — Raios na região. Evite áreas abertas.";
            case NEVE:
                return severidade == Severidade.PERIGO
                        ? "❄️ NEVASCA INTENSA — Evite sair. Risco de bloqueio de vias."
                        : "🌨️ NEVE LEVE — Atenção ao dirigir. Pistas podem estar escorregadias.";
            default:
                return "";
        }
    }

    public String formatarParaNotificacao() {
        switch (tipo) {
            case CALOR:
                return "Calor extremo: " + valor + "°C";
            case FRIO:
                return "Frio extremo: " + valor + "°C";
            case UMIDADE_ALTA:
                return "Umidade muito alta: " + valor + "%";
            case UMIDADE_BAIXA:
                return "Umidade muito baixa: " + valor + "%";
            case VENTO:
                return "Ventania forte: " + valor + " km/h";
            case CHUVA:
                return "Chuva intensa em " + data + ": " + valor + "mm";
            case PROBABILIDADE_CHUVA:
                return "Chuva provável em " + data + ": " + (int) valor + "%";
            case TEMPESTADE:
                return severidade == Severidade.CRITICO
                        ? "Tempestade severa com granizo na região"
                        : "Tempestade com raios na região";
            case NEVE:
                return severidade == Severidade.PERIGO
                        ? "Nevasca intensa"
                        : "Neve leve";
            default:
                return "";
        }
    }
}