package com.example.inventorymanagementsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "low_stock_channel";

    public static void showLowStockNotification(Context context, String itemName) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, "Low Stock Alerts",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Low Stock Alert")
                    .setContentText("Item " + itemName + " is running low!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            manager.notify(1, builder.build());
        }
    }
}
