package com.pokemongobot.tasks;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.EncounterFailedException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;

public abstract class Task {

    private BotProfile bot;

    public Task(BotProfile bot) {
        this.bot = bot;
    }

    public abstract void run() throws LoginFailedException, RemoteServerException, NoSuchItemException, EncounterFailedException;

    public BotProfile getBot() {
        return bot;
    }
}
