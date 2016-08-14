package com.pokemongobot;

import com.pokegoapi.google.common.geometry.S2LatLng;

import java.net.InetSocketAddress;
import java.util.List;

public class Options {

    private String name;

    private InetSocketAddress proxy;
    private String proxyCredentials;
    private boolean google;
    private String username;
    private String password;

    private S2LatLng startingLocation;
    private double walkingStepDistance;
    private double maxWalkingSpeed;
    private double runningStepDistance;
    private double maxDistance;
    private double timeReset;

    private boolean catchPokemon;
    private boolean lootPokestops;
    private boolean manageEggs;
    private boolean evolve;
    private List<String> keepUnevolved;

    private boolean transferPokemon;
    private boolean ivOverCp;
    private int iv;
    private int cp;
    private List<String> obligatory;
    private List<String> protect;

    public Options() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isManageEggs() {
        return manageEggs;
    }

    public void setManageEggs(boolean manageEggs) {
        this.manageEggs = manageEggs;
    }

    public boolean isIvOverCp() {
        return ivOverCp;
    }

    public void setIvOverCp(boolean ivOverCp) {
        this.ivOverCp = ivOverCp;
    }

    public InetSocketAddress getProxy() {
        return proxy;
    }

    public void setProxy(InetSocketAddress proxy) {
        this.proxy = proxy;
    }

    public String getProxyCredentials() {
        return proxyCredentials;
    }

    public void setProxyCredentials(String proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

    public boolean isGoogle() {
        return google;
    }

    public void setGoogle(boolean google) {
        this.google = google;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public S2LatLng getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(S2LatLng startingLocation) {
        this.startingLocation = startingLocation;
    }

    public double getWalkingStepDistance() {
        return walkingStepDistance;
    }

    public void setWalkingStepDistance(double walkingStepDistance) {
        this.walkingStepDistance = walkingStepDistance;
    }

    public double getMaxWalkingSpeed() {
        return maxWalkingSpeed;
    }

    public void setMaxWalkingSpeed(double maxWalkingSpeed) {
        this.maxWalkingSpeed = maxWalkingSpeed;
    }

    public double getRunningStepDistance() {
        return runningStepDistance;
    }

    public void setRunningStepDistance(double runningStepDistance) {
        this.runningStepDistance = runningStepDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getTimeReset() {
        return timeReset;
    }

    public void setTimeReset(double timeReset) {
        this.timeReset = timeReset;
    }

    public boolean isCatchPokemon() {
        return catchPokemon;
    }

    public void setCatchPokemon(boolean catchPokemon) {
        this.catchPokemon = catchPokemon;
    }

    public boolean isLootPokestops() {
        return lootPokestops;
    }

    public void setLootPokestops(boolean lootPokestops) {
        this.lootPokestops = lootPokestops;
    }

    public boolean isEvolve() {
        return evolve;
    }

    public void setEvolve(boolean evolve) {
        this.evolve = evolve;
    }

    public List<String> getKeepUnevolved() {
        return keepUnevolved;
    }

    public void setKeepUnevolved(List<String> keepUnevolved) {
        this.keepUnevolved = keepUnevolved;
    }

    public boolean isTransferPokemon() {
        return transferPokemon;
    }

    public void setTransferPokemon(boolean transferPokemon) {
        this.transferPokemon = transferPokemon;
    }

    public int getIv() {
        return iv;
    }

    public void setIv(int iv) {
        this.iv = iv;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public List<String> getObligatory() {
        return obligatory;
    }

    public void setObligatory(List<String> obligatory) {
        this.obligatory = obligatory;
    }

    public List<String> getProtect() {
        return protect;
    }

    public void setProtect(List<String> protect) {
        this.protect = protect;
    }
}
