package mightyaudio.core;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Helper class to delegate the intent-generation logic.
 * Created by rordulu on 6/21/16.
 */
public class SpotifyAuthHelper {
    private static GlobalClass globalClass =GlobalClass.getInstance();
private static final String TAG = SpotifyAuthHelper.class.getSimpleName();
    /**
     * SPOTIFY AUTH FOR APP:
     */

    private static final String PROTOCOL_VERSION = "1";
    private static final int SPOTIFY_RESULT_ERROR = -2;

    /**
     * The intent action to start the Spotify authorization flow. The auth flow is designed to be used with implicit intent, so
     * the clients need to specify the corresponding intent action.
     */
    private static final String SPOTIFY_AUTH_INTENT_ACTION = "com.spotify.sso.action.START_AUTH_FLOW";

    /**
     * SPOTIFY AUTH FOR APP&WEB REQUEST KEYS:
     */

    private static final String KEY_EXTRA_VERSION = "VERSION";
    private static final String KEY_CLIENT_ID = "CLIENT_ID";
    private static final String KEY_REQUESTED_SCOPES = "SCOPES";
    private static final String KEY_REDIRECT_URI = "REDIRECT_URI";
    private static final String KEY_STATE = "STATE";

    /**
     * SPOTIFY AUTH FOR APP RESPONSE KEYS:
     */

    private static final String EXTRA_REPLY = "REPLY";
    private static final String EXTRA_ERROR = "ERROR";
    private static final String EXTRA_STATE = KEY_STATE; // This might be an intent extra too, but refers to the same data
    private static final String KEY_RESPONSE_TYPE = "RESPONSE_TYPE";
    private static final String KEY_AUTHORIZATION_CODE = "AUTHORIZATION_CODE";

    /**
     * SPOTIFY AUTH FOR WEB
     */

    private static final String ACCOUNTS_SCHEME = "https";
    private static final String ACCOUNTS_AUTHORITY = "accounts.spotify.com";
    private static final String ACCOUNTS_PATH = "authorize";

    /**
     * SPOTIFY AUTH WEB RESPONSE KEYS
     */

    private static final String QUERY_PARAM_AUTHORIZATION_CODE = "code";
    private static final String QUERY_PARAM_STATE = "state";

    /**
     * Starts the Spotify App authentication activity or browser activity depending on the availability of the Spotify app.
     * @param activity           Activity to start the Spotify Authentication activity from.
     * @param clientId           Client ID of the application. (developer.spotify.com)
     * @param redirectURI        RedirectURI of your application.
     * @param state              (Optional) The state parameter might be set by the client to match an auth code request and its response.
     * @param responseType       Type of the response. ("code" or "token")
     * @param requestedScopes    Space separated list of scopes (For web based auth flow)
     * @param requestedScopesArray Array of scopes (For the app based auth flow)
     * @param spotifyRequestCode Request code for opening the Spotify Auth activity.
     */
    public static void startSpotifyAuthFlow(final Activity activity,
                                            String clientId,
                                            String redirectURI,
                                            String state,
                                            String responseType,
                                            String requestedScopes,
                                            String[] requestedScopesArray,
                                            final int spotifyRequestCode) {

        final Intent intent = new Intent();
        intent.setAction(SPOTIFY_AUTH_INTENT_ACTION);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
        // Check if the Spotify App is available to handle Authentication:
        Uri.Builder uriBuilder = new Uri.Builder();
      if (infos.isEmpty()) {
        //if (true) {
            // Spotify App not available. Fall back to browser.
            globalClass.dismissProgressBar();
            uriBuilder.scheme(ACCOUNTS_SCHEME)
                    .authority(ACCOUNTS_AUTHORITY)
                    .appendPath(ACCOUNTS_PATH)
                    .appendQueryParameter(KEY_CLIENT_ID.toLowerCase(), clientId)
                    .appendQueryParameter(KEY_REDIRECT_URI.toLowerCase(), redirectURI)
                    .appendQueryParameter(KEY_RESPONSE_TYPE.toLowerCase(), responseType)
                    .appendQueryParameter(KEY_REQUESTED_SCOPES.toLowerCase(), requestedScopes)
                    .appendQueryParameter("show_dialog", "true");

            if (!TextUtils.isEmpty(state)) {
                uriBuilder.appendQueryParameter(KEY_STATE, state);
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
            activity.startActivity(browserIntent);

        } else {
          globalClass.dismissProgressBar();
            // Spotify App is available. Provide the necessary parameters and start the activity.
            intent.putExtra(KEY_EXTRA_VERSION, PROTOCOL_VERSION);
            intent.putExtra(KEY_CLIENT_ID, clientId);
            intent.putExtra(KEY_REDIRECT_URI, redirectURI);
            intent.putExtra(KEY_RESPONSE_TYPE, responseType);
            intent.putExtra(KEY_REQUESTED_SCOPES, requestedScopesArray);
            intent.putExtra("show_dialog", "true");
            if (!TextUtils.isEmpty(state)) {
                intent.putExtra(KEY_STATE, state);
            }
          Log.e(TAG,"My URI "+intent.getAction());
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          builder.setMessage("Mighty would like to open Spotify")
                  //builder.setTitle("Alert")
                  .setCancelable(false)
                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          activity.startActivityForResult(intent, spotifyRequestCode);
                      }
                  }).setNegativeButton("No", null);
          AlertDialog alert = builder.create();
          alert.show();


        }

    }



    /**
     * Parses the response coming from Spotify App for authentication
     *
     * @param resultCode Activity result code returned from the Authentication
     * @param intent     Returned intent from the Authentication
     * @return parsed result
     */
    public static SpotifyAuthResult getResultForIntent(int resultCode, Intent intent) {
        // If the result code means there is an error
        if (resultCode == SPOTIFY_RESULT_ERROR) {
            String errorMessage;
            String state = null;
            // If intent is null, we have an invalid message.
            if (intent == null) {
                errorMessage = "Invalid message format";
            } else {
                errorMessage = intent.getStringExtra(EXTRA_ERROR);
                state = intent.getStringExtra(EXTRA_STATE);
            }
            // If an error message was not parsed, assign an Unkown Error tag.
            if (errorMessage == null) {
                errorMessage = "Unknown error";
            }

            return new SpotifyAuthResult(errorMessage, null, state);

        } else if (resultCode == Activity.RESULT_OK) { // If the result code means the response is ok.
            Bundle data = intent.getParcelableExtra(EXTRA_REPLY);

            // Handling if the data is null
            if (data == null) {
                return new SpotifyAuthResult("No data found.", null);
            } else {
                String code = data.getString(KEY_AUTHORIZATION_CODE);
                String state = data.getString(KEY_STATE);
                return new SpotifyAuthResult(null, code, state);

            }
        } else { // result code is not-understandable.
            return new SpotifyAuthResult("Couldn't interpret result code.", null);
        }
    }

    /**
     * Parses the response coming from spotify web page for authentication
     *
     * @param intent Returned intent from the Authentication
     * @return parsed result
     */
    public static SpotifyAuthResult getResultForBrowserIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            // Grabbing the authorization code.
            String code = uri.getQueryParameter(QUERY_PARAM_AUTHORIZATION_CODE);
            String state = uri.getQueryParameter(QUERY_PARAM_STATE);
            String errorMessage = null;
            if (code == null) {
                errorMessage = "There is no result code returned.";
            }
            return new SpotifyAuthResult(errorMessage, code, state);
        }
        return new SpotifyAuthResult("Result not found.", null);
    }

    /**
     * Class for defining an authentication result.
     */
    public static class SpotifyAuthResult {

        /**
         * The state optionally passed to the auth code request
         */
        @Nullable
       public final String state;

        /**
         * The error message that's captured.
         * null if there is no error.
         */
        @Nullable
       public final String errorMessage;

        /**
         * authorization code
         */
        @Nullable
     public final String authCode;

        SpotifyAuthResult(@Nullable String errorMessage, @Nullable String authCode) {
            this.errorMessage = errorMessage;
            this.authCode = authCode;
            this.state = null;
        }

        SpotifyAuthResult(@Nullable String errorMessage, @Nullable String authCode, @Nullable String state) {
            this.errorMessage = errorMessage;
            this.authCode = authCode;
            this.state = state;
        }
    }


}
