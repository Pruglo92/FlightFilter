package com.gridnine.testing.service;

import com.gridnine.testing.dto.FlightFilterRequest;
import com.gridnine.testing.model.Flight;

import java.util.List;

public interface FlightFilterService {

    List<Flight> filterFromCurrentTime(List<Flight> flights);

    List<Flight> filterArrivalBeforeDeparture(List<Flight> flights);

    List<Flight> filterMoreThanTwoHoursGroundTime(List<Flight> flights);

    List<Flight> applyFilter(List<Flight> flights, FlightFilterRequest filterRequest);

    void resetFilters(FlightFilterRequest filterRequest);
}
