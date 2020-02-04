package com.penoybalut.tot.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import com.penoybalut.tot.core.SFDataConfig;

/**
 * Created by ibo on 4/26/2014.
 */

@Singleton
public class TruckOTrackerHealthCheck extends InjectableHealthCheck {
    private final SFDataConfig sfdata;

    @Inject
    public TruckOTrackerHealthCheck(SFDataConfig sfdata) {
        this.sfdata = sfdata;
    }

    @Override
    protected Result check() throws Exception {

        if (sfdata.getHostUrl() == "") {
            return Result.unhealthy("missing sf data url");
        }

        return Result.healthy();
    }

    @Override
    public String getName() {
        return "Config Check";
    }
}
