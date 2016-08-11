package com.pokemongobot.tasks;

import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;
import com.pokemongobot.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BotManager extends Thread {

    private BotProfile botProfile;
    private static final List<Task> tasks = new ArrayList<>();

    public BotManager(BotProfile botProfile) {
        this.botProfile = botProfile;
        tasks.add(new CatchPokemon(botProfile));
        tasks.add(new PokestopNagivator(botProfile));
    }

    @Override
    public void run() {
        while (true) {
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                try {
                    ((Task) iterator.next()).run();
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                } catch (NoSuchItemException e) {
                    e.printStackTrace();
                } catch (AsyncPokemonGoException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
