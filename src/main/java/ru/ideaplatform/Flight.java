package ru.ideaplatform;

import java.time.ZonedDateTime;

/**
 * Создадим отдельную структуру для удобной манипуляции данными из JSON
 * Включим в нее только необходимые нам поля
 */
public record Flight(String airline,
        String origin,
        String destination,
        ZonedDateTime departureDateTime,
        ZonedDateTime arrivalDateTime,
        long flightDutationInMinutes,
        int price) {
}
