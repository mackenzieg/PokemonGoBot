package com.pokemongobot.tasks;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass;
import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Config;
import com.pokemongobot.Walk;
import org.fusesource.jansi.Ansi;

import java.util.List;

public class CatchPokemon extends Task {

    public CatchPokemon(BotProfile bot) {
        super(bot);
    }

    @Override
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException {

        List<CatchablePokemon> pokemons = getBot().getPokemonGo().getMap().getCatchablePokemon();
        System.out.println(pokemons.size() + " Pokemon");
        if (pokemons != null) {
            if (pokemons.size() > 0) {
                CatchablePokemon catchablePokemon = pokemons.get(0);

                EncounterResult encounterResult = catchablePokemon.encounterPokemon();
                if (encounterResult.getCaptureProbability().getCaptureProbabilityCount() <= Config.getCatchChanceUseRazzberry())
                    ; //TODO use razzberrry
                CatchResult catchResult = catchablePokemon.catchPokemon(Pokeball.POKEBALL);
                if (catchResult.getStatus().equals(CatchPokemonResponseOuterClass.CatchPokemonResponse.CatchStatus.CATCH_SUCCESS)) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Caught a " + catchablePokemon.getPokemonId().name() + "!"));
                } else {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Error Catching pokemon " + catchablePokemon.getPokemonId().name() + "... Fled?"));
                }
            }


            Walk.setLocation(this.getBot());
        }
    }

}
