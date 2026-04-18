package unicsul.itinerario.tempoamigo.ui.util;

import java.time.LocalTime;
import java.util.Random;

import unicsul.itinerario.tempoamigo.model.Clima;

public class ClimaVisualResolver {

    public enum CondicaoClima {
        TEMPESTADE("tempestade", "Tempestade", 2),
        NEVE("neve", "Nevando", 1),
        CHUVA("chuva", "Tempo Chuvoso", 1),
        VENTO("vento", "Ventania", 3),
        ENSOLARADO("ensolarado", "Tempo Bom", 3),
        NOITE("noite", "Tempo Bom", 2);

        public final String prefixo;
        public final String descricao;
        public final int totalVariacoes;

        CondicaoClima(String prefixo, String descricao, int totalVariacoes) {
            this.prefixo = prefixo;
            this.descricao = descricao;
            this.totalVariacoes = totalVariacoes;
        }
    }

    private static final LocalTime INICIO_DIA = LocalTime.of(6, 0);
    private static final LocalTime FIM_DIA = LocalTime.of(18, 0);

    private static final Random random = new Random();

    private ClimaVisualResolver() {
    }

    public static String resolverImagem(Clima clima) {
        CondicaoClima condicao = resolverCondicao(clima.getCodigoClima(), clima.getVelocidadeVento());
        int variacao = random.nextInt(condicao.totalVariacoes) + 1;
        return condicao.prefixo + "_" + variacao;
    }

    public static String resolverDescricao(Clima clima) {
        return resolverCondicao(clima.getCodigoClima(), clima.getVelocidadeVento()).descricao;
    }

    private static CondicaoClima resolverCondicao(int codigo, double velocidadeVento) {
        if (codigo >= 95) return CondicaoClima.TEMPESTADE;
        if (codigo >= 71 && codigo <= 77) return CondicaoClima.NEVE;
        if (codigo >= 51 && codigo <= 82) return CondicaoClima.CHUVA;
        if (velocidadeVento >= 30) return CondicaoClima.VENTO;
        return ehDia() ? CondicaoClima.ENSOLARADO : CondicaoClima.NOITE;
    }

    private static boolean ehDia() {
        LocalTime agora = LocalTime.now();
        return agora.isAfter(INICIO_DIA) && agora.isBefore(FIM_DIA);
    }
}