package mightyaudio.core;

import com.android.volley.VolleyError;

public interface DataCallback {
    void datOnResponse(String result, VolleyError error);
}
