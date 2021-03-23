package com.tecqza.handiwala;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    public SharedPrefs(Context context, String tag){
        sp = context.getSharedPreferences(tag, context.MODE_PRIVATE);
        edit = sp.edit();
    }

    public void setSharedPrefs(String key, String value){
        edit.putString(key,value);
        edit.commit();
    }

    public String getSharedPrefs(String key){
        return sp.getString(key,null);
    }

    public void clearAll(){
        sp.edit().clear().apply();
    }


    public void clearByName(String key){
        edit.remove(key);
        edit.commit();
    }


}
