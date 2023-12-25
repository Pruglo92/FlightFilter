package com.gridnine.testing.util;

import com.gridnine.testing.model.Flight;

import java.util.function.Predicate;

public record FlightPredicate(Predicate<Flight> predicate) {
}
