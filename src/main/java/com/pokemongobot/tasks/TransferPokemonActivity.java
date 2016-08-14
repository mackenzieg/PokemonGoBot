package com.pokemongobot.tasks;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokemongobot.Options;
import com.pokemongobot.PokemonBot;

import java.util.ArrayList;
import java.util.List;

public class TransferPokemonActivity implements BotActivity {

    private final PokemonBot bot;
    private final Options options;

    public TransferPokemonActivity(PokemonBot bot, Options options) {
        this.bot = bot;
        this.options = options;
    }

    @Override
    public void performActivity() {
        this.transferPokemon();
    }

    public List<Result> transferPokemon() {
        List<Pokemon> pokemons = bot.getInventory().getPokebank().getPokemons();
        List<Result> transferred = new ArrayList<>();
        if (pokemons.size() > 0) {
            pokemons.forEach(p -> {
                if (!p.isFavorite()) {
                    if (options.isIvOverCp() ? (p.getIvRatio() < options.getIv()) : (p.getCp() < options.getCp())) {
                        try {
                            Result result = p.transferPokemon();
                            transferred.add(result);
                        } catch (Exception e) {
                            //TODO Log
                            ;
                        }
                    }
                }
            });
        }

        return transferred;
    }

}
