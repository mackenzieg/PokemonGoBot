package com.pokemongobot.actions;

import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LootPokestop {

    public static List<PokestopLootResult> lootPokestops(final Logger logger, final List<Pokestop> pokestops) {
        final List<PokestopLootResult> result = new ArrayList<>(pokestops.size());
        pokestops.forEach(pokestop -> {
            PokestopLootResult r = lootPokestop(logger, pokestop);
            if(!(r == null))
                result.add(r);
        });
        return result;
    }

    public static PokestopLootResult lootPokestop(final Logger logger, final Pokestop pokestop) {
        try {
            if (pokestop.canLoot()) {
                PokestopLootResult pokestopLootResult = pokestop.loot();
                if (pokestopLootResult.wasSuccessful()) {
                    logger.info("Looted pokestop " + pokestop.getDetails().getName());
                } else {
                    logger.info("Failed looting pokestops reason " + pokestopLootResult.getResult().name());
                }
                return pokestopLootResult;
            }
        } catch (Exception e) {
            logger.debug("Error looting pokestop", e);
        }
        return null;
    }

}
