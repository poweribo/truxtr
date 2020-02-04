package com.penoybalut.tot;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.penoybalut.tot.service.DataPollerService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TruckOTrackerApplication extends Application<TruckOTrackerConfiguration> {
    private GuiceBundle guiceBundle;

    public static void main(String[] args) throws Exception {
        new TruckOTrackerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Truck-O-Tracker";
    }

    @Override
    public void initialize(Bootstrap<TruckOTrackerConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<TruckOTrackerConfiguration>newBuilder()
                .addModule(new TruckOTrackerModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(TruckOTrackerConfiguration.class)
                .build();

        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(TruckOTrackerConfiguration configuration, Environment environment) throws ClassNotFoundException {
    }

}
