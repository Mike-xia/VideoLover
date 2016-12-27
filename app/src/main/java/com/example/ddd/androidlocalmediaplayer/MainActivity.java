package com.example.ddd.androidlocalmediaplayer;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Connection;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ddd.androidlocalmediaplayer.R.id.listView1;
import static com.example.ddd.androidlocalmediaplayer.R.id.start;
import static com.example.ddd.androidlocalmediaplayer.R.id.video_path;

public class MainActivity extends AppCompatActivity {
    private ListView list;
    private ArrayList<Video> videoArrayList;
    private int number = 0;
    private SurfaceView surfaceView;
    private MediaPlayer player = new MediaPlayer();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView1);

        //findViews();
        registerListeners();
        initVideoList();
        initListView();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = videoArrayList.get(position);
                //play(position);
                Toast.makeText(MainActivity.this, video.getVideoPath(), Toast.LENGTH_SHORT).show();
                int data=position;

                Intent intent=new Intent(MainActivity.this,VideoPlay.class);
                intent.putExtra("data",data);
                startActivity(intent);
                 /*id=number;
                Uri contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id);*/

            }
        });
    }

    /*private void findViews(){
        list=(ListView)findViewById(listView1);
    }*/
    private void registerListeners() {

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

    private void initListView() {
        List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
        HashMap<String, String> map;
        SimpleAdapter simpleAdapter;
        for (Video video : videoArrayList) {
            map = new HashMap<String, String>();
            //map.put("videoName",video.getVideoName());
            map.put("videoPath", video.getVideoPath());
            map.put("videoTime", video.getVideoDuration());
            //      map.put("videoImage",video.getVideoImage());
            map.put("videoSize", video.getVideoSize());
            list_map.add(map);
        }
        String[] from = new String[]{"videoPath", "videoTime", "videoSize"};
        int[] to = {R.id.video_path, R.id.video_time, R.id.video_size};
        simpleAdapter = new SimpleAdapter(this, list_map, R.layout.video_item, from, to);
        list.setAdapter(simpleAdapter);

    }

}
