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

public class ListenPedidoAdmin extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference solicitudes;

    public ListenPedidoAdmin() {
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

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Solicitar solicitud = dataSnapshot.getValue(Solicitar.class);
        showNotificacion(dataSnapshot.getKey(), solicitud);
    }

    private void showNotificacion(String key, Solicitar solicitud) {
        Intent intent = new Intent(getBaseContext(), PedidoEstado.class);
        intent.putExtra("email", solicitud.getEmail());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("MemuExpress")
                .setContentInfo("Tu pedido fue actualizado")
                .setContentText("Pedido #" + key + " fue actualizado a " + Common.convertirCodigoEstado(solicitud.getEstado()))
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
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
