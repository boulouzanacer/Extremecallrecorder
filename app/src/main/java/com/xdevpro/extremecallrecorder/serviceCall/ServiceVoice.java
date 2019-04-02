package com.xdevpro.extremecallrecorder.serviceCall;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.xdevpro.extremecallrecorder.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by UK2016 on 03/01/2018.
 */

public class

ServiceVoice extends Service {

    private static String mFileName = null;
    private String sFileName = null;
    private String PHONE = "0";

    private MediaRecorder mRecorder = null;

    @Override
    public void onCreate() {
        super.onCreate();


        Log.e("TRACKKK", "onCreate  =========> SERVICE");
        // Record to the external cache directory for visibility
        File file = Environment.getExternalStoragePublicDirectory("0Data/Android/ExtremeRecorder/temp");
        if(!file.exists()){
            file.mkdirs();
        }
        mFileName = file.getPath();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("TRACKKK", "onStartCommand  =========> VOICE");

        if(intent != null){
            PHONE = intent.getStringExtra("PHONE_NUMBER");
        }else {
            PHONE = "0";
        }
        showNotification(PHONE);

        long time= System.currentTimeMillis();
        sFileName = "/audio_"+PHONE+"_"+String.valueOf(time)+".mp3";
        mFileName += sFileName;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        }
        // EventBus.getDefault().register(this);

        //TODO do something useful
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TRACKKK", "onDestroy =========> SERVICE");
       // InsertData("NASSER", "boulouza.nacer@gmail.com");
      //  EventBus.getDefault().unregister(this);
        String sourcePath = mFileName;
        File source = new File(sourcePath);

        String destinationPath = Environment.getExternalStoragePublicDirectory( "/0Data/Android/ExtremeRecorder/") +sFileName;
        File destination = new File(destinationPath);
        try
        {
            if(source.exists()){

                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

               source.delete();
                Log.v("TRACKKK", "Copy file successful.");

            }else{
                Log.v("TRACKKK", "Copy file failed. Source file missing.");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        stopRecording();


    }




    private void startRecording() {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.v("TRACKKK", "prepare() failed");
                Log.v("TRACKKK", ""+e.getMessage());
            }

            mRecorder.start();

    }

    private void stopRecording() {

        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception e) {
            Log.v("TRACKKK", ""+e.getMessage());
        }

    }

    protected void showNotification(String phone_number){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentText("New call recording of : " + phone_number );
        builder.setSmallIcon( R.drawable.ic_call_black_24dp );
        builder.setContentTitle("Extreme Call Recorder");
        NotificationManagerCompat.from(getApplicationContext()).notify(2541, builder.build());
    }
}