package requests;

import java.math.BigDecimal;
import java.sql.*;

public class request {
    public static ResultSet searchBySurname(Connection conn, String surname) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT CustomerSurname, CustomerName " +
                        "FROM Customers WHERE CustomerSurname = ?");
        preparedStatement.setString(1, surname);
        return preparedStatement.executeQuery();
    }

    public static ResultSet searchByNameAndTimes(Connection conn, String productName, Integer numberOfTimes) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT CustomerSurname, CustomerName " +
                        "FROM Customers " +
                        "JOIN Purchases on Purchases.CustomerId = Customers.CustomerId " +
                        "JOIN Products on Products.ProductId = Purchases.ProductId " +
                        "WHERE ProductName = ? " +
                        "GROUP BY Customers.CustomerId " +
                        "HAVING COUNT(Customers.CustomerId) >= ?");
        preparedStatement.setString(1, productName);
        preparedStatement.setInt(2, numberOfTimes);
        return preparedStatement.executeQuery();
    }

    public static ResultSet searchByMinMaxCost(Connection conn, BigDecimal minCost, BigDecimal maxCost) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(
                "Select CustomerSurname, CustomerName " +
                        "FROM Customers " +
                        "JOIN Purchases on Purchases.CustomerId = Customers.CustomerId " +
                        "JOIN Products on Products.ProductId = Purchases.ProductId " +
                        "GROUP BY Customers.CustomerId " +
                        "HAVING SUM(Products.Price) BETWEEN ? and ?");
        preparedStatement.setBigDecimal(1, minCost);
        preparedStatement.setBigDecimal(2, maxCost);
        return preparedStatement.executeQuery();
    }

    public static ResultSet searchByBadCustomers(Connection conn, Integer badCustomers) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(
                "Select CustomerSurname, CustomerName " +
                        "FROM Customers " +
                        "JOIN Purchases on Purchases.CustomerId = Customers.CustomerId " +
                        "JOIN Products on Products.ProductId = Purchases.ProductId " +
                        "GROUP BY Customers.CustomerId " +
                        "HAVING COUNT(Customers.CustomerId) < ?");
        preparedStatement.setInt(1, badCustomers);
        return preparedStatement.executeQuery();
    }

    public static ResultSet statForPeriod(Connection conn, Date startDate, Date endDate) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(
                "Select Customers.CustomerId, CustomerSurname, CustomerName, productname, SUM(Price)\n" +
                        "FROM Customers " +
                        "JOIN Purchases on Purchases.CustomerId = Customers.CustomerId " +
                        "JOIN Products on Products.ProductId = Purchases.ProductId " +
                        "WHERE purchasedate BETWEEN ? and ? and EXTRACT(DOW FROM purchasedate) BETWEEN 1 AND 5 " +
                        "GROUP BY productname, Customers.CustomerId " +
                        "Order by Customers.CustomerId");
        preparedStatement.setDate(1, startDate);
        preparedStatement.setDate(2, endDate);
        return preparedStatement.executeQuery();
    }
}
