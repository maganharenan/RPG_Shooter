package com.maganharenan.entities;

import com.maganharenan.main.Game;
import com.maganharenan.main.Sound;
import com.maganharenan.world.AStar;
import com.maganharenan.world.Camera;
import com.maganharenan.world.Vector2i;
import com.maganharenan.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Entity {

    private double speed = 0.6;
    private int frames = 0, maxFrames = 6, animationIndex = 0, maxAnimationIndex = 4;
    public int right_direction = 0, left_direction = 1, up_direction = 2, down_direction = 3;
    public int xDirection = left_direction;
    public int yDirection = down_direction;
    private int life = 100;
    private boolean isDamaged = false;
    private int damagedFrames = 10, currentDamagedFrames = 0;
    private Sound sound = new Sound();

    private BufferedImage[] moving_front_left;
    private BufferedImage[] moving_front_right;
    public static BufferedImage enemy_feedback_left;
    public static BufferedImage enemy_feedback_right;

    public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, null);

        moving_front_left = getSpritesArray(7, 0, 168);
        moving_front_right = getSpritesArray(7, 0, 192);
        enemy_feedback_left = Game.spritesheet.getSprite(168,168,24,24);
        enemy_feedback_right = Game.spritesheet.getSprite(168,192,24,24);
    }

    private BufferedImage[] getSpritesArray(int amountOfSprites, int x, int y) {
        BufferedImage[] spritesArray = new BufferedImage[amountOfSprites];

        for (int index = 0; index < amountOfSprites; index++) {
            spritesArray[index] = Game.spritesheet.getSprite(x + (index * getWidth()), y, getWidth(), getHeight());
        }

        return spritesArray;
    }

    @Override
    public void tick() {

        if (this.calculateDistanceBetween(this, Game.player) < 100) {

            if (path == null || path.size() == 0) {
                Vector2i start = new Vector2i((int)(this.x/ 24), (int)(this.y / 24));
                Vector2i end = new Vector2i((int)(Game.player.x / 24), (int)(Game.player.y / 24));

                path = AStar.findPath(Game.world, start, end);
            }

            if (new Random().nextInt(100) < 60) {
                followPath(path);
            }

            if (!this.isCollidingWithPlayer()) {
                /*
                if (Game.random.nextInt(100) < 40)
                    if ((int) x < Game.player.getX() && World.pathIsFree((int) (x + speed), this.getY()) && !isColliding((int) (x + speed), this.getY())) {
                        x += speed;
                        xDirection = right_direction;
                    } else if ((int) x > Game.player.getX() && World.pathIsFree((int) (x - speed), this.getY()) && !isColliding((int) (x - speed), this.getY())) {
                        x -= speed;
                        xDirection = left_direction;
                    }

                if ((int) y < Game.player.getY() && World.pathIsFree(this.getX(), (int) (y + speed)) && !isColliding(this.getX(), (int) (y + speed))) {
                    y += speed;
                    yDirection = down_direction;
                } else if ((int) y > Game.player.getY() && World.pathIsFree(this.getX(), (int) (y - speed)) && !isColliding(this.getX(), (int) (y - speed))) {
                    y -= speed;
                    yDirection = up_direction;
                }
                */

            } else {
                if (Game.random.nextInt(100) < 10) {
                    Game.player.life -= Game.random.nextInt(5);
                    if (Game.player.life <= 0) {
                        System.out.println("Life: " + Game.player.life);
                    }
                }
            }
        }

        receiveDamage();

        if (life <= 0) {
            Game.enemies.remove(this);
            Game.entities.remove(this);
            return;
        }

        frames++;
        if (frames == maxFrames) {
            frames = 0;
            animationIndex++;
            if (animationIndex > maxAnimationIndex) {
                animationIndex = 0;
            }
        }

        if (isDamaged) {
            this.currentDamagedFrames++;
            if (this.currentDamagedFrames == this.damagedFrames) {
                this.currentDamagedFrames = 0;
                this.isDamaged = false;
            }
        }
    }

    public void handleDamage(Bullet bullet) {
        isDamaged = true;
        life -= 40;
        Game.bullets.remove(bullet);
    }

    public void receiveDamage() {
        for(int index = 0; index < Game.bullets.size(); index++) {
            Bullet bullet = Game.bullets.get(index);
            if (Entity.isColliding(this, bullet)) {
                handleDamage(bullet);
            }
        }
    }

    public boolean isCollidingWithPlayer() {
        Rectangle currentEnemy = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
        Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);

        return currentEnemy.intersects(player);
    }

    public void render(Graphics graphics) {
        if (yDirection == down_direction) {

            if (xDirection == left_direction) {
                if (isDamaged) {
                    graphics.drawImage(enemy_feedback_left, this.getX() - Camera.x, this.getY() - Camera.y, null);
                } else {
                    graphics.drawImage(moving_front_left[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
                }
            } else if (xDirection == right_direction) {
                if (isDamaged) {
                    graphics.drawImage(enemy_feedback_right, this.getX() - Camera.x, this.getY() - Camera.y, null);
                } else {
                    graphics.drawImage(moving_front_right[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
                }
            }
        }
        else if (yDirection == up_direction) {
            if (xDirection == left_direction) {
                if (isDamaged) {
                    graphics.drawImage(enemy_feedback_left, this.getX() - Camera.x, this.getY() - Camera.y, null);
                } else {
                    graphics.drawImage(moving_front_left[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
                }
            }
            else if (xDirection == right_direction) {
                if (isDamaged) {
                    graphics.drawImage(enemy_feedback_right, this.getX() - Camera.x, this.getY() - Camera.y, null);
                } else {
                    graphics.drawImage(moving_front_right[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y, null);
                }
            }
        }
    }
}
