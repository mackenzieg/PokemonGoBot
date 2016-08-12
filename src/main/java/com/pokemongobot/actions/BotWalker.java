package com.pokemongobot.actions;

import com.pokegoapi.google.common.geometry.S2LatLng;
import com.pokemongobot.config.Config;
import com.pokemongobot.listeners.HeartBeatListener;
import com.pokemongobot.listeners.LocationListener;
import com.pokemongobot.tasks.BotActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class BotWalker {

    private static final double SPEED = Config.getSpeed();
    private final HeartBeatListener heartBeatListener;
    private final List<BotActivity> postStepActivities = new ArrayList<>();
    private final LocationListener locationListener;

    private AtomicReference<S2LatLng> currentLocation;
    private AtomicLong lastLocationMs = new AtomicLong(0);

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

    public synchronized void walkTo(final double stepSize, final S2LatLng start, final S2LatLng end) {
        S2LatLng[] steps = getStepsToDestination(start, end, stepSize);

        if (steps == null) {
            setCurrentLocation(end);
            return;
        } else
            System.out.println(steps.length + " Steps to destination");
        long preTime = System.currentTimeMillis();

        double totalDis = start.getEarthDistance(end);

        for (S2LatLng step : steps) {
            S2LatLng current = currentLocation.get();
            double distance = current.getEarthDistance(end);
            try {
                if (Double.compare(distance, SPEED) > 0) {
                    long timeout = getTimeoutForDistance(distance, step.getEarthDistance(end));
                    System.out.println(distance);
                    System.out.println(timeout);
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
                System.out.println("Walking too fast, slowing down");
                longSleep();
            }
        }
        long time = (System.currentTimeMillis() - preTime) / 1000;
        System.out.println(time + " seconds to travel");
        System.out.println("average speed was " + totalDis / time);
        performHeartBeat();
        performPostStepActivities();
    }

    public synchronized void runTo(final S2LatLng origin, final S2LatLng destination) {
        S2LatLng[] steps = getStepsToDestination(origin, destination, 5);
        if (steps == null) {
            setCurrentLocation(destination);
            return;
        } else if (steps.length == 1) {
            setCurrentLocation(destination);
            performHeartBeat();
            return;
        }

        for(S2LatLng step : steps) {
            double speed = setCurrentLocation(step);
        }

        longSleep();

    }

    protected static long getTimeoutForDistance(double distance, double distanceToEnd) {
        if (Double.isInfinite(distance) || Double.isNaN(distance) || (Double.compare(distance, 1) < 1)) {
            return 0;
        }
        Double ms = ((distance / SPEED)) + 75;
        return ms.longValue();
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
        for (int i = 1; i <= stepsRequired; i++) {
            steps[i] = S2LatLng.fromDegrees(steps[i - 1].latDegrees() + difference.latDegrees(), steps[i - 1].lngDegrees() + difference.lngDegrees());
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

    protected boolean longSleep() {
        return sleep(new Double((Math.random() * 2000)).intValue() + 1000);
    }

    protected synchronized boolean sleep(long wait) {
        try {
            Thread.sleep(wait);
            return true;
        } catch (InterruptedException ignore) {
            return false;
        }
    }

}
