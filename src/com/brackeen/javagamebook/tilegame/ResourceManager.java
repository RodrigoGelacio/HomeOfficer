package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite person1Sprite;
    private Sprite person2Sprite;
    private Sprite person3Sprite;
    private Sprite person4Sprite;
    private Sprite person5Sprite;
    private Sprite person6Sprite;
    private Sprite virusSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) throws IOException {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) throws IOException {
        String filename = "/images/"+name;
        return ImageIO.read(getClass().getResourceAsStream(filename));
        //new ImageIcon(filename).getImage();
        //read(new File(filename))
        //ImageIO.read(new File(filename));
    }

    /**
     * 
     * @param image
     * @return mirrored image
     */
    public Image getMirrorImage(Image image) {    
        return getScaledImage(image, -1, 1);
    }

    /**
     * 
     * @param image
     * @return flips the image
     */
    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    /**
     * 
     * @param image
     * @param x
     * @param y
     * @return image scaled 
     */
    private Image getScaledImage(Image image, float x, float y) {
        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }

    /**
     * Loads the next map.
     * @return tilemap object
     */
    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "src/maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }

    /**
     * Reloads the tilemap
     * @return tilemap object
     */
    public TileMap reloadMap() {
        try {
            return loadMap(
                "src/maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * It converts the string input into an actual tilemap.
     * @param filename
     * @return tilemap object
     * @throws IOException 
     */
    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);
                
                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, person1Sprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, person2Sprite, x, y);
                }
                else if (ch == '3'){
                   addSprite(newMap, person3Sprite, x, y); 
                }
                else if (ch == '4') {
                    addSprite(newMap, person4Sprite, x, y);
                }
                else if (ch == '5') {
                    addSprite(newMap, person5Sprite, x, y);
                }
                else if (ch == '6'){
                   addSprite(newMap, person6Sprite, x, y); 
                }
                else if (ch == '7'){
                    addSprite(newMap, virusSprite, x, y);
                }
            }
        }

        // adds the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(newMap.getWidth()/2));
        player.setY(TileMapRenderer.tilesToPixels(10));
        newMap.setPlayer(player);
        

        return newMap;
    }

    /**
     * Adds a character to the tilemap.
     * @param map
     * @param hostSprite
     * @param tileX
     * @param tileY 
     */
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() throws IOException {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("src/images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
        
    }

    /**
     * Loads all creature assets.
     */
    public void loadCreatureSprites() throws IOException {

        Image[][] imagesHorizontal = new Image[4][];
        Image[][] imagesUp = new Image[1][];
        Image[][] imagesDown = new Image[1][];
        // load left-facing images
        imagesHorizontal[0] = new Image[] {
            loadImage("playerLeft1.png"),
            loadImage("playerLeft2.png"),
            loadImage("playerLeft3.png"),
            loadImage("person1Left1.png"),
            loadImage("person1Left2.png"),
            loadImage("person1Left3.png"),
            loadImage("person2Left1.png"),
            loadImage("person2Left2.png"),
            loadImage("person2Left3.png"),
            loadImage("person3Left1.png"),
            loadImage("person3Left2.png"),
            loadImage("person3Left3.png"),
            loadImage("person4Left1.png"),
            loadImage("person4Left2.png"),
            loadImage("person4Left3.png"),
            loadImage("person5Left1.png"),
            loadImage("person5Left2.png"),
            loadImage("person5Left3.png"),
            loadImage("person6Left1.png"),
            loadImage("person6Left2.png"),
            loadImage("person6Left3.png"),
            loadImage("virus1.png"),
            loadImage("virus2.png"),
            loadImage("virus3.png"),
            
        };
        
        //load up images
        imagesUp[0] = new Image[]{
            loadImage("playerUp1.png"),
            loadImage("playerUp2.png"),
            loadImage("playerUp3.png"),
            loadImage("person1Up1.png"),
            loadImage("person1Up2.png"),
            loadImage("person1Up3.png"),
            loadImage("person2Up1.png"),
            loadImage("person2Up2.png"),
            loadImage("person2Up3.png"),
            loadImage("person3Up1.png"),
            loadImage("person3Up2.png"),
            loadImage("person3Up3.png"),
            loadImage("person4Up1.png"),
            loadImage("person4Up2.png"),
            loadImage("person4Up3.png"),
            loadImage("person5Up1.png"),
            loadImage("person5Up2.png"),
            loadImage("person5Up3.png"),
            loadImage("person6Up1.png"),
            loadImage("person6Up2.png"),
            loadImage("person6Up3.png"),
            loadImage("virus1.png"),
            loadImage("virus2.png"),
            loadImage("virus3.png"),
        };
        
        //load down images
        imagesDown[0] = new Image[]{
            loadImage("playerDown1.png"),
            loadImage("playerDown2.png"),
            loadImage("playerDown3.png"),
            loadImage("person1Down1.png"),
            loadImage("person1Down2.png"),
            loadImage("person1Down3.png"),
            loadImage("person2Down1.png"),
            loadImage("person2Down2.png"),
            loadImage("person2Down3.png"),
            loadImage("person3Down1.png"),
            loadImage("person3Down2.png"),
            loadImage("person3Down3.png"),
            loadImage("person4Down1.png"),
            loadImage("person4Down2.png"),
            loadImage("person4Down3.png"),
            loadImage("person5Down1.png"),
            loadImage("person5Down2.png"),
            loadImage("person5Down3.png"),
            loadImage("person6Down1.png"),
            loadImage("person6Down2.png"),
            loadImage("person6Down3.png"),
            loadImage("virus1.png"),
            loadImage("virus2.png"),
            loadImage("virus3.png"),
        };
        
        //Prepares the next array to save the next characters animation
        imagesHorizontal[1] = new Image[imagesHorizontal[0].length];
        imagesHorizontal[2] = new Image[imagesHorizontal[0].length];
        imagesHorizontal[3] = new Image[imagesHorizontal[0].length];
        for (int i=0; i<imagesHorizontal[0].length; i++) {
            // right-facing images
            imagesHorizontal[1][i] = getMirrorImage(imagesHorizontal[0][i]);
            // left-facing "dead" images
            imagesHorizontal[2][i] = getFlippedImage(imagesHorizontal[0][i]);
            // right-facing "dead" images
            imagesHorizontal[3][i] = getFlippedImage(imagesHorizontal[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[6];
        Animation[] person1Anim = new Animation[6];
        Animation[] person2Anim = new Animation[6];
        Animation[] person3Anim = new Animation[6];
        Animation[] person4Anim = new Animation[6];
        Animation[] person5Anim = new Animation[6];
        Animation[] person6Anim = new Animation[6];
        Animation[] virusAnim = new Animation[6];
        
        //loads all horizontal images for the persons in the game
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                imagesHorizontal[i][0], imagesHorizontal[i][1], imagesHorizontal[i][2]);
            person1Anim[i] = createPersonAnim(
                imagesHorizontal[i][3], imagesHorizontal[i][4], imagesHorizontal[i][5]);
            person2Anim[i] = createPersonAnim(
                imagesHorizontal[i][6], imagesHorizontal[i][7], imagesHorizontal[i][8]);
            person3Anim[i] = createPersonAnim(
                imagesHorizontal[i][9], imagesHorizontal[i][10], imagesHorizontal[i][11]);
            person4Anim[i] = createPersonAnim(
                imagesHorizontal[i][12], imagesHorizontal[i][13], imagesHorizontal[i][14]);
            person5Anim[i] = createPersonAnim(
                imagesHorizontal[i][15], imagesHorizontal[i][16], imagesHorizontal[i][17]);
            person6Anim[i] = createPersonAnim(
                imagesHorizontal[i][18], imagesHorizontal[i][19], imagesHorizontal[i][20]);  
            virusAnim[i] = createPersonAnim(
                imagesHorizontal[i][21], imagesHorizontal[i][22], imagesHorizontal[i][23]);
        }
        
        //Loads all up and down animations of the persons in the game.
        playerAnim[4] = createPlayerAnim(imagesUp[0][0], imagesUp[0][1],imagesUp[0][2]);
        playerAnim[5] = createPlayerAnim(imagesDown[0][0], imagesDown[0][1],imagesDown[0][2]);
        person1Anim[4] = createPersonAnim(imagesUp[0][3], imagesUp[0][4], imagesUp[0][5]);
        person1Anim[5] = createPersonAnim(imagesDown[0][3], imagesDown[0][4], imagesDown[0][5]);
        person2Anim[4] = createPersonAnim(imagesUp[0][6], imagesUp[0][7], imagesUp[0][8]);
        person2Anim[5] = createPersonAnim(imagesDown[0][6], imagesDown[0][7], imagesDown[0][8]);
        person3Anim[4] = createPersonAnim(imagesUp[0][9], imagesUp[0][10], imagesUp[0][11]);
        person3Anim[5] = createPersonAnim(imagesDown[0][9], imagesDown[0][10], imagesDown[0][11]);
        person4Anim[4] = createPersonAnim(imagesUp[0][12], imagesUp[0][13], imagesUp[0][14]);
        person4Anim[5] = createPersonAnim(imagesDown[0][12], imagesDown[0][13], imagesDown[0][14]);
        person5Anim[4] = createPersonAnim(imagesUp[0][15], imagesUp[0][16], imagesUp[0][17]);
        person5Anim[5] = createPersonAnim(imagesDown[0][15], imagesDown[0][16], imagesDown[0][17]);
        person6Anim[4] = createPersonAnim(imagesUp[0][18], imagesUp[0][19], imagesUp[0][20]);
        person6Anim[5] = createPersonAnim(imagesDown[0][18], imagesDown[0][19], imagesDown[0][20]);
        virusAnim[4] = createPersonAnim(imagesUp[0][21], imagesUp[0][22], imagesUp[0][23]);
        virusAnim[5] = createPersonAnim(imagesDown[0][21], imagesDown[0][22], imagesDown[0][23]);

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1], playerAnim[4], playerAnim[5],
            playerAnim[2], playerAnim[3]);
        person1Sprite = new Person(person1Anim[0], person1Anim[1], person1Anim[4], person1Anim[5], 
                person1Anim[2], person1Anim[3]);
        person2Sprite = new Person(person2Anim[0], person2Anim[1], person2Anim[4], person2Anim[5], 
                person2Anim[2], person2Anim[3]);
        person3Sprite = new Person(person3Anim[0], person3Anim[1], person3Anim[4], person3Anim[5], 
                person3Anim[2], person3Anim[3]);
        person4Sprite = new Person(person4Anim[0], person4Anim[1], person4Anim[4], person4Anim[5], 
                person4Anim[2], person4Anim[3]);
        person5Sprite = new Person(person5Anim[0], person5Anim[1], person5Anim[4], person5Anim[5], 
                person5Anim[2], person5Anim[3]);
        person6Sprite = new Person(person6Anim[0], person6Anim[1], person6Anim[4], person6Anim[5], 
                person6Anim[2], person6Anim[3]);
        virusSprite = new Virus(virusAnim[0], virusAnim[1], virusAnim[4], virusAnim[5], 
                virusAnim[2], virusAnim[3]);
    }

    /**
     * Stores the images in an animation List, and also the time in milliseconds to show the image.
     * @param player1
     * @param player2
     * @param player3
     * @return animation Object
     */
    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 150);
        return anim;
    }
    
    /**
     * 
     * @param person1
     * @param person2
     * @param person3
     * @return 
     */
    private Animation createPersonAnim(Image person1,
            Image person2, Image person3){
        Animation anim = new Animation();
        anim.addFrame(person1, 150);
        anim.addFrame(person2, 150);
        anim.addFrame(person3, 150);
        return anim;
    }

}
