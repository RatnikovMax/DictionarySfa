package com.example.myapplication;

        import android.database.SQLException;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.database.sqlite.SQLiteDatabase;
        import android.content.Context;
        import android.util.Log;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;

class DatabaseHelper extends SQLiteOpenHelper {
//    private static String DB_PATH = null; // полный путь к базе данных
    private static final String DB_NAME = "bdd.db";
    private static String DB_PATH = null;
    private static final int DB_VERSION = 1;
//    private static final int SCHEMA = 2; // версия базы данных
    static final String TABLE = "d"; // название таблицы в бд
    static final String COLUMN_ID = "_id";
    static final String COLUMN_WORD = "word";
    static final String COLUMN_MEANING = "meaning";
    private final Context myContext;

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) { }

    void create_db(){

        File file = new File(DB_PATH);
        if (!file.exists()) {
            //получаем локальную бд как поток
            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
                // Открываем пустую бд
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}