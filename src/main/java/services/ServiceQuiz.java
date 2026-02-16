package services;

import entities.Quiz;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuiz implements IService<Quiz> {

    private Connection connection;

    public ServiceQuiz() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Quiz q) throws SQLException {
        String sql = "INSERT INTO quiz(titre, description, type, actif) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, q.getTitre());
        ps.setString(2, q.getDescription());
        ps.setString(3, q.getType());
        ps.setBoolean(4, q.isActif());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Quiz q) throws SQLException {
        String sql = "UPDATE quiz SET titre=?, description=?, type=?, actif=? WHERE idquiz=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, q.getTitre());
        ps.setString(2, q.getDescription());
        ps.setString(3, q.getType());
        ps.setBoolean(4, q.isActif());
        ps.setInt(5, q.getIdQuiz());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Quiz q) throws SQLException {
        String sql = "DELETE FROM quiz WHERE idquiz=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, q.getIdQuiz());
        ps.executeUpdate();
    }

    @Override
    public List<Quiz> recuperer() throws SQLException {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Quiz q = new Quiz(
                    rs.getInt("idquiz"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getBoolean("actif")
            );
            list.add(q);
        }
        return list;
    }
}
