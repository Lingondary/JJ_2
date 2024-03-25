package ru.gb.lesson3;

import java.sql.*;

public class Homework {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            acceptConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void acceptConnection(Connection connection) throws SQLException {
        createStudentTable(connection);
        insertStudentData(connection);
        deleteRandomStudent(connection);

        updateStudent(connection, "Igor", "Igor");

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select id, first_name, second_name, age from student");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String secondName = resultSet.getString("second_name");
                int age = resultSet.getInt("age");

                System.out.println("id = " + id + ", firstName = " + firstName + ", secondName = " + secondName + ", age = " + age);
            }
        }
    }

    private static void insertStudentData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate("""
        insert into student(id, first_name, second_name, age) values
        (1, 'Igor', 'Igor', 25), 
        (2, 'Person #2', 'Second #2', 30), 
        (3, 'John', 'Doe', 28), 
        (4, 'Alex', 'Smith', 22), 
        (5, 'Peter', 'Parker', 35) 
        """);

            System.out.println("INSERT: affected rows: " + affectedRows);
        }
    }

    private static void updateStudent(Connection connection, String firstName, String secondName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("update student set second_name = ? where first_name = ?")) {
            stmt.setString(1, secondName);
            stmt.setString(2, firstName);

            stmt.executeUpdate();
        }
    }

    private static void deleteRandomStudent(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            System.out.println("DELETED: " + statement.executeUpdate("delete from student where id = 2"));
        }
    }

    private static void createStudentTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
      create table student (
        id int,
        first_name varchar(256),
        second_name varchar(256),
        age int
      )
      """);
        }
    }
}
