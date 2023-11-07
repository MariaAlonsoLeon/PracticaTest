package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Leer la API key desde el archivo
        String apiKey = readApiKeyFromFile("\\Users\\Maria\\Desktop\\apiKey.txt");

        // Cargar las locaciones
        List<Location> locations = loadLocations();

        // Crear instancias de los objetos con la API key
        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?", apiKey);
        WeatherStore store = new SQLiteWeatherStore("jdbc:sqlite:src/main/resources/weather.db");
        WeatherController weatherControl = new WeatherController(locations, 5, supplier, store);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MiTarea(weatherControl), 0, 6 * 60 * 60 * 1000); // Cada 6 horas
    }

    static class MiTarea extends TimerTask {
        private final WeatherController weatherControl;

        public MiTarea(WeatherController weatherControl) {
            this.weatherControl = weatherControl;
        }

        public void run() {
            try {
                weatherControl.execute(); // Llama a tu función execute() aquí
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<Location> loadLocations() {
        // Cargar las ubicaciones desde algún lugar (puede ser una lista estática)
        Location[] locations = {
                new Location("Gran Canaria", 28.11, -15.43),
                new Location("Tenerife", 28.46, -16.25),
                new Location("La Gomera", 28.09, -17.1),
                new Location("La Palma", 28.68, -17.76),
                new Location("El Hierro", 27.64, -17.98),
                new Location("Fuerteventura", 28.49, -13.86),
                new Location("Lanzarote", 28.96, -13.55),
                new Location("La Graciosa", 29.23, -13.5)
        };
        // Agregar las ubicaciones a la lista
        return Arrays.asList(locations);
    }

    private static String readApiKeyFromFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath)).split(": ")[1];
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // Retorna una cadena vacía en caso de error
        }
    }
}
