package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API_KEY no configurada. Asegúrate de haberla definido como una variable de entorno.");
            return;
        }

        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?", apiKey);
        WeatherStore store = new SQLiteWeatherStore("jdbc:sqlite:src/main/java/jdbc/weather.db");
        WeatherController weatherControl = new WeatherController(5, supplier, store);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MiTarea(weatherControl), 0, 6 * 60 * 60 * 1000); // Cada 6 horas
        Runtime.getRuntime().addShutdownHook(new Thread(timer::cancel));
    }

    static class MiTarea extends TimerTask {
        private final WeatherController weatherControl;
        private final Logger logger = Logger.getLogger(MiTarea.class.getName());

        public MiTarea(WeatherController weatherControl) {
            this.weatherControl = weatherControl;
        }

        public void run() {
            try {
                logger.info("Iniciando tarea...");
                weatherControl.execute();
                logger.info("Tarea completada.");
            } catch (IOException e) {
                logger.severe("Error durante la ejecución de la tarea: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
