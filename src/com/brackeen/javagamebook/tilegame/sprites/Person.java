/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
 *
 * The persons to save
 */
public class Person extends Creature{
    
    public static final float SPEED = 0.2f;
    
    public Person(Animation left, Animation right, Animation up, Animation down,
            Animation deadLeft, Animation deadRight) {
        super(left, right, up, down, deadLeft, deadRight);
    }
    
    
//    public void setY(float y) {
//        super.setY(y);
//    }
    
    public float getMaxSpeed() {
        return 0.2f;
    }
//    
//    public void setVelocityY(){
//        
//    }
}
    
