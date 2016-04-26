package rsa;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Wall on 2016/3/29.
 * sp save or read
 *
 */
public class SpUtils {
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    public static SpUtils spUtils;

    public SpUtils(Context context){
        editor=  context.getSharedPreferences("jr",Context.MODE_PRIVATE).edit();
        sp=context.getSharedPreferences("jr",Context.MODE_PRIVATE);
    }

    public static SpUtils getSpUtils(){
        return spUtils;
    }


    //
    public boolean saveAESkeyString(String key,String data){
        boolean b=false;
        if(editor!=null){
            editor.putString(key,data);
              b=editor.commit();
        }
        return b;
    }
    //
    public boolean saveByte(String key,byte[] data){
        boolean b=false;
        String data1=Base64.encode(data);
        if(editor!=null){
            editor.putString(key,data1);

            b=editor.commit();
        }
        return b;
    }


    public boolean saveboolean(String key,boolean ishavekey){
        boolean b=false;
        if(editor!=null){
            editor.putBoolean(key,ishavekey);

            b=editor.commit();
        }
        return b;
    }
    public boolean getboolean(String key){
        boolean ishavekey=false;
        if(sp!=null){
            ishavekey= sp.getBoolean(key,false);
        }
        return ishavekey;
    }

    public String readAESKeyString(String key){
        String aeskey="";
        if(sp!=null){
            aeskey= sp.getString(key,"err");
        }
        return aeskey;
    }


    public byte[] readRSAKeyString(String key){
        String aeskey="";
        if(sp!=null){
            aeskey= sp.getString(key,"err");
        }
        byte[] rsakey=Base64.decode(aeskey);
        return rsakey;
    }


}
