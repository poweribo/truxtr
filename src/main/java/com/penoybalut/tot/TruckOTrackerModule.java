package com.penoybalut.tot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.penoybalut.tot.core.FoodTruckInfo;
import com.penoybalut.tot.core.RedisConfig;
import com.penoybalut.tot.core.SFDataConfig;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Created by ibo on 4/25/14.
 */

public class TruckOTrackerModule extends AbstractModule {

    final static Logger logger = LoggerFactory.getLogger(TruckOTrackerModule.class);

    @Override
    protected void configure() {
    }

    @Provides
    public SFDataConfig provideSfData(TruckOTrackerConfiguration configuration) {
        return configuration.getSfdata();
    }

    @Provides
    public JedisPool provideJedisPool(TruckOTrackerConfiguration configuration) {
        RedisConfig redisConfig = configuration.getRedis();
        if (!"".equals(redisConfig.getPassword())) {
            return new JedisPool(new JedisPoolConfig(), redisConfig.getHostname(), redisConfig.getPort(),
                                 Protocol.DEFAULT_TIMEOUT, redisConfig.getPassword());
        } else {
            return new JedisPool(new JedisPoolConfig(), redisConfig.getHostname(), redisConfig.getPort());
        }
    }

    private final HibernateBundle<TruckOTrackerConfiguration> hibernateBundle =
            new HibernateBundle<TruckOTrackerConfiguration>(FoodTruckInfo.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TruckOTrackerConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Provides
    public SessionFactory provideSessionFactory(TruckOTrackerConfiguration configuration,
                                                Environment environment) {

        SessionFactory sf = hibernateBundle.getSessionFactory();
        if (sf == null) {
            try {
                hibernateBundle.run(configuration, environment);
            } catch (Exception e) {
                logger.error("Unable to run hibernatebundle");
            }
        }

        return hibernateBundle.getSessionFactory();
    }

}
