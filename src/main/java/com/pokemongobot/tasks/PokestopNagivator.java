package com.pokemongobot.tasks;

import POGOProtos.Inventory.Item.ItemAwardOuterClass;
import POGOProtos.Networking.Responses.FortSearchResponseOuterClass;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Walk;
import org.fusesource.jansi.Ansi;

import java.util.Collection;
import java.util.Optional;

public class PokestopNagivator extends Task {

    public PokestopNagivator(BotProfile bot) {
        super(bot);
    }

    @Override
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException {

        Collection<Pokestop> pokestops = getBot().getPokemonGo().getMap().getMapObjects().getPokestops();

        if (pokestops != null && pokestops.size() > 0) {
            Optional<Pokestop> optional = pokestops.stream().filter(Pokestop::canLoot).sorted((a, b) -> {
                S2LatLng locationA = S2LatLng.fromDegrees(a.getLatitude(), a.getLongitude());
                S2LatLng locationB = S2LatLng.fromDegrees(b.getLatitude(), b.getLongitude());
                S2LatLng self = S2LatLng.fromDegrees(getBot().getPokemonGo().getLatitude(), getBot().getPokemonGo().getLongitude());
                Double distanceA = self.getEarthDistance(locationA);
                Double distanceB = self.getEarthDistance(locationB);
                return distanceA.compareTo(distanceB);
            }).filter(p -> p.canLoot()).findFirst();
            if (optional.isPresent()) {
                Pokestop pokestop = optional.get();
                Walk.setLocation(getBot());
                PokestopLootResult result = pokestop.loot();
                if (result.getResult().equals(FortSearchResponseOuterClass.FortSearchResponse.Result.SUCCESS)) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a("Looted Pokestop!"));
                    for (ItemAwardOuterClass.ItemAward i : result.getItemsAwarded()) {
                        System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a("Received " + i.getItemCount() + " " + i.getItemId().name() + " from Pokestops!"));
                    }

                }
            }
        }

    }
}
