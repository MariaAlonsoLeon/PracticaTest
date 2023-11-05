package org.ulpgc.dacd.control;
import org.ulpgc.dacd.model.Location;
import java.util.*;


public class Main {
    // Cargar las locaciones
    static List<Location> locations = loadLocations();
    static WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/weather?", "bf24a2b08d87ba34d7ea55a06937231b");
    static WeatherStore store = new SQLiteWeatherStore("jdbc:sqlite:src/main/resources/weather.db");
    static WeatherController weatherControl = new WeatherController(locations, 5, supplier, store);

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MiTarea(), 0, 6 * 60 * 60 * 1000); // Cada 6 horas
    }

    static class MiTarea extends TimerTask {
        public void run() {
            weatherControl.execute(); // Llama a tu función execute() aquí
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
}

/*
public class Main {
    // Cargar las locaciones
    static List<Location> locations = loadLocations();
    public static void main(String[] args) {
        // Crear controladores para cada ubicación
        List<WeatherController> controllers = createControllers(locations);

        // Crear tarea periódica que se ejecuta cada 6 horas
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new WeatherDataUpdater(controllers), 0, 6 * 60 * 60 * 1000);
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

    private static List<WeatherController> createControllers(List<Location> locations) {
        List<WeatherController> controllers = new ArrayList<>();
        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?lat=#&lon=#", "bf24a2b08d87ba34d7ea55a06937231b");
        WeatherStore store = new SQLiteWeatherStore("DB_PATH");
        for (Location location : locations) {
            WeatherController controller = new WeatherController(locations, 7, supplier, store);
            controllers.add(controller);
        }
        return controllers;
    }

    private static class WeatherDataUpdater extends TimerTask {
        private final List<WeatherController> controllers;

        public WeatherDataUpdater(List<WeatherController> controllers) {
            this.controllers = controllers;
        }

        @Override
        public void run() {
            // Obtener datos meteorológicos y guardar en la base de datos para cada ubicación
            Instant timestamp = Instant.now(); // Puedes configurar la hora deseada aquí
            for (WeatherController controller : controllers) {
                controller.updateWeatherData(timestamp);
            }
        }
    }

    static class MiTarea extends TimerTask {
        public void run() {
            WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?lat=#&lon=#", "bf24a2b08d87ba34d7ea55a06937231b");
            WeatherStore store = new SQLiteWeatherStore("DB_PATH");
            WeatherController weatherController = new WeatherController(locations, 5, supplier, store);
            weatherController.execute(); // Llama a tu función execute() aquí
        }
    }
}*/
