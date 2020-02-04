package com.penoybalut.tot.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by ibo on 4/26/2014.
 */

public class RedisConfig
{
    @NotEmpty
    @JsonProperty
    private String hostname;

    @Min(1)
    @Max(65535)
    @JsonProperty
    private Integer port;

    @JsonProperty
    private String password;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}