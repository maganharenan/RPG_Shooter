package com.maganharenan.graphics;

import com.maganharenan.entities.Player;
import com.maganharenan.main.Game;
import java.awt.*;

public class UI {
    public void render(Graphics graphics) {
        graphics.setColor(Color.gray);
        graphics.fillRect(8, 8, 50, 10);

        if ((int)Game.player.life > 50) {
            graphics.setColor(Color.green);
        } else if ((int)Game.player.life < 50 && (int)Game.player.life > 10) {
            graphics.setColor(Color.yellow);
        } else {
            graphics.setColor(Color.red);
        }
        graphics.fillRect(8, 8, (int)((Game.player.life / Game.player.maxLife) * 50), 10);

        graphics.setColor(Color.black);
        graphics.setFont(new Font("arial", Font.BOLD, 8));
        graphics.drawString((int)Game.player.life + "/" + (int)Game.player.maxLife, 20, 16);
        graphics.drawString("Ammo: " + Game.player.getAmmo(), 8, 32);
    }
}
