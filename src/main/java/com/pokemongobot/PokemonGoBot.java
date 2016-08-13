package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.config.Config;
import okhttp3.OkHttpClient;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class PokemonGoBot {


    public PokemonGoBot() {
        OkHttpClient client = new OkHttpClient();
        PokemonGo pokemonGo = null;

        while (pokemonGo == null) {
            if (Config.isGoogle())
                pokemonGo = googleAuthentication(client);
            else
                pokemonGo = ptcAuthentication(client);
        }

        SimplePokemonBot bot = new SimplePokemonBot(S2LatLng.fromDegrees(Config.getLatitude(), Config.getLongitude()),
                pokemonGo, client);
        bot.wander();
    }

    public static void main(String[] args) throws InterruptedException {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Starting bot in 5..."));
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Change location now before start!"));
        Thread.sleep(2000);
        new PokemonGoBot();
    }

    public PokemonGo ptcAuthentication(OkHttpClient client) {
        PokemonGo pokemonGo = null;
        try {
            pokemonGo = new PokemonGo(new PtcCredentialProvider(client, Config.getUsername(), Config.getPassword()), client);
        } catch (AsyncPokemonGoException | LoginFailedException | RemoteServerException e) {
            e.printStackTrace();
        }
        return pokemonGo;
    }

    public PokemonGo googleAuthentication(OkHttpClient client) {

        PokemonGo pokemonGo = null;
        GoogleAutoCredentialProvider googleCredentialProvider;
        try {

            googleCredentialProvider = new GoogleAutoCredentialProvider(client, Config.getUsername(), Config.getPassword());
            pokemonGo = new PokemonGo(googleCredentialProvider, client);

        } catch (AsyncPokemonGoException | LoginFailedException | RemoteServerException e) {
            System.out.println("Error");
            //TODO log error
        }

        DeviceInfo deviceInfo = DeviceInfo.DEFAULT;
        if (pokemonGo == null)
            return null;
        pokemonGo.setDeviceInfo(deviceInfo);

        return pokemonGo;

    }

}
