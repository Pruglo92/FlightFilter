package com.gridnine.testing;

import com.gridnine.testing.dto.FlightFilterRequest;
import com.gridnine.testing.factory.FlightBuilder;
import com.gridnine.testing.model.Flight;
import com.gridnine.testing.service.FlightFilterService;
import com.gridnine.testing.service.impl.FlightFilterServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FlightFilterRequest filterRequest = new FlightFilterRequest();
        FlightFilterService flightFilterService = new FlightFilterServiceImpl();

        while (true) {
            System.out.println("Выберите действие:");
            System.out.format("1. Начали полёт до: %s%n", filterRequest.getBeforeDeparture() != null ? filterRequest.getBeforeDeparture() : "");
            System.out.format("2. Начали полёт после: %s%n", filterRequest.getAfterDeparture() != null ? filterRequest.getAfterDeparture() : "");
            System.out.format("3. Закончили полёт до: %s%n", filterRequest.getBeforeArrival() != null ? filterRequest.getBeforeArrival() : "");
            System.out.format("4. Закончили полёт после: %s%n", filterRequest.getAfterArrival() != null ? filterRequest.getAfterArrival() : "");
            System.out.format("5. Провели на земле больше чем (чч:мм): %s%n", filterRequest.getMaxGroundTime() != null ? filterRequest.getMaxGroundTime() : "");
            System.out.format("6. Провели на земле меньше чем (чч:мм): %s%n", filterRequest.getMinGroundTime() != null ? filterRequest.getMinGroundTime() : "");
            System.out.format("7. Провели в полёте больше чем (чч:мм): %s%n", filterRequest.getMaxAirDuration() != null ? filterRequest.getMaxAirDuration() : "");
            System.out.format("8. Провели в полёте меньше чем (чч:мм): %s%n", filterRequest.getMinAirDuration() != null ? filterRequest.getMinAirDuration() : "");
            System.out.format("9. Промежуточных остановок больше чем: %s%n", filterRequest.getMaxStops() != 0 ? filterRequest.getMaxStops() : "");
            System.out.format("10. Промежуточных остановок меньше чем: %s%n", filterRequest.getMinStops() != 0 ? filterRequest.getMinStops() : "");
            System.out.format("11. Общая длительность полёта больше чем (чч:мм): %s%n", filterRequest.getMaxFlightDuration() != null ? filterRequest.getMaxFlightDuration() : "");
            System.out.format("12. Общая длительность полёта меньше чем (чч:мм): %s%n", filterRequest.getMinFlightDuration() != null ? filterRequest.getMinFlightDuration() : "");
            System.out.format("0. Применить фильтры и вывести результат %n");
            System.out.println();
            System.out.println();
            System.out.format("13. Вывести все перелёты до текущего момента времени %n");
            System.out.format("14. Вывести все перелёты где сегменты с датой прилёта раньше даты вылета %n");
            System.out.format("15. Вывести все перелёты где общее время, проведённое на земле превышает два часа %n");
            System.out.format("16. Сбросить фильтры %n");
            System.out.format("17. Выйти из программы %n");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> filterRequest.setBeforeDeparture(getDateTimeInput(scanner, "до которого вылетели"));
                case 2 -> filterRequest.setAfterDeparture(getDateTimeInput(scanner, "после которого вылетели"));
                case 3 -> filterRequest.setBeforeArrival(getDateTimeInput(scanner, "до которого приземлились"));
                case 4 -> filterRequest.setAfterArrival(getDateTimeInput(scanner, "после которого приземлились"));
                case 5 -> filterRequest.setMaxGroundTime(getDurationInput(scanner, "больше чем"));
                case 6 -> filterRequest.setMinGroundTime(getDurationInput(scanner, "меньше чем"));
                case 7 -> filterRequest.setMaxAirDuration(getDurationInput(scanner, "больше чем"));
                case 8 -> filterRequest.setMinAirDuration(getDurationInput(scanner, "меньше чем"));
                case 9 -> filterRequest.setMaxStops(getIntInput(scanner, "больше чем"));
                case 10 -> filterRequest.setMinStops(getIntInput(scanner, "меньше чем"));
                case 11 -> filterRequest.setMaxFlightDuration(getDurationInput(scanner, "больше чем"));
                case 12 -> filterRequest.setMinFlightDuration(getDurationInput(scanner, "меньше чем"));
                case 0 -> {
                    List<Flight> filteredFlights = flightFilterService.applyFilter(FlightBuilder.createFlights(), filterRequest);
                    System.out.println("Результаты фильтрации: " + filteredFlights);
                }
                case 13 -> {
                    List<Flight> filteredFlights = flightFilterService.filterFromCurrentTime(FlightBuilder.createFlights());
                    System.out.println("Результаты фильтрации: " + filteredFlights);
                }
                case 14 -> {
                    List<Flight> filteredFlights = flightFilterService.filterArrivalBeforeDeparture(FlightBuilder.createFlights());
                    System.out.println("Результаты фильтрации: " + filteredFlights);
                }
                case 15 -> {
                    List<Flight> filteredFlights = flightFilterService.filterMoreThanTwoHoursGroundTime(FlightBuilder.createFlights());
                    System.out.println("Результаты фильтрации: " + filteredFlights);
                }
                case 16 -> {
                    flightFilterService.resetFilters(filterRequest);
                    System.out.println("Фильтры сброшены");
                }
                case 17 -> {
                    System.out.println("Программа завершена.");
                    System.exit(0);
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }
    }

    private static LocalDateTime getDateTimeInput(Scanner scanner, String prompt) {
        System.out.print("Введите дату и время " + prompt + " (в формате yyyy-MM-ddTHH:mm): ");
        return parseDateTime(scanner.next());
    }

    private static Duration getDurationInput(Scanner scanner, String prompt) {
        System.out.print("Введите продолжительность " + prompt + " (в формате чч:мм): ");
        return parseDuration(scanner.next());
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        System.out.print("Введите значение " + prompt + ": ");
        return scanner.nextInt();
    }

    private static LocalDateTime parseDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            logger.warning("Ошибка при преобразовании: " + e.getMessage());
        } catch (Exception e) {
            logger.warning("Произошла ошибка: " + e.getMessage());
        }
        return null;
    }

    private static Duration parseDuration(String input) {
        try {
            String[] parts = input.split(":");
            if (parts.length == 2) {
                long hours = Long.parseLong(parts[0]);
                long minutes = Long.parseLong(parts[1]);
                return Duration.ofHours(hours).plusMinutes(minutes);
            } else {
                logger.warning("Неправильный формат времени: " + input);
            }
        } catch (NumberFormatException e) {
            logger.warning("Ошибка при преобразовании: " + e.getMessage());
        } catch (Exception e) {
            logger.warning("Произошла ошибка: " + e.getMessage());
        }
        return null;
    }
}