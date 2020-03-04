package dao;

import entity.UserEmail;
import util.ConnectionFactory;

import java.sql.*;
import java.util.Optional;

public class UserEmailDao {

    private final ConnectionFactory connectionFactory;

    public UserEmailDao() {
        connectionFactory = new ConnectionFactory();
    }

    public Optional<UserEmail> get(long chat_id) {
        try (Connection connection = connectionFactory.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_email WHERE chat_id=" + chat_id);
            UserEmail userEmail = null;
            if(rs.next()) {
                userEmail = new UserEmail();
                userEmail.setChatId(rs.getLong("chat_id"));
                userEmail.setEmail(rs.getString("email"));
            }
            return Optional.ofNullable(userEmail);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean insert(UserEmail userEmail) {
        try (Connection connection = connectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO user_email VALUES (?, ?)");
            ps.setLong(1, userEmail.getChatId());
            ps.setString(2, userEmail.getEmail());
            int i = ps.executeUpdate();
            if(i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(UserEmail userEmail) {
        try(Connection connection = connectionFactory.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE user_email" +
                    " SET email=? WHERE chat_id=?");
            ps.setString(1, userEmail.getEmail());
            ps.setLong(2, userEmail.getChatId());
            int i = ps.executeUpdate();
            if(i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
