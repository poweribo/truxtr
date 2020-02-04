package com.penoybalut.tot.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.penoybalut.tot.service.FoodTruckService;
import com.penoybalut.tot.core.FoodTruckInfo;
import com.penoybalut.tot.db.FoodTruckDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class TruckTrackerResource {
    final static Logger logger = LoggerFactory.getLogger(TruckTrackerResource.class);

    private final FoodTruckService foodTruckService;
    private final FoodTruckDAO foodTruckDAO;

    @Inject
    public TruckTrackerResource(FoodTruckDAO foodTruckDAO, FoodTruckService foodTruckService) {
        this.foodTruckDAO = foodTruckDAO;
        this.foodTruckService = foodTruckService;
    }

    @GET
    @Path("/autosuggestwords")
    public Response getAutoSuggestWords() {
        Set suggestions = foodTruckService.getAutoSuggestWords();
        return Response.ok(suggestions).build();
    }

    @GET
    @Path("/trucknames")
    public Response getTruckNames() {
        Set truckNames = foodTruckService.getTruckNames();
        return Response.ok(truckNames).build();
    }

    @GET @UnitOfWork
    @Path("/foodtrucks")
    public Response getAllFoodTrucks(@QueryParam("keyword") String keyword,
                                     @QueryParam("latitude") String latitude,
                                     @QueryParam("longitude") String longitude) {
        if (keyword != null && latitude != null && longitude != null) {
            return getFoodTruckByKeyword(keyword, latitude, longitude);
        }
        final List<FoodTruckInfo> foodTrucks = foodTruckDAO.findAll();
        return Response.ok(foodTrucks).build();
    }

    @GET @UnitOfWork
    @Path("/foodtruck/{truckId}")
    public Response getFoodTruck(@PathParam("truckId") String truckId) {
        final Optional<FoodTruckInfo> foodTruck = foodTruckDAO.findById(Long.parseLong(truckId));
        if (foodTruck.isPresent()) {
            return Response.ok(foodTruck).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Response getFoodTruckByKeyword(String keyword, String latitude, String longitude) {
        final List<Long> foodTruckIdList = foodTruckService.getFoodTrucksIdsByKeyword(keyword);
        if (foodTruckIdList.size() == 0) {
            return Response.ok(new ArrayList<Long>()).build();
        }

        List<FoodTruckInfo> foodTrucks = foodTruckDAO.findByCompositeId(foodTruckIdList);
        foodTruckService.computeDistanceFromOrigin(foodTrucks, latitude, longitude);
        Collections.sort(foodTrucks);   // sort by distance

        // Limit to max of 100 results only
        if (foodTrucks.size() > 100) {
            foodTrucks = foodTrucks.subList(0, 100);
        }

        return Response.ok(foodTrucks).build();
    }

    @PreDestroy
    void destroy() {
        logger.info("Destroying TruckTrackerResource...");
    }
}
