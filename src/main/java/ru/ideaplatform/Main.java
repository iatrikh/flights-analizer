package ru.ideaplatform;

import static ru.ideaplatform.Utils.convertJsonArrayToFlightList;
import static ru.ideaplatform.Utils.parseJsonFile;
import static ru.ideaplatform.Utils.printMinFlightDurationByAirline;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONArray;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Hello, Idea Platform!\n");

        JSONArray flightsJsonArray = parseJsonFile("src/main/resources/tickets.json");

        List<Flight> allFlights = convertJsonArrayToFlightList(flightsJsonArray);

        // Нам нужны только перелеты из Владивостока в Тель-Авив
        List<Flight> flightsVvoTlv = allFlights.stream()
                .filter(flight -> flight.origin().equals("VVO") &&
                        flight.destination().equals("TLV"))
                .toList();

        // Выводим на консоль минимальную длительность перелета по авиакомпаниями
        printMinFlightDurationByAirline(flightsVvoTlv);

        int[] flightPriceArray = flightsVvoTlv.stream()
                .mapToInt(Flight::price)
                .sorted() // тут мы сразу сортируем цены, чтобы потом можно было найти медиану
                .toArray();

        double averageFlightPrice = IntStream.of(flightPriceArray)
                .average()
                .getAsDouble();

        double medianPrice;
        // Если выборка четная, то медиану находим по среднему из двух центральных
        // значений
        if (flightPriceArray.length % 2 == 0) {
            medianPrice = (flightPriceArray[flightPriceArray.length / 2]
                    + flightPriceArray[flightPriceArray.length / 2 - 1]) / 2;
        } else {
            medianPrice = flightPriceArray[flightPriceArray.length * 2];
        }

        // Выводим среднюю и медианную цены, а также их разницу
        System.out.println("Средняя и медианная цены перелета составили соответственно: " + averageFlightPrice + ", "
                + medianPrice + " (разница: " + (averageFlightPrice - medianPrice) + ")");
    }
}