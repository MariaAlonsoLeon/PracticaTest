package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherTask extends TimerTask {
    private final WeatherController weatherControl;
    private static final Logger logger = Logger.getLogger(WeatherTask.class.getName());

    public WeatherTask(WeatherController weatherControl) {
        this.weatherControl = weatherControl;
    }

    public void run() {
        try {
            logger.info("Iniciando tarea de obtención del clima...");
            weatherControl.execute();
            logger.info("Tarea de obtención del clima completada.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error durante la ejecución de la tarea de obtención del clima", e);
            throw new RuntimeException(e);
        }
    }
}
