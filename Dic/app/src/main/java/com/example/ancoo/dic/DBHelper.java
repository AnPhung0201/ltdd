package com.example.ancoo.dic;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by An Coo on 5/6/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    public static final String DATABASE_NAME ="my_dictionary.db";
    public static final int DATABASE_VERSION =1;

    private String DATABASE_LOCATION ="/data/data/com.example.ancoo.dic/databases/";
    private String DATABASE_FULL_PATH ="";

    private final String TBL_ENG_VI = "eng_vi";
    private final String TBL_VI_ENG = "vi_eng";
    private final String TBL_BOOKMARK = "bookmark";

    private final String COL_KEY ="key";
    private final String COL_VALUE ="value";


    public SQLiteDatabase mDB;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        mContext =context;

        //DATABASE_LOCATION =  mContext.getPackageName()+"/app/src/main/assets/";
        DATABASE_FULL_PATH = DATABASE_LOCATION+DATABASE_NAME;

        if(!isExistingDB()){
            try {
                extractAssetToDatabaseDirectory(DATABASE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_FULL_PATH,null);
    }

    boolean isExistingDB(){
        File file=new File(DATABASE_FULL_PATH);
        return file.exists();
    }

    public void extractAssetToDatabaseDirectory(String fileName) throws IOException{
        int length;
        InputStream sourceDataBase = this.mContext.getAssets().open(fileName);
        File destinationPath = new File(DATABASE_FULL_PATH);
        OutputStream destination = new FileOutputStream(destinationPath);

        byte[] buffer = new byte[4096];
        while ((length = sourceDataBase.read(buffer))>0){
            destination.write(buffer,0,length);
        }
        sourceDataBase.close();
        destination.flush();
        destination.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getWord(int dicType){
            String tableName =getTableName(dicType);
            String q ="select * from "+ tableName;
            Cursor result = mDB.rawQuery(q,null);

            ArrayList<String> source =new ArrayList<>();
            while (result.moveToNext()){
                source.add(result.getString(result.getColumnIndex(COL_KEY)));
            }

            return source;
    }
        //querry word base on key
    public Word getWord(String key, int dicType){
        String tableName =getTableName(dicType);
        String q ="select * from "+tableName+" where upper([key]) = upper(?)";
        Cursor result = mDB.rawQuery(q,new String[]{key});

        Word word =new Word();
        while (result.moveToNext()){
            word.key = result.getString(result.getColumnIndex(COL_KEY));
            word.value = result.getString(result.getColumnIndex(COL_VALUE));
        }

        return word;
    }

    //insert word to bookmark
    public void addBookmark(Word word){

        try {
            String q = "insert into bookmark("+COL_KEY+","+COL_VALUE+") values(?,?);";
            mDB.execSQL(q,new Object[]{word.key,word.value});
        }
        catch (SQLException ex){
        }
    }

    //remove bookmark
    public void removeBookmark(Word word){

        try {
            String q = "delete from bookmark where upper(["+COL_KEY+"]) = upper(?) and ["+COL_VALUE+"] =?;";
            mDB.execSQL(q,new Object[]{word.key,word.value});
        }
        catch (SQLException ex){
        }
    }

    //querry all word from bookmark
    public ArrayList<String> getAllWordFromBookmark(String key){

        String q ="SELECT * FROM bookmark ORDER BY [date] DESC;";
        Cursor result = mDB.rawQuery(q,new String[]{key});

        ArrayList<String> source =new ArrayList<>();
        while (result.moveToNext()){
            source.add(result.getString(result.getColumnIndex(COL_KEY)));
        }

        return source;
    }
    //querry is word mark
    public boolean isWordMark(Word word){

        String q ="SELECT * FROM bookmark WHERE upper([key]) =upper(?) AND [value] =?";
        Cursor result = mDB.rawQuery(q,new String[]{word.key,word.value});

        return result.getCount() > 0;

    }

    //querry word from bookmark by key
    public Word getWordFromBookmark(String key){

        String q ="SELECT * FROM bookmark WHERE upper([key]) =upper(?)";
        Cursor result = mDB.rawQuery(q,new String[]{key});
        Word word = null;
        while (result.moveToNext()){
            word = new Word();
            word.key = result.getString(result.getColumnIndex(COL_KEY));
            word.value = result.getString(result.getColumnIndex(COL_VALUE));
        }
        return word;
    }




    public String getTableName(int dicType ){
        String tableName = "";
        if(dicType == R.id.action_eng_vi)
        {
            tableName = TBL_ENG_VI;
        }else if(dicType == R.id.action_vi_eng){
            tableName = TBL_VI_ENG;
        }
        return tableName;
    }
}
