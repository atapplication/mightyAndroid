package mightyaudio.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mighty.audio.R;
import mightyaudio.Model.BT_Configuration;
import mightyaudio.Model.Headset_History;
import mightyaudio.TCP.Constants;
import mightyaudio.core.BtDeviceComunication;
import mightyaudio.core.GlobalClass;


public class BtDeviceAdapter extends BaseSwipeAdapter {
    private final String TAG = BtDeviceAdapter.class.getSimpleName();
    private List<Headset_History> BtDevices;
    private GlobalClass globalClass;
    private ViewHolders bt_viewHolder;
    private Activity context;
    private  Typeface custom_font;
    private BtDeviceComunication btDeviceComunication;
    private static Timer bletimer= new Timer();
    private static TimerTask progresstimerTask;
    Handler handler_timer = new Handler();

    public BtDeviceAdapter(Activity activity,List<Headset_History> mylist) {
        super();
        context = activity;
        globalClass = GlobalClass.getInstance();
        this.BtDevices=mylist;
        custom_font = Typeface.createFromAsset(context.getAssets(), "serenity-light.ttf");
        btDeviceComunication = (BtDeviceComunication)activity;
    }

    public void clear() {
        BtDevices.clear();
    }

    @Override
    public int getCount() {
        return BtDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return BtDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.bt_swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return View.inflate(context, R.layout.btscan_listitem_device,null);
    }

    @Override
    public void fillValues(final int position, View bt_view) {
        bt_viewHolder = new ViewHolders();
        bt_viewHolder.bt_deviceName = (TextView) bt_view.findViewById(R.id.bt_name);
        bt_viewHolder.bt_img_blue_tick = (ImageView) bt_view.findViewById(R.id.img_blue_tick);
        bt_viewHolder.bt_img_pair = (ImageView)bt_view.findViewById(R.id.img_pair);
        bt_viewHolder.btn_unpair = (ImageView)bt_view.findViewById(R.id.bt_disconnect);
        bt_viewHolder.text_arrow = (TextView)bt_view.findViewById(R.id.text_arrow);
        bt_viewHolder.bt_img_blue_tick.setVisibility(View.GONE);
        bt_viewHolder.bt_img_pair.setVisibility(View.GONE);
        bt_viewHolder.bt_img_plus = (ImageView) bt_view.findViewById(R.id.bt_img_plus);
        bt_viewHolder.img_btn_fram =(FrameLayout) bt_view.findViewById(R.id.img_btn_fram);
        bt_viewHolder.prog_fram =(FrameLayout) bt_view.findViewById(R.id.prog_fram);
        bt_viewHolder.prog_fram.setVisibility(View.GONE);
        bt_viewHolder.bt_progressBar = (ProgressBar) bt_view.findViewById(R.id.bt_progressBar3);
        bt_viewHolder.bt_swipeLayout = (SwipeLayout) bt_view.findViewById(R.id.bt_swipe_layout);
        bt_viewHolder.bt_btnDelete = (ImageView) bt_view.findViewById(R.id.bt_delete);
        bt_viewHolder.bt_swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        bt_viewHolder.bt_swipeLayout.setSwipeEnabled(false);

        bt_viewHolder.bt_swipeLayout.close(true);
        //Setting device name
        bt_viewHolder.bt_deviceName.setTypeface(custom_font);
        bt_viewHolder.text_arrow.setTypeface(custom_font);
        String bt_deviceName = null;
        byte[] byteText = BtDevices.get(position).getName().getBytes(Charset.forName("UTF-8"));
        try {
            bt_deviceName = new String(byteText , "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bt_deviceName != null && bt_deviceName.length() > 0){
            bt_viewHolder.bt_deviceName.setText(bt_deviceName);// + "(" + Play_list.get(i).getTracks_count() + ")");
            Log.e(TAG,"Status "+BtDevices.get(position).getStatus()+" "+BtDevices.get(position).getName());
            switch(BtDevices.get(position).getStatus()){
                case Constants.None :
                    bt_viewHolder.bt_img_blue_tick.setVisibility(View.GONE);
                    bt_viewHolder.bt_img_pair.setVisibility(View.GONE);
                    bt_viewHolder.bt_swipeLayout.setSwipeEnabled(false);
                    bt_viewHolder.text_arrow .setVisibility(View.INVISIBLE);
                    bt_viewHolder.img_btn_fram.setVisibility(View.VISIBLE);
                    bt_viewHolder.bt_img_plus.setVisibility(View.VISIBLE);
                    bt_viewHolder.prog_fram.setVisibility(View.INVISIBLE);
                    Log.e(TAG,"if(BtDevices.get(i).isPlus_button_clickable()) "+ BtDevices.get(position).isPlus_button_clickable());
                    Log.e(TAG,"case 0 else");
                    break;
                case Constants.Pair :
                    bt_viewHolder.bt_img_blue_tick.setVisibility(View.INVISIBLE);
                    bt_viewHolder.bt_img_pair.setVisibility(View.VISIBLE);
                    bt_viewHolder.bt_swipeLayout.setSwipeEnabled(true);
                    bt_viewHolder.bt_btnDelete.setVisibility(View.GONE);
                    bt_viewHolder.btn_unpair.setVisibility(View.VISIBLE);
                    bt_viewHolder.img_btn_fram.setVisibility(View.VISIBLE);
                    bt_viewHolder.bt_img_plus.setVisibility(View.VISIBLE);
                    bt_viewHolder.text_arrow .setVisibility(View.INVISIBLE);
                    bt_viewHolder.prog_fram.setVisibility(View.INVISIBLE);
                    Log.e(TAG,"case 1 else");
                    break;
                case Constants.Connect :
                    bt_viewHolder.bt_img_blue_tick.setVisibility(View.VISIBLE);
                    bt_viewHolder.bt_img_pair.setVisibility(View.INVISIBLE);
                    bt_viewHolder.bt_swipeLayout.setSwipeEnabled(true);
                    bt_viewHolder.bt_btnDelete.setVisibility(View.VISIBLE);
                    bt_viewHolder.btn_unpair.setVisibility(View.INVISIBLE);
                    bt_viewHolder.bt_img_plus.setVisibility(View.INVISIBLE);
                    bt_viewHolder.img_btn_fram.setVisibility(View.GONE);
                    bt_viewHolder.text_arrow.setVisibility(View.VISIBLE);
                    bt_viewHolder.prog_fram.setVisibility(View.INVISIBLE);
                    Log.e(TAG,"case 3 else");
            }
        }
        if( BtDevices.get(position).isPlus_button_clickable()){
            bt_viewHolder.img_btn_fram.setVisibility(View.INVISIBLE);
            bt_viewHolder.bt_img_plus.setVisibility(View.INVISIBLE);
            bt_viewHolder.prog_fram.setVisibility(View.VISIBLE);
        }

        bt_viewHolder.bt_img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"BT_DEVICES is Click" + BtDevices.get(position).isPlus_button_clickable()+" "+position+" "+ progressGoingOn());
                if(!progressGoingOn()){
                    if(BtDevices.get(0).getStatus() == 3){
                        BtDevices.get(0).setStatus(1);
                    }
                    BtDevices.get(position).setPlus_button_clickable(true);
                    if (BtDevices.get(position).getStatus()==1){
                        BT_Configuration sel_bt_headphone_model = new BT_Configuration(BtDevices.get(position).getName(), BtDevices.get(position).getMac_id(), Constants.Connect);
                        globalClass.sel_bt_headphone_model = sel_bt_headphone_model;
                        btDeviceComunication.btDeviceRequest();
                        Log.e(TAG,"plus4 " +BtDevices.get(position).getName()+ "Status4 "+BtDevices.get(position).getStatus());
                    }else{
                        BT_Configuration sel_bt_headphone_model = new BT_Configuration(BtDevices.get(position).getName(), BtDevices.get(position).getMac_id(), Constants.Pair);
                        globalClass.sel_bt_headphone_model = sel_bt_headphone_model;
                        btDeviceComunication.btDeviceRequest();
                        Log.e(TAG,"plus5 " +BtDevices.get(position).getName()+ "Status5 "+BtDevices.get(position).getStatus());
                    }
                    startTimer();
                    notifyDataSetChanged();
                }else globalClass.toastDisplay("Please wait for the current BT \n pairing request to complete");

            }
        });
        bt_viewHolder.bt_btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Headset_History bt_devices = BtDevices.get(position);
                if (bt_devices == null) return;
                System.out.println("BT_deviceName" + BtDevices.get(position).getName());
                BT_Configuration sel_bt_headphone_model = new BT_Configuration(BtDevices.get(position).getName(), BtDevices.get(position).getMac_id(), Constants.Disconnect);
                globalClass.sel_bt_headphone_model = sel_bt_headphone_model;
                btDeviceComunication.btDeviceRequest();
                bt_viewHolder.bt_img_blue_tick.setVisibility(View.INVISIBLE);
                bt_viewHolder.bt_img_pair.setVisibility(View.VISIBLE);
                bt_viewHolder.bt_swipeLayout.setSwipeEnabled(true);
                bt_viewHolder.bt_btnDelete.setVisibility(View.GONE);
                bt_viewHolder.btn_unpair.setVisibility(View.VISIBLE);
                bt_viewHolder.img_btn_fram.setVisibility(View.VISIBLE);
                bt_viewHolder.bt_img_plus.setVisibility(View.VISIBLE);
                bt_viewHolder.text_arrow .setVisibility(View.INVISIBLE);
                bt_viewHolder.prog_fram.setVisibility(View.INVISIBLE);
                BtDevices.get(position).setStatus(1);
                notifyDataSetChanged();

            }
        });

        bt_viewHolder.btn_unpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Headset_History bt_devices = BtDevices.get(position);
                if (bt_devices == null) return;
                System.out.println("BT_deviceName" + BtDevices.get(position).getName());

                BT_Configuration sel_bt_headphone_model = new BT_Configuration(BtDevices.get(position).getName(), BtDevices.get(position).getMac_id(), Constants.Unpair);
                globalClass.sel_bt_headphone_model = sel_bt_headphone_model;
                btDeviceComunication.btDeviceRequest();
                Log.e(TAG,"In side 1"+BtDevices.get(position).getStatus()+" "+BtDevices.get(position).getName());
                GlobalClass.hasmap_bt_scan_headset_lists.remove(BtDevices.get(position).getMac_id());
                BtDevices.remove(position);
                globalClass.scan_sel_bt_headphone_model= null;
                notifyDataSetChanged();
            }
        });
    }


    public void startTimer() {
        Log.e(TAG,"Timer_called start");
        initializeTimerTask();
        if (bletimer ==null)
            bletimer = new Timer();
        bletimer.schedule(progresstimerTask, 15000, 15000);
    }

    public void initializeTimerTask() {
        progresstimerTask = new TimerTask() {
            public void run() {
                handler_timer.post(new Runnable() {
                    public void run() {
                        Log.e(TAG,"Timer_called ");
                        if(globalClass.mighty_ble_device != null){
                            for(int i =0;i<BtDevices.size();i++){
                                BtDevices.get(i).setPlus_button_clickable(false);
                            }
                            notifyDataSetChanged();
                            stoptimertask();
                        }
                    }
                });
            }
        };
    }

    public void stoptimertask() {
        Log.e(TAG,"Timer_called stop");
        //stop the bletimer, if it's not already null
        if (bletimer != null) {
            bletimer.cancel();
            // bletimer.purge();
            bletimer = null;
            if (progresstimerTask != null){
                progresstimerTask.cancel();
            }
        }
    }

    private boolean progressGoingOn(){
        boolean goinOn= true;
        for(int i =0;i<BtDevices.size();i++){
            if(BtDevices.get(i).isPlus_button_clickable()){
                Log.e(TAG,"BT_DEVICES is Click Yes");
                return goinOn;
            }
            Log.e(TAG,"BT_DEVICES is Click outside");
        }
        return false;
    }

    public class ViewHolders {
        TextView bt_deviceName;
        ImageView btn_unpair;
        ImageView bt_btnDelete;
        FrameLayout img_btn_fram;
        FrameLayout prog_fram;
        ImageView bt_img_plus;
        ImageView bt_img_blue_tick;
        ImageView bt_img_pair;
        ProgressBar bt_progressBar;
        TextView text_arrow;
        SwipeLayout bt_swipeLayout;
    }
}
