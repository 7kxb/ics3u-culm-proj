// ------------------------------------------------------------------------------
// * Culminating Java project for ICS3U, by Kevin X.
// ! Packages
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;
// TODO: Use this combo to fold and unfold in VSC: Ctrl+K, (Ctrl+0 || Ctrl+J)
// TODO: Download the better comments extension on VSC
// ? "It works on my machine, I use arch btw"
// ------------------------------------------------------------------------------
public class Game extends Canvas implements Runnable { // ! contains the main game loop, basically stitches everything together
    public static int WIDTH = 800, HEIGHT = 600; // * setting width and height variables
    public static double xScale = 800.0/WIDTH, yScale = 600.0/HEIGHT; // * scaling for resized windows
    private Thread thread; // * initialize thread
    private boolean running = false; // * a variable indicating whether or not the game is running, used for checks
    private Handler handler; // * initialize handler
    public static int red = (int) (Math.random()*128); // ? randomize background colour, red channel
    public static int green = (int) (Math.random()*128); // ? randomize background colour, green channel
    public static int blue = (int) (Math.random()*128); // ? randomize background colour, blue channel
    public static Color randomColor = new Color(red, green, blue); // * randomize background colour
    public Game() { // * instance of the game
        handler = new Handler(); // * utilizes the handler we initialized and makes an instances
        this.addKeyListener(new KeyInput(handler)); // * adds a keylistener to our handler
        new Window(WIDTH, HEIGHT, "Game", this); // * creates our window with specified details
        // ! adding gameObjects to the handler
        handler.addObject(new Background((int)(100/xScale),(int)(100/yScale),ID.Background));
        handler.addObject(new Button((int)(200/xScale),(int)(300/yScale),ID.PlayButton));
        handler.addObject(new Button((int)(200/xScale),(int)(400/yScale),ID.ConfigButton));
        handler.addObject(new Monkey((int)(0/xScale),(int)(0/yScale),ID.BarMonkey));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.TitleMonkey));
        handler.addObject(new Monkey((int)(100/xScale),(int)(200/yScale),ID.TypeMonkey));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.HitMonkey));
        handler.addObject(new Monkey((int)(650/xScale),(int)(50/yScale),ID.ScoreMonkey));
        handler.addObject(new Monkey((int)(300/xScale),(int)(250/yScale),ID.ResultMonkey));
        handler.addObject(new Button((int)(50/xScale),(int)(500/yScale),ID.LeaveButton));
        handler.addObject(new Button((int)(550/xScale),(int)(500/yScale),ID.StartButton));
        handler.addObject(new Button((int)(350/xScale),(int)(50/yScale),ID.Level1Button));
        handler.addObject(new Button((int)(350/xScale),(int)(150/yScale),ID.Level2Button));
    }
    public synchronized void start() {thread = new Thread(this); thread.start(); running = true;} // * start method/function
    public synchronized void stop() {try {thread.join(); running = false;} catch (Exception e) {}} // * stop method/function
    public void run() { // * run method/function
        this.requestFocus(); // * requests the window to be automatically focused on startup
        long lastTime = System.nanoTime(); // * sets the last frame/tick to the system's internal clock
        double amountOfTicks = 100.0; // * 100 ticks or 100 fps
        double ns = 1_000_000_000.0 / amountOfTicks; // * defines how many nanoseconds are in a tick/frame
        double delta = 0; // * the amount of time since the last frame/tick and the next tick/frame
        long timer = System.currentTimeMillis(); // * a timer using the system's internal clock
        int frames = 0; // * a timer using the system's internal clock
        int ticks = 0; // * total amount of ticks that have been passed since
        while (running) { // * you can think of this as our main game loop (pygame devs)
            long now = System.nanoTime(); // * sets now variable to... well, now
            delta += (now - lastTime)/ns; // * adds the amount of nanoseconds between now and lasttime to delta
            lastTime = now; // * sets lasttime to now
            while (delta >= 1) {
                tick(ticks); // * pass a tick to the handler
                delta--; // * subtract 1 from delta
                ticks++; // * add 1 to the total amount of ticks that have been passed since
                if (running) {
                    Toolkit.getDefaultToolkit().sync(); // ? syncs the fps/tick on certain desktop enviroments on linux systems, irrelevant to windows/mac (idk why X11 and Wayland breaks someone who's also a nerd plz help)
                    render(); // * renders everything that the handler returns after we had just passed a tick to it
                    frames++; // * add 1 to the total amount of frames that have been passed since
                }
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                // TODO: System.out.println("FPS: "+frames+" | TPS: "+ticks);
                frames = 0;
                ticks = 0;
            }
        }
        stop(); // * if running is not true, then call stop
    }
    private void tick(int ticks) {handler.tick(ticks);} // * see main game loop (running), for info on how it's processed
    private void render() {
        BufferStrategy bs = this.getBufferStrategy(); // * gets our bufferstrategy
        if (bs == null) {this.createBufferStrategy(3); return;} // * since we currently don't have one, lets create one
        Graphics g = bs.getDrawGraphics(); // * our graphics (instance? idk what you'd describe this)
        g.setColor(Game.randomColor); // * set the colour of the (pen? if you used python turtle, you'd know) to our random colour
        g.fillRect(0,0,WIDTH,HEIGHT); // * fill the entire screen/window/display with a random coloured rectangle
        handler.render(g); // * renders our graphics using our handler
        g.dispose(); // * disposes of our graphics (instance? again idk)
        bs.show(); // * uses our bufferstrategy to show the new changes
    }
    public static void main(String[] args) { // * our main just calls a new game instance
        if (args.length != 0) {int w = Integer.parseInt(args[0]); int h = Integer.parseInt(args[1]);}
        new Game();
    }
}
// ------------------------------------------------------------------------------
class Button extends GameObject { // ! class for button gameobjects
    static boolean searching = false;
    static boolean L1 = false;
    static boolean L2 = false;
    static int w = Game.WIDTH/2;
    static int h = Game.HEIGHT/8;
    public Button (int x, int y, ID id) {super(x, y, id);}
    public void tick(int ticks) {}
    public void render(Graphics g) {
        if (id == ID.PlayButton && Button.searching == false && Monkey.typing == false) {
            g.setColor(Color.white);
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("F* - Play", x+w/8, y+h-h/4);
        }
        if (id == ID.ConfigButton && Button.searching == false && Monkey.typing == false) {
            g.setColor(Color.white);
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("J* - Config", x+w/8, y+h-h/4);
        }
        if (id == ID.LeaveButton && Button.searching) {
            g.setColor(Color.white);
            g.fillRect(x, y, w/2, h/2);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h/2-h/8));
            g.drawString("L* - Leave", x+w/16, y+h/2-h/8);
        }
        if (id == ID.StartButton && Button.searching && (L1 || L2) && Monkey.resultScreen == false) {
            g.setColor(Color.white);
            g.fillRect(x, y, w/2, h/2);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h/2-h/8));
            g.drawString("S* - Start", x+w/16, y+h/2-h/8);
        }
        if (id == ID.Level1Button && Button.searching && Monkey.resultScreen == false) {
            if (L1 == false) {g.setColor(Color.white);} else {g.setColor(Color.yellow);}
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("Q* - Padoru", x+w/8, y+h-h/4);
        }
        if (id == ID.Level2Button && Button.searching && Monkey.resultScreen == false) {
            if (L2 == false) {g.setColor(Color.white);} else {g.setColor(Color.yellow);}
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("W* - Test", x+w/8, y+h-h/4);
        }
    }
}
// ------------------------------------------------------------------------------
class Monkey extends GameObject { // ! class for text based gameobjects
    static int w = 0;
    static int h = Game.HEIGHT/20;
    static String qwerty = "";
    static boolean init = false;
    static boolean free = false;
    public static int eventNumber = 0;
    static String event = "";
    static int level = 0;
    static boolean typing = false;
    static int timer = 0;
    static int[] position = {0, 102, 354, 613, 859, 1144};
    static String[] text = {"","HASHIRESORIYO", "KAZENOYOUNI", "TSUKIMIHARAWO","PADORUPADORU",""};
    static int[] duration = {102, 354-102, 613-354, 859-613, 1144-859, 100};
    static int[] position2 = {0};
    static String[] text2 = {""};
    static int[] duration2 = {100};
    static int score = 0;
    static boolean resultScreen = false;
    public Monkey (int x, int y, ID id) {super(x, y, id);}
    public void tick(int ticks) {if (id == ID.TypeMonkey) {timer++; if (free) {parseChart();}}}
    public void render(Graphics g) {
        if (id == ID.TypeMonkey && typing) {
            g.setColor(Color.lightGray);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(36/Game.yScale)));
            g.drawString(qwerty, x, y);
        }
        if (id == ID.HitMonkey && typing) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(36/Game.yScale)));
            g.drawString(event, x, y);
        }
        if (id == ID.TitleMonkey && !Button.searching && !Monkey.typing) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(72/Game.yScale)));
            if (timer % 200 > 100) {g.drawString("RhythmTyper|", x, y);}
            else {g.drawString("RhythmTyper", x, y);}
        }
        if (id == ID.BarMonkey && typing) {
            g.setColor(Color.white);
            g.fillRect(x, y, w, h);
        }
        if (id == ID.ScoreMonkey && typing) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(20/Game.yScale)));
            g.drawString("Score: "+score, x, y);
        }
        if (id == ID.ResultMonkey && resultScreen) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(50/Game.yScale)));
            g.drawString("Score: "+score, x, y);
        }
    }
    public static void parseChart() {
        if (level == 1 && init) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Game.class.getResource("padoru.wav"));
                Clip sound = AudioSystem.getClip();
                sound.open(audioInputStream);
                FloatControl gainControl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-16.0f);
                sound.start();
                init = false;
                timer = 0;
                qwerty = "";
                KeyInput.i = 0;
                eventNumber = 0;
                score = 0;
            } catch (Exception err) {}
        }
        else if (level == 2 && init) {
            init = false;
            timer = 0;
            qwerty = "";
            KeyInput.i = 0;
            eventNumber = 0;
            score = 0;
        }
        if (level == 1) {
                if (timer >= position[eventNumber]) {
                event = text[eventNumber];
                w += Game.WIDTH*1.3/duration[eventNumber];
            }
            if (timer >= position[eventNumber] + duration[eventNumber]) {
                for (int idx = 0; idx < text[eventNumber].length(); idx++) {
                    if (qwerty.length() > idx) {
                        if (qwerty.charAt(idx) == text[eventNumber].charAt(idx)) {
                            score++;
                        }
                    }
                }
                eventNumber++;
                event = "";
                qwerty = "";
                KeyInput.i = 0;
                w = 0;
            }
            if (eventNumber >= text.length) {
                free = false;
                typing = false;
                Button.searching = true;
                resultScreen = true;
            }
        }
        else if (level == 2) {
            if (timer >= position2[eventNumber]) {
                event = text2[eventNumber];
                w += Game.WIDTH*1.3/duration2[eventNumber];
            }
            if (timer >= position2[eventNumber] + duration2[eventNumber]) {
                for (int idx = 0; idx < text2[eventNumber].length(); idx++) {
                    if (qwerty.length() > idx) {
                        if (qwerty.charAt(idx) == text2[eventNumber].charAt(idx)) {
                            score++;
                        }
                    }
                }
                eventNumber++;
                event = "";
                qwerty = "";
                KeyInput.i = 0;
                w = 0;
            }
            if (eventNumber >= text2.length) {
                free = false;
                typing = false;
                Button.searching = true;
                resultScreen = true;
            }
        }
    }
}
// ------------------------------------------------------------------------------
class Background extends GameObject { // ! class for background gameobjects
    static int w = Game.WIDTH/4;
    static int h = Game.HEIGHT/4;
    public Background(int x, int y, ID id) {super(x, y, id);}
    static int dx = 1;
    static int dy = 1;
    public void tick(int ticks) {
        if (id == ID.Background) {x += dx; y += dy;}
        if (x < 0 || x > Game.WIDTH-w) {dx *= -1;}
        if (y < 0 || y > Game.HEIGHT-h) {dy *= -1;}
    }
    public void render(Graphics g) {
        if (id == ID.Background) {
            Color color = new Color(Game.red/2, Game.green/2, Game.blue/2);
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }
    }
}
// ------------------------------------------------------------------------------
class KeyInput extends KeyAdapter { // ! contains keyadapter, basically handles key inputs for typing
    private Handler handler; // * this is a seperate handler from the other one, just for this class only
    public KeyInput(Handler handler) {this.handler = handler;} // * keyinput instance
    public static HashSet<Integer> keysPressed = new HashSet<>(); // * a hashset containing which keys are currently pressed
    static int i = 0;
    public static void clickSFX() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Game.class.getResource("click.wav"));
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            sound.start();
        } catch (Exception err) {}
    }
    @Override // * override avoids collision and makes this run faster (supposedly)
    public void keyPressed(KeyEvent e) { // * if a key is being pressed, add it to the hashset
        if (Monkey.typing) {
            int key = e.getKeyCode(); keysPressed.add(key);
            if (key >= 32) {Monkey.qwerty += (char) key; i++;}
            else if (key == 8 && i > 0) {
                Monkey.qwerty = Monkey.qwerty.substring(0,i-1);
                i--;
            }
        }
        if (!Button.searching && !Monkey.typing) {
            int key = e.getKeyCode();
            if ((char)key == 'F') {Button.searching = true; clickSFX();}
            if ((char)key == 'J') {
                Game.red = (int) (Math.random()*128);
                Game.green = (int) (Math.random()*128);
                Game.blue = (int) (Math.random()*128);
                Game.randomColor = new Color(Game.red, Game.green, Game.blue);
                clickSFX();
            }
        }
        if (Button.searching && !Monkey.typing && !(Button.L1 || Button.L2) && !Monkey.resultScreen) {
            int key = e.getKeyCode();
            if ((char)key == 'L') {Button.searching = false; clickSFX();}
        }
        if (Button.searching && !Monkey.typing && (Button.L1 || Button.L2)) {
            int key = e.getKeyCode();
            if ((char)key == 'L') {Monkey.resultScreen = false; Button.searching = true; Button.L1 = false; Button.L2 = false; clickSFX();}
        }
        if (Button.searching && !Monkey.typing && (Button.L1 || Button.L2) && !Monkey.resultScreen) {
            int key = e.getKeyCode();
            if ((char)key == 'S') {
                Monkey.typing = true; Button.searching = false; clickSFX();
                Monkey.free = true; Monkey.init = true;
            }
        }
        if (Button.searching && !Monkey.typing) {
            int key = e.getKeyCode();
            if ((char)key == 'Q') {Button.L1 = true; Monkey.level = 1; clickSFX();}
            if ((char)key == 'W') {Button.L2 = true; Monkey.level = 2; clickSFX();}
        }
    }
    @Override // * override avoids collision and makes this run faster (supposedly)
    public void keyReleased(KeyEvent e) {int key = e.getKeyCode(); keysPressed.remove(key);} // * if a key gets released, remove it from the hashset
}
// ------------------------------------------------------------------------------
abstract class GameObject { // ! handler for all gameobjects, contains getters and setters
    protected int x, y; // * each instance has its coords
    protected ID id; // * and an id
    protected int velX, velY; // * and its velocity (fsr i didnt use this, but its better)
    public GameObject(int x, int y, ID id) {this.x = x; this.y = y; this.id = id;} // * gameobject instance
    public abstract void tick(int ticks); // * when we recieve a tick from the handler or main game loop
    public abstract void render(Graphics g); // * used by our player and ball classes when we extend/include this one
    // ! these are just getters and setters
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public int getX() {return x;}
    public int getY() {return y;}
    public void setId(ID id) {this.id = id;}
    public ID getId() {return id;}
    public void setVelX(int velX) {this.velX = velX;}
    public void setVelY(int velY) {this.velY = velY;}
    public int getVelX() {return velX;}
    public int getVelY() {return velY;}
}
// ------------------------------------------------------------------------------
enum ID { // ! enum, list of ids, for our gameObjects
    PlayButton(),
    ConfigButton(),
    LeaveButton(),
    StartButton(),
    Level1Button(),
    Level2Button(),
    TypeMonkey(),
    HitMonkey(),
    TitleMonkey(),
    BarMonkey(),
    ScoreMonkey(),
    ResultMonkey(),
    Background();
}
// ------------------------------------------------------------------------------
class Handler { // ! handler for all events like gameobjects, graphics, etc
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    public void tick(int ticks) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.tick(ticks);
        }
    }
    public void render(Graphics g) { // * for each object, render it using graphics
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }
    }
    public void addObject(GameObject object) {this.object.add(object);} // * used in the game class
    public void removeObject(GameObject object) {this.object.remove(object);}
}
// ------------------------------------------------------------------------------
class Window extends Canvas { // ! game window
    Window (int width, int height, String title, Game Game) {
        JFrame frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(Game);
        frame.setVisible(true);
        Game.start();
    }
}
// ------------------------------------------------------------------------------