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
    private static final String DATABASE_NAME = "Theme.db";
    private static final int DATABASE_VERSION = 2;
    private static final String PREFERENCE_TABLE_NAME = "Theme";

    private static final String THEME_ID = "THEME_ID";
    private static final String THEME_NAME = "THEME_NAME";
    private static final String BACKGROUND = "BACKGROUND";
    private static final String SELECTED = "SELECTED";

    private static final String PREFERENCE_TABLE_CREATE =
            "CREATE TABLE " + PREFERENCE_TABLE_NAME + " (" +
                    THEME_ID + " INT PRIMARY KEY NOT NULL,\n" +
                    THEME_NAME + " TEXT NOT NULL,\n" +
                    BACKGROUND + " TEXT NOT NULL,\n" +
                    SELECTED + " TINYINT NOT NULL );";

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

    public boolean insertThemeData(int themeID, String name, String bgColor, int selected){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        //
        contentValues.put(THEME_ID, themeID);
        contentValues.put(THEME_NAME, name);
        contentValues.put(BACKGROUND, bgColor);
        contentValues.put(SELECTED, selected);

        long result = db.insert(PREFERENCE_TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }

        return true;

    }


    public String getData(String title){

        SQLiteDatabase db = this.getWritableDatabase();

       //


        try{
            Cursor res = db.rawQuery("select " + BACKGROUND + " from " + PREFERENCE_TABLE_NAME + " where " + THEME_NAME + " = \"" + title +"\";", null );

            if(res.getCount() == 0){
                Log.v("Cursor", "Query not getting any data");
            }
            Cursor result = db.rawQuery("SELECT * FROM " + PREFERENCE_TABLE_NAME, null);

            if(result.getCount() == 0){
                Log.v("Cursor", " nothing is database");
            }

            String str = res.getString(0);

            return str;
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
