package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String templateUrl;
    private final String apiKey;
    private static final Logger logger = Logger.getLogger(OpenWeatherMapSupplier.class.getName());

    private static final String TEMPERATURE_KEY = "temp";
    private static final String HUMIDITY_KEY = "humidity";
    private static final String CLOUDS_KEY = "all";
    private static final String WIND_SPEED_KEY = "speed";
    private static final String RAIN_PROBABILITY_KEY = "pop";
    private static final String LIST_KEY = "list";
    private static final String TIMESTAMP_KEY = "dt";

    public OpenWeatherMapSupplier(String templateUrl, String apiKey) {
        this.templateUrl = templateUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Weather> getWeathers(Location location, List<Instant> instants) {
        String url = buildUrl(location);
        return instants.stream()
                .map(instant -> {
                    try {
                        String jsonWeather = getWeatherFromUrl(url);
                        return parseJsonData(jsonWeather, location, instant);
                    } catch (IOException e) {
                        handleException("Error fetching weather data", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String buildUrl(Location location) {
        String coordinates = String.format("lat=%s&lon=%s", location.getLat(), location.getLon());
        return String.format("%s%s&appid=%s&units=metric", templateUrl, coordinates, apiKey);
    }

    private String getWeatherFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private Weather parseJsonData(String jsonData, Location location, Instant instant) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray list = jsonObject.getAsJsonArray(LIST_KEY);

            if (list != null) {
                JsonObject forecastItem = findMatchingWeatherItem(list, instant);
                if (forecastItem != null) {
                    return createWeatherFromForecastData(forecastItem, location, instant);
                }
            }
        } catch (Exception e) {
            handleException("Error parsing JSON", e);
        }
        return null;
    }

    private JsonObject findMatchingWeatherItem(JsonArray list, Instant instant) {
        long targetTimestamp = instant.getEpochSecond();
        for (int i = 0; i < list.size(); i++) {
            JsonObject weatherItem = list.get(i).getAsJsonObject();
            long forecastTimestamp = weatherItem.get(TIMESTAMP_KEY).getAsLong();
            if (forecastTimestamp == targetTimestamp) {
                return weatherItem;
            } else if (forecastTimestamp > targetTimestamp) {
                break;
            }
        }
        return null;
    }

    private Weather createWeatherFromForecastData(JsonObject forecastData, Location location, Instant instant) {
        JsonObject main = forecastData.getAsJsonObject("main");
        JsonObject cloud = forecastData.getAsJsonObject("clouds");
        JsonObject wind = forecastData.getAsJsonObject("wind");
        float temperature = main.get(TEMPERATURE_KEY).getAsFloat();
        int humidity = main.get(HUMIDITY_KEY).getAsInt();
        int clouds = cloud.get(CLOUDS_KEY).getAsInt();
        float windSpeed = wind.get(WIND_SPEED_KEY).getAsFloat();
        float rainProbability = forecastData.get(RAIN_PROBABILITY_KEY).getAsFloat();
        return new Weather(temperature, humidity, clouds, windSpeed, rainProbability, location, instant);
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
