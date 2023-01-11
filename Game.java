// ------------------------------------------------------------------------------
// * Culminating Java project for ICS3U
// ! Packages
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
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
        handler.addObject(new Button((int)(200/xScale),(int)(300/yScale),ID.PlayButton));
        handler.addObject(new Button((int)(200/xScale),(int)(400/yScale),ID.ConfigButton));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.TitleMonkey));
        handler.addObject(new Monkey((int)(100/xScale),(int)(100/yScale),ID.TypeMonkey));
        handler.addObject(new Button((int)(200/xScale),(int)(200/yScale),ID.Level1Button));
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
        } catch(Exception e) {e.printStackTrace();}
    }
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 240.0;
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
    public static void main(String[] args) {new Game();}
}
// ------------------------------------------------------------------------------
class Button extends GameObject {
    static boolean searching = false;
    static int w = Game.WIDTH/2;
    static int h = Game.HEIGHT/8;
    public Button (int x, int y, ID id) {super(x, y, id);}
    public void tick() {
        if (id == ID.PlayButton) {
            // ? if (!KeyInput.keysPressed.isEmpty()) {}
        }
    }
    public void render(Graphics g) {
        if (id == ID.PlayButton && Button.searching == false) {
            g.setColor(Color.white);
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("F* - Play", x+w/8, y+h-h/4);
        }
        if (id == ID.ConfigButton && Button.searching == false) {
            g.setColor(Color.white);
            g.fillRect(x, y, w, h);
            g.setColor(Color.black);
            g.setFont(new Font("Ariel", Font.PLAIN, h-h/4));
            g.drawString("J* - Config", x+w/8, y+h-h/4);
        }
        if (id == ID.Level1Button && Button.searching == true) {
            g.setColor(Color.white);
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
    public Monkey (int x, int y, ID id) {super(x, y, id);}
    public void tick() {
        if (id == ID.TypeMonkey) {
            // ? if (!KeyInput.keysPressed.isEmpty()) {}
        }
    }
    public void render(Graphics g) {
        if (id == ID.TypeMonkey && typing == true) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, 50));
            g.drawString(qwerty, x, y);
        }
        if (id == ID.TitleMonkey && Button.searching == false) {
            g.setColor(Color.white);
            g.setFont(new Font("Ariel", Font.PLAIN, 50));
            g.drawString("RhythmTyper", x, y);
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
        if (Monkey.typing == true) {
            int key = e.getKeyCode(); keysPressed.add(key);
            if (key >= 32) {Monkey.qwerty += (char) key; i++;}
            else if (key == 8 && i > 0) {
                Monkey.qwerty = Monkey.qwerty.substring(0,i-1);
                i--;
            }
        }
        if (Button.searching == false) {
            int key = e.getKeyCode();
            if ((char)key == 'F') {Button.searching = true;}
        }
        if (Button.searching == false) {
            int key = e.getKeyCode();
            if ((char)key == 'J') {
                Game.red = (int) (Math.random()*256);
                Game.green = (int) (Math.random()*256);
                Game.blue = (int) (Math.random()*256);
                Game.randomColor = new Color(Game.red, Game.green, Game.blue);
            }
        }
    }
    // TODO: Implement
    // ? System.out.println(key);
    // ! 10 = \n
    // *
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
    Level1Button();
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