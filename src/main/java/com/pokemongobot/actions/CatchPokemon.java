package com.pokemongobot.actions;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CatchPokemon {

    public static List<CatchResult> catchPokemon(Logger logger, List<CatchablePokemon> pokemonList) {
        List<CatchResult> results = new ArrayList<>(pokemonList.size());
        pokemonList.forEach(pokemon -> {
            CatchResult result = attemptCatch(logger, pokemon);
            if (result != null && !result.isFailed())
                results.add(result);
        });
        return results;
    }

    public static CatchResult attemptCatch(Logger logger, CatchablePokemon pokemon) {
        EncounterResult encounterResult = encounterResult(logger, pokemon);
        if (encounterResult == null || !encounterResult.wasSuccessful())
            return null;
        try {
            int probability = encounterResult.getCaptureProbability().getCaptureProbabilityCount(); //TODO calculate which ball to use also add config for this
            CatchResult catchResult;
            catchResult = pokemon.catchPokemon();
            CatchStatus catchStatus = catchResult.getStatus();
            while (catchStatus == CatchStatus.CATCH_MISSED) {
                catchResult = pokemon.catchPokemonBestBallToUse();
                catchStatus = catchResult.getStatus();
            }
            switch (catchResult.getStatus()) {
                case CATCH_SUCCESS:
                    //TODO Log
                    System.out.println("Caught pokemon " + pokemon.getPokemonId().name());
                    break;
                default:
                    //TODO Log
                    System.out.println("" + pokemon.getPokemonId().name() + " got away reason " + catchResult.getStatus().toString());
                    break;
            }
            return catchResult;
        } catch (AsyncPokemonGoException | RemoteServerException | EncounterFailedException | LoginFailedException | NoSuchItemException e) {
            System.out.println("Error");
            //TODO log error
        }
        return null;

    }

    public static EncounterResult encounterResult(Logger logger, CatchablePokemon pokemon) {
        try {
            return pokemon.encounterPokemon();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
