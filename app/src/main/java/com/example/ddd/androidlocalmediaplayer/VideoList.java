package com.example.ddd.androidlocalmediaplayer;

import java.util.ArrayList;

/**
 * Created by ddd on 2016/12/22.
 */

public class VideoList {
    private static ArrayList<Video> videoArray = new ArrayList<Video>();
    private VideoList(){}
    public static ArrayList<Video> getVideoList(){
        return  videoArray;
    }

}
