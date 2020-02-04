package com.penoybalut.tot.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ibo on 4/26/2014.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class Location {

    final boolean needsRecoding;
    final String longitude;
    final String latitude;

    @JsonCreator
    public Location(@JsonProperty("needs_recoding") boolean needsRecoding,
                    @JsonProperty("longitude") String longitude,
                    @JsonProperty("latitude") String latitude) {
        this.needsRecoding = needsRecoding;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @JsonProperty("needs_recoding")
    public boolean getNeedsRecoding() {
        return needsRecoding;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
