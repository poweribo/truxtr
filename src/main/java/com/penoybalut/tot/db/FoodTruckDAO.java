package com.penoybalut.tot.db;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.penoybalut.tot.core.FoodTruckInfo;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import java.util.List;

public class FoodTruckDAO extends AbstractDAO<FoodTruckInfo> {
    // to control the size of the first-level cache. todo: get from config
    private static final int BATCH_SIZE = 50;

    @Inject
    public FoodTruckDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<FoodTruckInfo> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public FoodTruckInfo create(FoodTruckInfo foodTruck) {
        return persist(foodTruck);
    }

    public List<FoodTruckInfo> findAll() {
        return list(namedQuery("com.penoybalut.tot.core.FoodTruckInfo.findAll"));
    }

    public List<FoodTruckInfo> findByCompositeId(List<Long> idList) {
        return list(namedQuery("com.penoybalut.tot.core.FoodTruckInfo.findByCompositeId").setParameterList("idList", idList));
    }

    public void batchInsert(List<FoodTruckInfo> foodTruckInfos) {
        int counter = 0;
        for (FoodTruckInfo foodTruck : foodTruckInfos) {
            persist(foodTruck);
            if (counter % BATCH_SIZE == 0) {
                currentSession().flush();
                currentSession().clear();
            }
            counter++;
        }
    }

    public int deleteAll() {
        Query q = namedQuery("com.penoybalut.tot.core.FoodTruckInfo.deleteAll");
        return q.executeUpdate();
    }
}
