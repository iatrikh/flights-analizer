package ru.ideaplatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * В этом классе соберем утилитарным методы, чтобы не засорять Main.
 * Семантический, можно было бы методы разбить еще помельче.
 * Но я решил не усложнять.
 */
public class Utils {

    /**
     * Работу по чтению JSON файла выносим в отдельный метод.
     * Для манипуляций с JSON используем библиотеку org.json
     * Думал насчет использования Jackson или GSON, но они
     * мне показались избыточными для поставленной задачи.
     * Метод возращает коллекцию JSON объектов используемой библиотеки.
     */
    public static JSONArray parseJsonFile(String path) throws IOException {

        String flightsJsonFileStr = new String(Files.readAllBytes(Paths.get(path)));

        // Убираем служебные символы, чтобы распарсить строку в JSON без ошибок
        flightsJsonFileStr = flightsJsonFileStr.replaceAll("\\p{C}", "");

        JSONObject parentJson = new JSONObject(flightsJsonFileStr);

        JSONArray flightsJsonArray = parentJson.optJSONArray("tickets");

        return flightsJsonArray;
    }

    /**
     * "Десериализуем" JSON объекты в нашу кастомную структуру Flight.
     * Тут много манипуляций с пакетом time.
     * Да, мешанина, больно смотреть. :)
     * Возвращаем лист.
     */
    public static List<Flight> convertJsonArrayToFlightList(JSONArray flightsJsonArray) {

        ZoneId departureZoneId = ZoneId.of("Asia/Vladivostok");
        ZoneId arrivalZoneId = ZoneId.of("Asia/Jerusalem");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

        List<Flight> flights = new ArrayList<>();

        for (Object flightJsonObject : flightsJsonArray) {

            JSONObject flightJson = (JSONObject) flightJsonObject;

            String airline = flightJson.optString("carrier");
            String origin = flightJson.optString("origin");
            String destination = flightJson.optString("destination");
            int price = Integer.parseInt(flightJson.optString("price"));

            String strDepartureDateTime = flightJson.optString("departure_date") + " "
                    + flightJson.optString("departure_time");
            String strArrivalDateTime = flightJson.optString("arrival_date") + " "
                    + flightJson.optString("arrival_time");

            // Получаем время вылета и прилета в формате ZonedDateTime
            ZonedDateTime departureDateTime = LocalDateTime.parse(strDepartureDateTime, formatter)
                    .atZone(departureZoneId);

            ZonedDateTime arrivalDateTime = LocalDateTime.parse(strArrivalDateTime, formatter)
                    .atZone(arrivalZoneId);

            // Получаем длительность перелета в минутах
            long flightDurationInMinutes = ChronoUnit.MINUTES.between(departureDateTime, arrivalDateTime);

            Flight flight = new Flight(airline, origin, destination, departureDateTime, arrivalDateTime,
                    flightDurationInMinutes, price);

            flights.add(flight);
        }

        return flights;
    }

    /**
     * Тут просто решил вынести в отдельный метод построчный вывод названия
     * авиакомпании и минимальную цену перелета.
     */
    public static void printMinFlightDurationByAirline(List<Flight> flights) {

        System.out.println("Минимальная длительность перелета из Владивостока в Тель-Авив по авиакомпаниям:");

        Map<String, List<Flight>> flightsByAirline = flights.stream()
                .collect(Collectors.groupingBy(Flight::airline));

        for (String airline : flightsByAirline.keySet()) {

            long minFlightDuration = flightsByAirline.get(airline).stream()
                    .map(Flight::flightDutationInMinutes)
                    .reduce(Math::min).get();

            long hours = minFlightDuration / 60;
            long minutes = minFlightDuration % 60;

            System.out.println(airline + " - " + hours + " часов, " + minutes + " минут.");
        }
    }
}
