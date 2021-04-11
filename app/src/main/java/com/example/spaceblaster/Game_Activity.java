package com.example.spaceblaster;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class Game_Activity extends AppCompatActivity {
    FrameLayout frame;
    public static float height;
    public static float width;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frame = findViewById(R.id.frame);
        setContentView(R.layout.activity_game_);
        SharedPreferences pref = getSharedPreferences("Stats",MODE_PRIVATE);
        GameManager.record = pref.getInt("record", 0);
        GameManager.coins = pref.getInt("money",0);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        initGame(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFullscreen();
        if(GameManager.pause){
            GameManager.player = MediaPlayer.create(this, R.raw.main);
            GameManager.player.setLooping(true);
            GameManager.player.setVolume(0.4f, 0.4f);
            GameManager.player.start();
        }
        GameManager.pause = false;
    }



    @Override
    protected void onStop() {
        super.onStop();
        GameManager.pause = true;
        if(GameManager.player!=null) {
            GameManager.player.stop();
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Stats",MODE_PRIVATE);
        SharedPreferences.Editor edir = pref.edit();
        Log.d("rgddv","dfdfsdf");
        edir.putInt("record",GameManager.record);
        edir.putInt("money", GameManager.coins);
        edir.apply();
    }


    public void initGame(boolean game){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg;
        if(!game){
            fg = null; // new Menu_Fragment();
        }else{
            fg = new Game_Fragment();
        }
        fm.beginTransaction().replace(R.id.frame, fg).commit();
    }

    private void setFullscreen(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}