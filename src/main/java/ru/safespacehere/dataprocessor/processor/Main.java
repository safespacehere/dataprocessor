package ru.safespacehere.dataprocessor.processor;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.safespacehere.dataprocessor.exceptions.NotExpectedTokenException;
import ru.safespacehere.dataprocessor.exceptions.WrongDataException;
import ru.safespacehere.dataprocessor.requests.Request;
import ru.safespacehere.dataprocessor.resources.Const;
import ru.safespacehere.dataprocessor.resources.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2){
            throw new RuntimeException("Неправильное число аргументов.");
        }
        File jsonFileInput = new File(args[0]).getAbsoluteFile();
        File jsonFileOutput = new File(args[1]).getAbsoluteFile();
        try {
            ResultSet resultSet;

            JsonParser jsonParser = new JsonFactory().createParser(jsonFileInput);
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonFileOutput, JsonEncoding.UTF8);

            String jdbcUrl = Resource.jdbcUrl;
            String username = Resource.username;
            String password = Resource.password;

            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            jsonGenerator.writeStartObject();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jsonParser.getCurrentName();
                if (Const.criterias.equals(fieldname)) {
                    jsonGenerator.writeStringField(Const.type, Const.search);
                    jsonGenerator.writeFieldName(Const.results);
                    jsonGenerator.writeStartArray();

                    jsonParser.nextToken();
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        String text = jsonParser.getText();
                        switch (text) {
                            case Const.lastName:
                                jsonParser.nextToken();

                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeFieldName(Const.criteria);
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField(Const.lastName, jsonParser.getText());
                                jsonGenerator.writeEndObject();

                                jsonGenerator.writeFieldName(Const.results);
                                jsonGenerator.writeStartArray();

                                resultSet = Request.searchBySurname(connection, jsonParser.getText());
                                while (resultSet.next()) {
                                    jsonGenerator.writeStartObject();
                                    jsonGenerator.writeStringField(Const.lastName, resultSet.getString(1));
                                    jsonGenerator.writeStringField(Const.firstName, resultSet.getString(2));
                                    jsonGenerator.writeEndObject();
                                }
                                jsonGenerator.writeEndArray();
                                jsonGenerator.writeEndObject();
                                break;
                            case Const.productName:
                                jsonParser.nextToken();
                                String name = jsonParser.getText();
                                jsonParser.nextToken();
                                if (!jsonParser.getText().equals(Const.minTimes)) {
                                    throw new NotExpectedTokenException("Передан неверный JSON-токен. " +
                                            "Ожидаемый токен: " + Const.minTimes + "Полученный токен: " + jsonParser.getText());
                                }
                                jsonParser.nextToken();
                                Integer times = jsonParser.getIntValue();

                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeFieldName(Const.criteria);
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField(Const.productName, name);
                                jsonGenerator.writeNumberField(Const.minTimes, times);
                                jsonGenerator.writeEndObject();

                                jsonGenerator.writeFieldName(Const.results);
                                jsonGenerator.writeStartArray();

                                resultSet = Request.searchByNameAndTimes(connection, name, times);
                                while (resultSet.next()) {
                                    jsonGenerator.writeStartObject();
                                    jsonGenerator.writeStringField(Const.lastName, resultSet.getString(1));
                                    jsonGenerator.writeStringField(Const.firstName, resultSet.getString(2));
                                    jsonGenerator.writeEndObject();
                                }
                                jsonGenerator.writeEndArray();
                                jsonGenerator.writeEndObject();
                                break;
                            case Const.minExpenses:
                                jsonParser.nextToken();
                                BigDecimal minCost = new BigDecimal(jsonParser.getText());
                                jsonParser.nextToken();
                                if (!jsonParser.getText().equals(Const.maxExpenses)) {
                                    throw new NotExpectedTokenException("Передан неверный JSON-токен. " +
                                            "Ожидаемый токен: " + Const.maxExpenses + "Полученный токен: " + jsonParser.getText());
                                }
                                jsonParser.nextToken();
                                BigDecimal maxCost = new BigDecimal(jsonParser.getText());

                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeFieldName(Const.criteria);
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeNumberField(Const.minExpenses, minCost);
                                jsonGenerator.writeNumberField(Const.maxExpenses, maxCost);
                                jsonGenerator.writeEndObject();

                                jsonGenerator.writeFieldName(Const.results);
                                jsonGenerator.writeStartArray();

                                if (minCost.compareTo(maxCost) == 1) {
                                    throw new WrongDataException("Переданная максимальная стоимость покупок меньше минимальной.");
                                }
                                resultSet = Request.searchByMinMaxCost(connection, minCost, maxCost);
                                while (resultSet.next()) {
                                    jsonGenerator.writeStartObject();
                                    jsonGenerator.writeStringField(Const.lastName, resultSet.getString(1));
                                    jsonGenerator.writeStringField(Const.firstName, resultSet.getString(2));
                                    jsonGenerator.writeEndObject();
                                }
                                jsonGenerator.writeEndArray();
                                jsonGenerator.writeEndObject();
                                break;
                            case Const.badCustomers:
                                jsonParser.nextToken();
                                Integer badCustomersNumber = jsonParser.getIntValue();

                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeFieldName(Const.criteria);
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeNumberField(Const.badCustomers, badCustomersNumber);
                                jsonGenerator.writeEndObject();

                                jsonGenerator.writeFieldName(Const.results);
                                jsonGenerator.writeStartArray();

                                resultSet = Request.searchByBadCustomers(connection, badCustomersNumber);
                                while (resultSet.next()) {
                                    jsonGenerator.writeStartObject();
                                    jsonGenerator.writeStringField(Const.lastName, resultSet.getString(1));
                                    jsonGenerator.writeStringField(Const.firstName, resultSet.getString(2));
                                    jsonGenerator.writeEndObject();
                                }
                                jsonGenerator.writeEndArray();
                                jsonGenerator.writeEndObject();
                                break;
                            case "[":
                                break;
                            case "{":
                                break;
                            case "]":
                                break;
                            case "}":
                                break;
                            default:
                                throw new NotExpectedTokenException("Передан неверный JSON-токен.");
                        }
                    }
                    jsonGenerator.writeEndArray();
                }
                if (Const.startDate.equals(fieldname)) {
                    jsonParser.nextToken();
                    LocalDate start = LocalDate.parse(jsonParser.getText());
                    jsonParser.nextToken();
                    if (!Const.endDate.equals(jsonParser.getText())) {
                        throw new NotExpectedTokenException("Передан неверный JSON-токен. " +
                                "Ожидаемый токен: " + Const.endDate + "Полученный токен: " + jsonParser.getText());
                    }
                    jsonParser.nextToken();
                    LocalDate end = LocalDate.parse(jsonParser.getText());
                    if (start.isAfter(end)) {
                        throw new WrongDataException("Переданная дата начала сбора статистика позже даты конца.");
                    }

                    if (start.isAfter(LocalDate.now()) || end.isAfter(LocalDate.now())) {
                        throw new WrongDataException("Переданная дата начала или конца сбора статистика позже текущей даты.");
                    }

                    jsonGenerator.writeStringField(Const.type, Const.stat);
                    jsonGenerator.writeNumberField(Const.totalDays, totalDaysWithoutWeekends(start, end));
                    jsonGenerator.writeFieldName(Const.customers);
                    jsonGenerator.writeStartArray();

                    resultSet = Request.statForPeriod(connection, start, end);
                    Long id = -1L;
                    BigDecimal totalForAll = new BigDecimal(0);
                    Long counter = 0L;
                    if (resultSet.isBeforeFirst()) {
                        resultSet.next();
                        while (!resultSet.isAfterLast()) {
                            id = resultSet.getLong(1);
                            jsonGenerator.writeStartObject();
                            jsonGenerator.writeStringField(Const.name, resultSet.getString(2) + " " + resultSet.getString(3));
                            jsonGenerator.writeFieldName(Const.purchases);
                            jsonGenerator.writeStartArray();
                            BigDecimal total = new BigDecimal(0);
                            do {
                                total = total.add(resultSet.getBigDecimal(5));
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField(Const.name, resultSet.getString(4));
                                jsonGenerator.writeNumberField(Const.expenses, resultSet.getBigDecimal(5));
                                jsonGenerator.writeEndObject();
                            }
                            while (resultSet.next() && id == resultSet.getLong(1));
                            totalForAll = totalForAll.add(total);
                            counter += 1L;
                            jsonGenerator.writeEndArray();
                            jsonGenerator.writeNumberField(Const.totalExpenses, total);
                            jsonGenerator.writeEndObject();
                        }
                    }
                    if (counter == 0L) {
                        counter = 1L;
                    }
                    jsonGenerator.writeEndArray();
                    jsonGenerator.writeNumberField(Const.totalExpenses, totalForAll);
                    jsonGenerator.writeNumberField(Const.avgExpenses, totalForAll.divide(BigDecimal.valueOf(counter), 2, RoundingMode.HALF_UP));
                }
            }
            jsonGenerator.writeEndObject();

            jsonGenerator.close();
            jsonParser.close();
            connection.close();

            prettyPrintIntoFile(jsonFileOutput);
        }
        catch (DateTimeParseException parseException) {
            exceptionOutput(jsonFileOutput, "Неправильный формат даты.");
        }
        catch (IOException ioException) {
            exceptionOutput(jsonFileOutput, "Ошибка ввода-вывода.");
        }
        catch (Exception ex) {
            exceptionOutput(jsonFileOutput, ex.getMessage());
        }
    }

    public static void exceptionOutput(File jsonFileOutput, String exceptionMessage){
        try {
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonFileOutput, JsonEncoding.UTF8);
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(Const.type, Const.error);
            jsonGenerator.writeStringField(Const.message, exceptionMessage);
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
            prettyPrintIntoFile(jsonFileOutput);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            System.out.println("Exception при попытке вывести в файл информацию об ошибках.");
        }
    }
    public static void prettyPrintIntoFile(File file) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObject = objectMapper.readTree(file);
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, jsonObject);
    }

    public static int totalDaysWithoutWeekends(LocalDate start, LocalDate end){
        int counter = 0;
        LocalDate tempStart = start;
        while(!tempStart.isEqual(end)){
            if(tempStart.getDayOfWeek() != DayOfWeek.SATURDAY && tempStart.getDayOfWeek() != DayOfWeek.SUNDAY) {
                counter += 1;
            }
            tempStart = tempStart.plusDays(1);
        }
        return counter;
    }
}