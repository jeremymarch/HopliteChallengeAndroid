package com.philolog.hc;

/**
 * Created by jeremy on 6/8/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    public SQLiteDatabase DB;
    public String DBPath;
    public static String DBName = "hcdatadb.sqlite";
    public static final int version = '1';
    public static Context currentContext;
    public static String tableName = "games";

    public DBHelper(Context context) {
        super(context, DBName, null, version);
        currentContext = context;
        //DBPath = "/data/data/" + context.getPackageName() + "/databases";
        DBPath = context.getDatabasePath(DBName).toString();

        createDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub

    }

    private void createDatabase() {
        boolean dbExists = checkDbExists();

        if (dbExists) {
// do nothing
        } else {
            DB = currentContext.openOrCreateDatabase(DBName, 0, null);
            /*
            DB.execSQL("CREATE TABLE IF NOT EXISTS " +
                    tableName +
                    " (LastName VARCHAR, FirstName VARCHAR," +
                    " Country VARCHAR, Age INT(3));");

            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('M','shumi','India',25);");
            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('C','sarah','India',25);");
            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('D','Lavya','USA',20);");
            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('V','Avi','EU',25);");
            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('T','Shenoi','Bangla',25);");
            DB.execSQL("INSERT INTO " +
                    tableName +
                    " Values ('L','Lamha','Australia',20);");
*/
        }

    }

    private boolean checkDbExists() {
        SQLiteDatabase checkDB = null;
        String myPath = DBPath;// + DBName;
        try {

            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

// database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }
Log.e("abc", "Opendb: " + myPath + ", " + (checkDB != null ? "true" : "false"));
        return checkDB != null ? true : false;
    }
}