package mightyaudio.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import mightyaudio.Model.Playlist;
import mighty.audio.R;
import mightyaudio.core.GlobalClass;
import mightyaudio.core.MusicMightyDeletePlaylist;

import static android.view.View.GONE;

public class MusicMightyAdapter extends BaseSwipeAdapter {
    private static final String TAG = MusicMightyAdapter.class.getSimpleName();
    private ArrayList<Playlist> Play_list;
    private ViewHolder viewHolder;
    private GlobalClass globalClass;
    private Activity activity;
    private Typeface custom_font_light;
    private Dialog dialog;
    private MusicMightyDeletePlaylist musicMightyDeletePlaylist;

    public MusicMightyAdapter(Activity activity, ArrayList<Playlist> play_list) {
        super();
        Play_list = play_list;
        this.activity = activity;
        musicMightyDeletePlaylist = (MusicMightyDeletePlaylist) activity;
        globalClass = GlobalClass.getInstance();
        custom_font_light = Typeface.createFromAsset(activity.getAssets(), "serenity-light.ttf");
        Log.e(TAG," playlist size"+Play_list.size());
    }

    @Override
    public int getCount() {
        return Play_list.size();
    }

    @Override
    public Object getItem(int i) {
        return Play_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        Play_list.clear();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.list_swipe_layout;
    }

    @Override
    public View generateView(int i, ViewGroup parent) {
        return View.inflate(activity, R.layout.music_playlist,null);
    }

    @Override
    public void fillValues(final int i, View view) {
        viewHolder = new ViewHolder();
        viewHolder.play_list_deviceName = (TextView) view.findViewById(R.id.play_list_name);
        viewHolder.play_list_track = (TextView) view.findViewById(R.id.play_list_track);
        viewHolder.play_list_track.setTypeface(custom_font_light);
        viewHolder.play_list_deviceName.setTypeface(custom_font_light);
        viewHolder.play_list_blue_tick = (ImageView) view.findViewById(R.id.play_list_blue_tick);
        viewHolder.play_list_grey_tick = (ImageView) view.findViewById(R.id.play_list_grey_tick);
        viewHolder.bt_delete = (ImageView) view.findViewById(R.id.play_list_delete);
        viewHolder.swipeLayout = (SwipeLayout) view.findViewById(R.id.list_swipe_layout);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.play_list_grey_tick.setVisibility(GONE);
        viewHolder.swipeLayout.close(true);

        String List_Name = Play_list.get(i).getName();
        if (List_Name != null && List_Name.length() > 0) {
            byte[] byteText = Play_list.get(i).getName().getBytes(Charset.forName("UTF-8"));
            try {
                String originalString= new String(byteText , "UTF-8");
                viewHolder.play_list_deviceName.setText(originalString);// + "(" + Play_list.get(i).getTracks_count() + ")");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(isInteger(Play_list.get(i).getTracks_count())){
                viewHolder.swipeLayout.setSwipeEnabled(true);
                if(Integer.parseInt(Play_list.get(i).getTracks_count()) == 0 && Play_list.get(i).getOffline() != 1 && globalClass.syncing_status )
                    viewHolder.play_list_track.setText("Syncing");
                else if(Integer.parseInt(Play_list.get(i).getTracks_count()) == 1)
                    viewHolder.play_list_track.setText(Play_list.get(i).getTracks_count() + " song");
                else viewHolder.play_list_track.setText(Play_list.get(i).getTracks_count() + " songs");
            }else{
                viewHolder.swipeLayout.setSwipeEnabled(false);
                viewHolder.play_list_track.setText(Play_list.get(i).getTracks_count());
            }
        } else {
            viewHolder.play_list_deviceName.setText("Unknown playlist");
        }

        int tick_indicator =  Play_list.get(i).getOffline();
        Log.e(TAG,"tick_indicate "+ Play_list.get(i).getOffline()+" "+i);

        if (tick_indicator == 1 ) {
            viewHolder.play_list_blue_tick.setVisibility(View.VISIBLE);
            viewHolder.play_list_grey_tick.setVisibility(View.GONE);
        } else {
            viewHolder.play_list_blue_tick.setVisibility(View.GONE);
            viewHolder.play_list_grey_tick.setVisibility(View.VISIBLE);
        }

        viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Playlist play_delete = Play_list.get(i);
                globalClass.delete_playlist.add(play_delete);
                prgrssDailogDelete();
                musicMightyDeletePlaylist.deletePlayList(play_delete);
                globalClass.mighty_playlist.remove(play_delete.getUri());
                if(globalClass.arrayPlayList.get(play_delete.getUri()) != null)
                    globalClass.arrayPlayList.get(play_delete.getUri()).setOffline(0);
                notifyDataSetChanged();
            }
        });
    }


    public boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }

    public class ViewHolder {
        TextView play_list_deviceName,play_list_track;
        ImageView play_list_blue_tick;
        ImageView play_list_grey_tick;
        ImageView bt_delete;
        SwipeLayout swipeLayout;
    }

    private void prgrssDailogDelete(){
        Log.e(TAG,"Prgressbar clicked");
        if(dialog != null){
            if(dialog.isShowing())
                return;
        }
        dialog = new Dialog(activity);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog != null){
                    dialog.cancel();
                    dialog.dismiss();
                }
            }
        },4000);
    }
}
