package com.example.ancoo.dic;

/**
 * Created by An Coo on 5/5/2018.
 */

public class DB {
    public static String[] getData(int id){
        if(id == R.id.action_eng_vi){
            return getEV();
        } else if (id == R.id.action_vi_eng)
        {
            return  getVE();
        }
        return new String[0];
    }
    public static String[] getEV(){
        String[] source = new  String[]{
                "A","Hi"
        };
        return source;
    }
    public static String[] getVE(){
        String[] source = new  String[]{
                "Một","Xin chào"
        };
        return source;
    }
}
