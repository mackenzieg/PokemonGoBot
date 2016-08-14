package com.pokemongobot.tasks;

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokemongobot.Options;
import com.pokemongobot.PokemonBot;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TransferPokemonActivity implements BotActivity {

    private final Logger logger;
    private final PokemonBot bot;
    private final Options options;

    public TransferPokemonActivity(PokemonBot bot, Options options) {
        this.bot = bot;
        this.options = options;
        this.logger = Logger.getLogger(options.getName());
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
                boolean protect = false;
                boolean obligatory = false;
                for (String name : options.getProtect()) {
                    if (p.getPokemonId().name().equalsIgnoreCase(name)) {
                        protect = true;
                        break;
                    }
                }
                for (String name : options.getObligatory()) {
                    if (p.getPokemonId().name().equalsIgnoreCase(name)) {
                        obligatory = true;
                        break;
                    }
                }
                if (!protect) {
                    if (obligatory || (!p.isFavorite() && (options.isIvOverCp() ? (p.getIvRatio() < options.getIv()) : (p.getCp() < options.getCp())))) {
                        try {
                            Result result = p.transferPokemon();
                            transferred.add(result);
                        } catch (Exception e) {
                            logger.error("Error transfering pokemon", e);
                        }
                    }
                }
            });
        }
        return transferred;
    }

}
