package com.example.ancoo.dic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String DATABASE_NAME = "my_dictionary.db";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    ArrayList<Word> listWord;//list từ vựng

    MenuItem menuSetting;
    Toolbar toolbar;
    DBHelper dbHelper;

    DictionaryFragment dictionaryFragment;
    BookmarkFragment bookmarkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        processCopy();
        docCSDL();

//        dbHelper = new DBHelper(this);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dictionaryFragment =new DictionaryFragment();
        bookmarkFragment = new BookmarkFragment();

        goToFragment(dictionaryFragment,true);

        dictionaryFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                String id = Global.getState(MainActivity.this,"dic_type");
                int dicType = id == null? R.id.action_eng_vi:Integer.valueOf(id);
                goToFragment(DetailFragment.getNewInstance(value,dbHelper,dicType),false);
            }
        });

        bookmarkFragment.setOnFragmentListener(new FragmentListener() {
            @Override
            public void onItemClick(String value) {
                String id = Global.getState(MainActivity.this,"dic_type");
                int dicType = id == null? R.id.action_eng_vi:Integer.valueOf(id);
                goToFragment(DetailFragment.getNewInstance(value,dbHelper,dicType),false);
            }
        });

        EditText edit_search =findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    dictionaryFragment.filterValue(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menuSetting = menu.findItem(R.id.action_settings);

       String id = Global.getState(this,"dic_type");
       if(id!=null)
           onOptionsItemSelected(menu.findItem(Integer.valueOf(id)));
       else {
           ArrayList<String> source = dbHelper.getWord(R.id.action_eng_vi);
           dictionaryFragment.resetDataSource(source);
       }
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//        if(R.id.action_settings == id) return true;
//
//        Global.saveState(this,"dic_type",String.valueOf(id));
//        ArrayList<String> source = dbHelper.getWord(id);
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_eng_vi) {
//            //return true;
//            dictionaryFragment.resetDataSource(source);
//            menuSetting.setIcon(getDrawable(R.drawable.ic_eng_vi));
//        }else if (id==R.id.action_vi_eng){
//            //return true;
//            dictionaryFragment.resetDataSource(source);
//            menuSetting.setIcon(getDrawable(R.drawable.ic_vi_eng));
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id==R.id.nav_bookmark){
            String activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName();
            if(!activeFragment.equals(BookmarkFragment.class.getSimpleName())) {
                goToFragment(bookmarkFragment, false);
            }
        }

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void goToFragment(Fragment fragment,boolean isTop){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if(!isTop)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName();
        if(activeFragment.equals(BookmarkFragment.class.getSimpleName())){
            //menuSetting.setVisible(false);
            toolbar.findViewById(R.id.edit_search).setVisibility(View.GONE);
            toolbar.setTitle("Bookmark");
        } else
        {
           // menuSetting.setVisible(true);
            toolbar.findViewById(R.id.edit_search).setVisibility(View.VISIBLE);
            toolbar.setTitle("");
        }
        return true;
    }






        private void docCSDL() {
        database=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor=database.rawQuery("select * from eng_vi",null);
        listWord=new ArrayList<>();
        while (cursor.moveToNext())
        {

            listWord.add(new Word(cursor.getString(0),cursor.getString(1)));
        }
        cursor.close();
            Toast.makeText(this, listWord.get(0).getKey(), Toast.LENGTH_LONG).show();

    }
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void CopyDataBaseFromAsset() {
        try
        {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = getDatabasePath();
            // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }       // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }
}
