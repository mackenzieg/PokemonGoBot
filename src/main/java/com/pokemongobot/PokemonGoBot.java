package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.api.device.SensorInfo;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongobot.tasks.BotManager;
import okhttp3.OkHttpClient;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Random;
import java.util.UUID;

public class PokemonGoBot {

    private BotProfile pokemonGoBot;

    public static void main(String[] args) throws InterruptedException {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Starting bot in 6..."));
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Change location now before start!"));
        Thread.sleep(6000);
        new PokemonGoBot();
    }

    public PokemonGoBot() {
        OkHttpClient client = new OkHttpClient();
        PokemonGo pokemonGo = null;

        if (Config.isGoogle())
            pokemonGo = googleAuthentication(client);

        pokemonGoBot = new BotProfile(pokemonGo,
                client, Config.getLatitude(), Config.getLongitude());
        BotManager botManager = new BotManager(pokemonGoBot);
        botManager.start();
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
