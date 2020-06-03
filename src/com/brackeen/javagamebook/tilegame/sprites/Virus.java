/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 * The Virus
 */
public class Virus extends Creature{
    
    //Speed of the virus
    private float SPEED = 0.3f;
    
    //Control Velocity in X to lunge against the player
    private boolean controlVelocityX;
    //Control Velocity in Y to lunge against the player
    private boolean controlVelocityY;
    
    //Virus Animations
    public Virus(Animation left, Animation right, Animation up, Animation down, Animation deadLeft, Animation deadRight) {
        super(left, right, up, down, deadLeft, deadRight);
        controlVelocityX = true;
        controlVelocityY = true;
    }
    
    //return Max Speed of the Virus
    public float getMaxSpeed() {
        return SPEED;
    }

   //Sets Max Speed of the Virus
    public void setMaxSpeed(float f) {
        SPEED = f;
    }
  
    //Sets control velocity X of the virus
    public void setControlVelocityX(boolean controlVelocityX) {
        this.controlVelocityX = controlVelocityX;
    }

    //Sets control velocity Y of the virus
    public void setControlVelocityY(boolean controlVelocityY) {
        this.controlVelocityY = controlVelocityY;
    }

    //Gets control velocity X
    public boolean isControlVelocityX() {
        return controlVelocityX;
    }

    //gets control velocity y
    public boolean isControlVelocityY() {
        return controlVelocityY;
    }
    
}
