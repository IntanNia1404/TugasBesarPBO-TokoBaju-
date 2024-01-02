package katalog_daftar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class katalog_application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Memuat FXML
        Parent root = FXMLLoader.load(getClass().getResource("katalog_daftar.fxml"));
        // Membuat scene dengan ukuran yang ditentukan di Scene Builder
        Scene scene = new Scene(root, 516, 396);

        stage.setTitle("Daftar Katalog");
        stage.setMinWidth(516);
        stage.setMinHeight(396);
        stage.setMaxWidth(516);
        stage.setMaxHeight(396);
        stage.setScene(scene);
        stage.show();
    }
}