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
    
    Player player;
    private float SPEED = 0.3f;
    private boolean controlVelocityX;
    private boolean controlVelocityY;
    
    
    public Virus(Animation left, Animation right, Animation up, Animation down, Animation deadLeft, Animation deadRight) {
        super(left, right, up, down, deadLeft, deadRight);
        controlVelocityX = true;
        controlVelocityY = true;
    }
    
    public float getMaxSpeed() {
        return SPEED;
    }

   
    public void setMaxSpeed(float f) {
        SPEED = f;
    }
  
   /* public void setVelocityX(float f){
        this.velocityX = f;
    }
    
    //To follow the player thorughout the map

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }
   */

    public void setControlVelocityX(boolean controlVelocityX) {
        this.controlVelocityX = controlVelocityX;
    }

    public void setControlVelocityY(boolean controlVelocityY) {
        this.controlVelocityY = controlVelocityY;
    }

    public boolean isControlVelocityX() {
        return controlVelocityX;
    }

    public boolean isControlVelocityY() {
        return controlVelocityY;
    }
    
}
