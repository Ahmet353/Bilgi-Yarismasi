package com.example.fatihdemirel.milyoner;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabase extends SQLiteOpenHelper {
    public static String tableName;
    private static final int DATABASE_VERSION = 3;
    private static String DATABASE_NAME = "milyoner_local";
    private static String TABLE_NAME = "User";
    private static String TABLE_NAME_FACE = "UserFacebook";
    private static String USER_ID = "id";
    private static String USER_NAME = "username";
    private static String PASSWORD = "userpassword";
    private static String POINT = "userpoint";
    private static String USERLIFE = "life";
    private static String NUMBERLEVEL1 = "numberlevel1";
    private static String NUMBERLEVEL2 = "numberlevel2";
    private static String NUMBERLEVEL3 = "numberlevel3";


    public LocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + USER_ID + " INTEGER PRIMARY KEY ,"
                + USER_NAME + " TEXT,"
                + PASSWORD + " TEXT,"
                + POINT + " TEXT,"
                + USERLIFE + " TEXT,"
                + NUMBERLEVEL1 + " TEXT,"
                + NUMBERLEVEL2 + " TEXT,"
                + NUMBERLEVEL3 + " TEXT)";
        db.execSQL(CREATE_TABLE);
        String CREATE_TABLE_FACE = "CREATE TABLE " + TABLE_NAME_FACE + "("
                + USER_ID + " INTEGER PRIMARY KEY ,"
                + USER_NAME + " TEXT,"
                + PASSWORD + " TEXT,"
                + POINT + " TEXT,"
                + USERLIFE + " TEXT,"
                + NUMBERLEVEL1 + " TEXT,"
                + NUMBERLEVEL2 + " TEXT,"
                + NUMBERLEVEL3 + " TEXT)";
        db.execSQL(CREATE_TABLE_FACE);
    }

    public void updateUser(User user) {
        String sql = "UPDATE " + tableName + " SET " + USER_NAME + "='" + user.username + "', " + PASSWORD + "='" + user.password + "', " + POINT + "='" + User.point + "', " + USERLIFE + "='" + user.life + "' WHERE id=" + User.userID + " ";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);

    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.USER_ID, user.userID);
        values.put(this.USER_NAME, user.username);
        values.put(this.PASSWORD, user.password);
        values.put(this.POINT, user.point);
        values.put(this.USERLIFE, user.life);
        db.insert(tableName, null, values);
        db.close();
    }

    public User getUser() {
        User loginUser = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.userID = cursor.getString(0);
                user.username = cursor.getString(1);
                user.password = cursor.getString(2);
                user.point = cursor.getInt(3);
                user.life = cursor.getInt(4);
                loginUser = user;
            } while (cursor.moveToNext());
        }
        db.close();
        return loginUser;
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}