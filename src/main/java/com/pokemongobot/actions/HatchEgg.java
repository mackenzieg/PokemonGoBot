package com.pokemongobot.actions;

import POGOProtos.Networking.Responses.UseItemEggIncubatorResponseOuterClass.UseItemEggIncubatorResponse.Result;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.inventory.Hatchery;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.pokemon.EggPokemon;
import com.pokegoapi.api.pokemon.HatchedEgg;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HatchEgg {

    public static List<HatchedEgg> getHatchedEggs(Logger logger, Hatchery hatchery) {
        try {
            final List<HatchedEgg> hatchedEggs = hatchery.queryHatchedEggs();
            if (hatchedEggs != null && hatchedEggs.size() > 0) {
                hatchedEggs.forEach(egg -> {
                    logger.info("Hatched egg " + egg.toString());
                });
            }
        } catch (RemoteServerException | LoginFailedException e) {
            logger.error("Error getting hatched eggs", e);
        }
        return new ArrayList<>();
    }

    public static List<EggIncubator> getIncubators(Inventories inventories) {
        return inventories.getIncubators().stream().filter(eggIncubator -> {
            try {
                return eggIncubator.isInUse();
            } catch (LoginFailedException | RemoteServerException | AsyncPokemonGoException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    public static EggPokemon getAvailableEgg(Inventories inventories) {
        Set<EggPokemon> eggs = inventories.getHatchery().getEggs();
        if (eggs.size() > 0) {
            Optional<EggPokemon> result = eggs.stream().filter(eggPokemon -> !eggPokemon.isIncubate())
                    .sorted((EggPokemon a, EggPokemon b) -> Long.compare(a.getCreationTimeMs(), b.getCreationTimeMs())).findFirst();
            if (result.isPresent())
                return result.get();
        }
        return null;
    }

    public static List<EggIncubator> fillIncubators(Logger logger, Inventories inventories) {
        final List<EggIncubator> filled = new ArrayList<>(inventories.getHatchery().getEggs().size());
        getIncubators(inventories).forEach(incubator -> {
            EggPokemon egg = getAvailableEgg(inventories);
            if (egg != null) {
                Result result = hatchEgg(logger, egg, incubator);
                if (result != null) {
                    logger.info("Putting " + egg.getEggKmWalkedTarget() + " in incubator");
                    filled.add(incubator);
                }
            }
        });
        return filled;
    }

    public static Result hatchEgg(Logger logger, EggPokemon egg, EggIncubator incubator) {
        try {
            return incubator.hatchEgg(egg);
        } catch (AsyncPokemonGoException | LoginFailedException | RemoteServerException e) {
            logger.error("Failed hatching egg", e);
        }
        return null;
    }

}
