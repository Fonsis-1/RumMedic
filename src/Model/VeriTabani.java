package Model;

import Controller.Doktor;
import Controller.Randevu;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

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
                    "sifre TEXT DEFAULT '1234'," +
                    "izinde INTEGER DEFAULT 0" +
                    ")";
            stmt.execute(sqlDoktor);
            
            try {
                stmt.execute("ALTER TABLE doktorlar ADD COLUMN sifre TEXT DEFAULT '1234'");
            } catch (SQLException ignored) {}

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
                {"Prof. Dr. Kenan Özkan", "Kardiyoloji", "ko123"},
                {"Op. Dr. Berna Yüksel", "Göz Hastalıkları", "by123"},
                {"Uzm. Dr. Serkan Bulut", "Dahiliye", "sb123"},
                {"Doç. Dr. Esra Aksoy", "Kardiyoloji", "ea123"},
                {"Op. Dr. Volkan Taş", "Göz Hastalıkları", "vt123"},
                {"Uzm. Dr. Merve Şen", "Acil Tıp", "ms123"},
                {"Yrd. Doç. Dr. Onur Kurt", "Anesteziyoloji", "ok123"},
                {"Prof. Dr. Deniz Kara", "Beyin ve Sinir Cerrahisi", "dk123"},
                {"Uzm. Dr. Cemal Ekinci", "Çocuk Sağlığı ve Hastalıkları", "ce123"},
                {"Dr. Öğr. Üyesi Hande Güler", "Dermatoloji (Cildiye)", "hg123"},
                {"Uzm. Dr. Tolga Sönmez", "Enfeksiyon Hastalıkları", "ts123"},
                {"Fzt. Dr. Aylin Toprak", "Fiziksel Tıp ve Rehabilitasyon", "at123"},
                {"Op. Dr. Barış Yavuz", "Genel Cerrahi", "bya123"},
                {"Prof. Dr. Selin Tunç", "Göğüs Hastalıkları", "st123"},
                {"Op. Dr. Metin Aslan", "Kadın Hastalıkları ve Doğum", "ma123"},
                {"Op. Dr. Gizem Erdoğan", "Kulak Burun Boğaz", "ge123"},
                {"Doç. Dr. Erhan Çetin", "Nöroloji", "ec123"},
                {"Op. Dr. Sinan Koçak", "Ortopedi ve Travmatoloji", "sk123"},
                {"Uzm. Dr. Banu Tekin", "Psikiyatri", "bt123"},
                {"Uzm. Dr. Ece Bilgin", "Radyoloji", "eb123"},
                {"Op. Dr. Fatih Duran", "Üroloji", "fd123"},
                {"Op. Dr. Nazlı Keskin", "Plastik Cerrahi", "nk123"},
                {"Dyt. Emre Acar", "Beslenme ve Diyet", "ea1234"}
        };

        for (String[] drBilgi : varsayilanDoktorlar) {
            doktorEkle(drBilgi[0], drBilgi[1], drBilgi[2]);
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
        return null;
    }

    // --- DOKTOR İŞLEMLERİ ---

    public static Doktor doktorGiris(String girilenAdSoyad, String sifre) {
        Locale trLocale = new Locale("tr", "TR");
        for (Doktor d : doktorListesi) {
            String dbIsim = d.getAdSoyad().toLowerCase(trLocale);
            String girilen = girilenAdSoyad.toLowerCase(trLocale);
            
            if (dbIsim.contains(girilen) && d.getSifre().equals(sifre)) {
                return d;
            }
        }
        return null;
    }

    public static void doktorEkle(String adSoyad, String brans, String sifre) {
        String sql = "INSERT INTO doktorlar(ad_soyad, brans, sifre) VALUES(?, ?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adSoyad);
            pstmt.setString(2, brans);
            pstmt.setString(3, sifre);
            pstmt.executeUpdate();
            verileriYukle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void doktorEkle(String adSoyad, String brans) {
        doktorEkle(adSoyad, brans, "1234");
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
        String sql = "UPDATE doktorlar SET izinde = ?, sifre = ? WHERE id = ?";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, d.isIzinde() ? 1 : 0);
            pstmt.setString(2, d.getSifre());
            pstmt.setInt(3, d.getId());
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

    // --- TAHLİL VE RAPOR İŞLEMLERİ ---

    public static void tahlilEkle(String tc, String tahlilAdi, String sonuc, String tarih) {
        String sql = "INSERT INTO tahliller(hasta_tc, tahlil_adi, sonuc, tarih) VALUES(?, ?, ?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, tahlilAdi);
            pstmt.setString(3, sonuc);
            pstmt.setString(4, tarih);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void raporEkle(String tc, String raporIcerik, String tarih) {
        String sql = "INSERT INTO raporlar(hasta_tc, rapor_icerik, tarih) VALUES(?, ?, ?)";
        try (Connection conn = baglantiOlustur();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, raporIcerik);
            pstmt.setString(3, tarih);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                String sifre = "1234";
                try { sifre = rsDoktor.getString("sifre"); } catch (Exception e) {}
                if(sifre == null) sifre = "1234";

                Doktor d = new Doktor(rsDoktor.getString("ad_soyad"), rsDoktor.getString("brans"), sifre);
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