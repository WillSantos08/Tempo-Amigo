package unicsul.itinerario.tempoamigo.factory;

import android.content.Context;

import unicsul.itinerario.tempoamigo.location.LocalizacaoClient;
import unicsul.itinerario.tempoamigo.network.clima.OpenMeteoApiClient;
import unicsul.itinerario.tempoamigo.repository.ClimaRepository;

public class ClimaRepositoryFactory {

    public static ClimaRepository criar(Context context) {
        return new ClimaRepository(
                new LocalizacaoClient(context),
                OpenMeteoApiClient.criar()
        );
    }
}
