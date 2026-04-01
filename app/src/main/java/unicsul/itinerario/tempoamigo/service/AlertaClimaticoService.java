package unicsul.itinerario.tempoamigo.service;


import java.util.ArrayList;
import java.util.List;
import unicsul.itinerario.tempoamigo.dto.ClimaDTO;

public class AlertaClimaticoService {

    private static final double TEMPERATURA_CALOR_EXTREMO = 35.0;
    private static final double TEMPERATURA_FRIO_EXTREMO = 10.0;
    private static final int UMIDADE_ALTA_EXTREMA = 95;
    private static final int UMIDADE_BAIXA_EXTREMA = 20;
    private static final double VENTO_EXTREMO = 60.0;
    private static final double CHUVA_EXTREMA = 50.0;
    private static final int PROBABILIDADE_CHUVA_EXTREMA = 90;

    private final ClimaDTO clima;

    public AlertaClimaticoService(ClimaDTO clima) {
        this.clima = clima;
    }

    public List<String> verificarAlertas() {
        List<String> alertas = new ArrayList<>();

        verificarTemperatura(alertas);
        verificarUmidade(alertas);
        verificarVento(alertas);
        verificarChuva(alertas);
        verificarProbabilidadeChuva(alertas);

        if (alertas.isEmpty()) {
            alertas.add("Nenhuma condição extrema detectada.");
        }

        return alertas;
    }

    private void verificarTemperatura(List<String> alertas) {
        double temp = clima.current.temperature2m;
        if (temp > TEMPERATURA_CALOR_EXTREMO) {
            alertas.add("🔴 CALOR EXTREMO: " + temp + "°C — Evite exposição ao sol e hidrate-se.");
        } else if (temp < TEMPERATURA_FRIO_EXTREMO) {
            alertas.add("🔵 FRIO EXTREMO: " + temp + "°C — Agasalhe-se e evite ficar ao relento.");
        }
    }

    private void verificarUmidade(List<String> alertas) {
        int umidade = clima.current.relativeHumidity2m;
        if (umidade > UMIDADE_ALTA_EXTREMA) {
            alertas.add("💧 UMIDADE EXTREMAMENTE ALTA: " + umidade + "% — Risco de doenças respiratórias.");
        } else if (umidade < UMIDADE_BAIXA_EXTREMA) {
            alertas.add("🏜️ UMIDADE EXTREMAMENTE BAIXA: " + umidade + "% — Hidrate-se e umidifique o ambiente.");
        }
    }

    private void verificarVento(List<String> alertas) {
        double vento = clima.current.windSpeed10m;
        if (vento > VENTO_EXTREMO) {
            alertas.add("🌪️ VENTANIA EXTREMA: " + vento + " km/h — Evite áreas abertas e fique abrigado.");
        }
    }

    private void verificarChuva(List<String> alertas) {
        for (int i = 0; i < clima.daily.time.size(); i++) {
            double chuva = clima.daily.precipitationSum.get(i);
            if (chuva > CHUVA_EXTREMA) {
                alertas.add("🌧️ CHUVA EXTREMA em " + clima.daily.time.get(i) + ": " + chuva + "mm — Risco de alagamentos.");
            }
        }
    }

    private void verificarProbabilidadeChuva(List<String> alertas) {
        for (int i = 0; i < clima.hourly.time.size(); i++) {
            int prob = clima.hourly.precipitationProbability.get(i);
            if (prob > PROBABILIDADE_CHUVA_EXTREMA) {
                alertas.add("⛈️ PROBABILIDADE EXTREMA DE CHUVA em " + clima.hourly.time.get(i) + ": " + prob + "%");
            }
        }
    }
}