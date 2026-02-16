package services;

import entities.QuizResult;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuizResult implements IService<QuizResult> {

    private Connection connection;

    public ServiceQuizResult() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(QuizResult r) throws SQLException {
        String sql = "INSERT INTO quiz_result(scoretotal, datepassage, interpretation, id_user, idquiz) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getScoreTotal());
        ps.setDate(2, Date.valueOf(r.getDatePassage()));
        ps.setString(3, r.getInterpretation());
        ps.setInt(4, r.getIdUser());  // correspond à id_user
        ps.setInt(5, r.getIdQuiz());
        ps.executeUpdate();
    }

    @Override
    public void modifier(QuizResult r) throws SQLException {
        String sql = "UPDATE quiz_result SET scoretotal=?, interpretation=? WHERE idresult=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getScoreTotal());
        ps.setString(2, r.getInterpretation());
        ps.setInt(3, r.getIdResult());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(QuizResult r) throws SQLException {
        String sql = "DELETE FROM quiz_result WHERE idresult=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getIdResult());
        ps.executeUpdate();
    }

    @Override
    public List<QuizResult> recuperer() throws SQLException {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_result";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            QuizResult r = new QuizResult(
                    rs.getInt("idresult"),
                    rs.getInt("scoretotal"),
                    rs.getDate("datepassage").toLocalDate(),
                    rs.getString("interpretation"),
                    rs.getInt("id_user"),   // id_user
                    rs.getInt("idquiz")
            );
            list.add(r);
        }
        return list;
    }

    // Récupérer les résultats d’un utilisateur (patient)
    public List<QuizResult> recupererParUtilisateur(int idUser) throws SQLException {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_result WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idUser);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            QuizResult r = new QuizResult(
                    rs.getInt("idresult"),
                    rs.getInt("scoretotal"),
                    rs.getDate("datepassage").toLocalDate(),
                    rs.getString("interpretation"),
                    rs.getInt("id_user"),
                    rs.getInt("idquiz")
            );
            list.add(r);
        }
        return list;
    }
}
