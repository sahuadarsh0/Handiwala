package com.tecqza.handiwala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String db_name = "handiwala";
    public static final String table = "cart";

    public static final String col_1 = "id";
    public static final String col_2 = "product";
    public static final String col_3 = "variety";
    public static final String col_4 = "qty";
    public static final String col_5 = "amount";

    public DatabaseHelper(Context context) {
        super(context, db_name, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + table + " (" + col_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + col_2 + " INTEGER(11), " + col_3 + " INTEGER(11), " + col_4 + " INTEGER(11), " + col_5 + " INTEGER(11) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    public boolean insertData(int product, int variety, int qty, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2, product);
        contentValues.put(col_3, variety);
        contentValues.put(col_4, qty);
        contentValues.put(col_5, amount);
        long res = db.insert(table, null, contentValues);
        if (res == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table, null);
        return res;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table);
    }

    public void deleteWhereProduct(int product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where product = " + product);
    }

    public void deleteWhereVariety(int variety) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where variety = " + variety);
    }

    public void updateQty(int variety, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("qty", qty);
        db.update(table, cv, "variety=" + variety, null);
    }

    public void increaseQty(int product) {
        SQLiteDatabase db = this.getWritableDatabase();
        int qty = totalQtyOfItems(product);
        qty++;
        ContentValues cv = new ContentValues();
        cv.put("qty", qty);
        db.update(table, cv, "product=" + product, null);
    }

    public void decreaseQty(int product) {
        SQLiteDatabase db = this.getWritableDatabase();
        int qty = totalQtyOfItems(product);
        qty--;
        if (qty > 0) {
            ContentValues cv = new ContentValues();
            cv.put("qty", qty);
            db.update(table, cv, "product=" + product, null);
        } else {
            deleteWhereProduct(product);
        }
    }

    public int cartTotalAmt() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select sum(qty*amount) grand_total from " + table, null);

        if (cursor.moveToFirst()) {
            int total = cursor.getInt(cursor.getColumnIndex("grand_total"));
            return total;
        } else {
            return 0;
        }
    }

    public boolean isVarietyExist(int variety) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table + " where variety = " + variety, null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public int totalCartItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) count from " + table, null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            return count;
        } else {
            return 0;
        }
    }

    public int totalQtyOfItems(int product) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select qty from " + table + " where product = " + product, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex("qty"));
        } else {
            return 0;
        }
    }

    public String getCartProductsId() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table, null);
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                i++;
                stringBuffer.append(res.getString(1));
                if (res.getCount() > i) {
                    stringBuffer.append(",");
                }
            }

        }
        return stringBuffer.toString();
    }

    public String getCartProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table, null);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        int i = 0;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                i++;
                stringBuffer.append("{" + "\"product\":\"").append(res.getString(1)).append("\"")
                        .append(", \"variety\":\"").append(res.getString(2)).append("\"")
                        .append(", \"qty\":\"").append(res.getString(3)).append("\"}");

                if (res.getCount() > i) {

                    if (res.getCount() > i) {
                        stringBuffer.append(",");
                    }

                }
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }


}
