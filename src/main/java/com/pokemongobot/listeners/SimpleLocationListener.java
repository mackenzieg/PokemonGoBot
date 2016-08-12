package com.pokemongobot.listeners;

import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.PokemonBot;

public class SimpleLocationListener implements LocationListener {

    private final PokemonBot bot;

    public SimpleLocationListener(PokemonBot bot) {
        this.bot = bot;
    }

    @Override
    public void updateCurrentLocation(S2LatLng point) {

    }

    @Override
    public void close() throws Exception {

    }

}
