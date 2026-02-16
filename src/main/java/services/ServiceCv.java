package services;

import entities.Cv;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCv implements IService<Cv> {

    private Connection connection;

    public ServiceCv() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Cv cv) throws SQLException {
        String sql = "INSERT INTO cv (id_user, nom_fichier, chemin_fichier, taille_fichier, date_upload, statut, commentaire) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cv.getIdUser());
        ps.setString(2, cv.getNomFichier());
        ps.setString(3, cv.getCheminFichier());
        ps.setInt(4, cv.getTailleFichier());
        ps.setDate(5, Date.valueOf(cv.getDateUpload()));
        ps.setString(6, cv.getStatut());
        ps.setString(7, cv.getCommentaire());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Cv cv) throws SQLException {
        String sql = "UPDATE cv SET nom_fichier=?, chemin_fichier=?, taille_fichier=?, " +
                "statut=?, commentaire=? WHERE id_cv=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, cv.getNomFichier());
        ps.setString(2, cv.getCheminFichier());
        ps.setInt(3, cv.getTailleFichier());
        ps.setString(4, cv.getStatut());
        ps.setString(5, cv.getCommentaire());
        ps.setInt(6, cv.getIdCv());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Cv cv) throws SQLException {
        String sql = "DELETE FROM cv WHERE id_cv=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cv.getIdCv());
        ps.executeUpdate();
    }

    @Override
    public List<Cv> recuperer() throws SQLException {
        List<Cv> list = new ArrayList<>();
        String sql = "SELECT * FROM cv ORDER BY date_upload DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Cv cv = new Cv(
                    rs.getInt("id_cv"),
                    rs.getInt("id_user"),
                    rs.getString("nom_fichier"),
                    rs.getString("chemin_fichier"),
                    rs.getInt("taille_fichier"),
                    rs.getDate("date_upload").toLocalDate(),
                    rs.getString("statut"),
                    rs.getString("commentaire")
            );
            list.add(cv);
        }
        return list;
    }

    // Récupérer les CV par utilisateur
    public List<Cv> recupererParUtilisateur(int idUser) throws SQLException {
        List<Cv> list = new ArrayList<>();
        String sql = "SELECT * FROM cv WHERE id_user=? ORDER BY date_upload DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idUser);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Cv cv = new Cv(
                    rs.getInt("id_cv"),
                    rs.getInt("id_user"),
                    rs.getString("nom_fichier"),
                    rs.getString("chemin_fichier"),
                    rs.getInt("taille_fichier"),
                    rs.getDate("date_upload").toLocalDate(),
                    rs.getString("statut"),
                    rs.getString("commentaire")
            );
            list.add(cv);
        }
        return list;
    }

    // Récupérer les CV en attente
    public List<Cv> recupererEnAttente() throws SQLException {
        List<Cv> list = new ArrayList<>();
        String sql = "SELECT * FROM cv WHERE statut='en_attente' ORDER BY date_upload ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Cv cv = new Cv(
                    rs.getInt("id_cv"),
                    rs.getInt("id_user"),
                    rs.getString("nom_fichier"),
                    rs.getString("chemin_fichier"),
                    rs.getInt("taille_fichier"),
                    rs.getDate("date_upload").toLocalDate(),
                    rs.getString("statut"),
                    rs.getString("commentaire")
            );
            list.add(cv);
        }
        return list;
    }

    // Valider un CV
    public void validerCv(int idCv) throws SQLException {
        String sql = "UPDATE cv SET statut='valide' WHERE id_cv=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idCv);
        ps.executeUpdate();
    }

    // Refuser un CV avec commentaire
    public void refuserCv(int idCv, String commentaire) throws SQLException {
        String sql = "UPDATE cv SET statut='refuse', commentaire=? WHERE id_cv=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, commentaire);
        ps.setInt(2, idCv);
        ps.executeUpdate();
    }
}