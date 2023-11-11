package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import org.ulpgc.dacd.model.WeatherCache;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String templateUrl;
    private final String apiKey;
    private final WeatherCache weatherCache = new WeatherCache();

    public OpenWeatherMapSupplier(String templateUrl, String apiKey) {
        this.templateUrl = templateUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Weather> getWeather(Location location, List<Instant> instants) throws IOException {
        List<Weather> weathers = new ArrayList<>();

        for (Instant instant : instants) {
            Weather cachedWeather = weatherCache.getWeatherFromCache(location, instant);
            if (cachedWeather != null) {
                weathers.add(cachedWeather);
            } else {
                String url = buildUrl(location);
                String jsonData = getWeatherFromUrl(url);
                Weather weather = parseJsonData(jsonData, location, instant);
                if (weather != null) {
                    weathers.add(weather);
                    weatherCache.cacheWeather(location, instant, weather);
                }
            }
        }
        return weathers;
    }

    private String buildUrl(Location location) {
        String coordinates = "lat=" + location.getLat() + "&lon=" + location.getLon();
        return templateUrl + coordinates + "&appid=" + apiKey + "&units=metric";
    }

    private String getWeatherFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).get();
        return document.text();
    }

    private Weather parseJsonData(String jsonData, Location location, Instant instant) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray list = jsonObject.getAsJsonArray("list");
        if (list != null) {
            long targetTimestamp = instant.getEpochSecond();
            for (int i = 0; i < list.size(); i++) {
                JsonObject forecastItem = list.get(i).getAsJsonObject();
                long forecastTimestamp = forecastItem.get("dt").getAsLong();
                if (forecastTimestamp == targetTimestamp) {
                    return createWeatherFromForecastData(forecastItem, location, instant);
                } else if (forecastTimestamp > targetTimestamp) {
                    break;
                }
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
}
