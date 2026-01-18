package Controller;

import java.io.Serializable;

public class Doktor implements Serializable {
    private static final long serialVersionUID = 1L;
    private String adSoyad;
    private String brans;
    private boolean izinde;

    public Doktor(String adSoyad, String brans) {
        this.adSoyad = adSoyad;
        this.brans = brans;
        this.izinde = false;
    }

    public String getAdSoyad() { return adSoyad; }
    public String getBrans() { return brans; }

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