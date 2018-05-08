package com.menuexpress.equipo6.menuexpress.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "pedidoDB.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public boolean checarExisteComida(String idComidam, String email) {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM detallePedido WHERE email = '%s' AND idComida = '%s'", email, idComidam);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount() > 0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    public List<Pedido> getCarrito(String email) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelet = {"email", "idComida", "nombreComida", "cantidad", "precio", "descuento", "imagen"};
        String sqlTable = "detallePedido";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelet, "email=?", new String[]{email}, null, null, null);

        final List<Pedido> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Pedido(
                        c.getString(c.getColumnIndex("email")),
                        c.getString(c.getColumnIndex("idComida")),
                        c.getString(c.getColumnIndex("nombreComida")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("precio")),
                        c.getString(c.getColumnIndex("descuento")),
                        c.getString(c.getColumnIndex("imagen"))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void agregarACarrito(Pedido pedido) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO detallePedido(email, idComida, nombreComida, cantidad, precio, descuento, imagen) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                pedido.getEmail(),
                pedido.getIdComida(),
                pedido.getNombreComida(),
                pedido.getCantidad(),
                pedido.getPrecio(),
                pedido.getDescuento(),
                pedido.getImagen());
        db.execSQL(query);
    }

    public void limpiarCarrito(String email) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM detallePedido WHERE email = '%s'", email);
        db.execSQL(query);
    }

    public int getContCarrito(String email) {
        int cont = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM detallePedido WHERE email = '%s'", email);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                cont = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return cont;
    }

    public void actualizarCarrito(Pedido pedido) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE detallePedido SET cantidad = '%s' WHERE email = '%s' AND idComida = '%s'", pedido.getCantidad(), pedido.getEmail(), pedido.getIdComida());
        db.execSQL(query);
    }

    public void incrementarCarrito(String email, String idComida) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE detallePedido SET cantidad = 'cantidad+1 WHERE email = '%s' AND idComida = '%s'", email, idComida);
        db.execSQL(query);
    }

    public void removerDelCarrito(String idComida, String email) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM detallePedido WHERE email = '%s' AND idComida = '%s'", email, idComida);
        db.execSQL(query);
    }

}
