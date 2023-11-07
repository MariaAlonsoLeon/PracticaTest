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
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String templateUrl;
    private final String apiKey;

    public OpenWeatherMapSupplier(String templateUrl, String apiKey) {
        this.templateUrl = templateUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Weather> getWeather(Location location, List<Instant> instants) throws IOException {
        List<Weather> weathers = new ArrayList<>();

        String url = buildUrl(location);
        String jsonData = getWeatherDataFromUrl(url);

        for (Instant instant : instants) {
            Weather weather = parseJsonData(jsonData, location, instant);
            if (weather != null) {
                weathers.add(weather);
            }
        }

        return weathers;
    }

    private String buildUrl(Location location) {
        String coordinates = "lat=" + location.getLat() + "&lon=" + location.getLon();
        System.out.println(templateUrl + coordinates  + "&appid=" + apiKey + "&units=metric");
        return templateUrl + coordinates + "&appid=" + apiKey + "&units=metric";
    }

    private String getWeatherDataFromUrl(String url) throws IOException {
        // Scraping
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private Weather parseJsonData(String jsonData, Location location, Instant instant) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray list = jsonObject.getAsJsonArray("list");

        if (list != null && list.size() > 0) {
            long targetTimestamp = instant.getEpochSecond();

            for (int i = 0; i < list.size(); i++) {
                JsonObject forecastData = list.get(i).getAsJsonObject();
                long forecastTimestamp = forecastData.get("dt").getAsLong();

                if (forecastTimestamp == targetTimestamp) {
                    return createWeatherFromForecastData(forecastData, location, instant);
                }
            }
        }

        return null; // No se encontraron datos para el Instant especificado
    }

    private Weather createWeatherFromForecastData(JsonObject forecastData, Location location, Instant instant) {
        double temperature = forecastData.getAsJsonObject("main").get("temp").getAsDouble();
        int humidity = forecastData.getAsJsonObject("main").get("humidity").getAsInt();
        int clouds = forecastData.getAsJsonObject("clouds").get("all").getAsInt();
        double windSpeed = forecastData.getAsJsonObject("wind").get("speed").getAsDouble();
        double rainProbability = forecastData.get("pop").getAsDouble();

        return new Weather(temperature, humidity, clouds, windSpeed, rainProbability, location, instant);
    }
}
