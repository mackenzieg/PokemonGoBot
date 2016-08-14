package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONException;

import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public Main() {
        try {
            Config config = new Config(new File("config.json"));
            List<Options> options = config.loadConfig();
            BotRunner botRunner;
            for (Options option : options) {
                botRunner = new BotRunner(option);
                botRunner.start();
            }
        } catch (AsyncPokemonGoException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Starting bot in 5..."));
        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Change location now before start!"));
        Thread.sleep(5000);

        new Main();
    }

    public static PokemonGo buildPokemonGo(Options option) throws LoginFailedException, RemoteServerException {
        OkHttpClient client;

        if (option.getProxy() != null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .proxy(new Proxy(Proxy.Type.HTTP, option.getProxy()));
            if (!option.getProxyCredentials().isEmpty()) {
                Authenticator proxyAuthenticator = (route, response) -> {
                    String credential = option.getProxyCredentials();
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                };
                builder.proxyAuthenticator(proxyAuthenticator);
            }
            client = builder.build();
        } else {
            client = new OkHttpClient();
        }

        CredentialProvider credentialProvider;

        if (option.isGoogle()) {
            credentialProvider = googleAuthentication(client, option.getUsername(), option.getPassword());
        } else {
            credentialProvider = ptcAuthentication(client, option.getUsername(), option.getPassword());
        }
        PokemonGo pokemonGo = new PokemonGo(credentialProvider, client);
        DeviceInfo deviceInfo = DeviceInfo.DEFAULT;
        pokemonGo.setDeviceInfo(deviceInfo);
        return pokemonGo;
    }

    private static CredentialProvider ptcAuthentication(OkHttpClient client, String username, String password) throws LoginFailedException, RemoteServerException {
        return new PtcCredentialProvider(client, username, password);
    }

    private static CredentialProvider googleAuthentication(OkHttpClient client, String username, String password) throws LoginFailedException, RemoteServerException {
        return new GoogleAutoCredentialProvider(client, username, password);
    }

}
