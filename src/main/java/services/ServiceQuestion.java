package services;

import entities.Question;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuestion implements IService<Question> {

    private Connection connection;

    public ServiceQuestion() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Question q) throws SQLException {
        String sql = "INSERT INTO question(contenu, score, idquiz) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, q.getContenu());
        ps.setInt(2, q.getScore());
        ps.setInt(3, q.getIdQuiz());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Question q) throws SQLException {
        String sql = "UPDATE question SET contenu=?, score=?, idquiz=? WHERE idquestion=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, q.getContenu());
        ps.setInt(2, q.getScore());
        ps.setInt(3, q.getIdQuiz());
        ps.setInt(4, q.getIdQuestion());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Question q) throws SQLException {
        String sql = "DELETE FROM question WHERE idquestion=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, q.getIdQuestion());
        ps.executeUpdate();
    }

    @Override
    public List<Question> recuperer() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Question q = new Question(
                    rs.getInt("idquestion"),
                    rs.getString("contenu"),
                    rs.getInt("score"),
                    rs.getInt("idquiz")
            );
            list.add(q);
        }
        return list;
    }

    // 🔍 Bonus utile : récupérer les questions d’un quiz
    public List<Question> recupererParQuiz(int idQuiz) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE idquiz=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idQuiz);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Question q = new Question(
                    rs.getInt("idquestion"),
                    rs.getString("contenu"),
                    rs.getInt("score"),
                    rs.getInt("idquiz")
            );
            list.add(q);
        }
        return list;
    }
}
