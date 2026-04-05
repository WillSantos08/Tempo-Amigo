package unicsul.itinerario.tempoamigo.network.clima;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import unicsul.itinerario.tempoamigo.model.Clima;
import unicsul.itinerario.tempoamigo.model.ClimaDiario;
import unicsul.itinerario.tempoamigo.model.ClimaHorario;

public class ClimaMapper {

    private static final DateTimeFormatter FORMATO_HORARIO =
            DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ClimaMapper() {
    }

    public static Clima fromOpenMeteo(OpenMeteoForecast dto) {
        return new Clima(
                dto.current.temperature2m,
                dto.current.relativeHumidity2m,
                dto.current.windSpeed10m,
                dto.current.precipitation,
                dto.current.weatherCode,
                mapearHorarios(dto.hourly),
                mapearDiarios(dto.daily)
        );
    }

    private static List<ClimaHorario> mapearHorarios(OpenMeteoForecast.Hourly hourly) {
        List<ClimaHorario> resultado = new ArrayList<>();
        for (int i = 0; i < hourly.time.size(); i++) {
            String horario = LocalDateTime.parse(hourly.time.get(i))
                    .format(FORMATO_HORARIO);
            resultado.add(new ClimaHorario(
                    horario,
                    hourly.temperature2m.get(i),
                    hourly.precipitationProbability.get(i)
            ));
        }
        return resultado;
    }

    private static List<ClimaDiario> mapearDiarios(OpenMeteoForecast.Daily daily) {
        List<ClimaDiario> resultado = new ArrayList<>();
        for (int i = 0; i < daily.time.size(); i++) {
            String data = LocalDate.parse(daily.time.get(i))
                    .format(FORMATO_DATA);
            resultado.add(new ClimaDiario(
                    data,
                    daily.temperature2mMax.get(i),
                    daily.temperature2mMin.get(i),
                    daily.precipitationSum.get(i)
            ));
        }
        return resultado;
    }
}