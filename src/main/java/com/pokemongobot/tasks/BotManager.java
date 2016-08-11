package com.pokemongobot.tasks;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.BotProfile;

import java.util.ArrayList;

public class BotManager extends Thread {

    public static final double range = 1000; //TODO get from config
    private BotProfile botProfile;
    private static final ArrayList<Task> tasks = new ArrayList<>();

    public BotManager(BotProfile botProfile) {
        this.botProfile = botProfile;
        tasks.add(new CatchPokemon(botProfile));
    }

    @Override
    public void run() {
        while (true) {
            for (Task t : tasks) {
                try {

                    t.run();

                    Thread.sleep(3000);

                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (LoginFailedException e) {
                    e.printStackTrace();
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                } catch (NoSuchItemException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
