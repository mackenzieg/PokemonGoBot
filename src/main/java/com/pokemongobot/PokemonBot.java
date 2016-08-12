package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.actions.BotWalker;

import java.util.List;

public interface PokemonBot {

    void setWalker(BotWalker botWalker);

    BotWalker getWalker();

    S2LatLng getCurrentLocation();

    S2LatLng setCurrentLocation(S2LatLng location);

    List<CatchablePokemon> getCatchablePokemon();

    Inventories getInventory();

    Map getMap();

    PokemonGo getApi();

    void wander();

    boolean fixSoftBan(S2LatLng destination);

}
