package com.pokemongobot.actions;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.SimplePokemonBot;
import com.pokemongobot.config.Config;
import okhttp3.OkHttpClient;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class PokemonGoBot {


    public static void main(String[] args) throws InterruptedException {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Starting bot in 6..."));
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Change location now before start!"));
        Thread.sleep(6000);
        new PokemonGoBot();
    }

    public PokemonGoBot() {
        OkHttpClient client = new OkHttpClient();
        PokemonGo pokemonGo;

        if (Config.isGoogle())
            pokemonGo = googleAuthentication(client);
        else
            pokemonGo = ptcAuthentication(client);

        new SimplePokemonBot(S2LatLng.fromDegrees(Config.getLatitude(), Config.getLongitude()), pokemonGo, client);
    }

    public PokemonGo ptcAuthentication(OkHttpClient client) {
        PokemonGo pokemonGo = null;
        try {
            pokemonGo = new PokemonGo(new PtcCredentialProvider(client, Config.getUsername(), Config.getPassword()), client);
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }
        return pokemonGo;
    }

    public PokemonGo googleAuthentication(OkHttpClient client) {

        PokemonGo pokemonGo = null;
        GoogleAutoCredentialProvider googleCredentialProvider = null;
        try {

            googleCredentialProvider = new GoogleAutoCredentialProvider(client, Config.getUsername(), Config.getPassword());
            pokemonGo = new PokemonGo(googleCredentialProvider, client);

        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }

        DeviceInfo deviceInfo = DeviceInfo.DEFAULT;
        pokemonGo.setDeviceInfo(deviceInfo);

        return pokemonGo;

    }

}
