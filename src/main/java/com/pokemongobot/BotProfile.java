package com.pokemongobot;

import com.google.common.util.concurrent.AtomicDouble;
import com.pokegoapi.api.PokemonGo;
import com.pokemongobot.tasks.BotManager;
import okhttp3.OkHttpClient;

import java.util.concurrent.atomic.AtomicBoolean;

public class BotProfile {

    private PokemonGo pokemonGo;
    private OkHttpClient http;
    private AtomicDouble latitude = new AtomicDouble();
    private AtomicDouble longitude = new AtomicDouble();
    private AtomicBoolean walking = new AtomicBoolean(false);

    public BotProfile(PokemonGo pokemonGo, OkHttpClient http, double latitude, double longitude) {
        pokemonGo.setLocation(latitude, longitude, 1);
        this.pokemonGo = pokemonGo;
        this.http = http;
        this.latitude.set(latitude);
        this.longitude.set(longitude);
        new BotManager(this);
    }

    public PokemonGo getPokemonGo() {
        return pokemonGo;
    }

    public void setPokemonGo(PokemonGo pokemonGo) {
        this.pokemonGo = pokemonGo;
    }

    public OkHttpClient getHttp() {
        return http;
    }

    public void setHttp(OkHttpClient http) {
        this.http = http;
    }

    public AtomicDouble getLatitude() {
        return latitude;
    }

    public void setLatitude(AtomicDouble latitude) {
        this.latitude = latitude;
    }

    public AtomicDouble getLongitude() {
        return longitude;
    }

    public void setLongitude(AtomicDouble longitude) {
        this.longitude = longitude;
    }

    public AtomicBoolean getWalking() {
        return walking;
    }

    public void setWalking(AtomicBoolean walking) {
        this.walking = walking;
    }

}
