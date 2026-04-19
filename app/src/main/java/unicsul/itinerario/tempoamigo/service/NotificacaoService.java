package unicsul.itinerario.tempoamigo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.stream.Collectors;

import unicsul.itinerario.tempoamigo.R;
import unicsul.itinerario.tempoamigo.model.Alerta;
import unicsul.itinerario.tempoamigo.model.ContatoEmergencia;
import unicsul.itinerario.tempoamigo.model.Localizacao;
import unicsul.itinerario.tempoamigo.model.MensagemEmergencia;

public class NotificacaoService {

    private static final String CANAL_ID = "alertas_climaticos";
    private static final String CANAL_NOME = "Alertas Climáticos";
    public static final int NOTIFICACAO_ID = 1;

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

    public void notificarAlertasSemContato(List<Alerta> alertas) {
        String conteudo = alertas.stream()
                .map(Alerta::formatarParaNotificacao)
                .collect(Collectors.joining("\n"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠️ Alertas Climáticos")
                .setContentText(alertas.size() + " alerta(s) detectado(s)")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(conteudo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICACAO_ID, builder.build());
    }

    public void notificarAlertas(List<Alerta> alertas, ContatoEmergencia contato, Localizacao localizacao) {
        MensagemEmergencia mensagem = new MensagemEmergencia(contato, localizacao);

        String conteudo = alertas.stream()
                .map(Alerta::formatarParaNotificacao)
                .collect(Collectors.joining("\n"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠️ Alertas Climáticos")
                .setContentText(alertas.size() + " alerta(s) detectado(s)")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(conteudo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(criarPendingIntentWhatsApp(mensagem, alertas))
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICACAO_ID, builder.build());
    }

    public Intent criarIntentWhatsApp(MensagemEmergencia mensagem, List<Alerta> alertas) {
        String url = "https://wa.me/" + mensagem.getNumero()
                + "?text=" + Uri.encode(mensagem.formatar(alertas));

        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    private PendingIntent criarPendingIntentWhatsApp(MensagemEmergencia mensagem, List<Alerta> alertas) {
        return PendingIntent.getActivity(
                context,
                0,
                criarIntentWhatsApp(mensagem, alertas),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}