package menuutama;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuUtamaController {

    @FXML
    private Button menudaftarkatalog;

    @FXML
    private Button menutransaksi;

    @FXML
    private void handleDaftarKatalog() throws IOException {
        loadScene("/katalog_daftar/daftarkatalog.fxml");
    }

    @FXML
    private void handleTransaksi() throws IOException {
        loadScene("/transaksi/transaksi.fxml");
    }

    private void loadScene(String fxmlPath) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.show();
    }
}
