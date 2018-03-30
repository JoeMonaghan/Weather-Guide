package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * REQUIREMENT 10 DATABASE FEATURE.
 * The class contains the necessary methods to access built in
 * SQLite database, create table, insert and retrieve the
 * information. By extending the class SQLiteOpenHelper helps
 * will provide all this.
 */

public class ThemeOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Locations.db";

    private static final int DATABASE_VERSION = 2;

    private static final String PREFERENCE_TABLE_NAME = "NameLocation";

    // columns names for database table.
    private static final String ID = "ID";
    private static final String PERSON_NAME = "PERSON_NAME";
    private static final String CITY = "CITY";

    // Create table statement
    private static final String PREFERENCE_TABLE_CREATE =
            "CREATE TABLE " + PREFERENCE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    PERSON_NAME + " TEXT NOT NULL,\n" +
                    CITY + " TEXT NOT NULL);";

    // constructor
    public ThemeOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * REQUIREMENT 10.
     * The sql statement to create the table will be executed.
     *
     * @param db instance of database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PREFERENCE_TABLE_CREATE);
    }

    /**
     * REQUIREMENT 10.
     * If the database engine gets an upgrade drop the table and
     * created it again.
     *
     * @param db         instance of database
     * @param oldVersion number
     * @param newVersion number
     */
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
     *
     * @param name of user
     * @param city (location of the user)
     * @return
     */
    public boolean insertNameLocation(String name, String city) {

        // create an instance of the database.
        SQLiteDatabase db = this.getWritableDatabase();

        // need the a content values instance to insert data.
        ContentValues contentValues = new ContentValues();

        contentValues.put(PERSON_NAME, name);
        contentValues.put(CITY, city);

        // if the insert is unsuccessful result will return - 1.
        long result = db.insert(PREFERENCE_TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }

        return true;

    }

    /**
     * REQUIREMENT 12.
     * A Cursor object is required to traverse all the records in
     * the database. The cursor object will contain all the
     * previously stored locations
     *
     * @return cursor object.
     */
    public Cursor getData() {

        // create a database instance
        SQLiteDatabase db = this.getWritableDatabase();

        // query the database to get all the records
        Cursor result = db.rawQuery("SELECT * FROM " + PREFERENCE_TABLE_NAME + ";", null);

        // if none records return null.
        if (result.getCount() == 0) {
            return null;
        }

        return result;

    }

}
