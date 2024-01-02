package katalog_daftar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class katalog_controller implements Initializable {
    @FXML
    private ComboBox<String> cbkategori;
    @FXML
    private TextField tfkodebarang;
    @FXML
    private TextField tfnamabarang;
    @FXML
    private TextField tfstock;
    @FXML
    private TextField tfharga;
    @FXML
    private RadioButton m_ukuran;
    @FXML
    private RadioButton l_ukuran;
    @FXML
    private RadioButton xl_ukuran;
    @FXML
    private Button inputButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ToggleGroup ukuranGroup;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/TokoBajuPBO";
    private static final String USER = "root";
    private static final String PASS = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbkategori.getItems().addAll(" -- pilih ", "Atasan", "Bawahan");
        cbkategori.setValue(" -- pilih "); // Set pilihan default

        // Grouping RadioButtons
        ukuranGroup = new ToggleGroup();
        m_ukuran.setToggleGroup(ukuranGroup);
        l_ukuran.setToggleGroup(ukuranGroup);
        xl_ukuran.setToggleGroup(ukuranGroup);

        // Set kode barang secara otomatis dan membuatnya read-only
        try {
            tfkodebarang.setText(generateNextKodeBarang());
            tfkodebarang.setEditable(false); // Membuat TextField kodebarang sebagai read-only
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error, seperti menampilkan dialog error
        }
    }

    private String generateNextKodeBarang() throws SQLException {
        String kodeBarangTerakhir = "PBO001"; // Default jika tidak ada data
        String query = "SELECT kode_barang FROM tb_katalog ORDER BY kode_barang DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastCode = rs.getString("kode_barang");
                int numericPart = Integer.parseInt(lastCode.substring(3)) + 1;
                kodeBarangTerakhir = String.format("PBO%03d", numericPart);
            }
        }
        return kodeBarangTerakhir;
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void inputButtonOnAction(ActionEvent event) {
        // Cek apakah ada kolom yang kosong
        if (isInputValid()) {
            String kategori = cbkategori.getValue();
            String kodeBarang = tfkodebarang.getText();
            String namaBarang = tfnamabarang.getText();
            int stock = Integer.parseInt(tfstock.getText());
            double harga = Double.parseDouble(tfharga.getText());
            String ukuran = m_ukuran.isSelected() ? "M" : l_ukuran.isSelected() ? "L" : "XL";

            // Simpan data ke database
            if (simpanKeDatabase(kategori, kodeBarang, namaBarang, stock, harga, ukuran)) {
                Stage stage = (Stage) inputButton.getScene().getWindow();
                showAlert("Berhasil didaftarkan", "Barang berhasil di daftarkan ke katalog.", Alert.AlertType.INFORMATION);
                stage.close(); // Tutup jendela setelah menyimpan data
            }
        } else {
            // Tampilkan pesan error
            showAlert("Error", "Masih ada kolom kosong", Alert.AlertType.ERROR);
        }
    }

    private boolean isInputValid() {
        if (cbkategori.getValue().equals(" -- pilih ") ||
                tfkodebarang.getText().isEmpty() ||
                tfnamabarang.getText().isEmpty() ||
                tfstock.getText().isEmpty() ||
                tfharga.getText().isEmpty() ||
                (!m_ukuran.isSelected() && !l_ukuran.isSelected() && !xl_ukuran.isSelected())) {
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean simpanKeDatabase(String kategori, String kodeBarang, String namaBarang, int stock, double harga, String ukuran) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tb_katalog (kategori, kode_barang, nama_barang, stock, harga, ukuran) VALUES (?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, kategori);
            pstmt.setString(2, kodeBarang);
            pstmt.setString(3, namaBarang);
            pstmt.setInt(4, stock);
            pstmt.setDouble(5, harga);
            pstmt.setString(6, ukuran);

            pstmt.executeUpdate();
            System.out.println("Data berhasil disimpan");
            return true; // Data berhasil disimpan
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
            e.printStackTrace();
            return false; // Gagal menyimpan data
        }
    }
}