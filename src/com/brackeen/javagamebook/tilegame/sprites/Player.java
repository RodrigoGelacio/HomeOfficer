package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;

    public Player(Animation left, Animation right,Animation up, Animation down,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right,up,down, deadLeft, deadRight);
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        setVelocityY(0);
    }


    public void setY(float y) {
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
