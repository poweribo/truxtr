package com.penoybalut.tot.service;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.penoybalut.tot.core.SFDataConfig;
import io.dropwizard.lifecycle.Managed;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ibo on 4/29/2014.
 */

public class DataPollerService implements Managed {

    final static Logger logger = LoggerFactory.getLogger(DataPollerService.class);

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture scheduledFuture;

    private final FoodTruckService fts;
    private final SessionFactory sessionFactory;
    private final SFDataConfig sfDataConfig;

    @Inject
    public DataPollerService(FoodTruckService fts, SessionFactory sessionFactory, SFDataConfig sfDataConfig) {
        this.fts = fts;
        this.sessionFactory = sessionFactory;
        this.sfDataConfig = sfDataConfig;
    }

    @Override
    public void start() throws Exception {
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay (
                new Runnable() {
                    @Timed
                    @Override
                    public void run() {
                        logger.info("Started DataPollerService|run()... ");
                        Session session = sessionFactory.openSession();
                        ManagedSessionContext.bind(session);
                        Transaction tx = session.beginTransaction();
                        fts.generateKeywordLookup();
                        tx.commit();
                        logger.info("DataPollerService|run() is done. It will run again after {} hours.", sfDataConfig.getUpdateInterval());
                    }
                }, 0, sfDataConfig.getUpdateInterval(), TimeUnit.HOURS // Update every X hrs
        );
    }

    @Override
    public void stop() throws Exception {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdownNow();
    }
}
