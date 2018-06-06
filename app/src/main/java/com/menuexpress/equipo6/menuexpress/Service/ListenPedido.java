package com.menuexpress.equipo6.menuexpress.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.PedidoEstado;
import com.menuexpress.equipo6.menuexpress.R;

import java.util.Random;

public class ListenPedido extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference solicitudes;

    public ListenPedido() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        solicitudes = db.getReference("solicitud");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        solicitudes.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            Solicitar solicitud = dataSnapshot.getValue(Solicitar.class);
            if (solicitud.getEstado().equals("0")) {
                mostrarNotificacion(dataSnapshot.getKey(), solicitud);
            }
        }

    }

    private void mostrarNotificacion(String key, Solicitar solicitud) {
        Intent intent = new Intent(getBaseContext(), PedidoEstado.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Menu Express")
                .setContentInfo("Nuevo pedido")
                .setContentText("Tienes un nuevo pedido #" + key)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //Para mostras varias notificacions, deben tener diferente id
        int randomInt = new Random().nextInt(9999 - 1) + 1;
        notificationManager.notify(randomInt, builder.build());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
