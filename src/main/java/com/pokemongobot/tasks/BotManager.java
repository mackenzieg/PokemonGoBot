package com.pokemongobot.tasks;

import com.pokegoapi.api.map.fort.FortDetails;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.exceptions.*;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Config;
import com.pokemongobot.Walk;

import java.util.*;

public class BotManager extends Thread {

    private BotProfile botProfile;
    private static final List<Task> tasks = new ArrayList<>();
    public static final Map<String, Long> taggedStops = new HashMap<>();

    public BotManager(BotProfile botProfile) {
        this.botProfile = botProfile;
        tasks.add(new PokestopNagivator(botProfile));
        tasks.add(new PokestopNagivator(botProfile));
        tasks.add(new CatchPokemon(botProfile));
        checkSoftBan(); // Check config to remove softban
        this.start();
    }

    @Override
    public void run() {

        while (true) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                try {
                    ((Task) iterator.next()).run();
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                } catch (NoSuchItemException e) {
                    e.printStackTrace();
                } catch (AsyncPokemonGoException e) {
                    e.printStackTrace();
                } catch (EncounterFailedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkSoftBan() {
        try {
            Collection<Pokestop> pokestops = botProfile.getPokemonGo().getMap().getMapObjects().getPokestops();
            Pokestop closest = null;
            double distance = 9999;
            for (Pokestop p : pokestops) {
                if(p.getDistance() < distance) {
                    closest = p;
                    distance = p.getDistance();
                }
            }
            Walk.walk(S2LatLng.fromDegrees(closest.getLatitude(), closest.getLongitude()), botProfile);
            while (Walk.flag)
                Thread.sleep(50);
            Walk.setLocation(botProfile);
            for(int i = 0; i < 80; i++) {
                PokestopLootResult result = closest.loot();
                if(result.wasSuccessful() && result.getItemsAwarded().size() > 0) {
                    System.out.println("Fixed soft ban");
                    return;
                } else {
                    System.out.println("Attempted spin failed trying again");
                    Thread.sleep(25);
                }
            }
        } catch (Exception e) {
            return;
        }
    }

}
