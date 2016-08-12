package com.pokemongobot.listeners;

import com.pokegoapi.google.common.geometry.S2LatLng;

public interface LocationListener extends AutoCloseable {

    void updateCurrentLocation(S2LatLng point);

}
