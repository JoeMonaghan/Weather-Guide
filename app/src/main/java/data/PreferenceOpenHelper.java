package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joe on 03/03/2017.
 */

public class PreferenceOpenHelper extends SQLiteOpenHelper {

    // .db was added in tutorial
    private static final String DATABASE_NAME = "Locations.db";
    private static final int DATABASE_VERSION = 2;
    private static final String PREFERENCE_TABLE_NAME = "NameLocation";

    private static final String ID = "ID";
    private static final String PERSON_NAME = "PERSON_NAME";
    private static final String CITY = "CITY";


    private static final String PREFERENCE_TABLE_CREATE =
            "CREATE TABLE " + PREFERENCE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    PERSON_NAME + " TEXT NOT NULL,\n" +
                    CITY + " TEXT NOT NULL);";

    public PreferenceOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PREFERENCE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PREFERENCE_TABLE_CREATE);
        onCreate(db);
    }

    public boolean insertLocationData(String name, String city){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();


        contentValues.put(PERSON_NAME, name);
        contentValues.put(CITY, city);

       long result = db.insert(PREFERENCE_TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }

        return true;

    }


    public Cursor getData(){

        SQLiteDatabase db = this.getWritableDatabase();

        try{

            Cursor result = db.rawQuery("select * from " + PREFERENCE_TABLE_NAME + ";", null );

            if(result.getCount() == 0 ){
                return null;
            }

            return result;

        } catch (Exception e){
            Log.v("Cursor", "Not working");
        }

        return null;

    }

    public boolean isInserted(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + PREFERENCE_TABLE_NAME, null);

        return result.getCount() > 0;
    }
}
