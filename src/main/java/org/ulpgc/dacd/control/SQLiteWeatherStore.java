package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class SQLiteWeatherStore implements WeatherStore {
    private final String databaseURL;

    public SQLiteWeatherStore(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    @Override
    public void save(List<Weather> weatherList) {
        if (weatherList.isEmpty()) {
            return;
        }
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            Location location = weatherList.get(0).getLocation();
            String tableName = location.getIsland().replace(" ", "_");
            createTableIfNotExists(connection, tableName);
            processWeatherList(connection, tableName, weatherList);
        } catch (SQLException e) {
            handleSQLException("Error al guardar datos meteorológicos.", e);
        }
    }

    @Override
    public Optional<Weather> loadWeather(Location location, Instant instant) {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            String tableName = location.getIsland().replace(" ", "_");
            createTableIfNotExists(connection, tableName);
            String selectDataSQL = "SELECT * FROM " + tableName + " WHERE date = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL)) {
                preparedStatement.setObject(1, instant);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(mapResultSetToWeather(resultSet, location));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error al cargar datos meteorológicos.", e);
        }
        return Optional.empty();
    }

    private void processWeatherList(Connection connection, String tableName, List<Weather> weatherList) {
        weatherList.forEach(weather -> {
            try {
                upsertRecord(connection, tableName, weather);
            } catch (SQLException e) {
                handleSQLException("Error al procesar datos meteorológicos.", e);
            }
        });
    }

    private void upsertRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        String formattedDate = weather.getTs().toString();
        String upsertDataSQL = "INSERT OR REPLACE INTO " + tableName +
                " (date, temperature, rain, humidity, cloud, wind_speed, update_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(upsertDataSQL)) {
            preparedStatement.setString(1, formattedDate);
            preparedStatement.setDouble(2, weather.getTemperature());
            preparedStatement.setDouble(3, weather.getRain());
            preparedStatement.setInt(4, weather.getHumidity());
            preparedStatement.setInt(5, weather.getClouds());
            preparedStatement.setDouble(6, weather.getWindSpeed());
            preparedStatement.setObject(7, Instant.now());
            preparedStatement.executeUpdate();
        }
    }

    private Weather mapResultSetToWeather(ResultSet resultSet, Location location) throws SQLException {
        double temperature = resultSet.getDouble("temperature");
        double rainfall = resultSet.getDouble("rain");
        int humidity = resultSet.getInt("humidity");
        int clouds = resultSet.getInt("cloud");
        double windSpeed = resultSet.getDouble("wind_speed");
        Instant instant = Instant.parse(resultSet.getString("date"));

        return new Weather(temperature, humidity, clouds, windSpeed, rainfall, location, instant);
    }

    private void createTableIfNotExists(Connection connection, String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (date TEXT PRIMARY KEY, temperature REAL, rain REAL, humidity INTEGER, cloud INTEGER, wind_speed REAL, update_date TEXT)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + " " + e.getMessage());
    }
}
