package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.io.IOException;
import java.time.Instant;

public class OpenWeatherMapSupplier implements WeatherSupplier {
    private final String templateUrl; // Hay que hacerla static
    private final String apiKey;

    public OpenWeatherMapSupplier(String templateUrl, String apiKey) {
        this.templateUrl = templateUrl;
        this.apiKey = apiKey;
    }

    @Override
    public Weather getWeather(Location location, Instant instant) {
        String url = buildUrl(location, instant);
        try {
            String jsonData = getWeatherDataFromUrl(url);
            return parseJsonData(jsonData, location, instant);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // O manejar el error de alguna otra manera
        }
    }

    private String buildUrl(Location location, Instant instant) {
        String coordinates = "lat=" + location.getLat() + "&lon=" + location.getLon();
        long unixTimestamp = instant.getEpochSecond();
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

        double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        int clouds = jsonObject.getAsJsonObject("clouds").get("all").getAsInt();
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        double rain = 0.0; // Valor predeterminado

        if (jsonObject.has("rain")) {
            JsonObject rainObject = jsonObject.getAsJsonObject("rain");
            if (rainObject.has("1h")) {
                rain = rainObject.getAsJsonPrimitive("1h").getAsDouble();
            }
        }

        return new Weather(temperature, humidity, clouds, windSpeed, rain, location, instant);
    }



}
