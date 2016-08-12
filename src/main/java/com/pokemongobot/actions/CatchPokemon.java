package com.pokemongobot.actions;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.*;
import com.pokemongobot.config.Config;

import java.util.ArrayList;
import java.util.List;

public class CatchPokemon {

    public static List<CatchResult> catchPokemon(List<CatchablePokemon> pokemonList) {
        List<CatchResult> results = new ArrayList<>(pokemonList.size());
        pokemonList.forEach(pokemon -> {
            CatchResult result = attemptCatch(pokemon);
            if (result != null && !result.isFailed())
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
            /*if (probability <= Config.getCatchChanceUseRazzberry())
                catchResult = pokemon.catchPokemonWithRazzBerry();
            else
                catchResult = pokemon.catchPokemon();*/
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
        } catch (AsyncPokemonGoException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        } catch (EncounterFailedException e) {
            e.printStackTrace();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (NoSuchItemException e) {
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
