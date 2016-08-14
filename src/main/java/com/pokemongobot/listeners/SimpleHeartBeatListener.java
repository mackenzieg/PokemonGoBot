package com.pokemongobot.listeners;

import com.pokemongobot.PokemonBot;
import com.pokemongobot.tasks.BotActivity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleHeartBeatListener implements HeartBeatListener {

    private final Logger logger;
    private final AtomicInteger heartBeatCount = new AtomicInteger(0);
    private final int heartbeatPace;
    private final AtomicLong lastPulse = new AtomicLong(0);
    private final PokemonBot bot;
    private List<BotActivity> activities = new ArrayList<>();

    public SimpleHeartBeatListener(int pace, PokemonBot bot) {
        this.heartbeatPace = pace;
        this.bot = bot;
        this.logger = Logger.getLogger(Thread.currentThread().getName());
    }


    @Override
    public synchronized void heartBeat()
    {
        if (shouldPulse() && (incrementHeartBeat() % heartbeatPace == 0))
        {
            updateLastPulse();
            setHeartBeatCount(1);
            getHeartbeatActivities().forEach(BotActivity::performActivity);
        }
    }

    public synchronized boolean shouldPulse()
    {
        long diff = System.currentTimeMillis() - getLastPulse();
        return diff > 1000;
    }

    public synchronized long getLastPulse()
    {
        return lastPulse.get();
    }

    public synchronized void updateLastPulse()
    {
        lastPulse.set(System.currentTimeMillis());
    }

    @Override
    public synchronized int incrementHeartBeat()
    {
        return heartBeatCount.getAndIncrement();
    }

    @Override
    public synchronized int getHeartBeatCount()
    {
        return heartBeatCount.get();
    }

    @Override
    public synchronized void setHeartBeatCount(int count)
    {
        heartBeatCount.set(count);
    }

    @Override
    public synchronized void addHeartBeatActivity(BotActivity activity)
    {
        this.activities.add(activity);
    }

    @Override
    public synchronized List<BotActivity> getHeartbeatActivities()
    {
        return activities;
    }

}
