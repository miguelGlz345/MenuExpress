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

    public List<Pedido> getCarrito() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelet = {"idComida", "nombreComida", "cantidad", "precio", "descuento"};
        String sqlTable = "detallePedido";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelet, null, null, null, null, null);

        final List<Pedido> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Pedido(c.getString(c.getColumnIndex("idComida")),
                        c.getString(c.getColumnIndex("nombreComida")),
                        c.getString(c.getColumnIndex("cantidad")),
                        c.getString(c.getColumnIndex("precio")),
                        c.getString(c.getColumnIndex("descuento"))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void agregarCarrito(Pedido pedido) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO detallePedido(idComida, nombreComida, cantidad, precio, descuento) VALUES ('%s', '%s', '%s', '%s', '%s');",
                pedido.getIdComida(),
                pedido.getNombreComida(),
                pedido.getCantidad(),
                pedido.getPrecio(),
                pedido.getDescuento());
        db.execSQL(query);
    }


    public void elmininarCarrito() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM detallePedido");
        db.execSQL(query);
    }

    public int getContCarrito() {
        int cont = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) detallePedido");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                cont = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return cont;
    }
}
