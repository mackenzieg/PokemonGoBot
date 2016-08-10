package com.pokemongobot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.SystemTimeImpl;
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

        if(Config.isGoogle())
            googleAuthentication(client);

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
        } catch (LoginFailedException e1) {
            e1.printStackTrace();
        } catch (RemoteServerException e1) {
            e1.printStackTrace();
        }

        try {
            pokemonGo = new com.pokegoapi.api.PokemonGo(googleCredentialProvider, client, new SystemTimeImpl());
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }

        String uuid = UUID.randomUUID().toString();
        Random random = new Random(uuid.hashCode());


        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(uuid);
        deviceInfo.setDeviceBrand("Apple");

        String[] device = devices[random.nextInt(devices.length)];
        deviceInfo.setDeviceModel(device[1]);
        deviceInfo.setDeviceModelBoot(device[0]);
        deviceInfo.setHardwareManufacturer("Apple");
        deviceInfo.setHardwareModel(device[2]);
        deviceInfo.setFirmwareBrand("iPhone OS");
        deviceInfo.setFirmwareType(osVersions[random.nextInt(osVersions.length)]);
        pokemonGo.setDeviceInfo(deviceInfo);

        return pokemonGo;

    }

    String[][] devices = new String[][]{
            {"iPad3,1", "iPad", "J1AP"},
            {"iPad3,2", "iPad", "J2AP"},
            {"iPad3,3", "iPad", "J2AAP"},
            {"iPad3,4", "iPad", "P101AP"},
            {"iPad3,5", "iPad", "P102AP"},
            {"iPad3,6", "iPad", "P103AP"},

            {"iPad4,1", "iPad", "J71AP"},
            {"iPad4,2", "iPad", "J72AP"},
            {"iPad4,3", "iPad", "J73AP"},
            {"iPad4,4", "iPad", "J85AP"},
            {"iPad4,5", "iPad", "J86AP"},
            {"iPad4,6", "iPad", "J87AP"},
            {"iPad4,7", "iPad", "J85mAP"},
            {"iPad4,8", "iPad", "J86mAP"},
            {"iPad4,9", "iPad", "J87mAP"},

            {"iPad5,1", "iPad", "J96AP"},
            {"iPad5,2", "iPad", "J97AP"},
            {"iPad5,3", "iPad", "J81AP"},
            {"iPad5,4", "iPad", "J82AP"},

            {"iPad6,7", "iPad", "J98aAP"},
            {"iPad6,8", "iPad", "J99aAP"},

            {"iPhone5,1", "iPhone", "N41AP"},
            {"iPhone5,2", "iPhone", "N42AP"},
            {"iPhone5,3", "iPhone", "N48AP"},
            {"iPhone5,4", "iPhone", "N49AP"},

            {"iPhone6,1", "iPhone", "N51AP"},
            {"iPhone6,2", "iPhone", "N53AP"},

            {"iPhone7,1", "iPhone", "N56AP"},
            {"iPhone7,2", "iPhone", "N61AP"},

            {"iPhone8,1", "iPhone", "N71AP"}
    };

    String[] osVersions = new String[]{"8.1.1", "8.1.2", "8.1.3", "8.2", "8.3", "8.4", "8.4.1",
            "9.0", "9.0.1", "9.0.2", "9.1", "9.2", "9.2.1", "9.3", "9.3.1", "9.3.2", "9.3.3", "9.3.4"};

}
