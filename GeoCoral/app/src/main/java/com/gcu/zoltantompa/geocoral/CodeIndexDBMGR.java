package com.gcu.zoltantompa.geocoral;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * code taken and modified from Lab5
 * this calss manages the database handling
 */

public class CodeIndexDBMGR extends SQLiteOpenHelper {



    private static final int DB_VER = 1;
    private static final String DB_PATH = "/data/data/com.gcu.zoltantompa.geocoral/databases/";
    private static final String DB_NAME = "dbcodedesc.s3db";
    private static final String TBL = "tblcodedesc";

    public static final String COL_code = "code";
    public static final String COL_typicalValues = "typicalValues";
    public static final String COL_description = "description";

    private final Context appContext;

    public CodeIndexDBMGR(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_codeDescriptions_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TBL + "("
                + COL_code + " STRING PRIMARY KEY,"
                + COL_typicalValues + "TEXT"+ ")";

        db.execSQL(CREATE_codeDescriptions_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TBL);
            onCreate(db);
        }
    }

    // ================================================================================
    // Creates a empty database on the system and rewrites it with your own database.
    // ================================================================================
    public void dbCreate() throws IOException {

        boolean dbExist = dbCheck();

        if(!dbExist){
            //By calling this method an empty database will be created into the default system path
            //of your application so we can overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDBFromAssets();

            } catch (IOException e) {

                throw new Error("Z-Error copying database");

            }
        }

    }

    // ============================================================================================
    // Check if the database already exist to avoid re-copying the file each time you open the application.
    // @return true if it exists, false if it doesn't
    // ============================================================================================
    private boolean dbCheck(){

        SQLiteDatabase db = null;

        String DB_PATH2 = appContext.getDatabasePath(DB_NAME).toString();

        try{

            String dbPath = DB_PATH2;// + DB_NAME;

            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

            db.setLocale(Locale.getDefault());
            db.setVersion(1);

        }catch(SQLiteException e){

            Log.e("SQLHelper","Z-Database not Found!");

        }

        if(db != null){

            db.close();

            Log.e("SQLHelper","Z-closing!");

        }

        return db != null ? true : false;
    }

    // ============================================================================================
    // Copies your database from your local assets-folder to the just created empty database in the
    // system folder, from where it can be accessed and handled.
    // This is done by transfering bytestream.
    // ============================================================================================
    private void copyDBFromAssets() throws IOException{

        InputStream dbInput = null;
        OutputStream dbOutput = null;
        String dbFileName = DB_PATH + DB_NAME;

        try {

            dbInput = appContext.getAssets().open(DB_NAME);
            dbOutput = new FileOutputStream(dbFileName);
            //transfer bytes from the dbInput to the dbOutput
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dbInput.read(buffer)) > 0) {
                dbOutput.write(buffer, 0, length);
            }

            //Close the streams
            dbOutput.flush();
            dbOutput.close();
            dbInput.close();
        } catch (IOException e)
        {
            throw new Error("Z-Problems copying DB!");
        }
    }


    public void addCodeIndexDBFields(CodeInstance aCodeInstance) {

        ContentValues values = new ContentValues();
        values.put(COL_code, aCodeInstance.getCode());
        values.put(COL_typicalValues, aCodeInstance.getTypicalValues());
        values.put(COL_description, aCodeInstance.getDescription());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TBL, null, values);
        db.close();
    }

    public List<CodeInstance> findAllCodeIndexEntries(){

        List<CodeInstance> list = new ArrayList<CodeInstance>();

        String query = "Select * FROM " + TBL;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        CodeInstance codeInstance;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            for (int i=0; i<cursor.getCount();i++) {

                codeInstance = new CodeInstance();
                codeInstance.setCode(cursor.getString(0));
                codeInstance.setTypicalValues(cursor.getString(1));
                codeInstance.setDescription(cursor.getString(2));


                list.add(codeInstance);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            codeInstance = null;
        }
        db.close();



        return list;
    }


    public CodeInstance findCodeIndexEntry(String code) {
        String query = "Select * FROM " + TBL + " WHERE " + COL_code + " =  \"" + code + "\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        CodeInstance CodeInstance = new CodeInstance();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            CodeInstance.setCode(cursor.getString(0));
            CodeInstance.setTypicalValues(cursor.getString(1));
            CodeInstance.setDescription(cursor.getString(2));
            cursor.close();
        } else {
            CodeInstance = null;
        }
        db.close();
        return CodeInstance;
    }

    public boolean removeCodeIndexEntry(String code) {

        boolean result = false;

        String query = "Select * FROM " + TBL + " WHERE " + COL_code + " =  \"" + code + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        CodeInstance CodeInstance = new CodeInstance();

        if (cursor.moveToFirst()) {
            CodeInstance.setCode(cursor.getString(0));
            db.delete(TBL, COL_code + " = ?",
                    new String[] { String.valueOf(CodeInstance.getCode()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
