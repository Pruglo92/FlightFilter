package com.gridnine.testing.service.impl;

import com.gridnine.testing.dto.FlightFilterRequest;
import com.gridnine.testing.model.Flight;
import com.gridnine.testing.model.Segment;
import com.gridnine.testing.service.FlightFilterService;
import com.gridnine.testing.util.FlightPredicate;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FlightFilterServiceImpl implements FlightFilterService {

    private static final Logger logger = Logger.getLogger(FlightFilterServiceImpl.class.getName());

    private List<Flight> filter(List<Flight> flights, List<FlightPredicate> filters) {
        Predicate<Flight> combinedFilter = filters.stream()
                .map(FlightPredicate::predicate)
                .reduce(Predicate::and)
                .orElse(flight -> true);

        return flights.stream()
                .filter(combinedFilter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> applyFilter(List<Flight> flights, FlightFilterRequest filterRequest) {
        List<FlightPredicate> filters = buildFilters(filterRequest);
        return filter(flights, filters);
    }

    private List<FlightPredicate> buildFilters(FlightFilterRequest filterRequest) {
        List<FlightPredicate> filters = new ArrayList<>();

        if (filterRequest.getBeforeDeparture() != null) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().stream()
                            .anyMatch(segment -> segment.getDepartureDate().isBefore(filterRequest.getBeforeDeparture()))
            ));
        }

        if (filterRequest.getAfterDeparture() != null) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().stream()
                            .allMatch(segment -> segment.getDepartureDate().isAfter(filterRequest.getAfterDeparture()))
            ));
        }

        if (filterRequest.getBeforeArrival() != null) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().stream()
                            .allMatch(segment -> segment.getArrivalDate().isBefore(filterRequest.getBeforeArrival()))
            ));
        }

        if (filterRequest.getAfterArrival() != null) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().stream()
                            .anyMatch(segment -> segment.getArrivalDate().isAfter(filterRequest.getAfterArrival()))
            ));
        }

        if (filterRequest.getMaxGroundTime() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalGroundTime(flight) > filterRequest.getMaxGroundTime().toMinutes()
            ));
        }

        if (filterRequest.getMinGroundTime() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalGroundTime(flight) < filterRequest.getMinGroundTime().toMinutes()
            ));
        }

        if (filterRequest.getMinFlightDuration() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalFlightTime(flight) < filterRequest.getMinFlightDuration().toMinutes()
            ));
        }

        if (filterRequest.getMaxFlightDuration() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalFlightTime(flight) > filterRequest.getMaxFlightDuration().toMinutes()
            ));
        }

        if (filterRequest.getMinAirDuration() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalAirTime(flight) < filterRequest.getMinAirDuration().toMinutes()
            ));
        }

        if (filterRequest.getMaxAirDuration() != null) {
            filters.add(new FlightPredicate(
                    flight -> calculateTotalAirTime(flight) > filterRequest.getMaxAirDuration().toMinutes()
            ));
        }

        if (filterRequest.getMinStops() > 0) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().size() - 1 < filterRequest.getMinStops()
            ));
        }

        if (filterRequest.getMaxStops() > 0) {
            filters.add(new FlightPredicate(
                    flight -> flight.getSegments().size() - 1 > filterRequest.getMaxStops()
            ));
        }

        return filters;
    }

    private long calculateTotalGroundTime(Flight flight) {
        List<Segment> segments = flight.getSegments();
        long totalGroundTime = 0;

        for (int i = 0; i < segments.size() - 1; i++) {
            LocalDateTime arrival = segments.get(i).getArrivalDate();
            LocalDateTime departure = segments.get(i + 1).getDepartureDate();
            totalGroundTime += Duration.between(arrival, departure).toMinutes();
        }

        return totalGroundTime;
    }

    private long calculateTotalAirTime(Flight flight) {
        List<Segment> segments = flight.getSegments();
        long totalAirTime = 0;

        for (Segment segment : segments) {
            LocalDateTime departure = segment.getDepartureDate();
            LocalDateTime arrival = segment.getArrivalDate();
            totalAirTime += Duration.between(departure, arrival).toMinutes();
        }

        return totalAirTime;
    }

    private long calculateTotalFlightTime(Flight flight) {
        List<Segment> segments = flight.getSegments();
        LocalDateTime firstDeparture = segments.get(0).getDepartureDate();
        LocalDateTime lastArrival = segments.get(segments.size() - 1).getArrivalDate();
        return Duration.between(firstDeparture, lastArrival).toMinutes();
    }

    @Override
    public List<Flight> filterFromCurrentTime(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now())))
                .toList();
    }

    @Override
    public List<Flight> filterArrivalBeforeDeparture(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate())))
                .toList();
    }

    @Override
    public List<Flight> filterMoreThanTwoHoursGroundTime(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> calculateTotalGroundTime(flight) > 120L)
                .toList();
    }

    @Override
    public void resetFilters(FlightFilterRequest filterRequest) {
        Class<?> clazz = filterRequest.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    if (field.getType() == int.class) {
                        field.set(filterRequest, 0);
                    }
                } else {
                    field.set(filterRequest, null);
                }
            } catch (IllegalAccessException e) {
                logger.warning("Ошибка доступа к полю" + field + e.getMessage());
            }
        }
    }
}
