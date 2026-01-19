package Controller;

import java.io.Serializable;

public class Doktor implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String adSoyad;
    private String brans;
    private String sifre;
    private boolean izinde;

    public Doktor(String adSoyad, String brans, String sifre) {
        this.adSoyad = adSoyad;
        this.brans = brans;
        this.sifre = sifre;
        this.izinde = false;
    }

    // Eski yapıcı metot uyumluluğu için (varsayılan şifre ile)
    public Doktor(String adSoyad, String brans) {
        this(adSoyad, brans, "1234");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAdSoyad() { return adSoyad; }
    public String getBrans() { return brans; }
    
    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public boolean isIzinde() { return izinde; }
    public void setIzinde(boolean izinde) { this.izinde = izinde; }

    @Override
    public String toString() {
        if (izinde) {
            return adSoyad + " [" + brans + "] (İZİNDE)";
        }
        return adSoyad + " [" + brans + "]";
    }
}