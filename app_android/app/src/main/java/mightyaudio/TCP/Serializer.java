package mightyaudio.TCP;


import android.util.Base64;
import android.util.Log;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.MessageBuffer;

import java.io.IOException;
import java.util.ArrayList;

import mightyaudio.Model.App_Info;
import mightyaudio.Model.BT_Configuration;
import mightyaudio.Model.BT_Scan_List;
import mightyaudio.Model.Battery_Info;
import mightyaudio.Model.Bit_Rate;
import mightyaudio.Model.Device_Info;
import mightyaudio.Model.Download;
import mightyaudio.Model.Events;
import mightyaudio.Model.FirmWare_Download;
import mightyaudio.Model.FirmWare_Upgrade;
import mightyaudio.Model.Headset_History;
import mightyaudio.Model.Headset_Status;
import mightyaudio.Model.Memory;
import mightyaudio.Model.MightyLogin;
import mightyaudio.Model.Playlist;
import mightyaudio.Model.Power;
import mightyaudio.Model.SpotifyLogin;
import mightyaudio.Model.Voice_Over;
import mightyaudio.Model.WiFi_Configuration;
import mightyaudio.Model.WiFi_History;
import mightyaudio.Model.WiFi_Static_Network;
import mightyaudio.Model.Wifi_Status;
import mightyaudio.core.GlobalClass;



public class Serializer {

    private static final String TAG = Serializer.class.getCanonicalName();
    public static GlobalClass globalClass = GlobalClass.getInstance();

    public static byte[] Packer(MightyMessage mightyMessage) {

        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        byte[] packedArray = null;

        //Write Header

        Log.d("++ Packing Header start", "+++");
        try {
            packer.packArrayHeader(7);
            packer.packInt(mightyMessage.MessageType);
            packer.packInt(mightyMessage.MessageID);
            packer.packInt(mightyMessage.RequestID);
            packer.packInt(mightyMessage.ResponseID);
            packer.packInt(mightyMessage.More);
            packer.packInt(mightyMessage.Size);
            packer.packString("AndroiClient");

            Log.d("++ Packing Header end", "+++");
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (mightyMessage.MessageID) {
            case Constants.MSG_ID_DEVICEINFO_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_DEVICEINFO_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_DEVICEINFO_ID");
                        //Write Data
                        packer.packArrayHeader(4);
                        packer.packInt(globalClass.device_info.getDevice_ID());
                        packer.packString(globalClass.device_info.getDevice_Name());
                        packer.packString(globalClass.device_info.getSW_Version());
                        packer.packString(globalClass.device_info.getHW_Serial_Number());

                        packer.close();
                        Log.d("Mighty", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_WIFICONFIGURATION_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "WIFICONFIGURATION");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET)
                    {
                        Log.d("Inside SET-->>", "WIFICONFIGURATION");
                        //Write Data
                        packer.packArrayHeader(1);
                        packer.packArrayHeader(6);

                        WiFi_Configuration wiFi_configuration = globalClass.wiFi_configuration_global;

                        System.out.println(wiFi_configuration.getAP_Name());
                        System.out.println(wiFi_configuration.getSSID());
                        System.out.println(wiFi_configuration.getPasscode());
                        System.out.println(wiFi_configuration.getMode());
                        System.out.println(wiFi_configuration.getConnection_Status());
                        System.out.println(wiFi_configuration.getDef());

                        Log.d("WIFICONFIGURATION", "Packing WIFICONFIGURATION Data Start");

                        packer.packString(wiFi_configuration.getSSID());
                        packer.packString(wiFi_configuration.getAP_Name());
                        packer.packString(wiFi_configuration.getPasscode());
                        packer.packInt(wiFi_configuration.getMode());
                        packer.packInt(wiFi_configuration.getConnection_Status());
                        packer.packInt(wiFi_configuration.getDef());
                        packer.close();
                        //  packer.close();
                        Log.d("WIFICONFIGURATION", "Packing WIFICONFIGURATION Data Done");
                        packedArray = packer.toByteArray();

                        return packedArray;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;


            case Constants.MSG_ID_WIFI_STATIC_NETWORK_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "WIFI_STATIC_NETWORK_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "WIFI_STATIC_NETWORK_ID");
                        //Write Data
                        packer.packArrayHeader(4);

                        WiFi_Static_Network wiFi_static_network = new WiFi_Static_Network();
                        packer.packString(wiFi_static_network.IPAddress);
                        packer.packString(wiFi_static_network.DNSAddress);
                        packer.packString(wiFi_static_network.NetworkMask);
                        packer.packString(wiFi_static_network.GatewayAddress);
                        packer.close();
                        Log.d("WIFI_STATIC_NETWORK_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_WIFI_HISTORY_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_WIFI_HISTORY_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_WIFI_HISTORY_ID");
                        //Write Data
                        packer.packArrayHeader(3);

                        WiFi_History wiFi_history = new WiFi_History();

                        for (int i = 0; i < 5; i++) {
                            packer.packArrayHeader(2);
                            packer.packString(wiFi_history.SSID_LIST + i);
                            packer.packString(wiFi_history.AP_NAME_LIST + i);

                            System.out.println(wiFi_history.SSID_LIST + i);
                            System.out.println(wiFi_history.AP_NAME_LIST + i);

                        }
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_BT_CONFIGURATION_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_BT_CONFIGURATION_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_BT_CONFIGURATION_ID");

                        //Write Data
                        packer.packArrayHeader(1);
                        packer.packArrayHeader(3);

                        BT_Configuration bt_configuration = globalClass.sel_bt_headphone_model;
                        packer.packString(bt_configuration.name);
                        packer.packString(bt_configuration.mac_id);
                        packer.packInt(bt_configuration.Status);
                        packer.close();
                        //packer.close();
                        Log.d("BT_CONFIGURATION_ID", "Packing Data Done");
                        Log.d(String.valueOf(bt_configuration.name), "bt_set name");
                        Log.d(String.valueOf(bt_configuration.Status), "bt_set status");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_BATTERY_INFO_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_BATTERY_INFO_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_BATTERY_INFO_ID");
                        //Write Data
                        packer.packArrayHeader(2);

                        Battery_Info battery_info = new Battery_Info();
                        packer.packInt(battery_info.Capacity);
                        packer.packInt(battery_info.AvailablePercentage);

                        packer.close();
                        Log.d("MSG_ID_BATTERY_INFO_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;


            case Constants.MSG_ID_PLAYLIST_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_PLAYLIST_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_PLAYLIST_ID");
                        //Write Data
                        packer.packArrayHeader(globalClass.delete_playlist.size());
                        Log.e(TAG,"Download_playlist ");
                        for (int i = 0; i < globalClass.delete_playlist.size(); i++) {
                            Log.e(TAG,"Delete_playlist "+i);
                            packer.packArrayHeader(7);

                            packer.packString(globalClass.delete_playlist.get(i).getName());
                            packer.packString(globalClass.delete_playlist.get(i).getUri());
                            packer.packString(globalClass.delete_playlist.get(i).getSnapshotId());
                            packer.packInt(Integer.parseInt(globalClass.delete_playlist.get(i).getTracks_count()));
                            packer.packInt(globalClass.delete_playlist.get(i).getOffline());
                            packer.packFloat(globalClass.delete_playlist.get(i).getDownloadProgress());
                            packer.packInt(globalClass.delete_playlist.get(i).getPublic_playlist());
                        }
                        packer.close();
                        packedArray = packer.toByteArray();
                        Log.d("MSG_ID_PLAYLIST_ID", "Packing Data Done");

                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_DOWNLOAD_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_DOWNLOAD_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET)
                    {
                        Log.d("Inside SET-->>", "MSG_ID_DOWNLOAD_ID SET" +globalClass.update_playlist);
                        Playlist spotify_playlist_model ;

                        if (globalClass.update_playlist) {
                            ArrayList<Playlist> update_play = globalClass.update_playlist_array;
                            packer.packArrayHeader(update_play.size());
                            for (int i = 0; i < update_play.size(); i++) {
                                packer.packArrayHeader(8);
                                spotify_playlist_model = update_play.get(i);

                                packer.packString(spotify_playlist_model.getName());
                                packer.packString(spotify_playlist_model.getUri());
                                packer.packFloat(1.0f);
                                packer.packFloat(1.0f);
                                packer.packInt(0);
                                packer.packInt(Integer.parseInt(spotify_playlist_model.getTracks_count()));
                                packer.packInt(5);
                                packer.packString(spotify_playlist_model.getSnapshotId());

                                Log.e(TAG, "Update_playlist ");
                                Log.e(TAG, "Update_playlist_name " + spotify_playlist_model.getName());
                                Log.e(TAG, "Update_playlist_uri " + spotify_playlist_model.getUri());
                                Log.e(TAG, "Update_playlist_tracks " + spotify_playlist_model.getTracks_count());
                                Log.e(TAG, "Update_playlist_hashkey " + spotify_playlist_model.getSnapshotId());
                            }
                            globalClass.update_playlist = false;
                            if(!globalClass.update_playlist_array.isEmpty())
                                globalClass.update_playlist_array.clear();
                        }else {
                            //ArrayList<Playlist> spotify_play = globalClass.spotify_playlist_obj_arraylist_selected;
                            packer.packArrayHeader(globalClass.spotify_playlist_obj_arraylist_selected.size());//globalClass.spotify_playlist_obj_arraylist_selected.size());
                            Log.e(TAG,"Download_playlist "+ globalClass.spotify_playlist_obj_arraylist_selected.size());
                            for (int i = 0; i < globalClass.spotify_playlist_obj_arraylist_selected.size(); i++) {
                                //spotify_playlist_model = spotify_play.get(i);

                                packer.packArrayHeader(8);
                                packer.packString(globalClass.spotify_playlist_obj_arraylist_selected.get(i).getName());
                                packer.packString(globalClass.spotify_playlist_obj_arraylist_selected.get(i).getUri());
                                packer.packFloat(1.0f);
                                packer.packFloat(1.0f);
                                packer.packInt(0);
                                packer.packInt(Integer.parseInt(globalClass.spotify_playlist_obj_arraylist_selected.get(i).getTracks_count()));
                                packer.packInt(0);
                                packer.packString(globalClass.spotify_playlist_obj_arraylist_selected.get(i).getSnapshotId());
                                packer.close();



                                Log.e(TAG,"Download_name "+globalClass.spotify_playlist_obj_arraylist_selected.get(i).getName());
                                Log.e(TAG,"Download_uri "+globalClass.spotify_playlist_obj_arraylist_selected.get(i).getUri());
                                Log.e(TAG,"Download_tracks "+globalClass.spotify_playlist_obj_arraylist_selected.get(i).getTracks_count());
                                Log.e(TAG,"Download_hash_key "+globalClass.spotify_playlist_obj_arraylist_selected.get(i).getSnapshotId());

                            }
                        }
                        packer.close();
                        Log.d("MSG_ID_DOWNLOAD_ID", "Packing Data Done "+ packer.toByteArray().toString());
                        packedArray = packer.toByteArray();

                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_VOICE_OVER_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_VOICE_OVER_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_VOICE_OVER_ID");
                        //Write Data
                        packer.packArrayHeader(4);

                        Voice_Over voice_over = new Voice_Over();
                        packer.packFloat(voice_over.speech_rate);
                        packer.packString(voice_over.voice_name);
                        packer.packString(voice_over.language);
                        packer.packInt(voice_over.default_voice);
                        packer.close();
                        Log.d("MSG_ID_VOICE_OVER_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_POWER_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_POWER_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_POWER_ID");
                        //Write Data
                        packer.packArrayHeader(2);

                        Power power = new Power();
                        packer.packInt(power.power_mode);
                        packer.packInt(power.sleep_timeout);

                        packer.close();
                        Log.d("MSG_ID_POWER_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;


            case Constants.MSG_ID_MEMORY_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_MEMORY_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_MEMORY_ID");
                        //Write Data
                        packer.packArrayHeader(4);

                        Memory memory = new Memory();
                        packer.packFloat(memory.Ram_total);
                        packer.packFloat(memory.Storage_total);
                        packer.packFloat(memory.Ram_free);
                        packer.packFloat(memory.Storage_free);

                        packer.close();
                        Log.d("MSG_ID_MEMORY_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_HEADSET_STATUS_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_HEADSET_STATUS_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_HEADSET_STATUS_ID");
                        //Write Data
                        packer.packArrayHeader(2);

                        Headset_Status headset_status = new Headset_Status();

                        packer.packInt(headset_status.HeadsetTypeConnected);
                        packer.packInt(headset_status.HeadsetConnectionStatus);


                        packer.close();
                        Log.d("HEADSET_STATUS_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_HEADSET_HISTORY_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_HEADSET_HISTORY_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_HEADSET_HISTORY_ID");

                        //Write Data
                        packer.packArrayHeader(3);

                        Headset_History headset_history = new Headset_History();

                        for (int i = 0; i < 5; i++) {
                            packer.packArrayHeader(2);
                            packer.packString(headset_history.name + i);
                            packer.packString(headset_history.mac_id + i);
                            packer.packInt(headset_history.Status + i);


                        }
                        packer.close();
                        Log.d("headset_history", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_BT_SCAN_LIST_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_BT_SCAN_LIST_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        System.out.println("packed:"+packedArray);
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_BT_SCAN_LIST_ID");
                        //Write Data
                        packer.packArrayHeader(3);

                        BT_Scan_List bt_scan_list = new BT_Scan_List();

                        for (int i = 0; i < 5; i++) {
                            packer.packArrayHeader(2);

                            //     packer.packString(bt_scan_list.BTScaList+i);
                            packer.packString(bt_scan_list.name + i);
                            packer.packString(bt_scan_list.mac_id + i);


                        }
                        packer.close();
                        Log.d("BT_SCAN_LIST_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_SPOTIFY_LOGIN_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_SPOTIFY_LOGIN_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_SPOTIFY_LOGIN_ID");
                        //Write Data
                        packer.packArrayHeader(7);

                        SpotifyLogin spotifyLogin = new SpotifyLogin();
                        spotifyLogin = globalClass.spotifyLogin;

                        System.out.println("spotify_" +spotifyLogin.getUsername());
                        System.out.println("spotify_" +spotifyLogin.getAuthentication_Token());
                        System.out.println("spotify_" +spotifyLogin.getOfflineToken());
                        System.out.println("spotify_" +spotifyLogin.getExperity());
                        System.out.println("spotify_" +spotifyLogin.getFacebookLinked());
                        System.out.println("spotify_" +spotifyLogin.getPrivateSession());
                        System.out.println("spotify_" +spotifyLogin.getPublishActivity());
                        System.out.println("spotify_" +spotifyLogin.getLogin_mode());


                        packer.packString(spotifyLogin.getUsername());
                        packer.packString(spotifyLogin.getAuthentication_Token());
                        packer.packString(spotifyLogin.getOfflineToken());
                        packer.packInt(spotifyLogin.getExperity());
                        packer.packString(spotifyLogin.getFacebookLinked());
                        packer.packInt(spotifyLogin.getPrivateSession());
                        packer.packInt(spotifyLogin.getPublishActivity());
                        packer.packInt(spotifyLogin.getLogin_mode());

                        packer.close();

                        Log.d("MSG_ID_SPOTIFY_LOGIN_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_MIGHTY_LOGIN_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_MIGHTY_LOGIN_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_MIGHTY_LOGIN_ID");
                        //Write Data
                        packer.packArrayHeader(3);

                        MightyLogin mightyLogin = globalClass.mightyLogin;

                        packer.packString(mightyLogin.getUsername());
                        packer.packString(mightyLogin.getHashPassword());
                        packer.packInt(mightyLogin.getLogin_mode());
                        packer.close();

                        Log.d("MSG_ID_MIGHTY_LOGIN_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_EVENTS_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_EVENTS_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_EVENTS_ID");
                        //Write Data
                        packer.packArrayHeader(5);

                        Events events = globalClass.events_global;

                        packer.packInt(globalClass.events_global.getBT_Status());
                        packer.packInt(globalClass.events_global.getWiFi_status());
                        packer.packInt(globalClass.events_global.getInternet_Connection());
                        packer.packInt(globalClass.events_global.getSpotify_Status());
                        packer.packInt(globalClass.events_global.getMighty_Cloud_Status());
                        packer.packInt(globalClass.events_global.getOffline_Status());

                        packer.close();

                        Log.d("MSG_ID_EVENTS_ID", "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_APP_INFO_T:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_ID_APP_INFO_T");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_ID_APP_INFO_T");
                        //Write Data
                        packer.packArrayHeader(5);

                        App_Info app_info = globalClass.global_app_Info;

                        packer.packString(globalClass.global_app_Info.getOS());
                        packer.packString(globalClass.global_app_Info.getOS_Version());
                        packer.packString(globalClass.global_app_Info.getSerial_Number());
                        packer.packString(globalClass.global_app_Info.getIMEI());
                        packer.packString(globalClass.global_app_Info.getApp_Version());

                        packer.close();

                        Log.d("MSG_ID_APP_INFO_T", "Packing Data Done");
                        Log.d("MSG_ID_APP_INFO_T", globalClass.global_app_Info.getOS());
                        Log.d("MSG_ID_APP_INFO_T", globalClass.global_app_Info.getOS_Version());
                        Log.d("MSG_ID_APP_INFO_T", globalClass.global_app_Info.getSerial_Number());
                        Log.d("MSG_ID_APP_INFO_T", globalClass.global_app_Info.getIMEI());
                        Log.d("MSG_ID_APP_INFO_T", globalClass.global_app_Info.getApp_Version());

                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_ID_HEADSET_DISCONNECT:
                break;

            case Constants.MSG_WIFI_STATUS_ID:
                try
                {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET)
                    {
                        Log.d("Inside GET-->>", "MSG_WIFI_STATUS_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_WIFI_STATUS_ID");
                        //Write Data
                        packer.packArrayHeader(5);

                        Wifi_Status wifi_status = new Wifi_Status();

                        packer.packInt(wifi_status.status);
                        packer.packString(wifi_status.ip_address);
                        packer.packInt(wifi_status.rssi);
                        packer.packString(wifi_status.ssid);
                        packer.packString(wifi_status.ap_name);

                        packer.close();

                        Log.d("MSG_WIFI_STATUS_ID", "Packing Data Done");

                        packedArray = packer.toByteArray();
                        return packedArray;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.MSG_FIRMWARE_UPGRADE_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_FIRMWARE_UPGRADE_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_FIRMWARE_UPGRADE_ID");
                        //Write Data
                        packer.packArrayHeader(10);

                        FirmWare_Upgrade firmWare_upgrade = globalClass.global_firm_ware;

                        packer.packInt(firmWare_upgrade.getAvailable_Status());
                        packer.packInt(firmWare_upgrade.getLast_Upgrade_Status());
                        packer.packInt(firmWare_upgrade.getUpgrade_Action());
                        packer.packString(firmWare_upgrade.getCurrent_Version());
                        packer.packString(firmWare_upgrade.getNew_Version());
                        packer.packString(firmWare_upgrade.getLast_Update_Date());
                        packer.packString(firmWare_upgrade.getURL());
                        packer.packString(firmWare_upgrade.getHash_Value());
                        packer.packInt(firmWare_upgrade.getHash_Type());
                        packer.packInt(firmWare_upgrade.getFW_Size());
                        packer.close();
                        //    packer.close();
                        Log.d("MSG_FIRMWARE_UPGRADE_ID", "Packing Data Done");
                        Log.e(TAG,"some value "+firmWare_upgrade.getAvailable_Status()+" "+firmWare_upgrade.getLast_Upgrade_Status()+" "+firmWare_upgrade.getUpgrade_Action()+" "+firmWare_upgrade.getCurrent_Version()+" "
                                +firmWare_upgrade.getNew_Version()+" "+firmWare_upgrade.getLast_Update_Date()+" "+firmWare_upgrade.getURL()+" "+firmWare_upgrade.getHash_Value()+" "
                                +firmWare_upgrade.getHash_Type()+" "+firmWare_upgrade.getFW_Size());
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Constants.MSG_FIRMWARE_DL_PROGRESS_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_FIRMWARE_DL_PROGRESS_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_FIRMWARE_DL_PROGRESS_ID");
                        //Write Data
                        packer.packArrayHeader(2);

                        FirmWare_Download  firmWare_download = new FirmWare_Download();

                        packer.packInt(firmWare_download.getStatus());
                        packer.packFloat(firmWare_download.getProgress());
                        packer.close();
                        Log.d(TAG, "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case Constants.MSG_WIFI_SCAN_LIST_ID:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_WIFI_SCAN_LIST_ID");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_WIFI_SCAN_LIST_ID");
                        //Write Data
                        packer.packArrayHeader(1);
                        packer.packArrayHeader(5);

                        Wifi_Status wifi_set = globalClass.wifi_status_global;
                        packer.packString(wifi_set.getAp_name());
                        packer.packString(wifi_set.getSsid());
                        packer.packInt(wifi_set.getSec_type());
                        packer.packInt(wifi_set.getStatus());
                        packer.packInt(wifi_set.getRssi());
                        packer.close();
                        //packer.close();
                        Log.d("MSG_WIFI_SCAN_LIST_ID", "Packing Data Done");
                        Log.d(String.valueOf(wifi_set.getAp_name()), "wifi_set name");
                        Log.d(String.valueOf(wifi_set.getSsid()), "wifi_set ssid");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case Constants.MSG_BIT_RATE_MODE:
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_BIT_RATE_MODE");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_BIT_RATE_MODE");
                        //Write Data
                        packer.packArrayHeader(1);
                        Bit_Rate bit_rate = new Bit_Rate();

                        packer.packInt(globalClass.global_set_bitrate);
                        packer.close();
                        Log.d(TAG, "Packing Data Done");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.MSG_ID_DEBUG_FEATURE :
                try {
                    if (mightyMessage.MessageType == Constants.MSG_TYPE_GET) {
                        Log.d("Inside GET-->>", "MSG_DEBUG");
                        packer.close();
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }

                    if (mightyMessage.MessageType == Constants.MSG_TYPE_SET) {
                        Log.d("Inside SET-->>", "MSG_DEBUG");
                        //Write Data
                        packer.packArrayHeader(2);

                        //FirmWare_Download  firmWare_download = new FirmWare_Download();
                        //Debug_feature  debug_feature = new Debug_feature();

                        packer.packInt(0);
                        packer.packString("anu");
                        packer.close();
                        Log.d(TAG, "Packing Data Done MSG_DEBUG");
                        packedArray = packer.toByteArray();
                        return packedArray;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return null;
    }

    public static MightyMessage UnPacker(byte[] inputByteArray) {

        MightyMessage mightyMessage = new MightyMessage();
        try {

            Log.d("UNPACKING START HERE", "***");

            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputByteArray);
            unpacker.unpackArrayHeader();
            mightyMessage.MessageType = unpacker.unpackInt();
            mightyMessage.MessageID = unpacker.unpackInt();
            mightyMessage.RequestID = unpacker.unpackInt();
            mightyMessage.ResponseID = unpacker.unpackInt();
            mightyMessage.More = unpacker.unpackInt();
            mightyMessage.Size = unpacker.unpackInt();
            mightyMessage.Token = unpacker.unpackString();

            Log.d("Mighty", "Message Type = " + mightyMessage.MessageType);
            Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
            Log.d("Mighty", "Message Request ID = " + mightyMessage.RequestID);
            Log.d("Mighty", "Message Response ID = " + mightyMessage.ResponseID);
            Log.d("Mighty", "Message More = " + mightyMessage.More);
            Log.d("Mighty", "Message Size = " + mightyMessage.Size);
            Log.d("Mighty", "Message Token = " + mightyMessage.Token);
            //     unpacker.close();

            if (mightyMessage.MessageType == 200) {
                System.out.println("SUCCESSFULLY RECEIVED");
                return mightyMessage;
            }

            if(mightyMessage.MessageType == 201)
            {
                System.out.println("SUCCESSFULLY RECEIVED FAILURE EVENT");
                return mightyMessage;
            }

            if (mightyMessage.MessageType == 301) {
                System.out.println("SUCCESSFULLY RECEIVED Event + 301");
                return mightyMessage;
            }
            if (mightyMessage.MessageType == 300) {
                System.out.println("SUCCESSFULLY RECEIVED JACK Event + 300");
                return mightyMessage;
            }

            if (mightyMessage.MessageType == 302) {
                System.out.println("SUCCESSFULLY RECEIVED Event + 302 Mighty_login");
                return mightyMessage;
            }

            //Force mighty login
            if (mightyMessage.MessageType == 303) {
                System.out.println("SUCCESSFULLY RECEIVED Event + 303 BLE Authorization failed");
                return mightyMessage;
            }

            // MSG_TYPE_RESP_ERR_MI_USER_ALIVE
            if (mightyMessage.MessageType == 304) {
                System.out.println("SUCCESSFULLY RECEIVED Event + 304 ERR_MI_USER_ALIVE");
                return mightyMessage;
            }


            switch (mightyMessage.MessageID) {
                case Constants.MSG_ID_DEVICEINFO_ID:
                    //Unpack the Data

                    try {
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Device_Info device_info = new Device_Info();
                        device_info.setDevice_Type(unpacker.unpackInt());
                        device_info.setDevice_Name(unpacker.unpackString());
                        device_info.setSW_Version(unpacker.unpackString());
                        device_info.setHW_Serial_Number(unpacker.unpackString());
                        device_info.setDevice_Manufactured_Region(""); //unpacker.unpackString();
                        device_info.setCurrentserviceuuid(""); //unpacker.unpackString();
                        mightyMessage.mightyObject = device_info;

                        mightyMessage.display_string = device_info.Device_Name;
                        globalClass.device_name = device_info.Device_Name;
                        globalClass.device_info = device_info;

                        Log.d(TAG,"Message Version = " + device_info.Mighty_Version);
                        Log.d(TAG,"SW Version = " + device_info.SW_Version);
                        Log.d(TAG, "Device ID = " + device_info.Device_ID);
                        Log.d(TAG,"Device Name = " + device_info.Device_Name);
                        Log.d(TAG,"Device Region = " + device_info.HW_Serial_Number);
                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        Log.e(TAG,"Packing Unpaclking");
                        globalClass.bleDisconnect();
                        e.printStackTrace();

                    }
                    break;

                case Constants.MSG_ID_WIFICONFIGURATION_ID:

                    //Unpack the Data
                    try {

                        System.out.println("Inside Serializer MSG_ID_WIFICONFIGURATION_ID");
                        unpacker.unpackArrayHeader();
                        String TAG = "WIFICONFIGURATION_ID";
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        WiFi_Configuration wiFi_configuration = new WiFi_Configuration();


                        wiFi_configuration.SSID = unpacker.unpackString();
                        wiFi_configuration.AP_Name = unpacker.unpackString();
                        wiFi_configuration.Passcode = unpacker.unpackString();
                        wiFi_configuration.Mode = unpacker.unpackInt();
                        wiFi_configuration.Connection_Status = unpacker.unpackInt();
                        wiFi_configuration.def = unpacker.unpackInt();

                        Log.d("WIFICONFIGURATION", "UN Packing Data Done");
                        mightyMessage.mightyObject = wiFi_configuration;


                        Log.d(TAG, "SSID = " + wiFi_configuration.SSID);
                        Log.d(TAG, "AP_NAME = " + wiFi_configuration.AP_Name);
                        Log.d(TAG, "PASSCODE = " + wiFi_configuration.Passcode);
                        Log.d(TAG, "MODE = " + wiFi_configuration.Mode);
                        Log.d(TAG, "Connection_status = " + wiFi_configuration.Connection_Status);
                        Log.d(TAG, "Def = " + wiFi_configuration.def);

                        wiFi_configuration = new WiFi_Configuration(wiFi_configuration.SSID,wiFi_configuration.AP_Name, wiFi_configuration.Passcode,wiFi_configuration.Mode, wiFi_configuration.Connection_Status, wiFi_configuration.def);

                        globalClass.wiFi_configuration_global = wiFi_configuration;

                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_WIFI_STATIC_NETWORK_ID:

                    try {
                        String TAG = "WIFI_STATIC_NETWORK_ID";
                        unpacker.unpackArrayHeader();

                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        WiFi_Static_Network wiFi_static_network = new WiFi_Static_Network();
                        wiFi_static_network.IPAddress = unpacker.unpackString();
                        wiFi_static_network.DNSAddress = unpacker.unpackString();
                        wiFi_static_network.NetworkMask = unpacker.unpackString();
                        wiFi_static_network.GatewayAddress = unpacker.unpackString();

                        Log.d("WIFI_STATIC_NETWORK_ID", "UN Packing Data Done");
                        mightyMessage.mightyObject = wiFi_static_network;

                        Log.d(TAG, "IPAddress=" + wiFi_static_network.IPAddress);
                        Log.d(TAG, "DNSAddress=" + wiFi_static_network.DNSAddress);
                        Log.d(TAG, "NetworkMask=" + wiFi_static_network.NetworkMask);
                        Log.d(TAG, "GatewayAddress=" + wiFi_static_network.GatewayAddress);
                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_WIFI_HISTORY_ID:

                    try {
                        String TAG = "WIFI_HISTORY_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                   unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_BT_CONFIGURATION_ID:

                    try {

                        String TAG = "BT_CONFIGURATION_ID";
                        int count = unpacker.unpackArrayHeader();
                        System.out.println("mycount:"+count);
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        BT_Scan_List bt_configuration;

                        String[] BT_list = new String[count];

                        for (int i = 0; i < count; i++)
                        {
                            unpacker.unpackArrayHeader();
                            String name = unpacker.unpackString();
                            String mac = unpacker.unpackString();
                            int Status = unpacker.unpackInt();
                            Log.d(TAG, "BT_name_ID" + i + "=" + name);
                            Log.d(TAG, "BT_mac_ID" + i + "=" + mac);
                            Log.d(TAG, "BT_Status" + i + "=" + Status);

                            BT_list[i] = name + "@" + mac + "@" + Status;

                            bt_configuration  = new BT_Scan_List(name,mac,Status);
                            Log.e(TAG,"name mca Status "+name+" "+mac+" "+Status);
                            globalClass.scan_sel_bt_headphone_model = bt_configuration;

                        }
                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_BATTERY_INFO_ID:

                    try {
                        String TAG = "BATTERY_INFO";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);


                        globalClass.battery_info.setStatus(unpacker.unpackInt());
                        globalClass.battery_info.setCapacity((int) unpacker.unpackInt());
                        globalClass.battery_info.setAvailablePercentage((int) unpacker.unpackInt());

                        Log.d(TAG, "UN Packing Data Done");
                        mightyMessage.mightyObject = globalClass.battery_info;

                        Log.d(TAG, "Status" + globalClass.battery_info.getStatus());
                        Log.d(TAG, "Capacity" + globalClass.battery_info.getCapacity());
                        Log.d(TAG, "AvailablePercentage" + globalClass.battery_info.getAvailablePercentage());

                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_PLAYLIST_ID:
                    //mighty playlist pull to refresh
                    globalClass.setgetplaylist=true;
                    try {
                        String TAG = "PLAYLIST";
                        int count = 0;
                        try
                        {
                            count = unpacker.unpackArrayHeader();
                        }
                        catch (Exception e)
                        {
                            if (!globalClass.mighty_playlist.isEmpty())
                                globalClass.mighty_playlist.clear();
                            Log.w(TAG, "Empty Playlist from MIGHTY");
                            return mightyMessage;

                        }

                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        if (!globalClass.mighty_playlist.isEmpty())
                            globalClass.mighty_playlist.clear();

                        for (int i = 0; i < count; i++) {
                            unpacker.unpackArrayHeader();
                            String name = unpacker.unpackString();
                            String uri = unpacker.unpackString();
                            String SnapshotId = unpacker.unpackString();
                            int tracks_count = unpacker.unpackInt();
                            int Offline = unpacker.unpackInt();
                            float DownloadProgress = unpacker.unpackFloat();
                            int public_playlist = unpacker.unpackInt();

                            Playlist playlist = new Playlist(name, uri, SnapshotId, String.valueOf(tracks_count), Offline, DownloadProgress, public_playlist);
                            globalClass.test_playlist.add(playlist);

                            globalClass.mighty_playlist.put(uri,playlist);

                            globalClass.Play_list_name.add(name);
                            globalClass.Play_list_uri.add(uri);

                        }

                        Log.e(TAG,"Serializer Size" + globalClass.mighty_playlist.size()+" "+globalClass);

                        unpacker.close();
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_DOWNLOAD_ID:

                    try {
                        String TAG = "MSG_ID_DOWNLOAD_ID";
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                        Download download = new Download();


                        unpacker.unpackArrayHeader();
                        unpacker.unpackArrayHeader();
                        download.setPlaylist_name(unpacker.unpackString());
                        download.setGetPlaylist_url(unpacker.unpackString());
                        download.setProgress(unpacker.unpackFloat());
                        download.setFree_space(unpacker.unpackFloat());
                        download.setDownloaded_tracks(unpacker.unpackInt());
                        download.setTotal_tracks(unpacker.unpackInt());
                        download.setStatus(unpacker.unpackInt());
                        download.setSnapshotId(unpacker.unpackString());
                        globalClass.download = download;
                        unpacker.close();
                        unpacker.close();

                        Log.e(TAG," download_unpack "+ download.getPlaylist_name() + download.getProgress() + download.getGetPlaylist_url() +" get_status"+ download.getStatus() + " " + download.getDownloaded_tracks() + " " + download.getTotal_tracks() + " " + download.getFree_space()+" hash_key "+download.getSnapshotId());
                        return mightyMessage;


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case Constants.MSG_ID_VOICE_OVER_ID:
                    try {
                        String TAG = "VOICE_OVER_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Voice_Over voice_over = new Voice_Over();

                        voice_over.speech_rate = unpacker.unpackFloat();
                        voice_over.voice_name = unpacker.unpackString();
                        voice_over.language = unpacker.unpackString();
                        voice_over.default_voice = unpacker.unpackInt();

                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = voice_over;

                        Log.d(TAG, "speech_rate" + voice_over.speech_rate);
                        Log.d(TAG, "voice_name" + voice_over.voice_name);
                        Log.d(TAG, "language" + voice_over.language);
                        Log.d(TAG, "default_voice" + voice_over.default_voice);
                        unpacker.close();
                        return mightyMessage;


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_POWER_ID:
                    try {
                        String TAG = "MSG_ID_POWER_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Power power = new Power();

                        power.power_mode = unpacker.unpackInt();
                        power.sleep_timeout = unpacker.unpackInt();

                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = power;

                        Log.d(TAG, "power mode" + power.power_mode);
                        Log.d(TAG, "sleep out time" + power.sleep_timeout);
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_MEMORY_ID:
                    try {
                        //        GlobalClass globalClass = new GlobalClass();

                        String TAG = "MSG_ID_MEMORY_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Memory memory = new Memory();

                        memory.Ram_total = unpacker.unpackFloat();
                        memory.Storage_total = unpacker.unpackFloat();
                        memory.Ram_free = unpacker.unpackFloat();
                        memory.Storage_free = unpacker.unpackFloat();

                        memory = new Memory(  memory.Ram_total, memory.Storage_total,memory.Ram_free,memory.Storage_free);

                        globalClass.memory = memory;

                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = memory;

                        Log.d(TAG, "ram_total=" + memory.Ram_total);
                        Log.d(TAG, "storage total=" + memory.Storage_total);
                        Log.d(TAG, "ram free=" + memory.Ram_free);
                        Log.d(TAG, "storage free=" + memory.Storage_free);


                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_HEADSET_STATUS_ID:
                    try {
                        String TAG = "HEADSET_STATUS_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Headset_Status headset_status = new Headset_Status();

                        headset_status.HeadsetTypeConnected = unpacker.unpackInt();
                        headset_status.HeadsetConnectionStatus = unpacker.unpackInt();

                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = headset_status;

                        Log.d(TAG, "ram_total=" + headset_status.HeadsetTypeConnected);
                        Log.d(TAG, "sleep out time=" + headset_status.HeadsetConnectionStatus);
                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case Constants.MSG_ID_HEADSET_HISTORY_ID:
                    try {
                        String TAG = "HEADSET_HISTORY_ID";
                        int count = unpacker.unpackArrayHeader();
                        System.out.println("count:"+count);
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                        if(!globalClass.hasmap_bt_scan_headset_lists.isEmpty())
                            globalClass.hasmap_bt_scan_headset_lists.clear();

                        Headset_History bt_scan_list_model;

                        // String[] BT_list = new String[count];

                        for (int i = 0; i < count; i++)
                        {
                            unpacker.unpackArrayHeader();
                            String name = unpacker.unpackString();
                            String mac = unpacker.unpackString();
                            int Status = unpacker.unpackInt();
                            Log.d(TAG, "BT_name_ID" + i + "=" + name);
                            Log.d(TAG, "BT_mac_ID" + i + "=" + mac);
                            Log.d(TAG, "BT_Status" + i + "=" + Status);

                            bt_scan_list_model  = new Headset_History(name,mac,Status);
                            globalClass.hasmap_bt_scan_headset_lists.put(mac, bt_scan_list_model);
                            //globalClass.bt_scan_headset_lists.add(bt_scan_list_model);

                        }
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                     //   globalClass.setBT_list(BT_list);
//
//
//
//                        String TAG = "HEADSET_HISTORY_ID";
//                        unpacker.unpackArrayHeader();
//                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
//
//                        Headset_History headset_history = new Headset_History();
//
//                    /*    headset_history.BTPairedHistoryList1 = unpacker.unpackString();
//                        headset_history.BTPairedHistoryList2 = unpacker.unpackString();
//                        headset_history.BTPairedHistoryList3 = unpacker.unpackString();
//                        headset_history.BTPairedHistoryList4 = unpacker.unpackString();
//                        headset_history.BTPairedHistoryList5 = unpacker.unpackString();
//
//
//                        Log.d(TAG, "UN Packing Data Done");
//
//                        mightyMessage.mightyObject = headset_history;
//
//                        Log.d(TAG,"BTPairedHistoryList1"+ headset_history.BTPairedHistoryList1);
//                        Log.d(TAG,"BTPairedHistoryList2"+ headset_history.BTPairedHistoryList2);
//                        Log.d(TAG,"BTPairedHistoryList3"+ headset_history.BTPairedHistoryList3);
//                        Log.d(TAG,"BTPairedHistoryList4"+ headset_history.BTPairedHistoryList4);
//                        Log.d(TAG,"BTPairedHistoryList5"+ headset_history.BTPairedHistoryList5);*/


                    break;


                case Constants.MSG_ID_BT_SCAN_LIST_ID:
                    try {
                        String TAG = "BT_SCAN_LIST_ID";
                        int count = unpacker.unpackArrayHeader();
                        System.out.println("count:"+count);
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);


                        //            final GlobalClass globalClass = new GlobalClass();
                        //             globalClass.bt_scan_lists_global_obj_list.clear();
                        Headset_History bt_scan_list_model;

                        String[] BT_list = new String[count];

                        for (int i = 0; i < count; i++)
                        {
                            unpacker.unpackArrayHeader();
                            String name = unpacker.unpackString();
                            String mac = unpacker.unpackString();
                            int Status = unpacker.unpackInt();
                            Log.d(TAG, "BT_name_ID" + i + "=" + name);
                            Log.d(TAG, "BT_mac_ID" + i + "=" + mac);
                            Log.d(TAG, "BT_Status" + i + "=" + Status);

                            BT_list[i] = name + "@" + mac + "@" + Status;

                            bt_scan_list_model  = new Headset_History(name,mac,Status);
                            globalClass.hasmap_bt_scan_headset_lists.put(mac, bt_scan_list_model);
                            //globalClass.bt_scan_headset_lists.add(bt_scan_list_model);

                        }

                        globalClass.setBT_list(BT_list);
                        unpacker.close();
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_WIFI_SCAN_LIST_ID:
                    try {
                        String TAG = "MSG_WIFI_SCAN_LIST_ID";
                        int count = unpacker.unpackArrayHeader();
                        System.out.println("count:"+count);
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        globalClass.wifi_lists_global.clear();
                        Wifi_Status wifi_scan_list_model;
                        String[] wifi_list = new String[count];

                        for (int i = 0; i < count; i++)
                        {
                            unpacker.unpackArrayHeader();
                            String Ap_name = unpacker.unpackString();
                            String ssid = unpacker.unpackString();
                            int sec_type = unpacker.unpackInt();
                            int wifi_status = unpacker.unpackInt();
                            int rssi = unpacker.unpackInt();
                            Log.d(TAG, "Wifi_AP_name" + i + "=" + Ap_name);
                            Log.d(TAG, "Wifi_SSID" + i + "=" + ssid);
                            Log.d(TAG, "Wifi_Sec_Type" + i + "=" + sec_type);
                            Log.d(TAG, "Wifi_Status" + i + "=" + wifi_status);
                            Log.d(TAG, "Wifi_RSSI" + i + "=" + rssi);

                            wifi_list[i] = Ap_name + "@ " + ssid + "@ " + sec_type + "@ " + wifi_status + "@ " + rssi;

                            Log.e(TAG,"wifi_list "+ wifi_list[i]);
                            wifi_scan_list_model  = new Wifi_Status(wifi_status,sec_type,rssi,"",ssid,Ap_name);
                            if(!wifi_scan_list_model.ap_name.equals("")) {
                                if(wifi_scan_list_model.getStatus() == 1) {
                                    globalClass.wifi_connected_global = wifi_scan_list_model;
                                    globalClass.wifi_status = true;
                                }
                                globalClass.wifi_lists_global.add(wifi_scan_list_model);
                            }
                            Log.e(TAG,"globalclass array"+globalClass.wifi_lists_global);

                        }

                        //   globalClass.setBT_list(BT_list);
                        unpacker.close();
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case Constants.MSG_ID_SPOTIFY_LOGIN_ID:
                    try {
                        String TAG = "SPOTIFY_LOGIN_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        SpotifyLogin spotifyLogin =  new SpotifyLogin();
                        spotifyLogin.Username = unpacker.unpackString();
                        spotifyLogin.Authentication_Token = unpacker.unpackString();
                        spotifyLogin.OfflineToken = unpacker.unpackString();
                        spotifyLogin.Experity_date = unpacker.unpackInt();
                        spotifyLogin.FacebookLinked = unpacker.unpackString();
                        spotifyLogin.PrivateSession = unpacker.unpackInt();
                        spotifyLogin.PublishActivity = unpacker.unpackInt();


                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = spotifyLogin;

                        Log.d(TAG,"Username="+spotifyLogin.Username);
                        Log.d(TAG,"Authentication_Token"+spotifyLogin.Authentication_Token);
                        Log.d(TAG,"OfflineToken"+spotifyLogin.OfflineToken);
                        Log.d(TAG,"Experity_date"+  spotifyLogin.Experity_date);
                        Log.d(TAG,"FacebookLinked"+ spotifyLogin.FacebookLinked);
                        Log.d(TAG,"PrivateSession"+spotifyLogin.PrivateSession);
                        Log.d(TAG,"PublishActivity"+spotifyLogin.PublishActivity);
                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                case Constants.MSG_ID_MIGHTY_LOGIN_ID:
                    try {
                        String TAG = "MIGHTY_LOGIN_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        MightyLogin mightyLogin = new MightyLogin();
                        mightyLogin.setUsername(unpacker.unpackString());
                        mightyLogin.setHashPassword(unpacker.unpackString());
                        mightyLogin.setLogin_mode(unpacker.unpackInt());

                        Log.d(TAG, "UN Packing Data Done");

                        mightyMessage.mightyObject = mightyLogin;
                        globalClass.mightyLogin = mightyLogin;
                        Log.d(TAG, "Username=" + mightyLogin.Username);
                        Log.d(TAG, "Username=" + mightyLogin.Username);
                        Log.d(TAG, "HashPassword" + mightyLogin.HashPassword);

                        unpacker.close();
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.MSG_ID_EVENTS_ID:
                    try {
                        String TAG = "MSG_ID_EVENTS_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);



                        Events events = new Events();

                        events.setBT_Status(unpacker.unpackInt());
                        events.setWiFi_status(unpacker.unpackInt());
                        events.setInternet_Connection(unpacker.unpackInt());
                        events.setSpotify_Status(unpacker.unpackInt());
                        events.setMighty_Cloud_Status(unpacker.unpackInt());
                        events.setOffline_Status(unpacker.unpackInt());

                        mightyMessage.mightyObject = events;

/*
                        events = new Events(events.BLEStatus,events.WiFiConnected,events.InternetConnection,events.BTScanStatus,events.BTPairingStatus,events.SpotifyStatus);
*/

                        globalClass.events_global = events;

                        Log.d(TAG, "BLEStatus :" + events.getBT_Status());
                        Log.d(TAG, "WiFiConnected :" + events.getWiFi_status());
                        Log.d(TAG, "InternetConnection :" + events.getInternet_Connection());
                        Log.d(TAG, "spotify :" + events.getSpotify_Status());
                        Log.d(TAG, "mighty_cloud :" + events.getMighty_Cloud_Status());
                        Log.d(TAG, "offline :" + events.getOffline_Status());

                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case Constants.MSG_ID_APP_INFO_T:
                    try {
                        String TAG = "MSG_ID_APP_INFO_T";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        App_Info app_info = new App_Info();

                        app_info.OS = unpacker.unpackString();
                        app_info.OS_Version = unpacker.unpackString();
                        app_info.Serial_Number = unpacker.unpackString();
                        app_info.IMEI = unpacker.unpackString();
                        app_info.App_Version = unpacker.unpackString();

                        mightyMessage.mightyObject = app_info;

                        Log.d("MSG_ID_APP_INFO_T", "OS =" + app_info.OS);
                        Log.d("MSG_ID_APP_INFO_T", "OS_Version =" + app_info.OS_Version);
                        Log.d("MSG_ID_APP_INFO_T", "Serial_Number =" + app_info.Serial_Number);
                        Log.d("MSG_ID_APP_INFO_T", "IMEI =" + app_info.IMEI);
                        Log.d("MSG_ID_APP_INFO_T", "App_Version =" + app_info.App_Version);
                        unpacker.close();
                        return mightyMessage;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case Constants.MSG_ID_HEADSET_DISCONNECT:
                    break;

                case Constants.MSG_FIRMWARE_DL_PROGRESS_ID:
                    try {
                        String TAG = "MSG_FIRMWARE_PROGRESS";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                        //Initlizing
                        FirmWare_Download firmWare_download = new FirmWare_Download();

                        //unpacking
                        firmWare_download.setStatus(unpacker.unpackInt());
                        firmWare_download.setProgress(unpacker.unpackFloat());

                        globalClass.global_firm_download = firmWare_download;

                        Log.e(TAG,"GET_Status "+ globalClass.global_firm_download.getStatus() + " progress "+ globalClass.global_firm_download.getProgress());

                        unpacker.close();

                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;


                case Constants.MSG_WIFI_STATUS_ID:
                    try {
                        String TAG = "MSG_WIFI_STATUS_ID";
                        unpacker.unpackArrayHeader();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);

                        Wifi_Status wifi_status = new Wifi_Status();

                        wifi_status.status = unpacker.unpackInt();
                        wifi_status.status = 1;
                        wifi_status.ip_address = unpacker.unpackString();
                        wifi_status.ssid = unpacker.unpackString();
                        wifi_status.ap_name = unpacker.unpackString();
                        // wifi_status.ap_name = unpacker.unpackString();

                        if(!GlobalClass.wifi_lists_global.isEmpty())
                            GlobalClass.wifi_lists_global.clear();

                        if(!wifi_status.ap_name.equals("")) {
                            Log.e(TAG,"Null");
                            globalClass.wifi_connected_global = wifi_status;
                            globalClass.wifi_status=true;
                            globalClass.wifi_lists_global.add(wifi_status);
                        }else{
                            globalClass.wifi_connected_global = null;
                        }
                        mightyMessage.mightyObject = wifi_status;

                        Log.d("MSG_WIFI_STATUS_ID", "flag =" + wifi_status.status);
                        Log.d("MSG_WIFI_STATUS_ID", "ip_address =" + wifi_status.ip_address);
                        Log.d("MSG_WIFI_STATUS_ID", "bssid =" + wifi_status.ssid);
                        Log.d("MSG_WIFI_STATUS_ID", "name =" + wifi_status.ap_name);
                        // Log.d("MSG_WIFI_STATUS_ID", "bonjour =" + wifi_status.ap_name);

                        unpacker.close();

                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Constants.MSG_BIT_RATE_MODE:
                    try {
                        String TAG = "MSG_BIT_RATE_MODE";
                        int count = unpacker.unpackArrayHeader();
                        System.out.println("count:"+count);
                        Bit_Rate bit_rate = new Bit_Rate();
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                        bit_rate.setBitRateMode(unpacker.unpackInt());
                        globalClass.global_bit_rate = bit_rate;
                        Log.e(TAG, "GET_BIT_RATE_MODE =" + globalClass.global_bit_rate.getBitRateMode()+" "+globalClass.global_bit_rate);

                        unpacker.close();

                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MSG_ID_DEBUG_FEATURE:
                    try {
                        byte[] payload;
                        String TAG = "MSG_ID_DEBUG_FEATURE";
                        int count = unpacker.unpackArrayHeader();
                        Log.e(TAG,"count:"+count);
                        Log.d("Mighty", "Message ID = " + mightyMessage.MessageID);
                        globalClass.debug_data.setSize(unpacker.unpackInt());

                        int payloadLength = unpacker.unpackBinaryHeader();
                        //payload = unpacker.readPayload(payloadLength);
                        MessageBuffer pay = unpacker.readPayloadAsReference(payloadLength);
                        //payload = pay.toByteArray();
                        String encodedString = Base64.encodeToString(pay.toByteArray(), Base64.DEFAULT);
                        System.out.println("encodedString "+encodedString);
                        globalClass.debug_data.setDebug_data(encodedString);
                        unpacker.close();
                        System.out.println("System_file ="+globalClass.debug_data.getDebug_data());
                        return mightyMessage;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                default:
                    Log.e("Mighty", "Invalid Message Received : " + mightyMessage.MessageID);

                    return null;

            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return null;
    }
}
