package com.pokemongobot.tasks;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokemongobot.PokemonBot;
import com.pokemongobot.actions.CatchPokemon;

import java.util.List;

public class CatchPokemonActivity implements BotActivity {

    private final PokemonBot pokemonBot;

    public CatchPokemonActivity(PokemonBot pokemonBot) {
        this.pokemonBot = pokemonBot;
    }

    public List<CatchResult> catchNearbyPokemon() {
        return CatchPokemon.catchPokemon(pokemonBot.getCatchablePokemon());
    }

    @Override
    public void performActivity() {
        catchNearbyPokemon();
    }

}
