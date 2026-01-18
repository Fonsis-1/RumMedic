package Model;

import Controller.Doktor;
import Controller.Randevu;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class VeriTabani {
    public static ArrayList<Doktor> doktorListesi = new ArrayList<>();
    public static ArrayList<Randevu> randevuListesi = new ArrayList<>();

    private static final String DOKTOR_DOSYA = "doktorlar.dat";
    private static final String RANDEVU_DOSYA = "randevular.dat";

    static {
        verileriYukle();

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

        boolean degisiklikYapildi = false;

        for (String[] drBilgi : varsayilanDoktorlar) {
            boolean mevcut = false;
            for (Doktor d : doktorListesi) {
                String mevcutAd = d.getAdSoyad();
                String yeniAd = drBilgi[0];

                if (mevcutAd.equals(yeniAd)) {
                    mevcut = true;
                    break;
                }
            }

            if (!mevcut) {
                doktorListesi.add(new Doktor(drBilgi[0], drBilgi[1]));
                degisiklikYapildi = true;
            }
        }

        if (degisiklikYapildi) {
            verileriKaydet();
        }
    }

    public static void doktorEkle(String adSoyad, String brans) {
        doktorListesi.add(new Doktor(adSoyad, brans));
        verileriKaydet();
    }

    public static void doktorSil(int index) {
        if (index >= 0 && index < doktorListesi.size()) {
            doktorListesi.remove(index);
            verileriKaydet();
        }
    }

    public static void topluDoktorSil(ArrayList<Integer> indices) {
        indices.sort(Collections.reverseOrder());
        for (int index : indices) {
            if (index >= 0 && index < doktorListesi.size()) {
                doktorListesi.remove(index);
            }
        }
        verileriKaydet();
    }

    public static void randevuOlustur(String ad, String soyad, String tc, Doktor doktor, String saat) {
        randevuListesi.add(new Randevu(ad, soyad, tc, doktor, saat));
        verileriKaydet();
    }

    public static void randevuIptal(int index) {
        if (index >= 0 && index < randevuListesi.size()) {
            randevuListesi.remove(index);
            verileriKaydet();
        }
    }

    public static void topluRandevuSil(ArrayList<Integer> indices) {
        indices.sort(Collections.reverseOrder());
        for (int index : indices) {
            if (index >= 0 && index < randevuListesi.size()) {
                randevuListesi.remove(index);
            }
        }
        verileriKaydet();
    }

    public static boolean randevuSaatiDoluMu(Doktor doktor, String saat) {
        for (Randevu r : randevuListesi) {
            if (r.getDoktor().getAdSoyad().equals(doktor.getAdSoyad()) && r.getSaat().equals(saat)) {
                return true;
            }
        }
        return false;
    }

    public static void verileriKaydet() {
        try {
            ObjectOutputStream oosDoktor = new ObjectOutputStream(new FileOutputStream(DOKTOR_DOSYA));
            oosDoktor.writeObject(doktorListesi);
            oosDoktor.close();

            ObjectOutputStream oosRandevu = new ObjectOutputStream(new FileOutputStream(RANDEVU_DOSYA));
            oosRandevu.writeObject(randevuListesi);
            oosRandevu.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void verileriYukle() {
        try {
            File fDoktor = new File(DOKTOR_DOSYA);
            if (fDoktor.exists()) {
                ObjectInputStream oisDoktor = new ObjectInputStream(new FileInputStream(DOKTOR_DOSYA));
                doktorListesi = (ArrayList<Doktor>) oisDoktor.readObject();
                oisDoktor.close();
            }

            File fRandevu = new File(RANDEVU_DOSYA);
            if (fRandevu.exists()) {
                ObjectInputStream oisRandevu = new ObjectInputStream(new FileInputStream(RANDEVU_DOSYA));
                randevuListesi = (ArrayList<Randevu>) oisRandevu.readObject();
                oisRandevu.close();
            }
        } catch (IOException | ClassNotFoundException e) {
        }
    }
}