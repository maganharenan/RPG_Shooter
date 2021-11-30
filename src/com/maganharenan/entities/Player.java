package com.maganharenan.entities;

import com.maganharenan.graphics.Spritesheet;
import com.maganharenan.main.Game;
import com.maganharenan.main.Sound;
import com.maganharenan.world.Camera;
import com.maganharenan.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    public boolean right, left, up, down;
    public int right_direction = 0, left_direction = 1, up_direction = 2, down_direction = 3;
    public double speed = 1.4;
    public int direction = down_direction;
    private boolean moved;
    public static double life = 100;
    public static double maxLife = 100;
    private int ammo = 0;
    protected static boolean hasWeapon = false;
    public static boolean shoot = false;

    private int frames = 0, maxFrames = 5, animationIndex = 0, maxAnimationIndex = 9;
    private BufferedImage[] movingDown;
    private BufferedImage[] movingUp;
    private BufferedImage[] movingLeft;
    private BufferedImage[] movingRight;
    private BufferedImage idleFront;
    private BufferedImage idleBack;
    private BufferedImage idleLeft;
    private BufferedImage idleRight;
    private Sound sound = new Sound();
    public boolean jump = false, isJumping = false;
    public int zAxis = 0;
    public int jumpCurrentFrame = 0, jumpMaxFrames = 50;
    public int jumpSpeed = 4;
    public boolean jumpingUp = false, landing = false;

    public Player(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
        loadSprites();
    }

    private void loadSprites() {
        movingDown = getSpritesArray(10, 0, 0);
        idleFront = Game.spritesheet.getSprite(0, 96, getWidth(), getHeight());
        movingUp = getSpritesArray(10, 0, 24);
        idleBack = Game.spritesheet.getSprite(0, 96, getWidth(), getHeight()); // TODO: load the real image
        movingLeft = getSpritesArray(10, 0, 48);
        idleLeft = Game.spritesheet.getSprite(0,96,getWidth(), getHeight()); // TODO: load the real image
        movingRight = getSpritesArray(10, 0, 72);
        idleRight = Game.spritesheet.getSprite(0,96,getWidth(),getHeight()); // TODO: load the real image
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
        depth = 2;
        if (jump) {
            if (!isJumping) {
                jump = false;
                isJumping = true;
                jumpingUp = true;
            }
        }

        if (isJumping) {
                if (jumpingUp) {
                    jumpCurrentFrame += jumpSpeed;
                } else if (landing) {
                    jumpCurrentFrame -= jumpSpeed;
                    if (jumpCurrentFrame <= 0) {
                        isJumping = false;
                        jumpingUp = false;
                        landing = false;
                    }
                }
                zAxis = jumpCurrentFrame;
                if (jumpCurrentFrame >= jumpMaxFrames) {
                    //isJumping = false;
                    jumpingUp = false;
                    landing = true;
                }
        }


        moved = false;
        if (right && World.pathIsFree((int)(x+speed), this.getY())) {
            moved = true;
            setX(x += speed);
            direction = right_direction;
        }
        else if (left && World.pathIsFree((int)(x-speed), this.getY())) {
            moved = true;
            setX(x -= speed);
            direction = left_direction;
        }

        if (up && World.pathIsFree(this.getX(), (int)(y-speed))) {
            moved = true;
            setY(y -= speed);
            direction = up_direction;
        }
        else if (down && World.pathIsFree(this.getX(), (int)(y+speed))) {
            moved = true;
            setY(y += speed);
            direction = down_direction;
        }

        if (moved) {
            frames++;
            if (frames == maxFrames) {
                frames = 0;
                animationIndex++;
                if (animationIndex > maxAnimationIndex) {
                    animationIndex = 0;
                }
            }
        }

        checkItems();

        if (shoot && hasWeapon && ammo > 0) {
            shoot = false;
            shoot();
        }

        if (life <= 0) {
            life = 0;
            Game.gameState = "GameOver";
        }

        Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 24 - Game.HEIGHT);
        Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 24 - Game.HEIGHT);
    }

    public void shoot() {
        sound.shoot.play();
        //sound.play("shoot.wav", 0);
        ammo--;
        int directionX = 0;
        int directionY = 0;
        if (direction == right_direction) {
            directionX = 1;
        }
        else if (direction == left_direction) {
            directionX = -1;
        }
        else if (direction == up_direction) {
            directionY = -1;
        }
        else if (direction == down_direction) {
            directionY = 1;
        }

        Bullet bullet = new Bullet(this.getX() + 10, this.getY() + 12, 3, 3, null, directionX, directionY);
        Game.bullets.add(bullet);
    }

    public void checkItems() {
        for (int index = 0; index < Game.entities.size(); index++) {
            Entity currentEntity = Game.entities.get(index);
            if (currentEntity instanceof Potion) {
                updateLifePoints(currentEntity);
            }
            else if (currentEntity instanceof Bomb) {
                updateAmmo(currentEntity);
            }
            else if (currentEntity instanceof Weapon) {
                updateWeapon(currentEntity);
            }
        }
    }

    public void updateLifePoints(Entity entity) {
        if (Entity.isColliding(this, entity)) {
            if (life < 100) {
                sound.getItem.play();
                //sound.play("getItem.wav", 0);
                life += 20;
                if (life > 100) {
                    life = 100;
                }
                Game.entities.remove(entity);
            }
        }
    }

    public void updateAmmo(Entity entity) {
        if (Entity.isColliding(this, entity)) {
            sound.getItem.play();
            //sound.play("getItem.wav", 0);
            ammo += 10;
            Game.entities.remove(entity);
        }
    }

    public void updateWeapon(Entity entity) {
        if (Entity.isColliding(this, entity)) {
            sound.getItem.play();
            //sound.play("getItem.wav", 0);
            hasWeapon = true;
            Game.entities.remove(entity);
        }
    }

    @Override
    public void render(Graphics graphics) {
        if (direction == down_direction) {
            if (moved) {
                graphics.drawImage(movingDown[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            } else {
                graphics.drawImage(idleFront, this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            }
        }
        else if (direction == up_direction) {
            if (moved) {
                graphics.drawImage(movingUp[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            } else {
                graphics.drawImage(idleBack, this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            }
        }

        if (direction == left_direction) {
            if (moved) {
                graphics.drawImage(movingLeft[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            } else {
                graphics.drawImage(idleLeft, this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            }
        }
        else if (direction == right_direction) {
            if (moved) {
                graphics.drawImage(movingRight[animationIndex], this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            } else {
                graphics.drawImage(idleRight, this.getX() - Camera.x, this.getY() - Camera.y - zAxis, null);
            }
        }

        if (isJumping) {
            graphics.setColor(Color.black);
            graphics.fillOval(this.getX() - Camera.x, this.getY() + 24 - Camera.y, 15, 8);
        }

    }

    public int getAmmo() {
        return ammo;
    }

}
