package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {

    public Grub(Animation left, Animation right,Animation up, Animation down,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft,up,down, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
