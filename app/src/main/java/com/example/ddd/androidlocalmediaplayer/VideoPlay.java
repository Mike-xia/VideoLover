package com.example.ddd.androidlocalmediaplayer;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ddd on 2016/12/22.
 */

public class VideoPlay extends AppCompatActivity{
    private ArrayList<Video> videoArrayList;
    private SurfaceView surfaceView;
    //private SurfaceHolder surfaceHolder;
    private MediaPlayer player=new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);


        surfaceView = (SurfaceView) findViewById(R.id.video_view);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new VideoPlay.SurfaceViewLis());
        getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        initVideoList();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onResume(){
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
              play(position);
           // play(3);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(player.isPlaying()){
                player.stop();
                player.release();
            }
        }
    }


    private void load(int number) {
        try {
            player=new MediaPlayer();
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
        } else{
            load(number);
            player.start();
        }
    }


    private void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    private void stop() {
        player.start();
    }

    private void replay() {
        player.start();
    }

    /*  private void moveNumberToNext() {
        //  if ((number) == VideoList.getVideoList().size() - 1) {
              // Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.tip_reach_bottom), Toast.LENGTH_SHORT).show();
          } else {
             ++number;
              play(number);
          }
      }

      private void moveNumberToPrevious() {
          if (number == 0) {
              //   Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.tip_reach_top), Toast.LENGTH_SHORT).show();
          } else {
              play(number);
          }
      }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }





}
