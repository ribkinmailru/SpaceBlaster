package com.example.spaceblaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CustomGameThread extends Thread {
    private long prevTime;
    private SurfaceHolder surfaceHolder;
    public boolean runFlag;
    private long createTime, nexttime, nextscore;
    private long cointime, cointtimedif, counttoscore;
    private Bitmap meteor, ship, coin, background, point,star_start,rock_start;
    private Matrix ship_matrix,back, back_second,start_button,start_button2,second_start_button,money,score;
    private ArrayList<Meteor> meteors,coins,points;
    private int direction;
    private boolean move;
    public SpaceShip space_ship;
    public int maxrotate;
    public float background_first_heigth;
    public boolean animationended;
    public Path path;
    private Paint paint,text;
    public RectF start,stop;
    private PointF star,rock;
    private float diff;
    PointF startposition;
    private float starttextposition_x;
    CustomSerfaceView surf;
    public CustomGameThread(SurfaceHolder surfaceHolder, Context context, CustomSerfaceView surf) {
        this.surfaceHolder = surfaceHolder;
        back = new Matrix();
        meteor = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.rock), 140, 105, true);
        coin = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.coin), 60, 60, true);
        point = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.friendlypoint), 80, 80, true);
        ship = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.main), 130, 130, true);
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.back2), (int)Game_Activity.width, (int)Game_Activity.height, true);

        start_button = new Matrix();
        second_start_button = new Matrix();
        startposition = new PointF(Game_Activity.width / 2 - ship.getWidth()/2f,Game_Activity.height - ship.getHeight()-15);
        diff = startposition.y+ship.getHeight()+15;
        drawmenu();
        this.surf = surf;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    public void run() {
        Canvas canvas = null;
        while (runFlag) {
            if(!GameManager.pause) {
                long now = System.currentTimeMillis();
                long elapsedTime = now - prevTime;
                if (elapsedTime > 10) {
                    if (!animationended) {
                        second_start_button.postTranslate(-10f, 0);
                        start_button.postTranslate(-10f, 0);
                        start_button.postRotate(-10, star.x + star_start.getWidth() / 2f, star.y + star_start.getHeight() / 2f);
                        star.offset(-10f, 0);
                        if (diff > startposition.y) {
                            ship_matrix.postTranslate(0, -2f);
                            diff += -2f;
                        } else {
                            ship_matrix.setTranslate(startposition.x, startposition.y);
                        }
                        start_button2.postTranslate(10f, 0);
                        starttextposition_x += 20f;
                        path.transform(start_button2);
                    } else {
                        prevTime = now;
                        float b = Game_Activity.height / 200;
                        back.postTranslate(0, b);
                        back_second.postTranslate(0, b);
                        background_first_heigth++;
                        if (background_first_heigth == 200) {
                            back.setTranslate(0, -Game_Activity.height);
                        } else if (background_first_heigth == 400) {
                            back_second.setTranslate(0, -Game_Activity.height);
                            background_first_heigth = 0;
                        }
                        float t = direction * 20;
                        if (move && space_ship.position.x + t < Game_Activity.width - ship.getWidth() && space_ship.position.x + t > 0) {
                            ship_matrix.postTranslate(t, 0);
                            space_ship.position.offset(t, 0);
                            space_ship.collider.position.offset(t, 0);
                            if (space_ship.rotation < maxrotate) {
                                ship_matrix.postRotate(direction, (space_ship.position.x + ship.getWidth() / 2f), space_ship.position.y + ship.getHeight() / 2f);
                                space_ship.rotation += 1;
                            } else if (space_ship.rotation >= -maxrotate) {
                                ship_matrix.postRotate(direction, (space_ship.position.x + ship.getWidth() / 2f), space_ship.position.y + ship.getHeight() / 2f);
                                space_ship.rotation -= 1;
                            }
                        }
                        for (int i = 0; i < meteors.size(); i++) {
                            Meteor m = meteors.get(i);
                            float x = (m.direction - m.spawnposition) / m.speed;
                            float y = Game_Activity.height / m.speed;
                            m.matrix.postTranslate(x, y);
                            m.collider.position.offset(x, y);
                            m.y = m.y + y;
                            if (m.y > Game_Activity.height + meteor.getHeight()) {
                                meteors.remove(m);
                            } else if (checkcollision(space_ship.collider, m.collider)) {
                                meteors.remove(m);
                                stopgame();
                            }
                        }
                        for (int i = 0; i < coins.size(); i++) {
                            Meteor m = coins.get(i);
                            float x = (m.direction - m.spawnposition) / m.speed;
                            float y = Game_Activity.height / m.speed;
                            m.matrix.postTranslate(x, y);
                            m.collider.position.offset(x, y);
                            m.y = m.y + y;
                            if (m.y > Game_Activity.height) {
                                coins.remove(m);
                            } else if (checkcollision(space_ship.collider, m.collider)) {
                                GameManager.open.play(3, 1f, 1f, 1, 0, 1);
                                coins.remove(m);
                                GameManager.coins++;
                            }
                        }
                        for (int i = 0; i < points.size(); i++) {
                            Meteor m = points.get(i);
                            float x = (m.direction - m.spawnposition) / m.speed;
                            float y = Game_Activity.height / m.speed;
                            m.matrix.postTranslate(x, y);
                            m.center.offset(x, y);
                            m.matrix.postRotate(5f, m.center.x, m.center.y);
                            m.collider.position.offset(x, y);
                            m.y = m.y + y;
                            if (m.y > Game_Activity.height) {
                                points.remove(m);
                            } else if (checkcollision(space_ship.collider, m.collider)) {
                                GameManager.open.play(2, 1f, 1f, 1, 0, 1);
                                points.remove(m);
                                GameManager.score++;
                            }
                        }
                        long createt = now - createTime;
                        if (createt > nexttime) {
                            nexttime = (int) (300 + Math.random() * 2000);
                            createTime = now;
                            Meteor meteor = new Meteor(this.meteor.getWidth(), false);
                            meteors.add(meteor);
                        }
                        long good = now - cointime;
                        if (good > cointtimedif) {
                            cointtimedif = (int) (300 + Math.random() * 2000);
                            cointime = now;
                            Meteor meteor = new Meteor(this.coin.getWidth(), true);
                            coins.add(meteor);
                        }
                        long lets = now - counttoscore;
                        if (lets > nextscore) {
                            nextscore = (int) (300 + Math.random() * 2000);
                            counttoscore = now;
                            Meteor meteor = new Meteor(this.point.getWidth(), true);
                            points.add(meteor);
                        }
                    }

                }


                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        if (!animationended) {
                            canvas.drawBitmap(background, back, null);
                            canvas.drawPath(path, paint);
                            canvas.drawBitmap(Bitmap.createScaledBitmap(point, 130, 130, true), start_button, null);
                            canvas.drawBitmap(Bitmap.createScaledBitmap(meteor, 170, 130, true), second_start_button, null);
                            canvas.drawBitmap(ship, ship_matrix, null);
                            canvas.drawText("Start", starttextposition_x, Game_Activity.height / 2 - 33f, text);
                            canvas.drawText("Exit", starttextposition_x, Game_Activity.height / 2 + 108f, text);
                        } else {
                            try {
                            canvas.drawBitmap(background, back, null);
                            canvas.drawBitmap(background, back_second, null);
                            canvas.drawBitmap(coin,money,null);
                            canvas.drawText(String.valueOf(GameManager.coins),80f,55f,text);
                            canvas.drawBitmap(point,score,null);
                            canvas.drawText(Integer.toString(GameManager.score),Game_Activity.width-50f,60f,text);
                            for (Meteor i : meteors) {
                                canvas.drawBitmap(meteor, i.matrix, null);
                            }
                            for (Meteor i : coins) {
                                canvas.drawBitmap(coin, i.matrix, null);
                            }
                            for (Meteor i : points) {
                                canvas.drawBitmap(point, i.matrix, null);
                            }
                            canvas.drawBitmap(ship, ship_matrix, null);

                            }catch (NullPointerException ignored){}
                        }
                    }
                } finally {
                    if (canvas != null) {
                        // отрисовка выполнена. выводим результат на экран
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public void drawmenu(){
        Canvas canvas = null;
        canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(null);
            synchronized (surfaceHolder) {
                canvas.drawBitmap(background, back,null);
                start = new RectF();
                path = new Path();
                score= new Matrix();
                money = new Matrix();
                money.setTranslate(10f,10f);
                score.setTranslate(Game_Activity.width-coin.getWidth()-10f,10f);
                start.set(Game_Activity.width/2-200f, Game_Activity.height/2-100f, Game_Activity.width/2+200f, Game_Activity.height/2);
                stop = new RectF();
                stop.set(Game_Activity.width/2-200f, Game_Activity.height/2+40f, Game_Activity.width/2+200f, Game_Activity.height/2+140f);
                paint = new Paint();
                text = new Paint();
                text.setColor(Color.WHITE);
                text.setTextSize(50);
                paint.setColor(Color.parseColor("#FF5722"));
                star = new PointF(Game_Activity.width/2-245f,Game_Activity.height/2-120f);
                rock = new PointF(Game_Activity.width/2-245f,Game_Activity.height/2+25f);
                starttextposition_x = Game_Activity.width/2-15f;
                start_button.setTranslate(star.x,star.y);
                second_start_button.setTranslate(rock.x,rock.y);
                path.addRoundRect(start,20f,20f,Path.Direction.CW);
                path.addRoundRect(stop,20f,20f,Path.Direction.CW);
                start_button2 = new Matrix();
                canvas.drawBitmap(background, back,null);
                path.transform(start_button2);
                canvas.drawPath(path,paint);
                star_start = Bitmap.createScaledBitmap(point, 130, 130, true);
                rock_start = Bitmap.createScaledBitmap(meteor, 170, 130, true);
                canvas.drawBitmap(star_start,start_button,null);
                canvas.drawBitmap(rock_start,second_start_button,null);
                canvas.drawText("Start", starttextposition_x, Game_Activity.height/2-33f,text);
                canvas.drawText("Exit", starttextposition_x, Game_Activity.height/2+108f,text);
                canvas.drawBitmap(coin,money,null);
                canvas.drawText(String.valueOf(GameManager.coins),80f,55f,text);
                canvas.drawText("Record:"+GameManager.record,Game_Activity.width-220f,55f,text);
            }
        }
        finally {
            if (canvas != null) {
                // отрисовка выполнена. выводим результат на экран
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void startgame(Context context){
        GameManager.score = 0;
        GameManager.player = MediaPlayer.create(context, R.raw.main);
        GameManager.player.setLooping(true);
        GameManager.player.setVolume(0.4f, 0.4f);
        GameManager.player.start();
        SensorManager sensor = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor acselerometr = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] > 0.1) {
                        move = true;
                        direction = -1;
                    } else if (event.values[0] < -0.1) {
                        move = true;
                        direction = 1;
                    } else {
                        move = false;
                    }
                }

                @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, acselerometr, SensorManager.SENSOR_DELAY_NORMAL);
        nexttime = 1500;
        cointtimedif = 2000;
        maxrotate = 2;
        meteors = new ArrayList<>();
        coins = new ArrayList<>();
        points = new ArrayList<>();
        ship_matrix = new Matrix();
        back_second = new Matrix();
        back_second.setTranslate(0,-Game_Activity.height);
        ship_matrix.setTranslate(startposition.x,startposition.y+ship.getHeight()+15);
        space_ship = new SpaceShip(startposition,ship.getWidth(), ship.getHeight());
        setRunning(true);
    }


    private boolean checkcollision(CircleColider ship, CircleColider el){
        float x_dif = ship.position.x - el.position.x;
        float t_dif = ship.position.y - el.position.y;
        float distance =(float) Math.sqrt( (x_dif*x_dif) + (t_dif*t_dif) );
        return distance <= ship.radius + el.radius;
    }

    public boolean checkcollisionbutton(RectF button, PointF click){
        if(button.left < click.x && button.right>click.x && button.top<click.y && button.bottom>click.y){
            return true;
        }
        return false;
    }

    public void stopgame(){
        if(GameManager.score>GameManager.record){
            GameManager.record = GameManager.score;
            SharedPreferences pref = surf.getContext().getApplicationContext().getSharedPreferences("Stats",MODE_PRIVATE);
            SharedPreferences.Editor edir = pref.edit();
            edir.putInt("record",GameManager.record);
            edir.apply();
        }
        setRunning(false);
        GameManager.player.stop();
        Handler handl = new Handler(Looper.getMainLooper());
        handl.postDelayed(new Runnable() {
            @Override
            public void run() {
                surf.thread = new CustomGameThread(surfaceHolder, surf.getContext(), surf);
            }
    },500);

    }
}
