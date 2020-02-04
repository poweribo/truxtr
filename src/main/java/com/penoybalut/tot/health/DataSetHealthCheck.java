package com.penoybalut.tot.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import com.penoybalut.tot.service.FoodTruckService;
import com.penoybalut.tot.core.FoodTruckInfo;

import java.util.List;

@Singleton
public class DataSetHealthCheck extends InjectableHealthCheck {
    private final FoodTruckService fts;

    @Inject
    public DataSetHealthCheck(FoodTruckService fts) {
        this.fts = fts;
    }

    @Override
    protected Result check() throws Exception {

        List<FoodTruckInfo> foodTrucks = fts.getFoodTruckData();
        if (foodTrucks == null || foodTrucks.size() == 0) {
            return Result.unhealthy("Empty dataset!");
        }

        return Result.healthy();
    }

    @Override
    public String getName() {
        return "Dataset HealthCheck";
    }
}
