package com.pokemongobot.tasks;

import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokemongobot.PokemonBot;
import com.pokemongobot.actions.CatchPokemon;
import org.apache.log4j.Logger;

import java.util.List;

public class CatchPokemonActivity implements BotActivity {

    private final Logger logger;
    private final PokemonBot pokemonBot;

    public CatchPokemonActivity(PokemonBot pokemonBot) {
        this.pokemonBot = pokemonBot;
        this.logger = Logger.getLogger(Thread.currentThread().getName());
    }

    public List<CatchResult> catchNearbyPokemon() {
        return CatchPokemon.catchPokemon(logger, pokemonBot.getCatchablePokemon());
    }

    @Override
    public void performActivity() {
        catchNearbyPokemon();
    }

}
