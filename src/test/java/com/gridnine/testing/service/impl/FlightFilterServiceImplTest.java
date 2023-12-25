package com.gridnine.testing.service.impl;

import com.gridnine.testing.dto.FlightFilterRequest;
import com.gridnine.testing.model.Flight;
import com.gridnine.testing.model.Segment;
import com.gridnine.testing.service.FlightFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlightFilterServiceImplTest {

    private FlightFilterService flightFilterService;
    private FlightFilterRequest filterRequest;

    @BeforeEach
    public void setup() {
        flightFilterService = new FlightFilterServiceImpl();
        filterRequest = new FlightFilterRequest();
    }

    @DisplayName("Проверка фильтрации по текущему времени для различных сценариев")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @MethodSource("createTestDataForFilterFromCurrentTime")
    void filterFromCurrentTime(List<Flight> flights, int expectedSize) {
        List<Flight> filteredFlights = flightFilterService.filterFromCurrentTime(flights);

        assertEquals(expectedSize, filteredFlights.size());
    }

    private static Stream<Arguments> createTestDataForFilterFromCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        return Stream.of(
                Arguments.of(List.of(), 0),
                Arguments.of(createFlightsBeforeCurrentTime(currentTime), 2),
                Arguments.of(createFlightsAfterCurrentTime(currentTime), 0),
                Arguments.of(createMixedFlightsForAfterAndBeforeCurrentTime(currentTime), 2)
        );
    }

    @DisplayName("Проверка фильтрации при прилете до вылета для различных сценариев")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @MethodSource("createTestDataForFilterArrivalBeforeDeparture")
    void filterArrivalBeforeDeparture(List<Flight> flights, int expectedSize) {
        assertEquals(expectedSize, flightFilterService.filterArrivalBeforeDeparture(flights).size());
    }

    private static Stream<Arguments> createTestDataForFilterArrivalBeforeDeparture() {
        return Stream.of(
                Arguments.of(emptyList(), 0),
                Arguments.of(createFlightsWithArrivalAfterDeparture(), 0),
                Arguments.of(createFlightsWithArrivalBeforeDeparture(), 2),
                Arguments.of(createMixedFlightsForArrivalAfterAndBeforeDeparture(), 2)
        );
    }

    @DisplayName("Проверка фильтрации по времени проведенному на земле более двух часов для различных сценариев")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @MethodSource("createTestDataForFilterMoreThanTwoHoursGroundTime")
    void filterMoreThanTwoHoursGroundTime(List<Flight> flights, int expectedSize) {
        assertEquals(expectedSize, flightFilterService.filterMoreThanTwoHoursGroundTime(flights).size());
    }

    private static Stream<Arguments> createTestDataForFilterMoreThanTwoHoursGroundTime() {
        return Stream.of(
                Arguments.of(emptyList(), 0),
                Arguments.of(createFlightsWithGroundTimeLessThanTwoHours(), 0),
                Arguments.of(createFlightsWithGroundTimeMoreThanTwoHours(), 2),
                Arguments.of(createMixedFlightWithGroundTimeMoreAndLessThanTwoHours(), 2)
        );
    }

    @Test
    @DisplayName("Проверка на сброс примитивных полей")
    void resetFilters_ShouldResetPrimitiveField() {
        filterRequest.setMaxStops(5);

        flightFilterService.resetFilters(filterRequest);

        assertEquals(0, filterRequest.getMaxStops());
    }

    @Test
    @DisplayName("Проверка на сброс объектов")
    void resetFilters_ShouldResetObjectField() {
        filterRequest.setBeforeDeparture(LocalDateTime.now());

        flightFilterService.resetFilters(filterRequest);

        assertNull(filterRequest.getBeforeDeparture());
    }

    @DisplayName("Проверка фильтрации полётов по всем полям фильтра")
    @ParameterizedTest
    @MethodSource("filterTestData")
    void filter_ShouldFilterFlightsBasedOnFilterRequest(List<Flight> flights,
                                                        LocalDateTime beforeDeparture, LocalDateTime afterDeparture,
                                                        LocalDateTime beforeArrival, LocalDateTime afterArrival,
                                                        Duration maxGroundTime, Duration minGroundTime,
                                                        Duration maxAirDuration, Duration minAirDuration,
                                                        Duration minFlightDuration, Duration maxFlightDuration,
                                                        int minStops, int maxStops, int expectedSize) {

        FlightFilterRequest filterRequest = new FlightFilterRequest();
        filterRequest.setBeforeDeparture(beforeDeparture);
        filterRequest.setAfterDeparture(afterDeparture);
        filterRequest.setBeforeArrival(beforeArrival);
        filterRequest.setAfterArrival(afterArrival);
        filterRequest.setMaxGroundTime(maxGroundTime);
        filterRequest.setMinGroundTime(minGroundTime);
        filterRequest.setMaxAirDuration(maxAirDuration);
        filterRequest.setMinAirDuration(minAirDuration);
        filterRequest.setMinFlightDuration(minFlightDuration);
        filterRequest.setMaxFlightDuration(maxFlightDuration);
        filterRequest.setMinStops(minStops);
        filterRequest.setMaxStops(maxStops);

        List<Flight> result = flightFilterService.applyFilter(flights, filterRequest);

        assertEquals(expectedSize, result.size());
    }

    static Stream<Arguments> filterTestData() {
        return Stream.of(
                Arguments.of(createUniversalTestFlights(1), LocalDateTime.now(), null, null, null, null, null, null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(2), null, LocalDateTime.now(), null, null, null, null, null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(3), null, null, LocalDateTime.now(), null, null, null, null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(4), null, null, null, LocalDateTime.now(), null, null, null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(4), null, null, null, null, Duration.ofHours(2), null, null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(1), null, null, null, null, null, Duration.ofHours(2), null, null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(4), null, null, null, null, null, null, Duration.ofHours(5), null, null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(4), null, null, null, null, null, null, null, Duration.ofHours(7), null, null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(2), null, null, null, null, null, null, null, null, Duration.ofHours(5), null, 0, 0, 1),
                Arguments.of(createUniversalTestFlights(2), null, null, null, null, null, null, null, null, null, Duration.ofHours(3), 0, 0, 1),
                Arguments.of(createUniversalTestFlights(5), null, null, null, null, null, null, null, null, null, null, 4, 0, 1),
                Arguments.of(createUniversalTestFlights(5), null, null, null, null, null, null, null, null, null, null, 0, 2, 1)
        );
    }


    private static List<Flight> createFlightsWithArrivalAfterDeparture() {
        Flight flight1 = createFlight(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        Flight flight2 = createFlight(LocalDateTime.now(), LocalDateTime.now().plusHours(3));
        return List.of(flight1, flight2);
    }

    private static List<Flight> createFlightsWithArrivalBeforeDeparture() {
        Flight flight1 = createFlight(LocalDateTime.now(), LocalDateTime.now().minusHours(2));
        Flight flight2 = createFlight(LocalDateTime.now(), LocalDateTime.now().minusHours(3));
        return List.of(flight1, flight2);
    }

    private static List<Flight> createFlightsBeforeCurrentTime(LocalDateTime currentTime) {
        Flight flight1 = createFlight(currentTime.minusHours(2), currentTime.minusHours(1));
        Flight flight2 = createFlight(currentTime.minusHours(3), currentTime.minusHours(2));
        return List.of(flight1, flight2);
    }

    private static List<Flight> createFlightsAfterCurrentTime(LocalDateTime currentTime) {
        Flight flight1 = createFlight(currentTime.plusHours(1), currentTime.plusHours(2));
        Flight flight2 = createFlight(currentTime.plusHours(3), currentTime.plusHours(4));
        return List.of(flight1, flight2);
    }

    private static List<Flight> createMixedFlightsForArrivalAfterAndBeforeDeparture() {
        return Stream.concat(createFlightsWithArrivalBeforeDeparture().stream(),
                createFlightsWithArrivalAfterDeparture().stream()).toList();
    }

    private static List<Flight> createMixedFlightsForAfterAndBeforeCurrentTime(LocalDateTime currentTime) {
        return Stream.concat(createFlightsAfterCurrentTime(currentTime).stream(),
                createFlightsBeforeCurrentTime(currentTime).stream()).toList();
    }

    private static Flight createFlight(LocalDateTime departure, LocalDateTime arrival) {
        Flight flight = mock(Flight.class);
        Segment segment = createSegment(departure, arrival);
        when(flight.getSegments()).thenReturn(List.of(segment));
        return flight;
    }

    private static List<Flight> createFlightsWithGroundTimeLessThanTwoHours() {
        Flight flight1 = createFlightWithGroundTime(1L);
        Flight flight2 = createFlightWithGroundTime(1L);
        return List.of(flight1, flight2);
    }

    private static List<Flight> createFlightsWithGroundTimeMoreThanTwoHours() {
        Flight flight1 = createFlightWithGroundTime(4L);
        Flight flight2 = createFlightWithGroundTime(5L);
        return List.of(flight1, flight2);
    }

    private static List<Flight> createMixedFlightWithGroundTimeMoreAndLessThanTwoHours() {
        return Stream.concat(createFlightsWithGroundTimeLessThanTwoHours().stream(),
                createFlightsWithGroundTimeMoreThanTwoHours().stream()).toList();
    }

    private static Flight createFlightWithGroundTime(long groundTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Flight flight = mock(Flight.class);
        Segment segment1 = createSegment(currentTime, currentTime.plusHours(1));
        Segment segment2 = createSegment(currentTime.plusHours(groundTime), currentTime.plusHours(3));
        when(flight.getSegments()).thenReturn(List.of(segment1, segment2));
        return flight;
    }

    private static Segment createSegment(LocalDateTime departure, LocalDateTime arrival) {
        Segment segment = mock(Segment.class);
        when(segment.getDepartureDate()).thenReturn(departure);
        when(segment.getArrivalDate()).thenReturn(arrival);
        return segment;
    }

    private static Segment createNewSegment(LocalDateTime departure, LocalDateTime arrival) {
        return new Segment(departure, arrival);
    }

    public static List<Flight> createUniversalTestFlights(int index) {
        List<Flight> flights = new ArrayList<>();

        switch (index) {
            case (1) -> {
                List<Segment> segments1 = new ArrayList<>();
                segments1.add(createNewSegment(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2)));
                segments1.add(createNewSegment(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5)));
                Flight flight1 = new Flight(segments1);
                flights.add(flight1);
            }
            case (2) -> {
                List<Segment> segments2 = new ArrayList<>();
                segments2.add(createNewSegment(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)));
                segments2.add(createNewSegment(LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(5)));
                Flight flight2 = new Flight(segments2);
                flights.add(flight2);
            }
            case (3) -> {
                List<Segment> segments3 = new ArrayList<>();
                segments3.add(createNewSegment(LocalDateTime.now().minusHours(20), LocalDateTime.now().minusHours(18)));
                segments3.add(createNewSegment(LocalDateTime.now().minusHours(17), LocalDateTime.now().minusHours(14)));
                Flight flight3 = new Flight(segments3);
                flights.add(flight3);
            }
            case (4) -> {
                List<Segment> segments4 = new ArrayList<>();
                segments4.add(createNewSegment(LocalDateTime.now().minusHours(8), LocalDateTime.now().minusHours(7)));
                segments4.add(createNewSegment(LocalDateTime.now().minusHours(4), LocalDateTime.now().plusHours(1)));
                Flight flight4 = new Flight(segments4);
                flights.add(flight4);
            }
            case (5) -> {
                List<Segment> segments5 = new ArrayList<>();
                segments5.add(createNewSegment(LocalDateTime.now().minusHours(8), LocalDateTime.now().minusHours(7)));
                segments5.add(createNewSegment(LocalDateTime.now().minusHours(4), LocalDateTime.now().plusHours(1)));
                segments5.add(createNewSegment(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5)));
                segments5.add(createNewSegment(LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(10)));
                Flight flight5 = new Flight(segments5);
                flights.add(flight5);
            }
        }
        return flights;
    }
}