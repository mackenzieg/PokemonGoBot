package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import org.apache.log4j.Logger;

public class BotRunner extends Thread {

    private final Options options;
    private Logger logger;

    public BotRunner(Options options) {
        this.options = options;
        this.setName(options.getName());
    }

    @Override
    public void run() {
        try {
            logger = Logger.getLogger(options.getName());
            PokemonGo pokemonGo = Main.buildPokemonGo(this.options);
            SimplePokemonBot simplePokemonBot = new SimplePokemonBot(pokemonGo, this.options);
            Thread.sleep(500);
            simplePokemonBot.wander();
        } catch (Exception e) {
            logger.debug("Error Starting " + this.getName(), e);
        }
    }

}
