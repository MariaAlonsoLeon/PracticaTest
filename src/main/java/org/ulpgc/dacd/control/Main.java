package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            logger.severe("API_KEY no configurada. Asegúrate de haberla definido como una variable de entorno.");
            return;
        }

        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?", apiKey);
        WeatherStore store = new SQLiteWeatherStore("jdbc:sqlite:src/main/java/jdbc/weather.db");
        WeatherController weatherControl = new WeatherController(5, supplier, store);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new WeatherTask(weatherControl), 0, 6 * 60 * 60 * 1000); // Cada 6 horas
        Runtime.getRuntime().addShutdownHook(new Thread(timer::cancel));
    }
}
