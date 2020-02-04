package com.penoybalut.tot.core;

/**
 * Created by ibo on 4/26/2014.
 *
 */

import com.sun.jersey.api.client.GenericType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "foodtrucks")
@NamedQueries({
        @NamedQuery(
                name = "com.penoybalut.tot.core.FoodTruckInfo.findAll",
                query = "SELECT p FROM FoodTruckInfo p"
        ),
        @NamedQuery(
                name = "com.penoybalut.tot.core.FoodTruckInfo.findById",
                query = "SELECT p FROM FoodTruckInfo p WHERE p.id = :id"
        )
        ,
        @NamedQuery(
                name = "com.penoybalut.tot.core.FoodTruckInfo.findByCompositeId",
                query = "SELECT p FROM FoodTruckInfo p WHERE p.id in (:idList)"
        ),
        @NamedQuery(
                name = "com.penoybalut.tot.core.FoodTruckInfo.deleteAll",
                query = "DELETE FROM FoodTruckInfo"
        )
})

@JsonIgnoreProperties(ignoreUnknown=true)
public class FoodTruckInfo implements Comparable<FoodTruckInfo> {
    private long id;
    private String objectid;
    private String applicant;
    private String facilitytype;
    private String cnn;
    private String locationdescription;
    private String address;
    private String blocklot;
    private String block;
    private String lot;
    private String permit;
    private String status;
    private String fooditems;
    private String x;
    private String y;
    private String latitude;
    private String longitude;
    private String schedule;
    private String noisent;
    private String approved;
    private String received;
    private String priorpermit;
    private String expirationdate;

    public static GenericType<List<FoodTruckInfo>> LIST_TYPE = new GenericType<List<FoodTruckInfo>>(){};

    public FoodTruckInfo(){}

    @JsonCreator
    public FoodTruckInfo(@JsonProperty("objectid") String objectid,
                         @JsonProperty("applicant") String applicant,
                         @JsonProperty("facilitytype") String facilitytype,
                         @JsonProperty("cnn") String cnn,
                         @JsonProperty("locationdescription") String locationdescription,
                         @JsonProperty("address") String address,
                         @JsonProperty("blocklot") String blocklot,
                         @JsonProperty("block") String block,
                         @JsonProperty("lot") String lot,
                         @JsonProperty("permit") String permit,
                         @JsonProperty("status") String status,
                         @JsonProperty("fooditems") String fooditems,
                         @JsonProperty("x") String x,
                         @JsonProperty("y") String y,
                         @JsonProperty("latitude") String latitude,
                         @JsonProperty("longitude") String longitude,
                         @JsonProperty("schedule") String schedule,
                         @JsonProperty("noisent") String noisent,
                         @JsonProperty("approved") String approved,
                         @JsonProperty("received") String received,
                         @JsonProperty("priorpermit") String priorpermit,
                         @JsonProperty("expirationdate") String expirationdate
    ) {
        this.objectid = objectid;
        this.applicant = applicant;
        this.facilitytype = facilitytype;
        this.cnn = cnn;
        this.locationdescription = locationdescription;
        this.address = address;
        this.blocklot = blocklot;
        this.block = block;
        this.lot = lot;
        this.permit = permit;
        this.status = status;
        this.fooditems = fooditems;
        this.x = x;
        this.y = y;
        this.latitude = latitude;
        this.longitude = longitude;
        this.schedule = schedule;
        this.noisent = noisent;
        this.approved = approved;
        this.received = received;
        this.priorpermit = priorpermit;
        this.expirationdate = expirationdate;
    }

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObjectid() {
        return objectid;
    }

    public String getApplicant() {
        return applicant;
    }

    public String getFacilitytype() {
        return facilitytype;
    }

    public String getCnn() {
        return cnn;
    }

    public String getLocationdescription() {
        return locationdescription;
    }

    public String getAddress() {
        return address;
    }

    public String getBlocklot() {
        return blocklot;
    }

    public String getBlock() {
        return block;
    }

    public String getLot() {
        return lot;
    }

    public String getPermit() {
        return permit;
    }

    public String getStatus() {
        return status;
    }

    @Column(length = 600)
    public String getFooditems() {
        return fooditems;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Column(length = 300)
    public String getSchedule() {
        return schedule;
    }

    public String getNoisent() {
        return noisent;
    }

    public String getApproved() {
        return approved;
    }

    public String getReceived() {
        return received;
    }

    public String getPriorpermit() {
        return priorpermit;
    }

    public String getExpirationdate() {
        return expirationdate;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public void setFacilitytype(String facilitytype) {
        this.facilitytype = facilitytype;
    }

    public void setCnn(String cnn) {
        this.cnn = cnn;
    }

    public void setLocationdescription(String locationdescription) {
        this.locationdescription = locationdescription;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBlocklot(String blocklot) {
        this.blocklot = blocklot;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFooditems(String fooditems) {
        this.fooditems = fooditems;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setNoisent(String noisent) {
        this.noisent = noisent;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public void setPriorpermit(String priorpermit) {
        this.priorpermit = priorpermit;
    }

    public void setExpirationdate(String expirationdate) {
        this.expirationdate = expirationdate;
    }

    private double distanceFromLoc;

    public double getDistanceFromLoc() {
        return distanceFromLoc;
    }

    public void setDistanceFromLoc(double distanceFromLoc) {
        this.distanceFromLoc = distanceFromLoc;
    }

    public int compareTo(FoodTruckInfo that) {
        if (this.distanceFromLoc < that.distanceFromLoc)
            return -1;
        else if (this.distanceFromLoc > that.distanceFromLoc)
            return 1;
        else
            return 0;
    }

    public static Comparator<FoodTruckInfo> FoocTruckNameComparator = new Comparator<FoodTruckInfo>() {
        public int compare(FoodTruckInfo ftinfo1, FoodTruckInfo ftinfo2) {
            String truckName1 = ftinfo1.getApplicant().toLowerCase();
            String truckName2 = ftinfo2.getApplicant().toLowerCase();
            return truckName1.compareTo(truckName2);
        }

    };
}
