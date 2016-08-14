package com.pokemongobot.listeners;

import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.PokemonBot;
import com.pokemongobot.actions.CatchPokemon;
import org.apache.log4j.Logger;

public class SimpleLocationListener implements LocationListener {

    private final PokemonBot bot;
    private final Logger logger;

    public SimpleLocationListener(PokemonBot bot) {
        this.bot = bot;
        this.logger = Logger.getLogger(Thread.currentThread().getName());
    }

    @Override
    public void updateCurrentLocation(S2LatLng point) {
        if (bot.getOptions().isCatchPokemon())
            CatchPokemon.catchPokemon(logger, bot.getCatchablePokemon());
    }

    @Override
    public void close() throws Exception {

    }

}
