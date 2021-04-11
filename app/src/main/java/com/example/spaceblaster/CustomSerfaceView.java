package com.example.spaceblaster;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;


public class CustomSerfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    Context context;
    CustomGameThread thread;
    Activity activity;

    public CustomSerfaceView(Context context, Activity activity) {
        super(context);
        getHolder().addCallback(this);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.context = context;
        this.activity = activity;
        this.setOnTouchListener(this);
        GameManager.open = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        GameManager.open.load(context, R.raw.open,1);
        GameManager.open.load(context, R.raw.score,2);
        GameManager.open.load(context, R.raw.money,3);
    }

    public CustomSerfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSerfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread = new CustomGameThread(holder, getContext(),this);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(thread.checkcollisionbutton(thread.start, new PointF(event.getX(),event.getY())) && !thread.runFlag){
                    thread.startgame(getContext());
                    thread.start();
                    GameManager.open.play(1,0.8f,0.8f,1,0,1);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable () {
                        public void run() {
                            thread.animationended = true;
                        }
                    }, 2000);
                }else if(thread.checkcollisionbutton(thread.stop, new PointF(event.getX(),event.getY())) && !thread.runFlag){
                    activity.finish();
                }
            }
        return false;
    }
}
