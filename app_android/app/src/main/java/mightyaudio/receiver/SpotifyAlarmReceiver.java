package mightyaudio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SpotifyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "spotify.refreshtoken.alarm";
    public SpotifyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent spotifyServiceIntent = new Intent(context, SpotifyIntentService.class);
        context.startService(spotifyServiceIntent);
    }
}
