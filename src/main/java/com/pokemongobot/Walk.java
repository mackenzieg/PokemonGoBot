package com.pokemongobot;

import com.google.common.util.concurrent.AtomicDouble;
import com.pokegoapi.google.common.geometry.S2LatLng;

import java.util.Timer;
import java.util.TimerTask;

public class Walk {

    public static double getSmallRandom() {
        return Math.random() * 0.0001 - 0.00005;
    }

    public static void setLocation(BotProfile botProfile) {
        setLocation(false, botProfile);
    }

    public static void setLocation(boolean random, BotProfile botProfile) {
        if (random) {
            botProfile.getPokemonGo().setLocation(botProfile.getLatitude().get() + getSmallRandom(), botProfile.getLongitude().get() + getSmallRandom(), 0);
        } else {
            botProfile.getPokemonGo().setLocation(botProfile.getLatitude().get(), botProfile.getLongitude().get(), 0);
        }
    }

    public static void walk(S2LatLng end, final BotProfile botProfile) {
        if (botProfile.getWalking().get())
            return;
        botProfile.getWalking().set(true);
        S2LatLng start = S2LatLng.fromDegrees(botProfile.getLatitude().get(), botProfile.getLongitude().get());
        S2LatLng difference = end.sub(start);
        double distance = start.getEarthDistance(end);
        double time = distance / 10D; //TODO get value from config

        final AtomicDouble stepsRequired = new AtomicDouble(time / (200D / 1000D));
        final double deltaLatitude = difference.latDegrees() / stepsRequired.get();
        final double deltaLongitude = difference.lngDegrees() / stepsRequired.get();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                botProfile.getLatitude().addAndGet(deltaLatitude);
                botProfile.getLongitude().addAndGet(deltaLongitude);
                stepsRequired.getAndAdd(-1);
                if (stepsRequired.get() <= 0) {
                    System.out.println("Destination reached.");
                    botProfile.getWalking().set(false);
                    cancel();
                }
            }
        }, 0, 200L);
    }

}
