package com.pokemongobot;

import POGOProtos.Map.Fort.FortDataOuterClass;
import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.CandyJar;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.inventory.Stats;
import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.fort.FortDetails;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.actions.BotWalker;
import com.pokemongobot.actions.CatchPokemon;
import com.pokemongobot.actions.EvolvePokemon;
import com.pokemongobot.actions.LootPokestop;
import com.pokemongobot.config.Config;
import com.pokemongobot.listeners.HeartBeatListener;
import com.pokemongobot.listeners.LocationListener;
import com.pokemongobot.listeners.SimpleHeartBeatListener;
import com.pokemongobot.listeners.SimpleLocationListener;
import com.pokemongobot.tasks.CatchPokemonActivity;
import com.pokemongobot.tasks.TransferPokemonActivity;
import okhttp3.OkHttpClient;

import java.util.*;
import java.util.stream.Collectors;

public class SimplePokemonBot implements PokemonBot {

    private State state;
    private final S2LatLng startLocation;
    private final PokemonGo api;
    private final OkHttpClient httpClient;
    private BotWalker botWalker;

    private State currentOperation = State.NAN;
    private State lastOperation = State.NAN;

    public SimplePokemonBot(S2LatLng startLocation, PokemonGo api, OkHttpClient httpClient) {
        this.startLocation = startLocation;
        this.api = api;
        this.httpClient = httpClient;
        this.setCurrentLocation(startLocation);

        CatchPokemonActivity catchPokemonActivity = new CatchPokemonActivity(this);
        TransferPokemonActivity transferPokemonActivity =
                new TransferPokemonActivity(this, Config.isIVVsCp() ? Config.getIV() : -1D, Config.isIVVsCp() ? -1 : Config.getCP());
        //TODO add location listener
        //TODO add heart beat listener
        HeartBeatListener heartBeatListener = new SimpleHeartBeatListener(50);
        heartBeatListener.addHeartBeatActivity(catchPokemonActivity);

        LocationListener locationListener = new SimpleLocationListener(this);

        if (Config.isTransfer()) {
            heartBeatListener.addHeartBeatActivity(transferPokemonActivity);
        }
        BotWalker botWalker = new BotWalker(this.startLocation, locationListener, heartBeatListener);
        botWalker.addPostStepActivity(catchPokemonActivity);
        this.botWalker = botWalker;
        this.state = State.NAN;

    }

    @Override
    public void wander() {
        long xp = 0;
        long time = System.currentTimeMillis();
        try {
            xp = this.getApi().getPlayerProfile().getStats().getExperience();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }
        boolean stop = false;
        while (!stop) {
            try {
                List<Pokestop> pokestops = getNearbyPokestops();
                System.out.println("Found " + pokestops.size() + " Pokestops nearby");
                if (pokestops.size() < 1) {
                    break;
                }
                catchNearbyPokemon();
                lootNearbyPokestops(false);
                if (Config.isTransfer()) {
                    doTransfers();
                    Thread.sleep(100 + getRandom().longValue());
                }
                if (Config.isEvolve()) {
                    doEvolutions();
                    Thread.sleep(100 + getRandom().longValue());
                }
                for (Pokestop p : pokestops) {
                    try {
                        System.out.println("Walking to " + p.getDetails().getName() + " "
                                + this.getCurrentLocation().getEarthDistance(S2LatLng.fromDegrees(p.getLatitude(), p.getLongitude())) + "m away");
                    } catch (AsyncPokemonGoException | RemoteServerException | LoginFailedException e) {
                        e.printStackTrace();
                    }
                    botWalker.runTo(getCurrentLocation(), S2LatLng.fromDegrees(p.getLatitude(), p.getLongitude()));

                    catchNearbyPokemon();
                    lootNearbyPokestops(false);

                    if (Config.isTransfer()) {
                        doTransfers();
                    }
                    if (Config.isEvolve()) {
                        doEvolutions();
                    }
                    getApi().getPlayerProfile().updateProfile();
                    try {
                        Thread.sleep(500 + new Random().nextInt(500 - 100 + 1) + 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (time + 10000 <= System.currentTimeMillis()) {
                        try {
                            time = System.currentTimeMillis();
                            System.out.println(getApi().getPlayerProfile().getStats().getExperience() - xp + " Experience gained");
                        } catch (LoginFailedException e) {
                            e.printStackTrace();
                        } catch (RemoteServerException e) {
                            e.printStackTrace();
                        }
                        //System.exit(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean fixSoftBan() {
        try {
            Thread.sleep(500);
            Pokestop pokestop = getNearestPokestop().get();
            return this.fixSoftBan(S2LatLng.fromDegrees(pokestop.getLatitude(), pokestop.getLongitude()));
        } catch (Exception e) {
            return false;
        }
    }


    public final boolean fixSoftBan(S2LatLng destination) {
        this.getWalker().runTo(this.getCurrentLocation(), destination);
        setCurrentLocation(destination);
        Optional<Pokestop> nearest = getNearestPokestop();
        if (!nearest.isPresent()) {
            return false;
        }

        Pokestop pokestop = nearest.get();

        try {
            long lon = Double.valueOf(pokestop.getLongitude()).longValue();
            long lat = Double.valueOf(pokestop.getLatitude()).longValue();

            Map map = getApi().getMap();

            for (int i = 0; i < 80; i++) {
                FortDetails d = map.getFortDetails(pokestop.getId(), lon, lat);

                if (d != null) {
                    System.out.println("Attempted spin");
                    //TODO attempted spin etc
                } else {
                    System.out.println("Get fort failed");
                    //TODO get fort fail
                }

                PokestopLootResult r = pokestop.loot();
                if (r.wasSuccessful() && r.getItemsAwarded().size() > 0) {
                    //TODO log xp items gained etc
                    return true;
                } else {
                    System.out.println("Failed");
                    //TODO log not successful
                }
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            PokestopLootResult finalTry = pokestop.loot();
            return finalTry.wasSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public final Optional<Pokestop> getNearestPokestop() {
        List<Pokestop> pokestops = getNearbyPokestops();
        return pokestops.stream().filter(Pokestop::canLoot).findFirst();
    }

    public final long getCurrentExperience() {
        try {
            Stats stats = getApi().getPlayerProfile().getStats();
            return stats.getExperience();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected final String getRuntime() {
        return "";
    }

    protected static Double getRandom() {
        return Math.random() * 750;
    }

    public List<Pokestop> getNearbyPokestops() {
        return getPokestops().stream().filter(pokestop ->
                //TODO get max distance from config, for now 5Km
                getCurrentLocation().getEarthDistance(S2LatLng.fromDegrees(pokestop.getLatitude(), pokestop.getLongitude())) <= 5000).sorted(
                (Pokestop a, Pokestop b) ->
                        Double.compare(
                                getCurrentLocation().getEarthDistance(S2LatLng.fromDegrees(a.getLatitude(), a.getLongitude())),
                                getCurrentLocation().getEarthDistance(S2LatLng.fromDegrees(b.getLatitude(), b.getLongitude())))
        ).collect(Collectors.toList());
    }


    private List<ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result> doTransfers() {
        TransferPokemonActivity a = new TransferPokemonActivity(this, Config.isIVVsCp() ? Config.getIV() : -1, Config.isIVVsCp() ? -1 : Config.getCP());
        return a.transferPokemon();
    }

    private List<EvolutionResult> doEvolutions() {
        Inventories inventories = null;
        try {
            inventories = getApi().getInventories();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }
        final CandyJar candyJar = inventories.getCandyjar();

        final List<Pokemon> pokemons = inventories.getPokebank()
                .getPokemons()
                .stream()
                .sorted((Pokemon a, Pokemon b) ->
                        Integer.compare(b.getCp(), a.getCp()))
                .collect(Collectors.toList());

        return EvolvePokemon.evolvePokemon(pokemons, candyJar);
    }

    @Override
    public Inventories getInventory() {
        try {
            return getApi().getInventories();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected synchronized final State updateOpStatus(State status) {
        this.lastOperation = this.currentOperation;
        this.currentOperation = status;

//        if(lastOperation != currentOperation)
//            LOG.info("Switching from " + this.lastOperation + " to " + this.currentOperation);

        return this.lastOperation;
    }

    @Override
    public final Map getMap() {
        return getApi().getMap();
    }

    public List<CatchResult> catchNearbyPokemon() {
        updateOpStatus(State.CATCHING);
        List<CatchablePokemon> catchablePokemon = getCatchablePokemon();
        if (catchablePokemon.size() == 0) {
            return new ArrayList<>();
        }

        return CatchPokemon.catchPokemon(catchablePokemon);
    }

    public final Collection<Pokestop> getPokestops() {
        try {
            return getMap().getMapObjects().getPokestops();
        } catch (AsyncPokemonGoException | RemoteServerException | LoginFailedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Collection<FortDataOuterClass.FortData> getGyms() {
        try {
            return getMap().getMapObjects().getGyms();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ArrayList<>();
    }

    public List<CatchablePokemon> getCatchablePokemon() {
        try {
            getCurrentLocation();
            return getMap().getCatchablePokemon();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public synchronized List<PokestopLootResult> lootNearbyPokestops(boolean walkToStops) {

        final S2LatLng origin = getCurrentLocation();

        List<Pokestop> pokestops = getNearbyPokestops();
        final List<PokestopLootResult> results = LootPokestop.lootPokestops(pokestops);

        if (!walkToStops) {
            return results;
        }

        pokestops.stream().filter(p -> p.canLoot(true)).forEach(p ->
        {
            botWalker.runTo(getCurrentLocation(), S2LatLng.fromDegrees(p.getLatitude(), p.getLongitude()));
            results.add(lootPokestop(p));
        });

        botWalker.runTo(getCurrentLocation(), origin);

        return results;
    }

    public PokestopLootResult lootPokestop(Pokestop pokestop) {
        updateOpStatus(State.LOOTING);
        return LootPokestop.lootPokestop(pokestop);
    }

    public final S2LatLng getStartLocation() {
        return startLocation;
    }

    public synchronized final PokemonGo getApi() {
        return api;
    }

    protected synchronized final OkHttpClient getHttpClient() {
        return httpClient;
    }

    public final synchronized S2LatLng setCurrentLocation(S2LatLng newLocation) {
        getApi().setLocation(newLocation.latDegrees(), newLocation.lngDegrees(), 1);
        return newLocation;
    }

    public final synchronized S2LatLng getCurrentLocation() {
        return S2LatLng.fromDegrees(getApi().getLatitude(), getApi().getLongitude());
    }

    public synchronized BotWalker getWalker() {
        return botWalker;
    }

    public synchronized void setWalker(BotWalker botWalker) {
        this.botWalker = botWalker;
    }

}
