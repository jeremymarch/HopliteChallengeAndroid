package com.philolog.hc;

/*
 * Created by jeremy on 6/8/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    public SQLiteDatabase DB;
    public String DBPath;
    public String dboldpath1;
    public String dboldname1 = "hcdatadb.sqlite";
    public static String DBName = "hcdatadb1-5.sqlite";
    public static final int version = '1';
    public Context currentContext;
    public static String tableName = "games";

    public DBHelper(Context context) {
        super(context, DBName, null, version);
        currentContext = context;
        //DBPath = "/data/data/" + context.getPackageName() + "/databases";

        DBPath = context.getDatabasePath(DBName).toString();
        dboldpath1 = context.getDatabasePath(dboldname1).toString();

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
        /*
        Log.e("hoplitetesting", "init old db");
        VerbSequence vs1 = new VerbSequence();

        vs1.vsInit(dboldpath1);
        vs1 = null;
        Log.e("hoplitetesting", "done init old db");
*/
        boolean dbExists = checkDbExists(DBPath);
        if (!dbExists)
        {
            Log.e("hoplitetesting", "need to copy db from assets");
            //DB = currentContext.openOrCreateDatabase(DBName, 0, null);
            try {
                Log.e("hoplitetesting", "copying db from assets");
                copyDatabase();

                //do we need to copy from old db?
                if ( checkDbExists(dboldpath1) ) {
                    Log.e("hoplitetesting", "old db exists, need to import to new db");
                    //if previous version exists...
                    //import old data into new db
                    VerbSequence vs = new VerbSequence();
                    int upgradeRes = vs.upgradedb(dboldpath1, DBPath);
                    Log.e("hoplitetesting", "upgrade db result" + upgradeRes);
                    if (upgradeRes == 0) //means success
                    {
                        //delete old db
                        currentContext.deleteDatabase(dboldname1);
                        Log.e("hoplitetesting", "deleted old db");
                    }
                    vs = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkDbExists(DBPath);

        Log.e("hoplitetesting", "done updating to new db");
    }

    private void copyDatabase() throws IOException {
        File dbFile = currentContext.getDatabasePath(DBName);
        InputStream is = currentContext.getAssets().open(DBName);
        OutputStream os = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }

        os.flush();
        os.close();
        is.close();
    }

    private boolean checkDbExists(String path) {
        SQLiteDatabase checkDB = null;
        String myPath = path;//DBPath;// + DBName;
        try {

            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (checkDB != null) {
            checkDB.close();
        }

        Log.e("DBHelper", "Opendb: " + myPath + ", " + (checkDB != null ? "true" : "false"));
        return (checkDB != null); // ? true : false;
    }
}