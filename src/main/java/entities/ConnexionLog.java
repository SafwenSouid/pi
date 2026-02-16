package entities;

import java.time.LocalDateTime;  // ✅ AJOUTER CET IMPORT
import java.time.format.DateTimeFormatter;

public class ConnexionLog {

    private int id;
    private int userId;
    private String userEmail;
    private String userName;
    private LocalDateTime date;
    private String ipAddress;
    private String status;

    public ConnexionLog() {}

    // Constructeur complet
    public ConnexionLog(int id, int userId, String userEmail, String userName,
                        LocalDateTime date, String ipAddress, String status) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.date = date;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    // Constructeur pour ajout (avec userName)
    public ConnexionLog(int userId, String userEmail, String userName, String status, String ipAddress) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.status = status;
        this.ipAddress = ipAddress;
        this.date = LocalDateTime.now();  // ✅ MAINTENANT FONCTIONNE
    }

    // Getters et Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getDateFormatted() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ConnexionLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", date=" + getDateFormatted() +
                ", ipAddress='" + ipAddress + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}