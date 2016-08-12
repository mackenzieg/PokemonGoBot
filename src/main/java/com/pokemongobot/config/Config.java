package com.pokemongobot.config;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;

public class Config {

    private static boolean Google = true;
    private static String Username = "";
    private static String Password = "";
    private static String ApiKey = "";
    private static double Latitude = 0;
    private static double Longitude = 0;
    private static double Speed = 0;
    private static boolean Transfer = false;
    private static boolean IVVsCp = true;          // Treat higher IV over CP
    private static int IV = 9999;
    private static int CP = 9999;
    private static boolean AutoDrop = false;

    private static int CatchChanceUseRazzberry = 50;

    private static boolean Evolve = false;          // Use XP egg when evolving
    private static boolean XPEggEvolve = false;
    private static int MinEvolveUseXpEgg = 0;          // Minimum amount to evolve before popping XP egg
    private static int MaxTime = 0;               // Time before resetting bot to starting location
    private static double MaxRange = 0;                // Max range bot can wonder
    private static boolean HandleSoftBan = false;          // Attempt to unban player

    static {

        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");

            properties.load(input);

            loadConfig(properties);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadConfig(Properties config) throws IllegalAccessException {
        Field[] allFields = Config.class.getDeclaredFields();
        for (Field field : allFields) {
            if(field.getType().isPrimitive()) {
                if(field.getType().isAssignableFrom(int.class)) {
                    field.setInt(Config.class, Integer.parseInt(config.getProperty(field.getName())));
                } else if(field.getType().isAssignableFrom(double.class)) {
                    field.setDouble(Config.class, Double.parseDouble(config.getProperty(field.getName())));
                } else if(field.getType().isAssignableFrom(boolean.class)) {
                    field.setBoolean(Config.class, Boolean.parseBoolean(config.getProperty(field.getName())));
                }
            } else {
                if(field.getType().isAssignableFrom(String.class)) {
                    field.set(Config.class, config.getProperty(field.getName()));
                }
            }
        }
    }

    public static boolean isGoogle() {
        return Google;
    }

    public static String getUsername() {
        return Username;
    }

    public static String getPassword() {
        return Password;
    }

    public static String getApiKey() {
        return ApiKey;
    }

    public static double getLatitude() {
        return Latitude;
    }

    public static double getLongitude() {
        return Longitude;
    }

    public static double getSpeed() {
        return Speed;
    }

    public static boolean isTransfer() {
        return Transfer;
    }

    public static boolean isIVVsCp() {
        return IVVsCp;
    }

    public static int getIV() {
        return IV;
    }

    public static int getCP() {
        return CP;
    }

    public static boolean isAutoDrop() {
        return AutoDrop;
    }

    public static boolean isEvolve() {
        return Evolve;
    }

    public static boolean isXPEggEvolve() {
        return XPEggEvolve;
    }

    public static int getMinEvolveUseXpEgg() {
        return MinEvolveUseXpEgg;
    }

    public static int getMaxTime() {
        return MaxTime;
    }

    public double getMaxRange() {
        return MaxRange;
    }

    public boolean isHandleSoftBan() {
        return HandleSoftBan;
    }

    public static int getCatchChanceUseRazzberry() {
        return CatchChanceUseRazzberry;
    }

}
