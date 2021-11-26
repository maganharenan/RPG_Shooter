package com.maganharenan.world;

public class Camera {

    public static int x;
    public static int y;

    public static int clamp(int currentAxisValue, int minValue, int maxValue) {
        if (currentAxisValue < minValue) {
            currentAxisValue = minValue;
        }
        if (currentAxisValue > maxValue) {
            currentAxisValue = maxValue;
        }

        return currentAxisValue;
    }
}
