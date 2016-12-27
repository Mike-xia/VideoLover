package com.example.ddd.androidlocalmediaplayer;

import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by ddd on 2016/12/14.
 */

public class Video {
   // private String videoName;
    private String videoPath;
    private String videoImage;
    private String videoDuration;
    private String videoSize;
    public Video(String videoImage,String videoPath,String videoDuration,String videoSize){
     //   this.videoName=videoName;
        this.videoPath=videoPath;
        this.videoImage=videoImage;
        this.videoDuration=videoDuration;
        this.videoSize=videoSize;
    }
 /*   public String getVideoName(){
        return this.videoName;
    }*/
    public String getVideoImage(){  return videoImage;}
    public String getVideoPath(){return this.videoPath;}
    public String getVideoDuration(){return this.videoDuration;}
    public String getVideoSize(){return this.videoSize;}
}
