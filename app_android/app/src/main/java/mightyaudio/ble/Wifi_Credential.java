package unizen.ble;

/**
 * Created by Admin on 12/6/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Wifi_Credential {


    private static final String Wifi_Name = "wifi_name";
  //  private static final String Wifi_Pass = "wifi_pass";

    public static void setPass(Context context, String name, String pass)
    {
        Context appContext = context.getApplicationContext();
//        Set<String> mylist = new HashSet<String>();
//        Set<String> mylist1 = new HashSet<String>();
      //  JSONArray temp = new JSONArray(name);
        System.out.println("name"+name);
        System.out.println("pass"+pass);

        Set<String> names = new HashSet<String>() ;
        names.add(name+":"+pass);
        for (String na : names) {
            System.out.println("forname"+na);
            na.split(":");
            if (name.equals(na.replace("[",""))) {
                System.out.println("Conditiontrue equals");
                names.remove(name +":"+ pass);
                names.add(name +":"+ pass);
            }else{
                names.add(name +":"+ pass);
            }
        }
        names.add(name+":"+pass);
        System.out.println("afterifNames"+names);


        //= temp.join(",").split(",");
        //String[] passs = pass;
        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        //mylist = new String[]{name,pass};
        //System.out.print("mylist"+mylist);
//        mylist.add(name);
//        mylist1.add(pass);
        editor.putString(Wifi_Name, String.valueOf(names));
        //editor.putString(Wifi_Pass, String.valueOf(passs));
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context appContext)
    {
        return appContext.getSharedPreferences(Wifi_Name, Context.MODE_PRIVATE);
    }

    public static String getPass(Context context)
    {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

     //   String pass = sharedPref.getString(Wifi_Pass, null);
        //Set<String> nas = new HashSet<String>();
        String nas = sharedPref.getString(Wifi_Name, null);
        System.out.println("names"+nas);
        if (nas == null ) {
            return null;
        }
        //return new String[] {names,passs};
        return nas;
    }
}

