// ------------------------------------------------------------------------------
// * Culminating Java project for ICS3U
// ! Packages
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import javax.sound.sampled.*;
// ? Ctrl+K, (Ctrl+0 || Ctrl+J)
// TODO: work
// ------------------------------------------------------------------------------
public class Game extends Canvas implements Runnable {
    public static int WIDTH = 800, HEIGHT = 600;
    public static double xScale = 800.0/WIDTH, yScale = 600.0/HEIGHT;
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    public static int red = (int) (Math.random()*256);
    public static int green = (int) (Math.random()*256);
    public static int blue = (int) (Math.random()*256);
    public static Color randomColor = new Color(red, green, blue);
    public Game() {
        handler = new Handler();
        this.addKeyListener(new KeyInput(handler));
        new Window(WIDTH, HEIGHT, "Game", this);
        handler.addObject(new Background((int)(100/xScale),(int)(100/yScale),ID.Background));
        handler.addObject(new Button((int)(200/xScale),(int)(300/yScale),ID.PlayButton));
        handler.addObject(new Button((int)(200/xScale),(int)(400/yScale),ID.ConfigButton));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.TitleMonkey));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.TypeMonkey));
        handler.addObject(new Button((int)(50/xScale),(int)(500/yScale),ID.LeaveButton));
        handler.addObject(new Button((int)(550/xScale),(int)(500/yScale),ID.StartButton));
        handler.addObject(new Button((int)(350/xScale),(int)(50/yScale),ID.Level1Button));
    }
    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {}
    }
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 360.0;
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
                tick();
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
    private void tick() {handler.tick();}
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Game.randomColor);
        g.fillRect(0,0,WIDTH,HEIGHT);
        handler.render(g);
        g.dispose();
        bs.show();
    }
    public static void main(String[] args) {
        if (args.length != 0) {
            int w = Integer.parseInt(args[0]);
            int h = Integer.parseInt(args[1]);
        }
        new Game();
    }
}
// ------------------------------------------------------------------------------
class Button extends GameObject {
    static boolean searching = false;
    static boolean L1 = false;
    static int w = Game.WIDTH/2;
    static int h = Game.HEIGHT/8;
    public Button (int x, int y, ID id) {super(x, y, id);}
    public void tick() {}
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
        if (id == ID.LeaveButton && Button.searching == true) {
            g.setColor(Color.white);
            g.fillRect(x, y, w/2, h/2);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h/2-h/8));
            g.drawString("L* - Leave", x+w/16, y+h/2-h/8);
        }
        if (id == ID.StartButton && Button.searching == true && L1 == true) {
            g.setColor(Color.white);
            g.fillRect(x, y, w/2, h/2);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h/2-h/8));
            g.drawString("S* - Start", x+w/16, y+h/2-h/8);
        }
        if (id == ID.Level1Button && Button.searching == true) {
            if (L1 == false) {g.setColor(Color.white);} else {g.setColor(Color.yellow);}
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("Q* - Level 1", x+w/8, y+h-h/4);
        }
    }
}
// ------------------------------------------------------------------------------
class Monkey extends GameObject {
    static String qwerty = "";
    static boolean typing = false;
    static int timer = 0;
    public Monkey (int x, int y, ID id) {super(x, y, id);}
    public void tick() {timer++;}
    public void render(Graphics g) {
        if (id == ID.TypeMonkey && typing == true) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(36/Game.yScale)));
            g.drawString(qwerty, x, y);
        }
        if (id == ID.TitleMonkey && Button.searching == false && Monkey.typing == false) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, (int)(72/Game.yScale)));
            if (timer % 1000 > 500) {g.drawString("RhythmTyper|", x, y);}
            else {g.drawString("RhythmTyper", x, y);}
        }
    }
}
// ------------------------------------------------------------------------------
class Background extends GameObject {
    static int w = Game.WIDTH/4;
    static int h = Game.HEIGHT/4;
    public Background(int x, int y, ID id) {super(x, y, id);}
    static int dx = 1;
    static int dy = 1;
    public void tick() {
        if (id == ID.Background) {
            x += dx;
            y += dy;
        }
        if (x < 0 || x > Game.WIDTH-w) {dx *= -1;}
        if (y < 0 || y > Game.HEIGHT-h) {dy *= -1;}
    }
    public void render(Graphics g) {
        if (id == ID.Background && Monkey.typing == false) {
            Color color = new Color(Game.red/2, Game.green/2, Game.blue/2);
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }
    }
}
// ------------------------------------------------------------------------------
class KeyInput extends KeyAdapter {
    private Handler handler;
    public KeyInput(Handler handler) {this.handler = handler;}
    public static HashSet<Integer> keysPressed = new HashSet<>();
    int i = 0;
    @Override
    public void keyPressed(KeyEvent e) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Game.class.getResource("click.wav"));
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            sound.start();
        } catch (Exception err) {}
        if (Monkey.typing == true) {
            int key = e.getKeyCode(); keysPressed.add(key);
            if (key >= 32) {Monkey.qwerty += (char) key; i++;}
            else if (key == 8 && i > 0) {
                Monkey.qwerty = Monkey.qwerty.substring(0,i-1);
                i--;
            }
        }
        if (Button.searching == false && Monkey.typing == false) {
            int key = e.getKeyCode();
            if ((char)key == 'F') {Button.searching = true;}
        }
        if (Button.searching == false && Monkey.typing == false) {
            int key = e.getKeyCode();
            if ((char)key == 'J') {
                Game.red = (int) (Math.random()*256);
                Game.green = (int) (Math.random()*256);
                Game.blue = (int) (Math.random()*256);
                Game.randomColor = new Color(Game.red, Game.green, Game.blue);
            }
        }
        if (Button.searching == true && Monkey.typing == false && Button.L1 == false) {
            int key = e.getKeyCode();
            if ((char)key == 'L') {Button.searching = false;}
        }
        if (Button.searching == true && Monkey.typing == false && Button.L1 == true) {
            int key = e.getKeyCode();
            if ((char)key == 'L') {Button.L1 = false;}
        }
        if (Button.searching == true && Monkey.typing == false && Button.L1 == true) {
            int key = e.getKeyCode();
            if ((char)key == 'S') {Monkey.typing = true; Button.searching = false;}
        }
        if (Button.searching == true && Monkey.typing == false) {
            int key = e.getKeyCode();
            if ((char)key == 'Q') {Button.L1 = true;}
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {int key = e.getKeyCode(); keysPressed.remove(key);}
}
// ------------------------------------------------------------------------------
abstract class GameObject {
    protected int x, y;
    protected ID id;
    protected int velX, velY;
    public GameObject(int x, int y, ID id) {this.x = x; this.y = y; this.id = id;}
    public abstract void tick();
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
enum ID {
    PlayButton(),
    ConfigButton(),
    TypeMonkey(),
    TitleMonkey(),
    LeaveButton(),
    StartButton(),
    Level1Button(),
    Background();
}
// ------------------------------------------------------------------------------
class Handler {
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.tick();
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
class Window extends Canvas {
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