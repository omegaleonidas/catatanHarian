package vsga.sidiq.catatanharian.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseBuilder extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db_student";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "student";
    public static final String KEY_ID = "id";
    public static final String KEY_FIRSTNAME = "name";

    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            KEY_FIRSTNAME+" TEXT );";

    public DatabaseBuilder(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        Log.d("table: ",CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '"+TABLE_NAME+"'");
        onCreate(sqLiteDatabase);
    }

    public long addStudents(String students){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FIRSTNAME,students);

        return db.insert(TABLE_NAME,null,contentValues);
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllStudentList(){
        ArrayList<String> studentList = new ArrayList<String>();
        String name = "";

        String selectQuery = "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                name = cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME));
                studentList.add(name);
            }while (cursor.moveToNext());
            Log.d("StudentList", studentList.toString());
        }
        return studentList;
    }
}
