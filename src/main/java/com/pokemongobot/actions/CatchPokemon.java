package com.pokemongobot.actions;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokemongobot.config.Config;

import java.util.ArrayList;
import java.util.List;

public class CatchPokemon {

    public static List<CatchResult> catchPokemon(List<CatchablePokemon> pokemonList) {
        List<CatchResult> results = new ArrayList<>(pokemonList.size());
        pokemonList.forEach(pokemon -> {
            CatchResult result = attemptCatch(pokemon);
            if (!result.isFailed())
                results.add(result);
        });
        return results;
    }

    public static CatchResult attemptCatch(CatchablePokemon pokemon) {
        EncounterResult encounterResult = encounterResult(pokemon);
        if (encounterResult == null || !encounterResult.wasSuccessful())
            return null;
        try {
            int probability = encounterResult.getCaptureProbability().getCaptureProbabilityCount();
            CatchResult catchResult = null;
            if (probability <= Config.getCatchChanceUseRazzberry())
                catchResult = pokemon.catchPokemonWithRazzBerry();
            else
                catchResult = pokemon.catchPokemon();
            CatchStatus catchStatus = catchResult.getStatus();
            while (catchStatus == CatchStatus.CATCH_MISSED) {
                if (probability <= Config.getCatchChanceUseRazzberry())
                    catchResult = pokemon.catchPokemonWithRazzBerry();
                else
                    catchResult = pokemon.catchPokemon();
                catchStatus = catchResult.getStatus();
            }
            switch (catchResult.getStatus()) {
                case CATCH_SUCCESS:
                    //TODO Log
                    System.out.println("Caught pokemon");
                    break;
                default:
                    //TODO Log
                    System.out.println("Pokemon got away");
                    break;
            }
            return catchResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static EncounterResult encounterResult(CatchablePokemon pokemon) {
        try {
            return pokemon.encounterPokemon();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
