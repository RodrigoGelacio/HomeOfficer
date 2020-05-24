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
    public static final float SPEED = 0.3f;
    
    public Virus(Animation left, Animation right, Animation up, Animation down, Animation deadLeft, Animation deadRight) {
        super(left, right, up, down, deadLeft, deadRight);
    }
    
    public float getMaxSpeed() {
        return 0.3f;
    }
  
    //To follow the player thorughout the map
    public void setVelocityX(){
        if (player.getX() > getX()){
            setX(getX() + 1);
        }
        else if(player.getX() < getX()){
            setX(getX() - 1);
        }
    }
    
    public void setVelocityY(){
        if (player.getY() > getY()){
            setY(getY() + 1);
        }
        else if (player.getY() < getY()){
            setY(getY() - 1);
        }
    }
}
