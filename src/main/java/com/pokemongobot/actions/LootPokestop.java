package com.pokemongobot.actions;

import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;

import java.util.ArrayList;
import java.util.List;

public class LootPokestop {

    public static List<PokestopLootResult> lootPokestops(final List<Pokestop> pokestops) {
        final List<PokestopLootResult> result = new ArrayList<>(pokestops.size());
        pokestops.forEach(pokestop -> {
            PokestopLootResult r = lootPokestop(pokestop);
            result.add(r);
        });
        return result;
    }

    public static PokestopLootResult lootPokestop(final Pokestop pokestop) {
        try {
            if (pokestop.canLoot()) {
                PokestopLootResult pokestopLootResult = pokestop.loot();
                if (pokestopLootResult.wasSuccessful()) {
                    //TODO add log
                    ;
                }
                return pokestopLootResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
