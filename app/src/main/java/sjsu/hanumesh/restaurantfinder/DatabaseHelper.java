package sjsu.hanumesh.restaurantfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Favorites.db";
    public static final String TABLE_NAME = "favorites_table";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,RATING TEXT,IMAGE TEXT,LOCATION TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String insertData(String name, String rating, String image, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("RATING",rating);
        contentValues.put("IMAGE",image);
        contentValues.put("LOCATION", location);
        Cursor isAlreadyPresent = db.rawQuery("select * from "+TABLE_NAME+" where NAME='"+name+"' AND LOCATION='"+location+"'",null);
        Log.d("searchQuery", "select * from " + TABLE_NAME + " where NAME='" + name + "' AND LOCATION='" + location + "'");
        if (isAlreadyPresent.getCount()==0) {

            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1)
                return "error";
            else
                return "added";
        }
        else{
            return "already_present";
        }

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_NAME,null);
        return result;
    }

    public boolean isPresent(String name, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor isAlreadyPresent = db.rawQuery("select * from "+TABLE_NAME+" where NAME='"+name+"' AND LOCATION='"+location+"'",null);
        Log.d("searchQuery","select * from "+TABLE_NAME+" where NAME='"+name+"' AND LOCATION='"+location+"'");
        if (isAlreadyPresent.getCount()==0)
            return false;
        else
            return true;

    }

    public boolean deleteRecord(String name, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"NAME='"+name+"'",null);
        return true;
    }
}
