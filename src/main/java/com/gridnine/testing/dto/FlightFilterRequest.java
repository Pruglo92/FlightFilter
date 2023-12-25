package com.gridnine.testing.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public class FlightFilterRequest {

    private LocalDateTime beforeDeparture;
    private LocalDateTime afterDeparture;
    private LocalDateTime beforeArrival;
    private LocalDateTime afterArrival;
    private Duration maxGroundTime;
    private Duration minGroundTime;
    private Duration maxAirDuration;
    private Duration minAirDuration;
    private Duration minFlightDuration;
    private Duration maxFlightDuration;
    private int minStops;
    private int maxStops;

    public LocalDateTime getBeforeDeparture() {
        return beforeDeparture;
    }

    public Duration getMaxAirDuration() {
        return maxAirDuration;
    }

    public Duration getMinAirDuration() {
        return minAirDuration;
    }

    public LocalDateTime getAfterDeparture() {
        return afterDeparture;
    }

    public LocalDateTime getBeforeArrival() {
        return beforeArrival;
    }

    public LocalDateTime getAfterArrival() {
        return afterArrival;
    }

    public Duration getMaxGroundTime() {
        return maxGroundTime;
    }

    public Duration getMinGroundTime() {
        return minGroundTime;
    }

    public Duration getMinFlightDuration() {
        return minFlightDuration;
    }

    public Duration getMaxFlightDuration() {
        return maxFlightDuration;
    }

    public int getMinStops() {
        return minStops;
    }

    public int getMaxStops() {
        return maxStops;
    }

    public void setBeforeDeparture(LocalDateTime beforeDeparture) {
        this.beforeDeparture = beforeDeparture;
    }

    public void setMaxAirDuration(Duration maxAirDuration) {
        this.maxAirDuration = maxAirDuration;
    }

    public void setMinAirDuration(Duration minAirDuration) {
        this.minAirDuration = minAirDuration;
    }

    public void setAfterDeparture(LocalDateTime afterDeparture) {
        this.afterDeparture = afterDeparture;
    }

    public void setBeforeArrival(LocalDateTime beforeArrival) {
        this.beforeArrival = beforeArrival;
    }

    public void setAfterArrival(LocalDateTime afterArrival) {
        this.afterArrival = afterArrival;
    }

    public void setMaxGroundTime(Duration maxGroundTime) {
        this.maxGroundTime = maxGroundTime;
    }

    public void setMinGroundTime(Duration minGroundTime) {
        this.minGroundTime = minGroundTime;
    }

    public void setMinFlightDuration(Duration minFlightDuration) {
        this.minFlightDuration = minFlightDuration;
    }

    public void setMaxFlightDuration(Duration maxFlightDuration) {
        this.maxFlightDuration = maxFlightDuration;
    }

    public void setMinStops(int minStops) {
        this.minStops = minStops;
    }

    public void setMaxStops(int maxStops) {
        this.maxStops = maxStops;
    }
}
