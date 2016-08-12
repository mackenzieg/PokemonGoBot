package com.pokemongobot.tasks;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokemongobot.PokemonBot;

import java.util.ArrayList;
import java.util.List;

public class TransferPokemonActivity implements BotActivity {

    private final PokemonBot bot;
    private double iv;
    private double cp;

    public TransferPokemonActivity(PokemonBot bot, double iv, double cp) {
        this.bot = bot;
        this.iv = iv;
        this.cp = cp;
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
                    if ((iv == -1 ? p.getCp() : p.getIvRatio()) < (iv == -1 ? cp : iv)) {
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
