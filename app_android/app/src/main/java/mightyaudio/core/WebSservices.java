package mightyaudio.core;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public final class WebSservices {
    protected static final String TAG = WebSservices.class.getSimpleName();
    private GlobalClass globalClass = GlobalClass.getInstance();
    private static class InstanceHolder{
        private static final WebSservices instance=new WebSservices();
    }

    public void jsonParsing(final DataCallback callback, String url, final JSONObject jsonObject, final String accesstoke_token){
        final String requestBody = jsonObject.toString();
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG,"Response:"+ response.toString());
                    callback.datOnResponse(response,null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"Error "+error);
                    callback.datOnResponse(null,error);
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<String,String>();
                    header.put("X-MIGHTY-TOKEN",accesstoke_token);
                    Log.e(TAG,"header "+header);
                    return header;
                }

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
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        globalClass.getInstance().addToRequestQueue(postRequest);
    }

    public static WebSservices getInstance(){
        return InstanceHolder.instance;
    }
}
