package mightyaudio.receiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import mightyaudio.core.GlobalClass;
import mightyaudio.core.SpotifySessionManager;


public class SpotifyIntentService extends IntentService {
    private static final String TAG = SpotifyIntentService.class.getSimpleName();
    private SpotifySessionManager spotifySessionManager;
    private SharedPreferences spotifyPref;
    private  GlobalClass globalClass= GlobalClass.getInstance();
    private JSONObject jsonObject;
    public SpotifyIntentService() {
        super("SpotifyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //SpotifyLogin with Cloud
        String msg = intent.getStringExtra("launch");
        Log.e(TAG,"Message "+msg);
        spotifySessionManager = new SpotifySessionManager(getApplicationContext());
        spotifyPref = getApplicationContext().getSharedPreferences(spotifySessionManager.PREF_NAME , Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",spotifyPref.getLong(spotifySessionManager.USERID,0l)+"");
            jsonObject.put("refresh_token",spotifyPref.getString(SpotifySessionManager.REFRESHTOKEN,""));
            jsonObject.put("client_id",globalClass.CLIENT_ID);
            jsonObject.put("client_secret",globalClass.Client_Secret);
            Log.e(TAG,"Temp "+spotifyPref.getString(SpotifySessionManager.ACCESSTOKEN,""));
            spotifyRefreshToken(jsonObject,msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void spotifyRefreshToken(JSONObject token_refres, final String message){
        try {
            Log.e(TAG,"SpotifyService json"+ token_refres.toString());
            final String requestBody = token_refres.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalClass.spotifyUrl+"spotifyaccess/refreshSpotifyAccessToken", new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"response "+ response);
                    if(Integer.parseInt(response) == 200) {
                        restoreRefreshToken(jsonObject,message);
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY"+ error.toString());
                    NetworkResponse response = error.networkResponse;

                    if (error instanceof ServerError && response != null) {
                        Log.e(TAG,"res in side"+response.statusCode);
                        if(response.statusCode == 400 | response.statusCode == 500 | error instanceof ServerError){
                            Log.e(TAG,"res in side"+response.statusCode);
                            spotifySessionManager.clearSession();
                            globalClass.send_set_spotifylogout();
                            globalClass.notifyBrowesFragment("Logout");
                            globalClass.spotify_tick();
                            GlobalClass.spotify_frag_status = false;
                            globalClass.spotify_status = false;
                            globalClass.spotifyloginlogout("Logout");
                            globalClass.stopRefreshToken();
                        }
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        Log.e(TAG,"parseNetworkResponse "+responseString);
                        if(Integer.parseInt(responseString) == 200) try {
                            try {
                                jsonObject =new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
                                Log.e(TAG, "Spotify refresh responce " + jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (UnsupportedEncodingException e) {
                            return com.android.volley.Response.error(new ParseError(e));
                        }
                        else Log.e(TAG, "Some things wrong");
                    }
                    return com.android.volley.Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            globalClass.getRequestQueue().add(stringRequest);
        } catch (Exception e) {
            Log.d(TAG,"Exception "+e.toString());
            e.printStackTrace();
        }
    }

    private void restoreRefreshToken(JSONObject refreshToke,String message){
        try {
            Log.e(TAG,"Spotify Token plalist");
            String spotify_username = spotifyPref.getString(spotifySessionManager.USER_NAME,"");
            long spotify_id = spotifyPref.getLong(spotifySessionManager.USERID,0l);
            String accessToken = refreshToke.getString("accessToken");
            String expire_time = refreshToke.getString("expiresIn");
            long expire_time_token= TimeUnit.HOURS.toMillis(1)+System.currentTimeMillis();
            String refreshToken = spotifyPref.getString(spotifySessionManager.REFRESHTOKEN,"");
                spotifySessionManager.clearSession();
                spotifySessionManager.createSpotifySession(spotify_username,spotify_id,accessToken,refreshToken,expire_time,expire_time_token);
            if(message != null){
                if(message.equals("launch")){
                    if(globalClass.arrayPlayList.isEmpty())
                    globalClass.retrivePlayListFromSpotify(0,50,0);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
