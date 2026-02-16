package services;

import entities.ConnexionLog;
import utils.MyDatabase;
import java.sql.*;
import java.time.LocalDateTime;  // ✅ AJOUTER CET IMPORT
import java.util.ArrayList;
import java.util.List;

public class ServiceConnexionLog implements IService<ConnexionLog> {

    private Connection connection;

    public ServiceConnexionLog() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(ConnexionLog log) throws SQLException {
        String sql = "INSERT INTO connexion_logs (user_id, user_email, user_name, date_connexion, ip_address, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, log.getUserId());
        ps.setString(2, log.getUserEmail());
        ps.setString(3, log.getUserName());
        ps.setTimestamp(4, Timestamp.valueOf(log.getDate()));  // ✅ MAINTENANT FONCTIONNE
        ps.setString(5, log.getIpAddress());
        ps.setString(6, log.getStatus());
        ps.executeUpdate();
    }

    @Override
    public void modifier(ConnexionLog log) throws SQLException {
        throw new UnsupportedOperationException("Modification non supportée pour les logs");
    }

    @Override
    public void supprimer(ConnexionLog log) throws SQLException {
        String sql = "DELETE FROM connexion_logs WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, log.getId());
        ps.executeUpdate();
    }

    @Override
    public List<ConnexionLog> recuperer() throws SQLException {
        List<ConnexionLog> list = new ArrayList<>();
        String sql = "SELECT * FROM connexion_logs ORDER BY date_connexion DESC LIMIT 1000";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            ConnexionLog log = new ConnexionLog(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    rs.getTimestamp("date_connexion").toLocalDateTime(),  // ✅ MAINTENANT FONCTIONNE
                    rs.getString("ip_address"),
                    rs.getString("status")
            );
            list.add(log);
        }
        return list;
    }

    // Récupérer les logs d'un utilisateur spécifique
    public List<ConnexionLog> recupererParUtilisateur(int userId) throws SQLException {
        List<ConnexionLog> list = new ArrayList<>();
        String sql = "SELECT * FROM connexion_logs WHERE user_id=? ORDER BY date_connexion DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ConnexionLog log = new ConnexionLog(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    rs.getTimestamp("date_connexion").toLocalDateTime(),
                    rs.getString("ip_address"),
                    rs.getString("status")
            );
            list.add(log);
        }
        return list;
    }

    // Récupérer les logs par date
    public List<ConnexionLog> recupererParDate(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<ConnexionLog> list = new ArrayList<>();
        String sql = "SELECT * FROM connexion_logs WHERE date_connexion BETWEEN ? AND ? ORDER BY date_connexion DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(debut));
        ps.setTimestamp(2, Timestamp.valueOf(fin));
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ConnexionLog log = new ConnexionLog(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    rs.getTimestamp("date_connexion").toLocalDateTime(),
                    rs.getString("ip_address"),
                    rs.getString("status")
            );
            list.add(log);
        }
        return list;
    }

    // Récupérer les connexions suspectes
    public List<ConnexionLog> recupererConnexionsSuspectes() throws SQLException {
        List<ConnexionLog> list = new ArrayList<>();
        String sql = "SELECT * FROM connexion_logs WHERE status='Échec' OR status='Tentative échouée' ORDER BY date_connexion DESC LIMIT 500";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ConnexionLog log = new ConnexionLog(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_name"),
                    rs.getTimestamp("date_connexion").toLocalDateTime(),
                    rs.getString("ip_address"),
                    rs.getString("status")
            );
            list.add(log);
        }
        return list;
    }

    // Nettoyer les anciens logs
    public int nettoyerAnciensLogs(int jours) throws SQLException {
        String sql = "DELETE FROM connexion_logs WHERE date_connexion < DATE_SUB(NOW(), INTERVAL ? DAY)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, jours);
        return ps.executeUpdate();
    }

    // Compter les connexions aujourd'hui
    public int compterConnexionsAujourdhui() throws SQLException {
        String sql = "SELECT COUNT(*) FROM connexion_logs WHERE DATE(date_connexion) = CURDATE() AND status='Connexion'";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}