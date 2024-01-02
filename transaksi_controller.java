package transaksi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.*;

public class transaksi_controller {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/TokoBajuPBO";
    private static final String USER = "root";
    private static final String PASS = "";

    @FXML
    private ComboBox<String> cbkategori;

    @FXML
    private ComboBox<String> cbkodebarang;

    @FXML
    private TextField tfnamabarang;

    @FXML
    private TextField tfqty;

    @FXML
    private TextField tfharga;

    @FXML
    private TextField tftotalharga;

    @FXML
    private RadioButton m_ukuran;

    @FXML
    private RadioButton l_ukuran;

    @FXML
    private RadioButton xl_ukuran;

    @FXML
    private TextField tfketerangan;

    @FXML
    private TextField tfstock;

    @FXML
    private Button buyButton;

    @FXML
    private Button cancelButton;
    private ToggleGroup ukuranGroup;

    @FXML
    private void initialize() {

        setupComboBoxListeners();
        tfnamabarang.setEditable(false);
        tfstock.setEditable(false);
        tfharga.setEditable(false);
        tftotalharga.setEditable(false);
        tfketerangan.setEditable(false);

        ukuranGroup = new ToggleGroup();
        m_ukuran.setToggleGroup(ukuranGroup);
        l_ukuran.setToggleGroup(ukuranGroup);
        xl_ukuran.setToggleGroup(ukuranGroup);

        m_ukuran.setOnAction(event -> checkAndDisplayStockAvailability(cbkodebarang.getValue(), "M"));
        l_ukuran.setOnAction(event -> checkAndDisplayStockAvailability(cbkodebarang.getValue(), "L"));
        xl_ukuran.setOnAction(event -> checkAndDisplayStockAvailability(cbkodebarang.getValue(), "XL"));

        ObservableList<String> kategoriList = FXCollections.observableArrayList("--pilih", "Atasan", "Bawahan");
        cbkategori.setItems(kategoriList);
        cbkategori.setValue("--pilih");

        cbkategori.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (!newValue.equals("--pilih")) {
                loadKodeBarangBerdasarkanKategori(newValue);
            } else {
                cbkodebarang.setItems(FXCollections.observableArrayList());
            }
        });

    }

    private Connection connectDB() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            return conn;
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return null;
        }
    }

    private void checkAndDisplayStockAvailability(String kodeBarang, String ukuran) {
        if (kodeBarang == null || kodeBarang.isEmpty()) {
            tfketerangan.setText("");
            return;
        }

        String query = "SELECT COUNT(*) FROM tb_katalog WHERE kode_barang = ? AND ukuran = ?";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, kodeBarang);
            pstmt.setString(2, ukuran);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    tfketerangan.setText(count > 0 ? "Tersedia" : "Tidak Tersedia");
                } else {
                    tfketerangan.setText("Tidak Tersedia");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            tfketerangan.setText("Error memeriksa stok");
        }
    }

    private void loadKodeBarangBerdasarkanKategori(String kategori) {
        ObservableList<String> kodeBarangList = FXCollections.observableArrayList();
        String query = "SELECT kode_barang FROM tb_katalog WHERE kategori = '" + kategori + "'";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                kodeBarangList.add(rs.getString("kode_barang"));
            }

            cbkodebarang.setItems(kodeBarangList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setupComboBoxListeners() {
        cbkodebarang.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                updateKategori(newValue);
                updateNamaBarang(newValue);
                updateStock(newValue);
                updateHarga(newValue);
            }
        });

        tfqty.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateTotalHarga();
        });
    }

    private void updateNamaBarang(String kodeBarang) {
        String query = "SELECT nama_barang FROM tb_katalog WHERE kode_barang = '" + kodeBarang + "'";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                tfnamabarang.setText(rs.getString("nama_barang"));
            } else {
                tfnamabarang.setText(""); // Membersihkan jika tidak ada hasil
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            tfnamabarang.setText(""); // Membersihkan jika terjadi error
        }
    }

    private void updateStock(String kodeBarang) {
        String query = "SELECT stock FROM tb_katalog WHERE kode_barang = '" + kodeBarang + "'";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                tfstock.setText(String.valueOf(rs.getInt("stock")));
            } else {
                tfstock.setText(""); // Membersihkan jika tidak ada hasil
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            tfstock.setText(""); // Membersihkan jika terjadi error
        }
    }

    private void updateHarga(String kodeBarang) {
        String query = "SELECT harga FROM tb_katalog WHERE kode_barang = '" + kodeBarang + "'";
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                tfharga.setText(String.valueOf(rs.getDouble("harga")));
            } else {
                tfharga.setText(""); // Membersihkan jika tidak ada hasil
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            tfharga.setText(""); // Membersihkan jika terjadi error
        }
    }

    private void calculateTotalHarga() {
        try {
            int qty = Integer.parseInt(tfqty.getText());
            double harga = Double.parseDouble(tfharga.getText());
            double total = qty * harga;
            tftotalharga.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            tftotalharga.setText(""); // Membersihkan jika input tidak valid
        }
    }

    private void updateKategori(String kodeBarang) {
    }

    @FXML
    private void buyButtonOnAction() {
        if (validasiInput()) {
            simpanTransaksi();
            kurangiStock();
            tampilkanNotifikasiPembelian();
        }
    }

    private boolean validasiInput() {
        // Validasi input. Pastikan semua kolom terisi dan qty tidak melebihi stock.
        if (cbkategori.getValue().equals("--pilih") ||
                cbkodebarang.getValue() == null ||
                tfnamabarang.getText().isEmpty() ||
                tfqty.getText().isEmpty() ||
                tfharga.getText().isEmpty() ||
                tfstock.getText().isEmpty() ||
                Integer.parseInt(tfqty.getText()) > Integer.parseInt(tfstock.getText())) {
            System.out.println("Input tidak valid");
            return false;
        }
        return true;
    }

    private void simpanTransaksi() {
        // Implementasi penyimpanan data ke tabel tb_transaksi
        String query = "INSERT INTO tb_transaksi (kode_barang, nama_barang, ukuran, quantity, harga, total_harga) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, cbkodebarang.getValue());
            pstmt.setString(2, tfnamabarang.getText()); // Menambahkan nama_barang
            // Anda perlu menambahkan logika untuk mendapatkan ukuran yang dipilih
            String selectedSize = ((RadioButton) ukuranGroup.getSelectedToggle()).getText();
            pstmt.setString(3, selectedSize); // Menambahkan ukuran
            pstmt.setInt(4, Integer.parseInt(tfqty.getText())); // quantity
            pstmt.setDouble(5, Double.parseDouble(tfharga.getText())); // harga
            pstmt.setDouble(6, Double.parseDouble(tftotalharga.getText())); // total_harga
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }


    private void kurangiStock() {
        // Mengurangi stock pada tb_katalog
        String query = "UPDATE tb_katalog SET stock = stock - ? WHERE kode_barang = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(tfqty.getText()));
            pstmt.setString(2, cbkodebarang.getValue());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void tampilkanNotifikasiPembelian() {
        // Menampilkan notifikasi bahwa pembelian berhasil
        System.out.println("Pembelian berhasil!");
        showAlert("Pembelian berhasil!", "Transaksi telah berhasil dilakukan.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
