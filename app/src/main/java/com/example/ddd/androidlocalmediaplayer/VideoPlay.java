package com.example.ddd.androidlocalmediaplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;

/**
 * Created by ddd on 2016/12/22.
 */


public class VideoPlay extends AppCompatActivity {
    private ArrayList<Video> videoArrayList;
    private SurfaceView surfaceView;
    //private SurfaceHolder surfaceHolder;
    private MediaPlayer player = new MediaPlayer();
    private ImageButton pauseAndPlay;
    private int time = 0;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SeekBar seekBar = null;
    private boolean playflag=true;
    private boolean pauseflag=false;
    private PopupWindow popupWindow;
    private RelativeLayout popPu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video);


        // android.media.session.MediaController mediaController=new android.media.session.MediaController(this,)
        final ImageButton pauseAndPlay =(ImageButton) findViewById(R.id.PauseAndPlay);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        final TextView mwz=(TextView)findViewById(R.id.mwz) ;
        //  pause.setAlpha(0.8f);

        View popupView = getLayoutInflater().inflate(R.layout.popupwindow, null);

        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popPu=(RelativeLayout)super.findViewById(R.id.pop);
        RelativeLayout videoView=(RelativeLayout)super.findViewById(R.id.media);

        surfaceView = (SurfaceView) findViewById(R.id.video_view);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceViewLis());

         pauseAndPlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(player.isPlaying()){
                     player.pause();
                     pauseAndPlay.setImageResource(ic_media_play);
                 }else{
                     player.start();
                     pauseAndPlay.setImageResource(ic_media_pause);
                 }
             }
         });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              // mwz.setText("当前值:" +player.getDuration()/1000);
                seekBar.setMax(player.getDuration()/1000);
                //updateseekbar update=new updateseekbar();
                //执行者execute
               // update.execute(1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress()*1000);
            }
        });
        popPu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              // popupWindow.showAsDropDown(v);
                // popupWindow.dismiss();
                if (popPu.getVisibility()==View.INVISIBLE || popPu.getVisibility()==View.GONE){
                    popPu.setVisibility(View.VISIBLE);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        popPu.setVisibility(View.INVISIBLE); //view是要隐藏的控件
                    }
                }, 3000);
                mwz.setText("当前总时间:" +player.getDuration()/1000+"秒");
                return true;
            }

        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popPu.getVisibility()==View.INVISIBLE || popPu.getVisibility()==View.GONE){
                    popPu.setVisibility(View.VISIBLE);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popPu.setVisibility(View.INVISIBLE); //view是要隐藏的控件
                    }
                }, 5000);
                return true;
            }
        });

        initVideoList();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }



    private void initVideoList() {
        videoArrayList = VideoList.getVideoList();
        if (videoArrayList.isEmpty()) {
            Cursor mVideoCursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.VideoColumns.TITLE);
            // int indexTitle=mVideoCursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE);

            int indexTotalTime = mVideoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
            int indexPath = mVideoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);

           /* int imageId=mVideoCursor.getInt(mVideoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String selection=MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                  */
            int indexImage = mVideoCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
            int indexSize = mVideoCursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE);
            for (mVideoCursor.moveToFirst(); !mVideoCursor.isAfterLast(); mVideoCursor.moveToNext()) {
                // String strTitle=mVideoCursor.getString(indexTitle);
                String strTotalTime = mVideoCursor.getString(indexTotalTime);
                String strPath = mVideoCursor.getString(indexPath);
                String strImage = mVideoCursor.getString(indexImage);
                String strSize = mVideoCursor.getString(indexSize);

                Video video = new Video(strImage, strPath, strTotalTime, strSize);
                videoArrayList.add(video);

            }
        }
    }


    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Intent intent=getIntent();
            int position=intent.getIntExtra("data",1);
           // play(2);

             play(position);
             }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (player.isPlaying()) {

                //  player.getDuration();
                player.stop();
                player.release();
            }
        }
    }


    private void load(int number) {
        try {
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(VideoList.getVideoList().get(number).getVideoPath());
            player.setDisplay(surfaceView.getHolder());
            player.prepare();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play(int number) {
        if (player != null && player.isPlaying()) {
            player.stop();
        } else {
            load(number);
            // player.seekTo(player.getDuration());
            player.start();
            player.setScreenOnWhilePlaying(true);

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
    private class updateseekbar extends AsyncTask<Integer, Integer, String>{//AsyncTask的使用...
        protected void onPostExecute(String result){}
        protected void onProgressUpdate(Integer...progress){
            seekBar.setProgress(progress[0]);
        }
        @Override
        protected String doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            while(VideoPlay.this.playflag){
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                this.publishProgress(VideoPlay.this.player.getCurrentPosition());
            }
            return null;
        }
    }

}
