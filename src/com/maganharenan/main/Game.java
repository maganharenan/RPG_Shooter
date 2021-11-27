package com.maganharenan.main;

import com.maganharenan.entities.Bullet;
import com.maganharenan.entities.Enemy;
import com.maganharenan.entities.Entity;
import com.maganharenan.entities.Player;
import com.maganharenan.graphics.Spritesheet;
import com.maganharenan.graphics.UI;
import com.maganharenan.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    public static final int WIDTH = 224;
    public static final int HEIGHT = 224;
    public static final int SCALE = 3;

    private BufferedImage image;

    public static List<Entity> entities;
    public static Spritesheet spritesheet;

    public static int currentLevel = 1;
    private int maxLevel = 2;
    public static World world;
    public static Player player;
    public static List<Enemy> enemies;
    public static List<Bullet> bullets;
    public UI ui;
    public Menu menu;
    public static String gameState = "Menu";
    private boolean showGameOverMessage = false;
    private int framesMessage = 0, maxFramesMessage = 10;
    public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
    public Font font;
    public int[] pixels;

    public static Random random;
    private boolean restartGame = false;
    public boolean saveGame = false;

    public Game() {
        Sound sound = new Sound();
        sound.play("level1.wav", 1000);
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        menu = new Menu();
        ui = new UI();
        random = new Random();
        addKeyListener(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        spritesheet = new Spritesheet("/spritesheet.png");
        entities = new ArrayList<Entity>();
        enemies = new ArrayList<Enemy>();
        initEntities();
        bullets = new ArrayList<Bullet>();
        world = new World("/level1.png");
    }

    public static void initEntities() {
        player = new Player(0,0,24,24, spritesheet.getSprite(0,72,24,24));
        entities.add(player);
    }

    public void initFrame() {
        frame = new JFrame("Zelda Clone");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void tick() {
        if (gameState == "Running") {
            if (this.saveGame) {
                this.saveGame = false;
                String[] key = {"level", "life"};
                int[] value = {currentLevel, (int)player.life};
                Menu.saveGame(key, value, 10);
            }

            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                entity.tick();
            }

            for (int index = 0; index < bullets.size(); index++) {
                bullets.get(index).tick();
            }

            if (enemies.size() == 0) {
                // Follow to the next level
                currentLevel++;
                if (currentLevel > maxLevel) {
                    currentLevel = 1;
                }
                String newWorld = "level" + currentLevel + ".png";
                World.restartGame(newWorld);
            }
        } else if (gameState == "GameOver") {
            framesMessage++;
            if (this.framesMessage == maxFramesMessage) {
                this.framesMessage = 0;
                if (this.showGameOverMessage) {
                    this.showGameOverMessage = false;
                } else {
                    this.showGameOverMessage = true;
                }
            }
        } else if (gameState == "Menu") {
            menu.tick();
        }

        if (restartGame) {
            currentLevel = 1;
            String newWorld = "level" + currentLevel + ".png";
            World.restartGame(newWorld);
            gameState = "Running";
            restartGame = false;
            Player.life = Player.maxLife;
        }
    }

    public void render() {
        BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(0,0,0));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        // Game render
        world.render(graphics);

        Collections.sort(entities, Entity.nodeSorter);

        for (int index = 0; index < entities.size(); index++) {
            Entity entity = entities.get(index);
            entity.render(graphics);
        }

        for (int index = 0; index < bullets.size(); index++) {
            bullets.get(index).render(graphics);
        }

        ui.render(graphics);

        graphics.dispose();
        graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
        //graphics.setFont(font);
        //graphics.drawString("Teste de nova fonte", 40, 40);

        if (gameState == "GameOver") {
            Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.setColor(new Color(0,0,0,100));
            graphics2D.fillRect(0,0,WIDTH * SCALE, HEIGHT * SCALE);
            graphics2D.setFont(new Font("arial", Font.BOLD, 36));
            graphics2D.setColor(Color.white);
            graphics2D.drawString("Game Over", (WIDTH * SCALE) / 2 - 100, (HEIGHT * SCALE) / 2 - 25);
            graphics2D.setFont(new Font("arial", Font.BOLD, 20));
            if (showGameOverMessage) {
                graphics2D.drawString("Press Enter to restart", (WIDTH * SCALE) / 2 - 100, (HEIGHT * SCALE) / 2 + 25);
            }
        } else if (gameState == "Menu") {
            menu.render(graphics);
        }

        bufferStrategy.show();

    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS " + frames);
                frames = 0;
                timer += 1000;
            }
        }

        stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.left = true;
        }
        else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.right = true;
        }

        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            if (gameState == "Menu") {
                menu.up = true;
            } else {
                player.up = true;
            }
        }
        else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            if (gameState == "Menu") {
                menu.down = true;
            } else {
                player.down = true;
            }
        }

        if (keyCode == KeyEvent.VK_U) {
            player.shoot = true;
        }

        if (keyCode == KeyEvent.VK_I) {
            player.jump = true;
        }

        if (keyCode == KeyEvent.VK_ENTER && gameState == "GameOver") {
            this.restartGame = true;
        }
        else if (keyCode == KeyEvent.VK_ENTER && gameState == "Menu") {
            menu.enter = true;
        }

        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (gameState == "Menu") {
                gameState = "Running";
                menu.pause = false;
            } else if (gameState == "Running") {
                gameState = "Menu";
                menu.pause = true;
            }
        }

        if (keyCode == KeyEvent.VK_SHIFT) {
            this.saveGame = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.left = false;
        }
        else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.right = false;
        }

        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            if (gameState == "Menu") {
                menu.up = false;
            } else {
                player.up = false;
            }
        }
        else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            if (gameState == "Menu") {
                menu.down = false;
            } else {
                player.down = false;
            }
        }

        if (keyCode == KeyEvent.VK_U) {
            player.shoot = false;
        }

        if (keyCode == KeyEvent.VK_I) {
            player.jump = false;
        }

        else if (keyCode == KeyEvent.VK_ENTER) {
            menu.enter = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Player.shoot = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Player.shoot = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
