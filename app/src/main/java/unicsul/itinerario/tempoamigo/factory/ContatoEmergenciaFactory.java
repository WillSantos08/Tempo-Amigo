package unicsul.itinerario.tempoamigo.factory;

import android.content.Context;
import java.util.concurrent.CompletableFuture;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;

public class ContatoEmergenciaFactory {

    private final Context context;

    public ContatoEmergenciaFactory(Context context) {
        this.context = context;
    }

    public CompletableFuture<ContatoEmergencia> buscar() {
        // TODO: substituir pelo Room quando implementado
        // AppDatabase db = AppDatabase.getInstance(context);
        // return CompletableFuture.supplyAsync(() -> {
        //     ContatoEmergenciaEntity entity = db.contatoEmergenciaDao().buscarPrimeiro();
        //     return new ContatoEmergencia(entity.numero, entity.nome, entity.mensagemInicial);
        // });

        ContatoEmergencia contatoFixo = new ContatoEmergencia(
                "5511981571589",
                "Vinícius",
                "Olá Vinícius, estou recebendo alertas climáticos na minha região e preciso de ajuda!"
        );

        return CompletableFuture.completedFuture(contatoFixo);
    }
}