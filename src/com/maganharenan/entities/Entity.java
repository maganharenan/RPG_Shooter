package com.maganharenan.entities;

import com.maganharenan.main.Game;
import com.maganharenan.world.Camera;
import com.maganharenan.world.Node;
import com.maganharenan.world.Vector2i;
import com.maganharenan.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

public class Entity {

    public static BufferedImage lifeBottle_entity = Game.spritesheet.getSprite(24, 144, 12, 15);
    public static BufferedImage weapon_entity = Game.spritesheet.getSprite(74,128,17,12);
    public static BufferedImage bomb_entity = Game.spritesheet.getSprite(0,144,12,15);
    public static BufferedImage enemy_entity = Game.spritesheet.getSprite(51,121,21,23);
    public static BufferedImage enemy_feedback = Game.spritesheet.getSprite(168,168,24,24);

    protected double x;
    protected double y;
    protected int z;
    protected int width;
    protected int height;

    public int depth;

    public double speed = 0.6;

    private int maskX, maskY, maskWidth, maskHeight;

    protected List<Node> path;

    private BufferedImage sprite;

    public Entity(int x, int y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = width;
        this.maskHeight = height;
    }

    public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            if (o2.depth < o1.depth) {
                return  +1;
            }
            if (o2.depth > o1.depth) {
                return -1;
            }

            return 0;
        }
    };

    public void setMask(int maskX, int maskY, int maskWidth, int maskHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskWidth = maskWidth;
        this.maskHeight = maskHeight;
    }

    public int getX() {
        return (int)this.x;
    }

    public int getY() {
        return (int)this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void tick() {

    }

    public void  followPath(List<Node> path) {
        if (path != null) {
            if (path.size() > 0) {
                Vector2i target = path.get(path.size() - 1).tile;

                if (x < target.x * 24 && World.pathIsFree((int) (x + speed), this.getY()) && !isColliding((int) (x + 1), this.getY())) {
                    x++;
                }
                else if (x > target.x * 24 && World.pathIsFree((int) (x - speed), this.getY()) && !isColliding((int) (x - 1), this.getY())) {
                    x--;
                }

                if (y < target.y * 24 && World.pathIsFree(this.getX(), (int) (y + speed)) && !isColliding(this.getX(), (int) (y + 1))) {
                    y++;
                }
                else if (y > target.y * 24 && World.pathIsFree(this.getX(), (int) (y - speed)) && !isColliding(this.getX(), (int) (y - 1))) {
                    y--;
                }

                if (x == target.x * 24 && y == target.y * 24) {
                    path.remove(path.size() - 1);
                }
            }
        }
    }

    public double calculateDistanceBetween(Entity firstEntity, Entity secondEntity) {
        double xDistance = (firstEntity.getX() - secondEntity.getX()) * (firstEntity.getX() - secondEntity.getX());
        double yDistance = (firstEntity.getY() - secondEntity.getY()) * (firstEntity.getY() - secondEntity.getY());
        double totalAxisDistance = xDistance + yDistance;

        return Math.sqrt(totalAxisDistance);
    }

    public boolean isColliding(int nextX, int nextY) {
        Rectangle currentEnemy = new Rectangle(nextX, nextY, 18, 13);

        for (int index = 0; index < Game.enemies.size(); index++) {
            Enemy enemy = Game.enemies.get(index);
            if (enemy == this) {
                continue;
            } else {
                Rectangle targetEnemy = new Rectangle(enemy.getX(), enemy.getY(), 18, 13);
                if (currentEnemy.intersects(targetEnemy)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isColliding(Entity firstEntity, Entity secondEntity) {
        Rectangle firstEntityMask = new Rectangle(
                firstEntity.getX() + firstEntity.maskX,
                firstEntity.getY() + firstEntity.maskY,
                firstEntity.maskWidth,
                firstEntity.maskHeight);
        Rectangle secondEntityMask = new Rectangle(
                secondEntity.getX() + secondEntity.maskX,
                secondEntity.getY() + secondEntity.maskY,
                secondEntity.maskWidth,
                secondEntity.maskHeight);

        if (firstEntityMask.intersects(secondEntityMask) && firstEntity.z == secondEntity.z) {
            return true;
        }

        return false;
    }

    public void render(Graphics graphics) {
        graphics.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
    }
}
