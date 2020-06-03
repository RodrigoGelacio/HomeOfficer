package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    
    
    //Speed of the player
    private float SPEED = 0.5f;

    //Player Animations
    public Player(Animation left, Animation right,Animation up, Animation down,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right,up,down, deadLeft, deadRight);
    }


    //Called if the player collided with a tile horizontally
    public void collideHorizontal() {
        setVelocityX(0);
    }

    //Called if the player collided with a tile vertically
    public void collideVertical() {
        setVelocityY(0);
    }
    
    //keep track of the player in y
    public void setY(float y) {
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }

    //Set Max Speed of the player
    public void setMaxSpeed(float f){
        SPEED = f;
    }

    //Get Max Speed of the player
    public float getMaxSpeed() {
        return SPEED;
    }

}
