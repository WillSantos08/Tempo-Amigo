package unicsul.itinerario.tempoamigo.network;

import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RetryUtil {

    private static final String TAG = "RetryUtil";
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public static <T> CompletableFuture<T> executar(
            Supplier<CompletableFuture<T>> supplier,
            int maxTentativas,
            long delayInicial
    ) {
        return iniciarTentativa(supplier, maxTentativas, delayInicial, 1);
    }

    private static <T> CompletableFuture<T> iniciarTentativa(
            Supplier<CompletableFuture<T>> supplier,
            int tentativasRestantes,
            long delayInicial,
            int tentativaAtual
    ) {
        return supplier.get().exceptionally(erro -> {
            throw new RuntimeException(erro);
        }).handle((resultado, erro) -> {
            if (erro == null) {
                return CompletableFuture.completedFuture(resultado);
            }

            if (tentativasRestantes <= 1) {
                Log.e(TAG, "Todas as tentativas esgotadas.");
                CompletableFuture<T> falha = new CompletableFuture<>();
                falha.completeExceptionally(erro);
                return falha;
            }

            long delay = delayInicial * (1L << (tentativaAtual - 1));
            Log.w(TAG, String.format("Tentativa %d falhou. Retrying em %dms...",
                    tentativaAtual, delay));

            CompletableFuture<T> proxima = new CompletableFuture<>();
            scheduler.schedule(
                    () -> iniciarTentativa(supplier, tentativasRestantes - 1, delayInicial, tentativaAtual + 1)
                            .whenComplete((r, e) -> {
                                if (e != null) proxima.completeExceptionally(e);
                                else proxima.complete(r);
                            }),
                    delay,
                    TimeUnit.MILLISECONDS
            );
            return proxima;

        }).thenCompose(f -> f);
    }
}