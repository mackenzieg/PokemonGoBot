package com.pokemongobot.listeners;

import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.PokemonBot;
import com.pokemongobot.actions.CatchPokemon;

public class SimpleLocationListener implements LocationListener {

    private final PokemonBot bot;

    public SimpleLocationListener(PokemonBot bot) {
        this.bot = bot;
    }

    @Override
    public void updateCurrentLocation(S2LatLng point) {
        CatchPokemon.catchPokemon(bot.getCatchablePokemon());
    }

    @Override
    public void close() throws Exception {

    }

}
