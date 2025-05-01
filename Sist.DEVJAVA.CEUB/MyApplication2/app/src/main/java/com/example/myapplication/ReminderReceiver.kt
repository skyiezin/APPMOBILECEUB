package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Receiver responsável por disparar notificações agendadas
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Cria a notificação com canal 'monthly_reminder' (inicialmente seriam mensais)
        val builder = NotificationCompat.Builder(context, "monthly_reminder")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícone da notificação
            .setContentTitle("Lembrete financeiro")        // Título da notificação
            .setContentText("Já cadastrou seus dados financeiros do mês?") // Texto da notificação
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridade padrão
        // Exibe a notificação com ID fixo 1001
        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }
    }
}
