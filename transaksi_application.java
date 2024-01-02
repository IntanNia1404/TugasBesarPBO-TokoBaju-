package transaksi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class transaksi_application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Memuat FXML
        Parent root = FXMLLoader.load(getClass().getResource("transaksi.fxml"));
        // Membuat scene dengan ukuran yang ditentukan di Scene Builder
        Scene scene = new Scene(root, 600, 487);

        stage.setTitle("Transaksi");
        stage.setMinWidth(600);
        stage.setMinHeight(487);
        stage.setMaxWidth(600);
        stage.setMaxHeight(487);
        stage.setScene(scene);
        stage.show();
    }
}