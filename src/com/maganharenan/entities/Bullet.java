package com.maganharenan.entities;

import com.maganharenan.main.Game;
import com.maganharenan.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Entity {

    private int directionX;
    private int directionY;
    private double speed = 4;
    private int lifeTime = 30;
    private int currentLifeTime = 0;

    public Bullet(int x, int y, int width, int height, BufferedImage sprite, int directionX, int directionY) {
        super(x, y, width, height, sprite);

        this.directionX = directionX;
        this.directionY = directionY;
    }

    public void tick() {
        x += directionX * speed;
        y += directionY * speed;
        currentLifeTime++;
        if (currentLifeTime == lifeTime) {
            Game.bullets.remove(this);
        }
    }

    public void render(Graphics graphics) {
        graphics.setColor(Color.yellow);
        graphics.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
    }
}
