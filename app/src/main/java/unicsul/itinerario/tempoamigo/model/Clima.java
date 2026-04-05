package unicsul.itinerario.tempoamigo.model;

import java.util.List;

public class Clima {

    private final double temperatura;
    private final int umidade;
    private final double velocidadeVento;
    private final double precipitacaoAtual;
    private final int codigoClima;
    private final List<ClimaHorario> previsaoHoraria;
    private final List<ClimaDiario> previsaoDiaria;

    public Clima(
            double temperatura,
            int umidade,
            double velocidadeVento,
            double precipitacaoAtual,
            int codigoClima,
            List<ClimaHorario> previsaoHoraria,
            List<ClimaDiario> previsaoDiaria
    ) {
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.velocidadeVento = velocidadeVento;
        this.precipitacaoAtual = precipitacaoAtual;
        this.codigoClima = codigoClima;
        this.previsaoHoraria = previsaoHoraria;
        this.previsaoDiaria = previsaoDiaria;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public int getUmidade() {
        return umidade;
    }

    public double getVelocidadeVento() {
        return velocidadeVento;
    }

    public double getPrecipitacaoAtual() {
        return precipitacaoAtual;
    }

    public int getCodigoClima() {
        return codigoClima;
    }

    public List<ClimaHorario> getPrevisaoHoraria() {
        return previsaoHoraria;
    }

    public List<ClimaDiario> getPrevisaoDiaria() {
        return previsaoDiaria;
    }
}