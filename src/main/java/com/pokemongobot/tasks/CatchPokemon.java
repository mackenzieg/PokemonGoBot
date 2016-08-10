package com.pokemongobot.tasks;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.EncounterFailedException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Walk;

public class CatchPokemon extends Task {

    public CatchPokemon(BotProfile bot) {
        super(bot);
    }

    @Override
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException, EncounterFailedException {
        final CatchablePokemon[] catchablePokemon = new CatchablePokemon[1];

        Walk.setLocation(this.getBot());
        EncounterResult encounterResult = catchablePokemon[0].encounterPokemon();
        if (encounterResult.wasSuccessful()) {
            CatchResult result = catchablePokemon[0].catchPokemonBestBallToUse();
            System.out.println("Capture Pokemon id: " + result.getCapturedPokemonId());
        }
    }

}
