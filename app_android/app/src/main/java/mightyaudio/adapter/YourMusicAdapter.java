package mightyaudio.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;
import mighty.audio.R;
import mightyaudio.Model.Playlist;
import mightyaudio.TCP.Constants;
import mightyaudio.core.GlobalClass;


public class YourMusicAdapter extends BaseAdapter {
    private static final String TAG = YourMusicAdapter.class.getSimpleName();
    GlobalClass globalClass = GlobalClass.getInstance();
    private Activity context;
    public int Total_track;
    public float Track_size;
    public float consumedMemory;
    private ArrayList<Playlist> data= new ArrayList<Playlist>();
    private ArrayList<Playlist> filterData =new ArrayList<Playlist>();
    public static boolean connection;
    private ImageLoader imageLoader;
    private ViewHolder item;
    private ProgressDialog progressDialog;
    private String autofil_status;
    private MaterialDialog  mMaterialDialog;
    private View promptView ;
    private TextView header_txt;
    private TextView message_txt;
    private Dialog dialog;


    public interface MusicFragmentInterface{
        public void PlaylistSizePink(float track_size);
    }

    public interface BrowseFragmentCommunication {
        public void alertSDisplay(String msg,String tittle);
        public void playListSizeInfo(float Track_size);
    }
    private MusicFragmentInterface musicFragmentInterface;

    private BrowseFragmentCommunication browseFragmentCommunication;
    public static HashMap<String,Playlist> selectedlist =new HashMap<String,Playlist>();
    public YourMusicAdapter(Activity context, ArrayList<Playlist> datalist, BrowseFragmentCommunication callback){
        this.context= context;
        browseFragmentCommunication =  callback;
        this.data.addAll(datalist);
        if(!filterData.isEmpty())
            filterData.clear();
        filterData.addAll(data);
        Log.e(TAG,"data size "+data.size()+" "+datalist.size());
        imageLoader = globalClass.getImageLoader();
        musicFragmentInterface = (MusicFragmentInterface)context;
    }

    public void addPlayList(ArrayList<Playlist> playlists ) {
        data.addAll(playlists);
        filterData.addAll(playlists);
        Log.e(TAG,"size of playlist "+playlists.size()+" "+filterData.size());
    }
    public void clearAdapter(){
        data.clear();
        filterData.clear();
    }

    private class ViewHolder
    {
        Typeface custom_font_light;
        FrameLayout img_btn_plus;
        NetworkImageView music_image;
        ImageView img_blue_tick;
        TextView txt_play_name;
        TextView text_track_detail;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View vi, ViewGroup parent) {
        if (vi == null) {
            vi = View.inflate(context, R.layout.list_item_spotify,null);
            item = new ViewHolder();
            item.custom_font_light = Typeface.createFromAsset(context.getAssets(), "serenity-light.ttf");
            item.img_btn_plus = (FrameLayout) vi.findViewById(R.id.img_btn_plus);
            item.music_image = (NetworkImageView) vi.findViewById(R.id.music_image);
            item.img_blue_tick = (ImageView) vi.findViewById(R.id.img_blue_tick);
            item.txt_play_name = (TextView) vi.findViewById(R.id.entity_title);
            item.text_track_detail = (TextView) vi.findViewById(R.id.text_track_detail);
            vi.setTag(item);
        }else {
            item = (ViewHolder) vi.getTag();
            item.music_image.setImageUrl(null, imageLoader);
            item.img_btn_plus.setVisibility(View.INVISIBLE);
            item.img_blue_tick.setVisibility(View.INVISIBLE);
        }
        item.txt_play_name.setTypeface(item.custom_font_light);
        item.text_track_detail.setTypeface(item.custom_font_light);

        //handling text based on count (song or Songs)
        //item.txt_play_name.setText(data.get(position).getName());
        byte[] byteText = data.get(position).getName().getBytes(Charset.forName("UTF-8"));
        try {
            String originalString= new String(byteText , "UTF-8");
            item.txt_play_name.setText(originalString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(Integer.parseInt(data.get(position).getTracks_count())  <= 1){
            if(data.get(position).getSpotifFollowup() == null)
                item.text_track_detail.setText(data.get(position).getTracks_count()+" song");
            else item.text_track_detail.setText("by "+data.get(position).getSpotifFollowup()+" ∙ "+data.get(position).getTracks_count()+" songs");
        }
        else {
            if(data.get(position).getSpotifFollowup() == null)
                item.text_track_detail.setText(data.get(position).getTracks_count()+" songs");
            else item.text_track_detail.setText("by "+data.get(position).getSpotifFollowup()+" ∙ "+data.get(position).getTracks_count()+" songs");
        }
        //For Display the tick base on mighty Playlist
        Log.e(TAG,"getOffline Status "+data.get(position).getOffline()+" "+position+" "+connection);
        if(connection){
            if(data.get(position).getOffline() == Constants.DL_NONE )
            item.img_btn_plus.setVisibility(View.VISIBLE);
            else if(data.get(position).getOffline() == Constants.SELECTED_PLAYLIST_STATUS){
                item.img_btn_plus.setVisibility(View.INVISIBLE);
                item.img_blue_tick.setVisibility(View.VISIBLE);
            }else{
                item.img_btn_plus.setVisibility(View.INVISIBLE);
                item.img_blue_tick.setVisibility(View.INVISIBLE);
            }
        }else if(!connection){
            item.img_btn_plus.setVisibility(View.INVISIBLE);
            item.img_blue_tick.setVisibility(View.INVISIBLE);
        }

        //Loding Image
        if(data.get(position).getPlayListUrl() != null){
            item.music_image.setImageUrl(data.get(position).getPlayListUrl(),imageLoader);
        }else{
            item.music_image.setBackgroundResource(R.drawable.empty_playlist_image);
        }

        ///final ViewHolder finalItem = item;
        item.img_btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Storage_freeeee "+ Track_size +" "+consumedMemory +" "+globalClass.memory.getStorage_total());
                if(!globalClass.syncing_status) {
                    if(Integer.parseInt(data.get(position).getTracks_count())  != 0){

                        Total_track = 0;
                        Track_size = 0;
                        data.get(position).setOffline(Constants.SELECTED_PLAYLIST_STATUS);
                        Log.e(TAG,"postion value "+position+" "+data.get(position)+" "+data.get(position).getOffline()+" "+position);
                        selectedlist.put(data.get(position).getUri(), data.get(position));
                        for (Playlist value : selectedlist.values()) {
                            Total_track = Total_track + Integer.parseInt(value.getTracks_count()) ;
                            Track_size = Total_track * globalClass.global_bit_rate.downloadQuality(globalClass.global_bit_rate.getBitRateMode());   //2.88f;
                        }
                        if ((((consumedMemory + Track_size) / globalClass.memory.getStorage_total()) * 100) < 90.0) {
                            SelectedPlayList();
                        } else {
                            dialogMemrypartialFull(context.getString(R.string.not_enough_memory_tittle),context.getString(R.string.not_enough_memory_message),data.get(position));
                        }
                    }else globalClass.alertDailogSingleText(context.getString(R.string.playlist_have_no_track),context);
                }else browseFragmentCommunication.alertSDisplay(context.getString(R.string.playlist_selectwhile_sync_meaage),context.getString(R.string.playlist_sync_tittle));
            }
        });
        //final ViewHolder finalItem1 = item;
        item. img_blue_tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here app is crashing we need test
                    selectedlist.remove(data.get(position).getUri()).setOffline(Constants.DL_NONE);
                    Total_track = Total_track - Integer.parseInt(data.get(position).getTracks_count());
                    float remove_size = (float) (Integer.parseInt(data.get(position).getTracks_count()) * globalClass.global_bit_rate.downloadQuality(globalClass.global_bit_rate.getBitRateMode()));
                    Track_size = Track_size - remove_size;
                    browseFragmentCommunication.playListSizeInfo(Track_size);
                    musicFragmentInterface.PlaylistSizePink(Track_size);
                    notifyDataSetChanged();
            }
        });
        return vi;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        data.clear();
        if (charText.length() == 0) {
            data.addAll(filterData);
        }
        else{
            for (Playlist wp : filterData)
            {
                if (wp != null && wp.getName() != null && wp.getName().toLowerCase(Locale.getDefault()).contains(charText))
                    data.add(wp);
            }
        }
        notifyDataSetChanged();
    }

    public void autofill(String status){
        autofil_status =status;
        if (autofil_status.equals("selected"))
            prgrssDailogShow();
        Thread thread = new Thread(null, loadMoreListItems);
        thread.start();
    }

    private Runnable loadMoreListItems = new Runnable() {
        @Override
        public void run() {
            if (autofil_status.equals("selected")) {
                consumedMemory = globalClass.memory.getStorage_total() - globalClass.memory.getStorage_free();
                ArrayList<Playlist> sortingPlalist = new ArrayList<Playlist>();
                sortingPlalist.addAll(data);
                Collections.sort(sortingPlalist, Playlist.TracksComparator);

                for(int j = 0;j<sortingPlalist.size();j++){
                    Log.e(TAG,"Sorting Order plalist "+sortingPlalist.get(j).getTracks_count());

                    int i = 0;
                    while (i < data.size()) {
                        if(sortingPlalist.get(j).getUri() == data.get(i).getUri()){
                            Log.e(TAG," Sorting Order Inside if condition While loop break ");
                            if ((((consumedMemory + Track_size) / globalClass.memory.getStorage_total()) * 100) < 90.0) {
                                if (data.get(i).getOffline() == 1) {
                                    Log.e(TAG, "if condition_blue");
                                } else if (data.get(i).getOffline() == 3) {
                                    Log.e(TAG, "if condition_grey");
                                }else if(Integer.parseInt(data.get(i).getTracks_count()) == 0) {
                                    Log.e(TAG,"Ignoring plalist zero track");
                                }else
                                {
                                    Total_track = Total_track + Integer.parseInt(data.get(i).getTracks_count());
                                    float _size = Total_track * globalClass.global_bit_rate.downloadQuality(globalClass.global_bit_rate.getBitRateMode());   //2.88f;
                                    if ((((consumedMemory + _size) / globalClass.memory.getStorage_total()) * 100) < 90.0) {
                                        data.get(i).setOffline(Constants.SELECTED_PLAYLIST_STATUS); //Changing Status Selected Playlist
                                        selectedlist.put(data.get(i).getUri(), data.get(i));
                                        Track_size = _size;
                                    }else Log.e(TAG," else_condition_size");
                                }
                            }
                            break;
                        }
                        Log.e(TAG," Sorting Order Inside While loop "+ i +" "+j);
                        i++;
                    }
                }
                Total_track = 0;
                for (Playlist value : selectedlist.values()) {
                    Total_track = Total_track + Integer.parseInt(value.getTracks_count());
                    Track_size = Total_track * globalClass.global_bit_rate.downloadQuality(globalClass.global_bit_rate.getBitRateMode());
                }
                Log.e(TAG, "size of selected " + selectedlist.size() +"tracks_size "+ Track_size+ " tracks "+Total_track);
            }else{
                if(!selectedlist.isEmpty()){
                    for(Playlist playlist_obj : selectedlist.values()){
                        selectedlist.get(playlist_obj.getUri()).setOffline(Constants.DL_NONE);
                    }
                }
                selectedlist.clear();
                Track_size = 0.0f;
                Total_track = 0;

            }
            context.runOnUiThread(returnRes);
        }
    };
    //check it is reqired or not check thread
    private Runnable returnRes = new Runnable() {
        @Override
        public void run() {
            browseFragmentCommunication.playListSizeInfo(Track_size);
            musicFragmentInterface.PlaylistSizePink(Track_size);
            notifyDataSetChanged();
            if (autofil_status.equals("selected"))
            progrssDailogCancel();
        }

    };

    private void SelectedPlayList(){
        musicFragmentInterface.PlaylistSizePink(Track_size);
        browseFragmentCommunication.playListSizeInfo(Track_size);
        notifyDataSetChanged();
    }


    public void dialogMemrypartialFull(String tittle, String myMsg, final Playlist playlist){
        Log.e(TAG,"mighty Registration bradcast alert");
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        promptView = layoutInflater.inflate(R.layout.materialdesin_custom_dailog, null);
        header_txt=(TextView)promptView.findViewById(R.id.text_header);
        message_txt=(TextView)promptView.findViewById(R.id.text_message);
        header_txt.setText(tittle);
        message_txt.setText(myMsg);
        mMaterialDialog = new MaterialDialog(context)
                .setView(promptView)
                .setPositiveButton(context.getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedPlayList();
                        mMaterialDialog.dismiss();

                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playlist.setOffline(Constants.DL_NONE);
                        selectedlist.remove(playlist.getUri());
                        Total_track = 0;
                        Track_size = 0;
                        for (Playlist value : selectedlist.values()) {
                            Total_track = Total_track + Integer.parseInt(value.getTracks_count());
                            Track_size = Total_track * globalClass.global_bit_rate.downloadQuality(globalClass.global_bit_rate.getBitRateMode());   //2.88f;
                        }
                        SelectedPlayList();
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    private void prgrssDailogShow(){
        Log.e(TAG,"Prgressbar clicked_autofill");
        if(dialog != null){
            if(dialog.isShowing())
                return;
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.custom_pogrssbar);
        dialog.setCancelable(false);
        dialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show ();
/*        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
    }
    private void progrssDailogCancel(){
        if(dialog != null){
            dialog.cancel();
            dialog.dismiss();
        }
    }

}
