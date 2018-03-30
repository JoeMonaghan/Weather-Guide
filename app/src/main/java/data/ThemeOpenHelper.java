package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * REQUIREMENT 12
 * CREATED
 *
 *
 * Created by joe on 03/03/2017.
 */

public class ThemeOpenHelper extends SQLiteOpenHelper {

    // .
    private static final String DATABASE_NAME = "Locations.db";
    // the version of database.
    private static final int DATABASE_VERSION = 2;

    // I have g
    private static final String PREFERENCE_TABLE_NAME = "NameLocation";

    private static final String ID = "ID";
    private static final String PERSON_NAME = "PERSON_NAME";
    private static final String CITY = "CITY";


    // Create table
    private static final String PREFERENCE_TABLE_CREATE =
            "CREATE TABLE " + PREFERENCE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    PERSON_NAME + " TEXT NOT NULL,\n" +
                    CITY + " TEXT NOT NULL);";

    public ThemeOpenHelper(Context context) {
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

    /**
     * REQUIREMENT 12.
     * Insert the name and city which the user is located.
     * If the insertion is unsuccessful return false, otherwise return
     * true.
     * MODIFTED AND INSERTED
     * @param name of user
     * @param city (location of the user)
     * @return
     */
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


    /**
     * REQUIREMENT 12.
     * A Cursor object is required to traverse all the records in
     * the database. The cursor will contain all the previous  stored locations
     * ACCESSED.
     *
     * @return
     */
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

}
