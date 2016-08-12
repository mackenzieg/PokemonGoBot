package com.pokemongobot.listeners;

import com.pokemongobot.tasks.BotActivity;

import java.util.List;

public interface HeartBeatListener {

    void heartBeat();

    int incrementHeartBeat();

    int getHeartBeatCount();

    void setHeartBeatCount(int count);

    void addHeartBeatActivity(BotActivity activity);

    List<BotActivity> getHeartbeatActivities();

}
