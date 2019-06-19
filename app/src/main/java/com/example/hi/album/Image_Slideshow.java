package com.example.hi.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class Image_Slideshow extends AppCompatActivity {
    ViewFlipper viewFlipper;
    boolean Flag=true;
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slideshow);
        Intent intent=getIntent();
        ArrayList<String> data=new ArrayList<String>();
        data.clear();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            data = bundle.getStringArrayList("data");
        }
        viewFlipper=(ViewFlipper) findViewById(R.id.ViewFlipper);
        viewFlipper.removeAllViews();
        for (String photoPath: data){
            ImageView imageView=new ImageView(this);
            File imgFile=new File(photoPath);
            Glide.with(getApplicationContext()).load(imgFile).into(imageView);
            viewFlipper.addView(imageView);
        }
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in); // load an animation
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out); // load an animation

        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);


    }
    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if(Flag==false)
            {
                if (e1.getX() > e2.getX()) {
                    viewFlipper.showNext();
                }
                else if (e1.getX() < e2.getX()) {
                    viewFlipper.showPrevious();
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
        @Override
        public boolean onSingleTapConfirmed (MotionEvent e)
        {
            Animation in = AnimationUtils.loadAnimation(Image_Slideshow.this,android.R.anim.fade_in); // load an animation
            Animation out = AnimationUtils.loadAnimation(Image_Slideshow.this,android.R.anim.fade_out); // load an animation

            viewFlipper.setInAnimation(in);
            viewFlipper.setOutAnimation(out);
            if(Flag==true)
            {
                viewFlipper.stopFlipping();
                Flag = false;
            }
            else
            {
                viewFlipper.startFlipping();
                Flag = true;
            }
            return super.onSingleTapConfirmed(e);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    @Override
    public void onStop() {
        super.onStop();
        MainActivity.collectedimgs.clear();
    }
}
