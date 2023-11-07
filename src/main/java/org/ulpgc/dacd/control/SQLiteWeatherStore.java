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
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            if (weatherList.isEmpty()) {
                return; // No hay datos para guardar
            }

            Location location = weatherList.get(0).getLocation();
            String tableName = location.getIsland().replace(" ", "_");

            // Crear la tabla si no existe
            createTableIfNotExists(connection, tableName);

            // Obtener la última fecha registrada en la base de datos
            Instant lastSavedDate = getLastSavedDate(connection, tableName);

            for (Weather weather : weatherList) {
                Instant currentDate = weather.getTs();
                if (currentDate.isAfter(lastSavedDate)) {
                    // Insertar nuevo registro si la fecha es posterior a la última registrada
                    insertNewRecord(connection, tableName, weather);
                } else {
                    // Actualizar registro existente si la fecha coincide
                    updateExistingRecord(connection, tableName, weather);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Instant getLastSavedDate(Connection connection, String tableName) throws SQLException {
        String lastSavedDateSQL = "SELECT MAX(date) FROM " + tableName;
        try (PreparedStatement lastSavedDateStatement = connection.prepareStatement(lastSavedDateSQL);
             ResultSet resultSet = lastSavedDateStatement.executeQuery()) {
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp(1);
                if (timestamp != null) {
                    return timestamp.toInstant();
                }
            }
        }
        // Retorna una fecha mínima si no se encuentra ninguna fecha registrada
        return Instant.MIN;
    }

    private void insertNewRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        String insertDataSQL = "INSERT INTO " + tableName + " (date, temperature, rain, humidity, cloud, wind_speed) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataSQL)) {
            preparedStatement.setObject(1, weather.getTs());
            preparedStatement.setDouble(2, weather.getTemperature());
            preparedStatement.setDouble(3, weather.getRain());
            preparedStatement.setInt(4, weather.getHumidity());
            preparedStatement.setInt(5, weather.getClouds());
            preparedStatement.setDouble(6, weather.getWindSpeed());
            preparedStatement.executeUpdate();
        }
    }

    private void updateExistingRecord(Connection connection, String tableName, Weather weather) throws SQLException {
        String updateDataSQL = "UPDATE " + tableName + " " +
                "SET temperature = ?, rain = ?, humidity = ?, cloud = ?, wind_speed = ? " +
                "WHERE date = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateDataSQL)) {
            preparedStatement.setDouble(1, weather.getTemperature());
            preparedStatement.setDouble(2, weather.getRain());
            preparedStatement.setInt(3, weather.getHumidity());
            preparedStatement.setInt(4, weather.getClouds());
            preparedStatement.setDouble(5, weather.getWindSpeed());
            preparedStatement.setObject(6, weather.getTs());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Optional<Weather> loadWeather(Location location, Instant instant) {
        try (Connection connection = DriverManager.getConnection(databaseURL)) {
            String tableName = location.getIsland().replace(" ", "_");

            // Crear la tabla si no existe
            createTableIfNotExists(connection, tableName);

            String selectDataSQL = "SELECT * FROM " + tableName + " WHERE date = ?"; // Selecciono todos los campos de la base de datos

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDataSQL)) {
                preparedStatement.setObject(1, instant); // Cambia la columna fecha por el instante actual
                ResultSet resultSet = preparedStatement.executeQuery(); // Datos de la base de datos

                if (resultSet.next()) {
                    double temperature = resultSet.getDouble("temperature");
                    double rainfall = resultSet.getDouble("rain");
                    int humidity = resultSet.getInt("humidity");
                    int clouds = resultSet.getInt("clouds");
                    double windSpeed = resultSet.getDouble("wind_speed");

                    Weather weather = new Weather(temperature, humidity, clouds, windSpeed, rainfall, location, instant);
                    return Optional.of(weather);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void createTableIfNotExists(Connection connection, String tableName) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date DATETIME, " +
                "temperature REAL, " +
                "rain REAL, " +
                "humidity INTEGER, " +
                "cloud INTEGER, " +
                "wind_speed REAL)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.execute();
        }
    }
}
