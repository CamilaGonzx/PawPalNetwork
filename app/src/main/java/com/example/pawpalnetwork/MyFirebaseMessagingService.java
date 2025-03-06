package com.example.pawpalnetwork;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Obtener el título y el cuerpo del mensaje
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Nueva notificación";
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "Tienes una notificación";

        // Mostrar la notificación
        showNotification(title, message);
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "default_channel_id";

        // Crear el canal de notificación (necesario para Android Oreo y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Canal de Notificaciones", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la intención para abrir la aplicación cuando se toque la notificación
        Intent intent = new Intent(this, MainActivity.class); // Cambia MainActivity a la actividad que quieras abrir
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Usa tu propio ícono
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Mostrar la notificación
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Envía el nuevo token a tu servidor para identificar el dispositivo
    }
}
