package com.pokemongobot.actions;

import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.Options;
import com.pokemongobot.PokemonBot;
import com.pokemongobot.listeners.HeartBeatListener;
import com.pokemongobot.listeners.LocationListener;
import com.pokemongobot.tasks.BotActivity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class BotWalker {

    private static double SPEED;
    private final Logger logger;
    private final HeartBeatListener heartBeatListener;
    private final List<BotActivity> postStepActivities = new ArrayList<>();
    private final LocationListener locationListener;
    private final Options options;
    private PokemonBot bot;
    private AtomicReference<S2LatLng> currentLocation;
    private AtomicLong lastLocationMs = new AtomicLong(0);

    public BotWalker(final PokemonBot bot, final S2LatLng start, final LocationListener locationListener,
                     final HeartBeatListener heartBeatListener, final Options options) {
        this.bot = bot;
        this.currentLocation = new AtomicReference<>(start);
        this.locationListener = locationListener;
        this.heartBeatListener = heartBeatListener;
        this.options = options;
        SPEED = options.getMaxWalkingSpeed();
        logger = Logger.getLogger(Thread.currentThread().getName());
    }

    protected static long getTimeoutForDistance(double distance) {
        if (Double.isInfinite(distance) || Double.isNaN(distance) || (Double.compare(distance, 1) < 1)) {
            return 0;
        }
        Double ms = ((distance / SPEED)) + 75;
        return ms.longValue();
    }

    public synchronized void addPostStepActivity(BotActivity activity) {
        this.postStepActivities.add(activity);
    }

    public synchronized void performPostStepActivities() {
        postStepActivities.forEach(BotActivity::performActivity);
    }

    protected synchronized void performHeartBeat() {
        heartBeatListener.heartBeat();
    }

    public synchronized void walkTo(final S2LatLng start, final S2LatLng end) {
        S2LatLng[] steps = getStepsToDestination(start, end, options.getWalkingStepDistance());

        if (steps == null) {
            setCurrentLocation(end);
            return;
        }
        long preTime = System.currentTimeMillis();

        double totalDis = start.getEarthDistance(end);

        for (S2LatLng step : steps) {
            S2LatLng current = currentLocation.get();
            double distance = current.getEarthDistance(end);
            try {
                if (Double.compare(distance, SPEED) > 0) {
                    long timeout = getTimeoutForDistance(distance);
                    if (timeout > 0)
                        Thread.sleep(timeout);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            double speed = setCurrentLocation(step);
            performHeartBeat();
            if (!Double.isNaN(speed) && !Double.isInfinite(speed)
                    && (Double.compare(speed, SPEED) > 0)) {
                logger.debug("Walking too fast, slowing down");
                longSleep();
            }
        }
        long time = (System.currentTimeMillis() - preTime) / 1000;
        performHeartBeat();
        performPostStepActivities();
    }

    public synchronized void runTo(final S2LatLng origin, final S2LatLng destination) {
        S2LatLng[] steps = getStepsToDestination(origin, destination, options.getRunningStepDistance());
        setCurrentLocation(origin);
        if (steps == null) {
            setCurrentLocation(destination);
            return;
        } else if (steps.length == 1) {
            setCurrentLocation(destination);
            performHeartBeat();
            return;
        }
        for (int i = steps.length - 1; i >= 0; i--) {
            double speed = setCurrentLocation(steps[i]);
            sleep(10); //TODO make random
        }

        longSleep();

    }

    public final synchronized double setCurrentLocation(S2LatLng newLocation) {
        try {
            double speed = 0;
            boolean update = true;
            S2LatLng current = currentLocation.get();
            if (Double.compare(newLocation.latDegrees(), current.latDegrees()) == 0 &&
                    Double.compare(newLocation.lngDegrees(), current.lngDegrees()) == 0)
                update = false;
            speed = getCurrentSpeed(newLocation);
            newLocation.add(S2LatLng.fromDegrees(getSmallRandom(), getSmallRandom()));
            if (update)
                locationListener.updateCurrentLocation(newLocation);
            lastLocationMs.set(System.currentTimeMillis());
            currentLocation.set(newLocation);
            bot.setCurrentLocation(newLocation);
            return speed;
        } catch (Exception e) {
            logger.debug("Error setting current location", e);
        }
        return 0;
    }


    public double getSmallRandom() {
        return Math.random() * 0.0001 - 0.00005;
    }

    public final S2LatLng[] getStepsToDestination(final S2LatLng start, final S2LatLng end, final double stepMeters) {
        if (start.getEarthDistance(end) == 0)
            return new S2LatLng[]{start};
        double deltaLat = end.latDegrees() - start.latDegrees();
        double deltaLng = end.lngDegrees() - start.lngDegrees();
        double distance = start.getEarthDistance(end);
        final int stepsRequired = (int) Math.round(distance / stepMeters);

        S2LatLng[] steps = new S2LatLng[stepsRequired + 1];
        S2LatLng previous = start;
        steps[0] = start;
        for (int i = 0; i <= stepsRequired; i++) {
            steps[i] = S2LatLng.fromDegrees(previous.latDegrees() + deltaLat, previous.lngDegrees() + deltaLng);
            previous = steps[i];
        }
        return steps;
    }

    protected double getCurrentSpeed(S2LatLng newLocation) {
        long lastMs = getLastLocationMs();
        if (lastMs > 0) {
            S2LatLng current = currentLocation.get();
            long currentTime = System.currentTimeMillis();
            double distance = current.getEarthDistance(newLocation);

            if (distance == Double.POSITIVE_INFINITY || distance == Double.NaN || Double.compare(distance, 1) < 0) {
                return 0;
            }

            return distance / (currentTime - lastMs);
        }

        return 0;
    }

    public final synchronized long getLastLocationMs() {
        return lastLocationMs.get();
    }

    protected boolean longSleep() {
        return sleep(new Double((Math.random() * 2000)).intValue() + 1000);
    }

    protected synchronized boolean sleep(long wait) {
        try {
            Thread.sleep(wait);
            return true;
        } catch (InterruptedException ignore) {
            logger.debug("Error pausing thread", ignore);
            return false;
        }
    }

}
