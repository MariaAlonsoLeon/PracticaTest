package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Logger;

public class WeatherTask extends TimerTask {
    private final WeatherController weatherControl;
    private static final Logger logger = Logger.getLogger(WeatherTask.class.getName());

    public WeatherTask(WeatherController weatherControl) {
        this.weatherControl = weatherControl;
    }

    public void run() {
        logger.info("Starting weather data retrieval task...");
        try {
            weatherControl.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Weather data retrieval task completed.");
    }
}
