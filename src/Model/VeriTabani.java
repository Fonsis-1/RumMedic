package Model;

import Controller.Doktor;
import Controller.Randevu;

import java.sql.*;
import java.util.ArrayList;

public class VeriTabani {
    public static ArrayList<Doktor> doktorListesi = new ArrayList<>();
    public static ArrayList<Randevu> randevuListesi = new ArrayList<>();

    private static final String DB_URL = "jdbc:sqlite:rummedic.db";

    static {
        tablolariOlustur();
        verileriYukle();

        if (doktorListesi.isEmpty()) {
            varsayilanDoktorlariEkle();
        }
    }

    private static Connection baglantiOlustur() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void tablolariOlustur() {
        try (Connection conn = baglantiOlustur();
             Statement stmt = conn.createStatement()) {
            
            String sqlDoktor = "CREATE TABLE IF NOT EXISTS doktorlar (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ad_soyad TEXT NOT NULL," +
                    "brans TEXT NOT NULL," +
                    "izinde INTEGER DEFAULT 0" +
                    ")";
            stmt.execute(sqlDoktor);

            String sqlRandevu = "CREATE TABLE IF NOT EXISTS randevular (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hasta_ad TEXT NOT NULL," +
                    "hasta_soyad TEXT NOT NULL," +
                    "tc_no TEXT NOT NULL," +
                    "doktor_id INTEGER," +
                    "saat TEXT NOT NULL," +
                    "FOREIGN KEY(doktor_id) REFERENCES doktorlar(id)" +
                    ")";
            stmt.execute(sqlRandevu);

            String sqlTahlil = "CREATE TABLE IF NOT EXISTS tahliller (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hasta_tc TEXT NOT NULL," +
                    "tahlil_adi TEXT NOT NULL," +
                    "sonuc TEXT NOT NULL," +
                    "tarih TEXT NOT NULL" +
                    ")";
            stmt.execute(sqlTahlil);

            String sqlRapor = "CREATE TABLE IF NOT EXISTS raporlar (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hasta_tc TEXT NOT NULL," +
                    "rapor_icerik TEXT NOT NULL," +
                    "tarih TEXT NOT NULL" +
                    ")";
            stmt.execute(sqlRapor);

            String sqlHasta = "CREATE TABLE IF NOT EXISTS hastalar (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tc_no TEXT NOT NULL UNIQUE," +
                    "ad_soyad TEXT NOT NULL," +
                    "sifre TEXT NOT NULL" +
                    ")";
            stmt.execute(sqlHasta);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void varsayilanDoktorlariEkle() {
        String[][] varsayilanDoktorlar = {
                {"Prof. Dr. Kenan Özkan", "Kardiyoloji"},
                {"Op. Dr. Berna Yüksel", "Göz Hastalıkları"},
                {"Uzm. Dr. Serkan Bulut", "Dahiliye"},
                {"Doç. Dr. Esra Aksoy", "Kardiyoloji"},
                {"Op. Dr. Volkan Taş", "Göz Hastalıkları"},
                {"Uzm. Dr. Merve Şen", "Acil Tıp"},
                {"Yrd. Doç. Dr. Onur Kurt", "Anesteziyoloji"},
                {"Prof. Dr. Deniz Kara", "Beyin ve Sinir Cerrahisi"},
                {"Uzm. Dr. Cemal Ekinci", "Çocuk Sağlığı ve Hastalıkları"},
                {"Dr. Öğr. Üyesi Hande Güler", "Dermatoloji (Cildiye)"},
                {"Uzm. Dr. Tolga Sönmez", "Enfeksiyon Hastalıkları"},
                {"Fzt. Dr. Aylin Toprak", "Fiziksel Tıp ve Rehabilitasyon"},
                {"Op. Dr. Barış Yavuz", "Genel Cerrahi"},
                {"Prof. Dr. Selin Tunç", "Göğüs Hastalıkları"},
                {"Op. Dr. Metin Aslan", "Kadın Hastalıkları ve Doğum"},
                {"Op. Dr. Gizem Erdoğan", "Kulak Burun Boğaz"},
                {"Doç. Dr. Erhan Çetin", "Nöroloji"},
                {"Op. Dr. Sinan Koçak", "Ortopedi ve Travmatoloji"},
                {"Uzm. Dr. Banu Tekin", "Psikiyatri"},
                {"Uzm. Dr. Ece Bilgin", "Radyoloji"},
                {"Op. Dr. Fatih Duran", "Üroloji"},
                {"Op. Dr. Nazlı Keskin", "Plastik Cerrahi"},
                {"Dyt. Emre Acar", "Beslenme ve Diyet"}
        };

        for (String[] drBilgi : varsayilanDoktorlar) {
            doktorEkle(drBilgi[0], drBilgi[1]);
        }
    }

    // --- HASTA İŞLEMLERİ ---

    public static boolean hastaKayit(String tc, String adSoyad, String sifre) {
        String sql = "INSERT INTO hastalar(tc_no, ad_soyad, sifre) VALUES(?, ?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, adSoyad);
            pstmt.setString(3, sifre);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // e.printStackTrace(); // Unique constraint hatası olabilir (Aynı TC ile kayıt)
            return false;
        }
    }

    public static String hastaGiris(String tc, String sifre) {
        String sql = "SELECT ad_soyad FROM hastalar WHERE tc_no = ? AND sifre = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, sifre);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ad_soyad");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Giriş başarısız
    }



    public static void doktorEkle(String adSoyad, String brans) {
        String sql = "INSERT INTO doktorlar(ad_soyad, brans) VALUES(?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adSoyad);
            pstmt.setString(2, brans);
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void doktorSil(int id) {
        String sql = "DELETE FROM doktorlar WHERE id = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void doktorGuncelle(Doktor d) {
        String sql = "UPDATE doktorlar SET izinde = ? WHERE id = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, d.isIzinde() ? 1 : 0);
            pstmt.setInt(2, d.getId());
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void randevuOlustur(String ad, String soyad, String tc, Doktor doktor, String saat) {
        String sql = "INSERT INTO randevular(hasta_ad, hasta_soyad, tc_no, doktor_id, saat) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ad);
            pstmt.setString(2, soyad);
            pstmt.setString(3, tc);
            pstmt.setInt(4, doktor.getId());
            pstmt.setString(5, saat);
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void randevuIptal(int id) {
        String sql = "DELETE FROM randevular WHERE id = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean randevuSaatiDoluMu(Doktor doktor, String saat) {
        String sql = "SELECT count(*) FROM randevular WHERE doktor_id = ? AND saat = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doktor.getId());
            pstmt.setString(2, saat);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String[]> tahlilGetir(String tc) {
        ArrayList<String[]> tahliller = new ArrayList<>();
        String sql = "SELECT * FROM tahliller WHERE hasta_tc = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tahliller.add(new String[]{
                        rs.getString("tahlil_adi"),
                        rs.getString("sonuc"),
                        rs.getString("tarih")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tahliller;
    }

    public static ArrayList<String[]> raporGetir(String tc) {
        ArrayList<String[]> raporlar = new ArrayList<>();
        String sql = "SELECT * FROM raporlar WHERE hasta_tc = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                raporlar.add(new String[]{
                        rs.getString("rapor_icerik"),
                        rs.getString("tarih")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return raporlar;
    }

    public static void verileriYukle() {
        doktorListesi.clear();
        randevuListesi.clear();

        try (Connection conn = baglantiOlustur();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rsDoktor = stmt.executeQuery("SELECT * FROM doktorlar");
            while (rsDoktor.next()) {
                Doktor d = new Doktor(rsDoktor.getString("ad_soyad"), rsDoktor.getString("brans"));
                d.setId(rsDoktor.getInt("id"));
                d.setIzinde(rsDoktor.getInt("izinde") == 1);
                doktorListesi.add(d);
            }

            ResultSet rsRandevu = stmt.executeQuery("SELECT * FROM randevular");
            while (rsRandevu.next()) {
                int doktorId = rsRandevu.getInt("doktor_id");
                Doktor doktor = null;
                for (Doktor d : doktorListesi) {
                    if (d.getId() == doktorId) {
                        doktor = d;
                        break;
                    }
                }
                
                if (doktor != null) {
                    Randevu r = new Randevu(
                            rsRandevu.getString("hasta_ad"),
                            rsRandevu.getString("hasta_soyad"),
                            rsRandevu.getString("tc_no"),
                            doktor,
                            rsRandevu.getString("saat")
                    );
                    r.setId(rsRandevu.getInt("id"));
                    randevuListesi.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}