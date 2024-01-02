package menuutama;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML for the main menu
        Parent root = FXMLLoader.load(getClass().getResource("menuutama.fxml"));
        primaryStage.setTitle("Menu Utama");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        // Tombol untuk membuka Katalog Daftar
        Button btnOpenKatalog = (Button) root.lookup("#menudaftarkatalog");
        btnOpenKatalog.setOnAction(e -> openKatalog());

        // Tombol untuk membuka Transaksi
        Button btnOpenTransaksi = (Button) root.lookup("#menutransaksi");
        btnOpenTransaksi.setOnAction(e -> openTransaksi());
    }

    private void openKatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/katalog_daftar/katalog_daftar.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Daftar Katalog");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTransaksi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transaksi/transaksi.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaksi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
