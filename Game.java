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
// ? Use this combo to fold and unfold in VSC: Ctrl+K, (Ctrl+0 || Ctrl+J)
// ? Download the better comments extension on VSC
// ------------------------------------------------------------------------------
public class Game extends Canvas implements Runnable { // ! contains the main game loop, basically stitches everything together
    public static int WIDTH = 800, HEIGHT = 600;
    public static double xScale = 800.0/WIDTH, yScale = 600.0/HEIGHT;
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    public static int red = (int) (Math.random()*128);
    public static int green = (int) (Math.random()*128);
    public static int blue = (int) (Math.random()*128);
    public static Color randomColor = new Color(red, green, blue);
    public Game() {
        handler = new Handler();
        this.addKeyListener(new KeyInput(handler));
        new Window(WIDTH, HEIGHT, "Game", this);
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
    public synchronized void start() {thread = new Thread(this); thread.start(); running = true;}
    public synchronized void stop() {try {thread.join(); running = false;} catch (Exception e) {}}
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 100.0;
        double ns = 1_000_000_000.0 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int ticks = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            while (delta >= 1) {
                tick(ticks);
                delta--;
                ticks++;
                if (running) {
                    Toolkit.getDefaultToolkit().sync();
                    render();
                    frames++;
                }
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
                ticks = 0;
            }
        }
        stop();
    }
    private void tick(int ticks) {handler.tick(ticks);}
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {this.createBufferStrategy(3); return;}
        Graphics g = bs.getDrawGraphics();
        g.setColor(Game.randomColor);
        g.fillRect(0,0,WIDTH,HEIGHT);
        handler.render(g);
        g.dispose();
        bs.show();
    }
    public static void main(String[] args) {
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
    private Handler handler;
    public KeyInput(Handler handler) {this.handler = handler;}
    public static HashSet<Integer> keysPressed = new HashSet<>();
    static int i = 0;
    public static void clickSFX() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Game.class.getResource("click.wav"));
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            sound.start();
        } catch (Exception err) {}
    }
    @Override
    public void keyPressed(KeyEvent e) {
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
    @Override
    public void keyReleased(KeyEvent e) {int key = e.getKeyCode(); keysPressed.remove(key);}
}
// ------------------------------------------------------------------------------
abstract class GameObject { // ! handler for all gameobjects, contains getters and setters
    protected int x, y;
    protected ID id;
    protected int velX, velY;
    public GameObject(int x, int y, ID id) {this.x = x; this.y = y; this.id = id;}
    public abstract void tick(int ticks);
    public abstract void render(Graphics g);
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
enum ID { // ! enum, list of ids
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
    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }
    }
    public void addObject(GameObject object) {this.object.add(object);}
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