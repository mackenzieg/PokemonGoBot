package com.pokemongobot.tasks;

import com.pokegoapi.exceptions.EncounterFailedException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;

public class EvolvePokemon extends Task {

    public EvolvePokemon(BotProfile bot) {
        super(bot);
    }

    @Override
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException, EncounterFailedException {

    }

}
