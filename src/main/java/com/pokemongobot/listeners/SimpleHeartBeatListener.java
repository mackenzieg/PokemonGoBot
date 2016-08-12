package com.pokemongobot.listeners;

import com.pokemongobot.tasks.BotActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleHeartBeatListener implements HeartBeatListener {

    private final AtomicInteger heartBeatCount = new AtomicInteger(0);
    private List<BotActivity> activities = new ArrayList<>();
    private final int heartbeatPace;
    private final AtomicLong lastPulse = new AtomicLong(0);

    public SimpleHeartBeatListener(int pace) {
        this.heartbeatPace = pace;
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
