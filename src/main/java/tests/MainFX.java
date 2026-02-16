package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

            // Créer la scène avec taille minimum
            Scene scene = new Scene(root, 900, 700);

            // AJOUTER LE CSS MENSOS
            String css = getClass().getResource("/css/mensos-theme.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configurer la fenêtre
            primaryStage.setTitle("MENSOS - Application de Quiz Mental");
            primaryStage.setScene(scene);

            // ✅ Fenêtre maximisée
            primaryStage.setMaximized(true);

            // ✅ Taille minimum (empêche de rétrécir trop)
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // ✅ Afficher la fenêtre
            primaryStage.show();

            System.out.println("✅ Application démarrée avec le thème MENSOS");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}