package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteWeatherStore implements WeatherStore {
    private static final Logger logger = Logger.getLogger(SQLiteWeatherStore.class.getName());

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
            String tableName = buildTableName(location);
            createTableIfNotExists(connection, tableName);
            processWeatherList(connection, tableName, weatherList);
        } catch (SQLException e) {
            handleSQLException("Error saving weather data.", e);
        }
    }

    @Override
    public Optional<Weather> loadWeather(Location location, Instant instant) {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            String tableName = buildTableName(location);
            createTableIfNotExists(connection, tableName);
            String selectDataSQL = buildSelectDataSQL(tableName);
            return executeSelectStatement(connection, selectDataSQL, instant, location);
        } catch (SQLException e) {
            handleSQLException("Error loading weather data.", e);
        }
        return Optional.empty();
    }

    private String buildTableName(Location location) {
        return location.getIsland().replace(" ", "_");
    }

    private String buildSelectDataSQL(String tableName) {
        return "SELECT * FROM " + tableName + " WHERE date = ?";
    }

    private Optional<Weather> executeSelectStatement(Connection connection, String selectDataSQL, Instant instant, Location location) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL)) {
            preparedStatement.setObject(1, instant);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToWeather(resultSet, location));
            }
        }
        return Optional.empty();
    }

    private void processWeatherList(Connection connection, String tableName, List<Weather> weatherList) {
        weatherList.forEach(weather -> {
            try {
                upsertRecord(connection, tableName, weather);
            } catch (SQLException e) {
                handleSQLException("Error processing weather data.", e);
            }
        });
    }

    private void upsertRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        String formattedDate = weather.getTs().toString();
        String insertOrUpdateDataSQL = buildUpsertSQL(tableName, weather, formattedDate);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertOrUpdateDataSQL);
        } catch (SQLException e) {
            handleSQLException("Error executing SQL query.", e);
        }
    }

    private String buildUpsertSQL(String tableName, Weather weather, String formattedDate) {
        return String.format(
                "INSERT OR REPLACE INTO %s (date, temperature, rain, humidity, cloud, wind_speed, update_date) " +
                        "VALUES ('%s', %s, %s, %s, %s, %s, '%s')",
                tableName,
                formattedDate,
                weather.getTemperature(),
                weather.getRain(),
                weather.getHumidity(),
                weather.getClouds(),
                weather.getWindSpeed(),
                Instant.now()
        );
    }

    private Weather mapResultSetToWeather(ResultSet resultSet, Location location) throws SQLException {
        float temperature = resultSet.getFloat("temperature");
        float rainfall = resultSet.getFloat("rain");
        int humidity = resultSet.getInt("humidity");
        int clouds = resultSet.getInt("cloud");
        float windSpeed = resultSet.getFloat("wind_speed");
        Instant instant = Instant.parse(resultSet.getString("date"));

        return new Weather(temperature, humidity, clouds, windSpeed, rainfall, location, instant);
    }

    private void createTableIfNotExists(Connection connection, String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (date TEXT PRIMARY KEY, temperature REAL, rain REAL, humidity INTEGER, cloud INTEGER, wind_speed REAL, update_date TEXT)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            handleSQLException("Error creating table.", e);
        }
    }

    private void handleSQLException(String message, SQLException e) {
        logger.log(Level.SEVERE, message, e);
    }
}