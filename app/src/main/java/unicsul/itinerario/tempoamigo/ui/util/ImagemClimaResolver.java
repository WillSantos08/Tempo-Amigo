package unicsul.itinerario.tempoamigo.ui.util;

import java.time.LocalTime;
import java.util.Random;

import unicsul.itinerario.tempoamigo.model.Clima;

public class ImagemClimaResolver {

    private static final LocalTime INICIO_DIA = LocalTime.of(6, 0);
    private static final LocalTime FIM_DIA = LocalTime.of(18, 0);

    private static final Random random = new Random();

    private ImagemClimaResolver() {
    }

    public static String resolver(Clima clima) {
        boolean ehDia = ehDia();
        String condicao = resolverCondicao(clima.getCodigoClima(), clima.getVelocidadeVento(), ehDia);
        return condicao + "_" + variacaoAleatoria(condicao);
    }

    private static String resolverCondicao(int codigo, double velocidadeVento, boolean ehDia) {
        if (codigo >= 95) return "tempestade";
        if (codigo >= 71 && codigo <= 77) return "neve";
        if (codigo >= 51 && codigo <= 82) return "chuva";
        if (velocidadeVento >= 30) return "vento";
        return ehDia ? "ensolarado" : "noite";
    }

    private static int variacaoAleatoria(String condicao) {
        int total = totalVariacoes(condicao);
        return random.nextInt(total) + 1;
    }

    private static int totalVariacoes(String condicao) {
        switch (condicao) {
            case "ensolarado":
                return 3;
            case "noite":
                return 2;
            case "vento":
                return 3;
            case "tempestade":
                return 2;
            case "chuva":
                return 1;
            case "neve":
                return 1;
            default:
                return 1;
        }
    }

    private static boolean ehDia() {
        LocalTime agora = LocalTime.now();
        return agora.isAfter(INICIO_DIA) && agora.isBefore(FIM_DIA);
    }
}