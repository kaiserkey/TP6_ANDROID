package com.example.tp6_android;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private Thread backgroundThread;
    private boolean isServiceRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServiceRunning) {
            isServiceRunning = true;

            backgroundThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isServiceRunning) {
                        mostrarSMS();
                        try {
                            Thread.sleep(9000); // Espera 9 segundos
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            backgroundThread.start();
        }

        return START_STICKY;
    }

    private void mostrarSMS() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = {"_id", "address", "body", "date"};
        Cursor cursor = contentResolver.query(uri, projection, null, null, "date DESC LIMIT 5");

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("MyService", "----------------------------------------------------------");
            do {
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));
                @SuppressLint("Range") long date = cursor.getLong(cursor.getColumnIndex("date"));

                Log.d("DatosSMS", "Address: " + address + ", Body: " + body + ", Date: " + date);
            } while (cursor.moveToNext());
            Log.d("MyService", "----------------------------------------------------------");
            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        if (backgroundThread != null) {
            backgroundThread.interrupt();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}