package com.pokemongobot.tasks;

import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Walk;

import java.util.Collection;
import java.util.Map;

public class PokestopNagivator extends Task {

    private static long cooldownTime = 300000;

    public PokestopNagivator(BotProfile bot) {
        super(bot);
    }

    @Override
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException {
        clearPokestops(BotManager.taggedStops);

        Collection<Pokestop> pokestops = getBot().getPokemonGo().getMap().getMapObjects().getPokestops();


        if (pokestops.size() <= 0 || Walk.flag)
            return;

        Pokestop closest = null;
        long distance = 9999;
        for (Pokestop pokestop : pokestops) {
            if (BotManager.taggedStops.get(pokestop.getId()) == null) {
                if (pokestop.getDistance() <= distance)
                    closest = pokestop;
            }
        }
        if (closest != null)
            Walk.walk(S2LatLng.fromDegrees(closest.getLatitude(), closest.getLongitude()), getBot());

    }

    public static void clearPokestops(Map hm) {
        hm.keySet().stream().filter(o -> ((Long) hm.get(o)).doubleValue() - System.currentTimeMillis() >= cooldownTime).forEach(hm::remove);
    }

}
