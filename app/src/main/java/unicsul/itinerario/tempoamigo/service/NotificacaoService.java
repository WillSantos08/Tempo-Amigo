package unicsul.itinerario.tempoamigo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import java.util.List;

import unicsul.itinerario.tempoamigo.R;

public class NotificacaoService {

    private static final String CANAL_ID      = "alertas_climaticos";
    private static final String CANAL_NOME    = "Alertas Climáticos";
    private static final int    NOTIFICACAO_ID = 1;

    private static final String WHATSAPP_NUMERO   = "5511999999999"; // TODO: pegar dinamicamente
    private static final String WHATSAPP_MENSAGEM = "Olá, vi os alertas climáticos!"; // TODO: melhorar a adicionar localização

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificacaoService(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        criarCanal();
    }

    private void criarCanal() {
        NotificationChannel canal = new NotificationChannel(
                CANAL_ID,
                CANAL_NOME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(canal);
    }

    public void notificarAlertas(List<String> alertas) {
        String conteudo = String.join("\n", alertas);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠\uFE0F Alertas Climáticos")
                .setContentText(alertas.size() + " alerta(s) detectado(s)")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(conteudo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(criarPendingIntentWhatsApp(conteudo))
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICACAO_ID, builder.build());
    }

    private PendingIntent criarPendingIntentWhatsApp(String conteudo) {
        String url = "https://wa.me/" + WHATSAPP_NUMERO + "?text=" + Uri.encode(WHATSAPP_MENSAGEM + "\n\n" + conteudo);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}