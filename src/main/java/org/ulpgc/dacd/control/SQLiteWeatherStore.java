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
            return; // No hay datos para guardar
        }
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            Location location = weatherList.get(0).getLocation();
            String tableName = location.getIsland().replace(" ", "_");
            createTableIfNotExists(connection, tableName);
            Instant lastSavedDate = getLastSavedDate(connection, tableName);

            for (Weather weather : weatherList) {
                Instant currentDate = weather.getTs();
                if (currentDate.isAfter(lastSavedDate)) {
                    insertOrUpdateRecord(connection, tableName, weather);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
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
                    double temperature = resultSet.getDouble("temperature");
                    double rainfall = resultSet.getDouble("rain");
                    int humidity = resultSet.getInt("humidity");
                    int clouds = resultSet.getInt("cloud");
                    double windSpeed = resultSet.getDouble("wind_speed");

                    Weather weather = new Weather(temperature, humidity, clouds, windSpeed, rainfall, location, instant);
                    return Optional.of(weather);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return Optional.empty();
    }

    private void handleSQLException(SQLException e) {
        // Manejar la excepción de acuerdo a los requisitos de tu aplicación.
        e.printStackTrace();
    }

    private void createTableIfNotExists(Connection connection, String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "temperature REAL, " +
                "rain REAL, " +
                "humidity INTEGER, " +
                "cloud INTEGER, " +
                "wind_speed REAL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private Instant getLastSavedDate(Connection connection, String tableName) throws SQLException {
        String lastSavedDateSQL = "SELECT MAX(date) FROM " + tableName;
        try (PreparedStatement lastSavedDateStatement = connection.prepareStatement(lastSavedDateSQL);
             ResultSet resultSet = lastSavedDateStatement.executeQuery()) {
            if (resultSet.next()) {
                String maxDateStr = resultSet.getString(1);
                if (maxDateStr != null) {
                    return Instant.parse(maxDateStr);
                }
            }
        }
        return Instant.MIN;
    }

    private void insertOrUpdateRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        String formattedDate = weather.getTs().toString();
        String insertOrUpdateDataSQL = "REPLACE INTO " + tableName + " (date, temperature, rain, humidity, cloud, wind_speed) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrUpdateDataSQL)) {
            preparedStatement.setString(1, formattedDate);
            preparedStatement.setDouble(2, weather.getTemperature());
            preparedStatement.setDouble(3, weather.getRain());
            preparedStatement.setInt(4, weather.getHumidity());
            preparedStatement.setInt(5, weather.getClouds());
            preparedStatement.setDouble(6, weather.getWindSpeed());
            preparedStatement.executeUpdate();
        }
    }
}
