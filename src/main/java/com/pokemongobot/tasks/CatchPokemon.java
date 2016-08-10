package com.pokemongobot.tasks;

import POGOProtos.Networking.Responses.CatchPokemonResponseOuterClass;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.EncounterFailedException;
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
    public void run() throws LoginFailedException, RemoteServerException, NoSuchItemException, EncounterFailedException {

        List<CatchablePokemon> pokemons = getBot().getPokemonGo().getMap().getCatchablePokemon();
        if (pokemons != null) {
            if (pokemons.size() > 0) {
                CatchablePokemon catchablePokemon = pokemons.get(0);

                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Starting bot in 6..."));

                EncounterResult encounterResult = catchablePokemon.encounterPokemon();
                if (encounterResult.getCaptureProbability().getCaptureProbabilityCount() <= Config.getCatchChanceUseRazzberry())
                    ; //TODO use razzberrry
                CatchResult catchResult = catchablePokemon.catchPokemonBestBallToUse();
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
