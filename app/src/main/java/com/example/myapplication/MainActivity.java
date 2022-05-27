package com.example.myapplication;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor wordCursor;
    SimpleCursorAdapter wordAdapter;
    ListView wordList;
    EditText wordFilter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.myapplication.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.search, R.id.favourites, R.id.history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


///////////////////////////////////////////////////////////////

        wordList = findViewById(R.id.wordList);
        wordFilter = findViewById(R.id.wordFilter);
        sqlHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        sqlHelper.create_db();
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            db = sqlHelper.open();
            wordCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
            String[] headers = new String[]{DatabaseHelper.COLUMN_WORD, DatabaseHelper.COLUMN_MEANING};
            wordAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                    wordCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

            // если в текстовом поле есть текст, выполняем фильтрацию
            // данная проверка нужна при переходе от одной ориентации экрана к другой
            if (!wordFilter.getText().toString().isEmpty())
                wordAdapter.getFilter().filter(wordFilter.getText().toString());

            // установка слушателя изменения текста
            wordFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    wordAdapter.getFilter().filter(s.toString());
                }
            });

            // устанавливаем провайдер фильтрации
            wordAdapter.setFilterQueryProvider(constraint -> {

                if (constraint == null || constraint.length() == 0) {

                    return db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
                } else {
                    return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                            DatabaseHelper.COLUMN_WORD + " like ?", new String[]{"%" + constraint + "%"});
                }
            });

            wordList.setAdapter(wordAdapter);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        wordCursor.close();
    }
}