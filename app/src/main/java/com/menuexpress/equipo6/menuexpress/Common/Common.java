package com.menuexpress.equipo6.menuexpress.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

import java.util.Calendar;
import java.util.Locale;

public class Common {

    public static Usuario currentUser;
    public static Solicitar currentResquest;

    public static final String EMAIL_KEY = "email";
    public static final String PASS_KEY = "contraseña";

    public static final String DELETE = "Borrar";
    public static final String UPDATE = "Actualizar";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String convertirCodigoEstado(String estado) {
        if (estado.equals("0"))
            return "colocado";
        else if (estado.equals("1"))
            return "en proceso";
        else
            return "completado";
    }

    //Checar si está conectado a internet
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String getFecha(long time) {
        Locale locale = new Locale("es", "MX");
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return date.toString();
    }
}
