package com.penoybalut.tot.service;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.penoybalut.tot.core.FoodTruckInfo;
import com.penoybalut.tot.core.SFDataConfig;
import com.penoybalut.tot.db.FoodTruckDAO;
import com.socrata.api.Soda2Consumer;
import com.socrata.exceptions.LongRunningQueryException;
import com.socrata.exceptions.SodaError;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PreDestroy;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by ibo on 4/25/14.
 */

public class FoodTruckService {
    private static final String FOOD_TRUCK_NAME_KEY = "truckNames";
    private static final String AUTO_SUGGEST_WORD_KEY = "autoSuggestWords";
    private static final String TRUCK_NAME_ID_KEY = "ft:";
    private static final String KEYWORD_KEY_PREFIX = "kw:";
    private static final String WORD_DELIMETER = "\\W";    // regex delimeter - matches any non-word char

    private final JedisPool jedisPool;
    private final SFDataConfig sfdata;
    private final FoodTruckDAO foodTruckDAO;

    final static Logger logger = LoggerFactory.getLogger(FoodTruckService.class);

    @Inject
    public FoodTruckService(FoodTruckDAO foodTruckDAO, JedisPool jedisPool, SFDataConfig sfdata) {
        this.foodTruckDAO = foodTruckDAO;
        this.jedisPool = jedisPool;
        this.sfdata = sfdata;
    }

    public List<FoodTruckInfo> getFoodTruckData() {
        Soda2Consumer consumer = Soda2Consumer.newConsumer(sfdata.getHostUrl());

        List<FoodTruckInfo> foodTruckInfos = null;

        URI uri = null;
        ClientResponse response = null;

        try {
            uri = new URI(sfdata.getFullURL());
            logger.info("SF Data URL : {}", sfdata.getFullURL());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        try {
            response = consumer.getHttpLowLevel().queryRaw(uri, MediaType.APPLICATION_JSON_TYPE);
            foodTruckInfos = response.getEntity(FoodTruckInfo.LIST_TYPE);
            logger.info("Fetched {} records", foodTruckInfos.size());
        } catch (LongRunningQueryException e) {
            logger.error(e.getMessage());
        } catch (SodaError sodaError) {
            logger.error(sodaError.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // remove data with no latitude/longitude/expired permit
        for(int i=foodTruckInfos.size()-1; i>=0; i--) {
            if (foodTruckInfos.get(i).getLatitude() == null ||
                foodTruckInfos.get(i).getLongitude() == null ||
                "EXPIRED".equals(foodTruckInfos.get(i).getStatus())) {
                foodTruckInfos.remove(i);
            }
        }

        return foodTruckInfos;
    }

    /*
     * Used for caching data from sfgov
     * Replace the data if it already exist
     * This is used by DataPollerService that runs every X hrs.
     */
    @Timed
    public void generateKeywordLookup() {
        List<FoodTruckInfo> foodTruckInfos = getFoodTruckData();
        if (foodTruckInfos == null) return;

        logger.info("filtered data size from sfgov is {}", foodTruckInfos.size());

        Jedis jedis = jedisPool.getResource();

        // remove redis data
        jedis.flushDB();

        // reset id counter to zero
        jedis.set("id", "0");

        for (FoodTruckInfo ftinfo : foodTruckInfos) {
            // grab incremented id from redis
            long foodTruckId = jedis.incr("id");
            ftinfo.setId(foodTruckId);

            // insert food truck keywords
            insertTruckFoodItems(jedis, ftinfo.getFooditems(), foodTruckId);

            // insert truck names
            insertTruckNames(jedis, ftinfo.getApplicant(), foodTruckId);
        }

        logger.info("deleted old data {}", foodTruckDAO.deleteAll());
        foodTruckDAO.batchInsert(foodTruckInfos);
        logger.info("Completed caching data");
    }

    private void insertTruckFoodItems(Jedis jedis, String foodItems, long truckId) {
        if (foodItems == null) return;
        String[] foodItemList = foodItems.split(WORD_DELIMETER);
        for (String keyword : foodItemList) {
            String formattedKeyword = keyword.trim().toLowerCase();
            jedis.sadd(KEYWORD_KEY_PREFIX + formattedKeyword, String.valueOf(truckId));
            jedis.sadd(AUTO_SUGGEST_WORD_KEY, formattedKeyword);
        }
    }

    private void insertTruckNames(Jedis jedis, String truckName, long truckId) {
        String formattedTruckName = truckName.trim().toLowerCase();
        jedis.sadd(FOOD_TRUCK_NAME_KEY, formattedTruckName);
        jedis.sadd(TRUCK_NAME_ID_KEY + formattedTruckName, String.valueOf(truckId));
    }

    public Set<String> getAutoSuggestWords() {
        Jedis jedis = jedisPool.getResource();
        return jedis.smembers(AUTO_SUGGEST_WORD_KEY);
    }

    public Set<String> getTruckNames() {
        Jedis jedis = jedisPool.getResource();
        return jedis.smembers(FOOD_TRUCK_NAME_KEY);
    }

    public List<Long> getFoodTrucksIdsByKeyword(String keyword) {
        Jedis jedis = jedisPool.getResource();

        Set<String> truckIdSet = jedis.smembers(KEYWORD_KEY_PREFIX + keyword.trim().toLowerCase());
        truckIdSet.addAll(jedis.smembers(TRUCK_NAME_ID_KEY + keyword.trim().toLowerCase()));

        List<Long> truckIdList = new ArrayList<>();
        for (String s : truckIdSet) {
            truckIdList.add(Long.parseLong(s));
        }

        return truckIdList;
    }

    public void computeDistanceFromOrigin(List<FoodTruckInfo> foodTrucks, String latitude, String longitude) {
        double originLat = Double.parseDouble(latitude);
        double originLong = Double.parseDouble(longitude);

        logger.info("search result size {}", foodTrucks.size());

        DecimalFormat df = new DecimalFormat("#.##");

        for (FoodTruckInfo ftinfo : foodTrucks) {
            double destLat = Double.parseDouble(ftinfo.getLatitude());
            double destLong = Double.parseDouble(ftinfo.getLongitude());
            double distanceFromLoc = calcDistance(originLat, originLong, destLat, destLong, true);
            distanceFromLoc = Double.valueOf(df.format(distanceFromLoc));
            ftinfo.setDistanceFromLoc(distanceFromLoc);
        }
    }

    private static final double EARTH_RADIUS_KM = 6371.0d;
    private static final double EARTH_RADIUS_MI = 3959.0d;

    private double calcDistance(double lat1, double lng1, double lat2, double lng2, boolean useMiles) {
        double earthRadius = useMiles ? EARTH_RADIUS_MI : EARTH_RADIUS_KM;

        double dLat = toRadian(lat2 - lat1);
        double dLng = toRadian(lng2 - lng1);
        double a = Math.pow(Math.sin(dLat/2), 2)  +
                Math.cos(toRadian(lat1)) * Math.cos(toRadian(lat2)) * Math.pow(Math.sin(dLng/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    private static double toRadian(double degrees) {
        return (degrees * Math.PI) / 180.0d;
    }


}
