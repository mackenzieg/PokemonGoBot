package com.pokemongobot;

import com.pokegoapi.google.common.geometry.S2LatLng;
import okhttp3.Credentials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Config {

    private File file;

    public Config(File file) {
        this.file = file;
    }

    public List<Options> loadConfig() throws JSONException {

        List<BotWrapper> jsonObjects = readJson(this.file);
        List<Options> options = new ArrayList<>(jsonObjects.size());

        for (BotWrapper botWrapper : jsonObjects) {
            JSONObject jsonObject = botWrapper.getJsonObject();
            Options option = new Options();

            option.setName(botWrapper.getName());

            JSONObject proxy = (JSONObject) jsonObject.get("proxy");
            if (!(proxy.getString("ip").isEmpty() && proxy.getString("port").isEmpty())) {
                option.setProxy(new InetSocketAddress(proxy.getString("ip"), Integer.valueOf(proxy.getString("port"))));
                if (!(proxy.getString("user").isEmpty() && proxy.getString("password").isEmpty())) {
                    option.setProxyCredentials(Credentials.basic(proxy.getString("user"), proxy.getString("password")));
                }
            } else {
                option.setProxy(null);
            }

            if (jsonObject.isNull("ptc")) {
                JSONObject google = (JSONObject) jsonObject.get("google");
                option.setGoogle(true);
                option.setUsername(google.getString("username"));
                option.setPassword(google.getString("password"));
            } else {
                JSONObject ptc = (JSONObject) jsonObject.get("ptc");
                option.setGoogle(false);
                option.setUsername(ptc.getString("username"));
                option.setPassword(ptc.getString("password"));
            }

            JSONObject location = (JSONObject) jsonObject.get("location");
            option.setStartingLocation(S2LatLng.fromDegrees(location.getDouble("latitude"), location.getDouble("longitude")));
            option.setWalkingStepDistance(location.getDouble("walking_step_distance"));
            option.setMaxWalkingSpeed(location.getDouble("max_speed_walking"));
            double maxDistance = location.getDouble("max_distance");
            if (maxDistance < 0D) {
                option.setMaxDistance(Double.POSITIVE_INFINITY);
            } else {
                option.setMaxDistance(maxDistance);
            }

            double maxTime = location.getDouble("time_reset");
            if (maxTime <= 0D) {
                option.setTimeReset(Double.POSITIVE_INFINITY);
            } else {
                option.setTimeReset(maxTime);
            }

            JSONObject farming = (JSONObject) jsonObject.get("farming");
            option.setCatchPokemon(farming.getBoolean("catch_pokemon"));
            option.setCatchPokemon(farming.getBoolean("loot_pokestops"));
            option.setManageEggs(farming.getBoolean("manage_eggs"));

            JSONObject evolve = (JSONObject) farming.get("evolve_pokemon");
            option.setEvolve(evolve.getBoolean("evolve"));
            JSONArray keepUnevolved = evolve.getJSONArray("keep_unevolved");
            List<String> keepUn = new ArrayList<>(keepUnevolved.length());
            for (int i = 0; i < keepUnevolved.length(); i++) {
                keepUn.add(keepUnevolved.getString(i));
            }
            option.setKeepUnevolved(keepUn);

            JSONObject transfer = (JSONObject) jsonObject.get("transfer_pokemon");
            option.setTransferPokemon(transfer.getBoolean("transfer"));
            option.setIvOverCp(transfer.getBoolean("iv_over_cp"));
            option.setIv(transfer.getInt("threshold_iv"));
            option.setCp(transfer.getInt("treshold_cp"));

            JSONArray obligatory = (JSONArray) transfer.get("obligatory");
            List<String> ob = new ArrayList<>(obligatory.length());
            for (int i = 0; i < obligatory.length(); i++) {
                ob.add(obligatory.getString(i));
            }
            option.setObligatory(ob);

            JSONArray protect = (JSONArray) transfer.get("protect");
            List<String> prot = new ArrayList<>(protect.length());
            for (int i = 0; i < protect.length(); i++) {
                prot.add(protect.getString(i));
            }
            option.setObligatory(prot);

            options.add(option);

        }

        return options;
    }

    private List<BotWrapper> readJson(File file) {
        String jsonData = "";
        BufferedReader bufferedReader = null;
        try {
            String line;
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        List<BotWrapper> jsonObjects = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject bots = (JSONObject) jsonObject.get("bots");
            Iterator iterator = bots.sortedKeys();
            jsonObjects = new ArrayList<>();
            while (iterator.hasNext()) {
                String botPath = (String) iterator.next();
                if (!botPath.contains("comment")) {
                    BotWrapper botWrapper = new BotWrapper();
                    botWrapper.setName(botPath);
                    botWrapper.setJsonObject((JSONObject) bots.get(botPath));
                    jsonObjects.add(botWrapper);
                    System.out.println("Loading bot with name: " + botWrapper.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObjects == null)
            return new ArrayList<>();

        return jsonObjects;
    }

    private class BotWrapper {
        private JSONObject jsonObject;
        private String name;

        public JSONObject getJsonObject() {
            return jsonObject;
        }

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
