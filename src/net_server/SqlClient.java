package net_server;

import java.sql.*;

public class SqlClient {

    private static Connection connection;
    private static Statement statement;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat-server/chat.db");
            System.out.println("База Подключена!");
            CreateDB ();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

   synchronized static void CreateDB () throws ClassNotFoundException, SQLException
    {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text, 'password' text,'nickname' text);");
        System.out.println("Таблица создана или уже существует.");
    }

    synchronized static void Registration (String login,String password,String nickname) throws SQLException
    {
        String value=String.format("('%s','%s','%s')",login, password,nickname);
        statement.execute("INSERT INTO users (login, password,nickname) VALUES "+value+";");
        System.out.println("Registration ok");
    }

    synchronized static void ChangeRegistrationInformation (String login,String nickname) throws SQLException
    {
        String query = String.format("id,login='%s'", login);
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()){
           int id=resultSet.getInt(1);
            statement.executeUpdate(
                    "UPDATE users SET nickname = "+nickname+ " where id = "+id);
            System.out.println("Id= "+ String.valueOf(id));
            resultSet.close();
        }

    }

    synchronized static void disconnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static String getNickname(String login, String password) {
        String response=null;
        String query = String.format("select nickname from users where login='%s' and password='%s'", login, password);
        try (ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()){
                response=resultSet.getString(1);
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

}
