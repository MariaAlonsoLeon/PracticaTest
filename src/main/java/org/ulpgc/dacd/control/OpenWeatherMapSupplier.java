package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String templateUrl;
    private final String apiKey;

    public OpenWeatherMapSupplier(String templateUrl, String apiKey) {
        this.templateUrl = templateUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Weather> getWeather(Location location, List<Instant> instants) {
        String url = buildUrl(location);
        System.out.println(url);
        return instants.stream()
                .map(instant -> {
                    try {
                        String jsonData = getWeatherFromUrl(url);
                        return parseJsonData(jsonData, location, instant);
                    } catch (IOException e) {
                        handleIOException(e);
                        return null;
                    }
                })
                .filter(weather -> weather != null)
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
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray list = jsonObject.getAsJsonArray("list");
        if (list != null) {
            JsonObject forecastItem = findMatchingForecastItem(list, instant);
            if (forecastItem != null) {
                return createWeatherFromForecastData(forecastItem, location, instant);
            }
        }
        return null;
    }

    private JsonObject findMatchingForecastItem(JsonArray list, Instant instant) {
        long targetTimestamp = instant.getEpochSecond();
        for (int i = 0; i < list.size(); i++) {
            JsonObject forecastItem = list.get(i).getAsJsonObject();
            long forecastTimestamp = forecastItem.get("dt").getAsLong();
            if (forecastTimestamp == targetTimestamp) {
                return forecastItem;
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

        double temperature = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();
        int clouds = cloud.get("all").getAsInt();
        double windSpeed = wind.get("speed").getAsDouble();
        double rainProbability = forecastData.get("pop").getAsDouble();

        return new Weather(temperature, humidity, clouds, windSpeed, rainProbability, location, instant);
    }

    private void handleIOException(IOException e) {
        System.err.println("Error al obtener el clima: " + e.getMessage());
    }
}
