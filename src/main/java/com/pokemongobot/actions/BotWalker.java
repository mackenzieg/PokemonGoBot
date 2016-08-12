package com.pokemongobot.actions;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.listeners.HeartBeatListener;
import com.pokemongobot.listeners.LocationListener;
import com.pokemongobot.tasks.BotActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class BotWalker {

    private final double STEP_SIZE = 1;
    private final HeartBeatListener heartBeatListener;
    private final List<BotActivity> postStepActivities = new ArrayList<>();
    private final LocationListener locationListener;

    private AtomicReference<S2LatLng> currentLocation;
    private AtomicLong lastLocationMs = new AtomicLong(0);
    private double lastAltitude = 2;

    public BotWalker(final S2LatLng start, final LocationListener locationListener,
                     final HeartBeatListener heartBeatListener) {
        this.currentLocation = new AtomicReference<>(start);
        this.locationListener = locationListener;
        this.heartBeatListener = heartBeatListener;
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
        S2LatLng[] steps = getStepsToDestination(start, end, STEP_SIZE);
        if (steps == null) {
            setCurrentLocation(end);
            return;
        } else if(steps.length == 1) {
            setCurrentLocation(end);
            performHeartBeat();
            performPostStepActivities();
            return;
        }
        for(S2LatLng step : steps) {
            double speed = setCurrentLocation(step);
            performHeartBeat();
        }
        performHeartBeat();
        performPostStepActivities();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public final synchronized double setCurrentLocation(S2LatLng newLocation) {
        try {
            double speed = 0;
            boolean update = true;
            S2LatLng current = currentLocation.get();
            if (currentLocation != null) {
                if (Double.compare(newLocation.latDegrees(), current.latDegrees()) == 0 &&
                        Double.compare(newLocation.lngDegrees(), current.lngDegrees()) == 0)
                    update = false;
                speed = getCurrentSpeed(newLocation);
            }
            newLocation.add(S2LatLng.fromDegrees(getSmallRandom(), getSmallRandom()));
            if (update)
                locationListener.updateCurrentLocation(newLocation);
            lastLocationMs.set(System.currentTimeMillis());
            currentLocation.set(newLocation);
            return speed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public double getSmallRandom() {
        return Math.random() * 0.0001 - 0.00005;
    }

    public final S2LatLng[] getStepsToDestination(final S2LatLng start, final S2LatLng end, final double stepMeters) {
        if (start.getEarthDistance(end) == 0)
            return new S2LatLng[]{start};
        S2LatLng difference = end.sub(start);
        double distance = start.getEarthDistance(end);
        final int stepsRequired = (int) Math.round(distance / stepMeters);
        S2LatLng[] steps = new S2LatLng[stepsRequired + 1];
        steps[0] = S2LatLng.fromDegrees(start.latDegrees(), start.lngDegrees());
        for (int i = 0; i < stepsRequired; i++) {
            steps[i + 1] = S2LatLng.fromDegrees(steps[i].latDegrees() + difference.latDegrees(), steps[i].lngDegrees() + difference.lngDegrees());
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

    protected final synchronized void setLastLocationMs(long ms) {
        lastLocationMs.set(ms);
    }

}
