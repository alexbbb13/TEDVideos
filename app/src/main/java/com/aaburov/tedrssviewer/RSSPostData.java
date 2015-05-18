package com.aaburov.tedrssviewer;

import android.graphics.Bitmap;

/**
 * Created by Giorgio on 15.05.2015.
 */
public class RSSPostData {
    public String postThumbUrl=null;
    public String postVideoUrl=null;
    public String postTitle=null;
    public String postInfo=null;


    public boolean isFilled(){
    return (postThumbUrl!=null && postVideoUrl!=null && postTitle!=null && postInfo!=null);
    }

}



