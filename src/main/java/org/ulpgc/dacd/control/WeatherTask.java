package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Logger;

public class WeatherTask extends TimerTask {
    private final WeatherController weatherController;
    private static final Logger logger = Logger.getLogger(WeatherTask.class.getName());

    public WeatherTask(WeatherController weatherController) {
        this.weatherController = weatherController;
    }

    @Override
    public void run() {
        logger.info("Starting weather data retrieval task...");
        try {
            weatherController.execute();
        } catch (IOException e) {
            logger.severe("Error during weather data retrieval task: " + e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Weather data retrieval task completed.");
    }
}
