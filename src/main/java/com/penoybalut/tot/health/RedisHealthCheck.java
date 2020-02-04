package com.penoybalut.tot.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Singleton
public class RedisHealthCheck extends InjectableHealthCheck {
    private final JedisPool jedisPool;

    @Inject
    public RedisHealthCheck(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    protected Result check() throws Exception {
        Jedis jedis;

        try {
            jedis = jedisPool.getResource();
        } catch (Exception ex) {
            return Result.unhealthy("Redis is not working!");
        }

        if (jedis == null) {
            return Result.unhealthy("Redis is not working!");
        }

        jedis.set("codeword", "valar morghulis");

        String codeword = jedis.get("codeword");

        if (!"valar morghulis".equals(codeword)) {
            return Result.unhealthy("Redis not !");
        }

        return Result.healthy();
    }

    @Override
    public String getName() {
        return "Redis HealthCheck";
    }
}
