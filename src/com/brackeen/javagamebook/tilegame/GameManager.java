package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;

/**
 * GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

    public static void main(String[] args) {

        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT
            = new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private SoundClipped[] ouchSounds;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound personCaptured;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction moveUp;
    private GameAction moveDown;
    private GameAction jump;
    private GameAction exit;
    private GameAction pause;
    private GameAction controls;
    private boolean bPause = false;
    private boolean bControls = false;
    private boolean bExit = false;

    private int vidas;
    private int score;

    public void init() {
        super.init();

        // set up input manager
        initInput();

        vidas = 3;
        score = 0;

        // start resource manager
        resourceManager = new ResourceManager(
                screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
                resourceManager.loadImage("concrete.jpg"));
        
        renderer.setPause(
                resourceManager.loadImage("PauseMenu.png"));
        
        renderer.setControls(
                resourceManager.loadImage("ControlsMenu.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        ouchSounds = new SoundClipped[4];
        for (int i = 0; i < 4; i++) {
            String number = new String(Integer.toString(i+1));
            ouchSounds[i] = new SoundClipped("/sounds/ouch" + number + ".wav");
        }
        // soundManager = new SoundManager(PLAYBACK_FORMAT);
        // personCaptured = soundManager.getSound("src/sounds/ouch.wav");
        //boopSound = soundManager.getSound("src/sounds/boop2.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence
                = midiPlayer.getSequence("src/sounds/beast-attack.mid");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
    }

    /**
     * Closes any resurces used by the GameManager.
     */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }

    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        moveUp = new GameAction("moveUp");
        moveDown = new GameAction("moveDown");
        jump = new GameAction("jump",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        
        pause = new GameAction("pause",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        controls = new GameAction("controls",
                GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
                screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveUp, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(controls, KeyEvent.VK_H);
    }

    private void checkInput(long elapsedTime) {
        if(bPause){
            if (exit.isPressed()) {
                bExit = true;
                if(bExit){
                    stop();
                }
            }
        }
        Player player = (Player) map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            float velocityY = 0;
            if (moveLeft.isPressed()) {
                velocityX -= player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX += player.getMaxSpeed();
            }
            if (moveUp.isPressed()) {
                velocityY -= player.getMaxSpeed();
            }
            if (moveDown.isPressed()) {
                velocityY += player.getMaxSpeed();
            }
            player.setVelocityX(velocityX);
            player.setVelocityY(velocityY);

        }

    }

    public void draw(Graphics2D g) {
        renderer.draw(g, map, screen.getWidth(), screen.getHeight());
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.setColor(Color.BLUE);
        g.drawString("Vidas: " + vidas, 10, screen.getHeight() - 10);
        g.drawString("Score: " + score, 10, screen.getHeight() - 30);
        if(bPause){
            g.drawImage(renderer.Pause, screen.getWidth()/2 - 250, screen.getHeight()/2-250, 500, 500, null);
        }
        
        if(bControls){
                g.drawImage(renderer.Controls, screen.getWidth()/2 - 250, screen.getHeight()/2-250, 500, 500, null);
            }
        
    }

    /**
     * Gets the current map.
     */
    public TileMap getMap() {
        return map;
    }

    /**
     * Turns on/off drum playback in the midi music (track 1).
     */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer == null) {
            sequencer.setTrackMute(DRUM_TRACK,
                    !sequencer.getTrackMute(DRUM_TRACK));
        }
    }

    /**
     * Gets the tile that a Sprites collides with. Only the Sprite's X or Y
     * should be changed, not both. Returns null if no collision is detected.
     */
    public Point getTileCollision(Sprite sprite,
            float newX, float newY) {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
                toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
                toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x = fromTileX; x <= toTileX; x++) {
            for (int y = fromTileY; y <= toTileY; y++) {
                if (x < 0 || x >= map.getWidth()
                        || map.getTile(x, y) != null) {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }

    /**
     * Checks if two Sprites collide with one another. Returns false if the two
     * Sprites are the same. Returns false if one of the Sprites is a Creature
     * that is not alive.
     */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature) s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature) s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth()
                && s2x < s1x + s1.getWidth()
                && s1y < s2y + s2.getHeight()
                && s2y < s1y + s1.getHeight());
    }

    /**
     * Gets the Sprite that collides with the specified Sprite, or null if no
     * Sprite collides with the specified Sprite.
     */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite) i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }

    /**
     * Updates Animation, position, and velocity of all Sprites in the current
     * map.
     */
    public void update(long elapsedTime) {

        Creature player = (Creature) map.getPlayer();

        // player is dead! start map over
        if (vidas <= 0) {
            map = resourceManager.reloadMap();
            vidas = 3;
            score = 0;
            return;
        }
        
        
        
        if(pause.isPressed()){
            if(bPause){
                bPause = false;
                // Make the cursor invisible
                //inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
            }else{
                bPause = true;
                //Make the cursor visible
                //inputManager.setCursor(Cursor.getDefaultCursor());
            }
        }
        
        if(controls.isPressed()){
            if(!bControls){
               bControls = true;
               bPause = false;
            }else{
                bControls = false;
                bPause = true;
            }
        }
        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite) i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature) sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                } else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
    }

    /**
     * Updates the creature, applying gravity for creatures that aren't flying,
     * and checks collisions.
     */
    private void updateCreature(Creature creature,
            long elapsedTime) {

        // apply gravity
        /*if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }*/
        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile
                = getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        } else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                        TileMapRenderer.tilesToPixels(tile.x)
                        - creature.getWidth());
            } else if (dx < 0) {
                creature.setX(
                        TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player) creature, true);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        } else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                        TileMapRenderer.tilesToPixels(tile.y)
                        - creature.getHeight());
            } else if (dy < 0) {
                creature.setY(
                        TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player) creature, true);
        }

    }

    /**
     * Checks for Player collision with other Sprites. If canKill is true,
     * collisions with Creatures will kill them.
     */
    public void checkPlayerCollision(Player player,
            boolean canKill) {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp) collisionSprite);
        } 
        else if (collisionSprite instanceof Virus){
            Creature badguy = (Creature) collisionSprite;
            if(canKill){
                //kill the badguy and make player bounce
                //soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                vidas--;
            }
        }
        else if (collisionSprite instanceof Person) {
            Creature badguy = (Creature) collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                
                int rand = (int)(Math.random() * (4));
                System.out.println(rand);
                ouchSounds[rand].play();
                badguy.setState(Creature.STATE_DYING);
                score += 10;
                //player.setY(badguy.getY() - player.getHeight());
               // player.jump(true);
            }
            else if(vidas<=0) {
                // player.jump(true);
            } else {
                // player dies!
                player.setState(Creature.STATE_DYING);
            }
        }
    }

    /**
     * Gives the player the speicifed power up and removes it from the map.
     */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(personCaptured);
        } else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(personCaptured);
            toggleDrumPlayback();
        } else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(personCaptured,
                    new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }

}
