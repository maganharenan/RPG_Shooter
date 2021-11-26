package com.maganharenan.main;

import com.maganharenan.entities.Player;
import com.maganharenan.world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Menu {

    public String[] options = {"New Game", "Load Game", "Exit"};
    public int currentOption = 0;
    public int maxOptions = options.length - 1;
    private BufferedImage background;

    public boolean up, down, enter;
    public static boolean pause = false;

    public static boolean saveExists = false, saveGame = false;

    public Menu() {
        BufferedImage temp;

        try {
            temp = ImageIO.read(getClass().getResource("/background.png"));
            background = temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        if (up) {
            up = false;
            currentOption--;
            if (currentOption < 0) {
                currentOption = maxOptions;
            }
        }
        if (down) {
            down = false;
            currentOption++;
            if (currentOption > maxOptions) {
                currentOption = 0;
            }
        }
        if (enter) {
            if (options[currentOption] == "New Game" || options[currentOption] == "Continue") {
                enter = false;
                Game.gameState = "Running";
                pause = false;
            }
            if (options[currentOption] == "Exit") {
                System.exit(1);
            }
            if (options[currentOption] == "Load Game") {
                File file = new File("save.txt");
                if (file.exists()) {
                    loadGame(loadSaveGameFile(10));
                }
            }
        }
    }

    public static void loadGame(String file) {
        String[] splitSaveFile = file.split("/");
        for (int index = 0; index < splitSaveFile.length; index++) {
            String[] splitedDictionary = splitSaveFile[index].split(":");
            switch (splitedDictionary[0]) {
                case "level":
                    Game.currentLevel = Integer.parseInt(splitedDictionary[1]);
                    World.restartGame("level"+splitedDictionary[1]+".png");
                    Game.gameState = "Running";
                    pause = false;
                    break;
                case "life":
                    Player.life = Double.parseDouble(splitedDictionary[1]);
                    break;
            }
        }
    }

    public static String loadSaveGameFile(int encode) {
        String line = "";
        File file = new File("save.txt");
        if (file.exists()) {
            String singleLine = null;
            try {
                BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                while ((singleLine = reader.readLine()) != null) {
                    String[] transitionLine = singleLine.split(":");
                    char[] value = transitionLine[1].toCharArray();
                    transitionLine[1] = "";
                    for (int index = 0; index < value.length; index++) {
                        value[index] -= encode;
                        transitionLine[1] += value[index];
                    }
                    line += transitionLine[0];
                    line += ":";
                    line += transitionLine[1];
                    line += "/";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return line;
    }

    public static void saveGame(String[] key, int[] value, int encode) {
        BufferedWriter write = null;
        try {
            write = new BufferedWriter(new FileWriter("save.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < key.length; index++) {
            String currentValue = key[index];
            currentValue += ":";
            char[] charArray = Integer.toString(value[index]).toCharArray();
            for (int charIndex = 0; charIndex < charArray.length; charIndex++) {
                charArray[charIndex] += encode;
                currentValue += charArray[charIndex];
            }

            try {
                write.write(currentValue);
                if (index < key.length - 1) {
                    write.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics graphics) {
        graphics.drawImage(background, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
        //graphics.setColor(Color.black);
        //graphics.fillRect(0,0,  Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
        graphics.setColor(Color.black);
        graphics.setFont(new Font("arial", Font.BOLD, 36));
        graphics.drawString("Jimmy Adventures", (Game.WIDTH * Game.SCALE) / 2 - 150, (Game.HEIGHT * Game.SCALE) / 2 - 180);
        graphics.setFont(new Font("arial", Font.BOLD, 25));
        for (int index = 0; index <= maxOptions; index++) {
            String option = options[index];

            if (option == "New Game" && pause == true) {
                option = "Continue";
            }

            graphics.drawString(option, (Game.WIDTH * Game.SCALE) / 2 + 70, ((Game.HEIGHT * Game.SCALE) / 2) + (20 * index) + (10 * index));
        }

        graphics.drawString(">", (Game.WIDTH * Game.SCALE) / 2 + 50, ((Game.HEIGHT * Game.SCALE) / 2) + (20 * currentOption) + (10 * currentOption));
    }

}
