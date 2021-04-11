package com.example.spaceblaster;

import android.graphics.Matrix;
import android.graphics.PointF;

public class Meteor {
    public Matrix matrix;
    public int speed;
    float spawnposition;
    float y;
    public int direction;
    public boolean coin;
    public CircleColider collider;
    public PointF center;
    public Meteor(float t, boolean coin) {
        this.coin = coin;
        matrix = new Matrix();
        direction = (int)(Math.random()*(Game_Activity.width-t));
        spawnposition =  (int)(Math.random()*(Game_Activity.width - t));
        speed = (int)(50+Math.random()*40);
        matrix.setTranslate(spawnposition, -t);
        collider = new CircleColider(t/2,new PointF(spawnposition+t/2, -t));
        this.center = new PointF(spawnposition+t/2,-t/2);
    }
}
