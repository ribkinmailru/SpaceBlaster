package com.example.spaceblaster;

import android.graphics.PointF;

public class SpaceShip {
    public PointF position;
    public int rotation;
    public CircleColider collider;

    public SpaceShip(PointF position, float width, float height){
        this.position = position;
        collider = new CircleColider(width/2,new PointF(position.x+width/2,position.y+height/2));
        rotation = 0;
    }
}
