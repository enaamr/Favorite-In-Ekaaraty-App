package ekaraatayforiraq.ekaraaty.com.ekaraaty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


/**
 * Created by enaam on 8/3/2018.
 */

public class DBCon extends SQLiteOpenHelper {
    private static final String DBName = "Favorite";
    private static final int version = 1;
    private static final String tableName1 = "FavTable";
    private static final String Id = "Id";
    private static final String Path = "Path";
    private static final String DocId = "DocId";
    private static final String DropTable1 = "Drop Table if exists " + tableName1;
    ///statement to create table1
    String stat_create_table1 = "CREATE TABLE IF NOT EXISTS " + tableName1 + " (" + Id + "	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " + Path + " Text  NOT NULL, " + DocId + " Text  NOT NULL );";
    private Context context;

    public DBCon(Context context) {
        super(context, DBName, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(stat_create_table1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DropTable1);


    }

    //insert the path of post and document id the user selected to save as fav
    public long insert(String path, String DocIds) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Path, path);
        values.put(DocId, DocIds);

        long in = db.insert(tableName1, null, values);
        db.close();
        return in;

    }

    //remove post from fav must delete from SQLite table
    public void deletIndex(int id) {
        String delete = "delete from " + tableName1 + " where " + tableName1 + "." + Id + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(delete);

    }

    //to check if the document is fav or not
    //this method take the id of filed to check
    public boolean isFav(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String get = "select * from " + tableName1 + " where " + tableName1 + "." + Id + " = " + id;
        Cursor cursor = db.rawQuery(get, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    //thi method find the id of document
    public int find(String s) {
        String q = "SElECT " + tableName1 + "." + Id + " FROM " + tableName1 + " WHERE " + tableName1 + "." + DocId + " = '" + s + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        int k = 0;
        Cursor cursor = db.rawQuery(q, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            k = cursor.getInt(cursor.getColumnIndex(Id));
            cursor.moveToNext();
        }
        return k;
    }

    // Search if the path exit
    public ArrayList<String> getInfo(String path) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selecQuary = "SELECT " + DocId + " from " + tableName1 + " where " + Path + " = '" + path + "'";

        ArrayList<String> arrayList = new ArrayList<>();

        String s = null;


        Cursor cursor = db.rawQuery(selecQuary, null);
        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            s = cursor.getString(cursor.getColumnIndex(DocId));

            arrayList.add(s);


            cursor.moveToNext();
        }
        return arrayList;

    }
}


