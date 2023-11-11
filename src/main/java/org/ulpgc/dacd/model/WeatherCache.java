package org.ulpgc.dacd.model;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherCache {
    private final List<WeatherCacheItem> cacheItems = new CopyOnWriteArrayList<>();
    private int cacheExpiryMinutes = 30;

    public Weather getWeatherFromCache(Location location, Instant instant) {
        WeatherCacheItem matchingItem = null;

        for (WeatherCacheItem item : cacheItems) {
            if (item.isMatch(location, instant) && item.isValid()) {
                matchingItem = item;
                break;
            }
        }

        if (matchingItem != null) {
            return matchingItem.weather;
        }

        return null;
    }

    public void cacheWeather(Location location, Instant instant, Weather weather) {
        WeatherCacheItem cacheItem = new WeatherCacheItem(location, instant, weather);
        cacheItems.add(cacheItem);
    }

    private class WeatherCacheItem {
        private final Location location;
        private final Instant instant;
        private final Weather weather;
        private final Instant expiryTime;

        public WeatherCacheItem(Location location, Instant instant, Weather weather) {
            this.location = location;
            this.instant = instant;
            this.weather = weather;
            this.expiryTime = Instant.now().plusSeconds(cacheExpiryMinutes * 60);
        }

        public boolean isMatch(Location location, Instant instant) {
            return this.location.equals(location) && this.instant.equals(instant);
        }

        public boolean isValid() {
            return Instant.now().isBefore(expiryTime);
        }
    }
}
