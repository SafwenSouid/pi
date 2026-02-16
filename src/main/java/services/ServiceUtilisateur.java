package services;

import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur> {

    private Connection connection;

    public ServiceUtilisateur() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateur(nom, prenom, email, numerotel, date_naissance, role, date_creation, motdepasse, actif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, u.getNom());
        ps.setString(2, u.getPrenom());
        ps.setString(3, u.getEmail());
        ps.setString(4, u.getNumeroTel());
        ps.setDate(5, Date.valueOf(u.getDateNaissance()));
        ps.setString(6, u.getRole());
        ps.setDate(7, Date.valueOf(u.getDateCreation()));
        ps.setString(8, u.getMotDePasse());
        ps.setBoolean(9, u.isActif());  // ✅ AJOUTÉ
        ps.executeUpdate();
    }

    @Override
    public void modifier(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, numerotel=?, date_naissance=?, role=?, motdepasse=?, actif=? WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, u.getNom());
        ps.setString(2, u.getPrenom());
        ps.setString(3, u.getEmail());
        ps.setString(4, u.getNumeroTel());
        ps.setDate(5, Date.valueOf(u.getDateNaissance()));
        ps.setString(6, u.getRole());
        ps.setString(7, u.getMotDePasse());
        ps.setBoolean(8, u.isActif());  // ✅ AJOUTÉ
        ps.setInt(9, u.getIdUser());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Utilisateur u) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id_user = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, u.getIdUser());
        ps.executeUpdate();
    }

    @Override
    public List<Utilisateur> recuperer() throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY id_user DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("numerotel"),
                    rs.getDate("date_naissance") != null ?
                            rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getString("motdepasse"),
                    rs.getBoolean("actif")  // ✅ AJOUTÉ
            );
            list.add(u);
        }
        return list;
    }

    // ✅ NOUVELLE MÉTHODE - Récupérer un utilisateur par son ID
    public Utilisateur recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("numerotel"),
                    rs.getDate("date_naissance") != null ?
                            rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getString("motdepasse"),
                    rs.getBoolean("actif")
            );
        }
        return null;
    }

    // ✅ NOUVELLE MÉTHODE - Récupérer les utilisateurs par rôle
    public List<Utilisateur> recupererParRole(String role) throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE role=? ORDER BY id_user DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("numerotel"),
                    rs.getDate("date_naissance") != null ?
                            rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getString("motdepasse"),
                    rs.getBoolean("actif")
            );
            list.add(u);
        }
        return list;
    }

    // ✅ NOUVELLE MÉTHODE - Récupérer les utilisateurs actifs/inactifs
    public List<Utilisateur> recupererParStatut(boolean actif) throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE actif=? ORDER BY id_user DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, actif);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("numerotel"),
                    rs.getDate("date_naissance") != null ?
                            rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getString("motdepasse"),
                    rs.getBoolean("actif")
            );
            list.add(u);
        }
        return list;
    }

    // ✅ NOUVELLE MÉTHODE - Rechercher des utilisateurs
    public List<Utilisateur> rechercher(String motCle) throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY id_user DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        String searchPattern = "%" + motCle + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("numerotel"),
                    rs.getDate("date_naissance") != null ?
                            rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getDate("date_creation").toLocalDate(),
                    rs.getString("motdepasse"),
                    rs.getBoolean("actif")
            );
            list.add(u);
        }
        return list;
    }

    // ✅ NOUVELLE MÉTHODE - Compter les utilisateurs
    public int compterUtilisateurs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // ✅ NOUVELLE MÉTHODE - Compter les utilisateurs par rôle
    public int compterParRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE role=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // ✅ NOUVELLE MÉTHODE - Compter les utilisateurs par statut
    public int compterParStatut(boolean actif) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE actif=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, actif);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // ✅ NOUVELLE MÉTHODE - Activer/Désactiver un utilisateur
    public void changerStatut(int idUser, boolean actif) throws SQLException {
        String sql = "UPDATE utilisateur SET actif=? WHERE id_user=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, actif);
        ps.setInt(2, idUser);
        ps.executeUpdate();
    }
}