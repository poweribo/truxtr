package com.penoybalut.tot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.penoybalut.tot.core.RedisConfig;
import com.penoybalut.tot.core.SFDataConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TruckOTrackerConfiguration extends Configuration {
    @Valid
    @NotNull
    private SFDataConfig sfdata = new SFDataConfig();

    public SFDataConfig getSfdata() {
        return sfdata;
    }

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @Valid
    @NotNull
    @JsonProperty
    private RedisConfig redis = new RedisConfig();

    public RedisConfig getRedis() {
        return redis;
    }

}
