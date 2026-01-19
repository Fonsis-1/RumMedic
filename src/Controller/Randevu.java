package Controller;

public class Randevu {
    private int id;
    private String hastaAd;
    private String hastaSoyad;
    private String tcNo;
    private Doktor doktor;
    private String saat;

    public Randevu(String hastaAd, String hastaSoyad, String tcNo, Doktor doktor, String saat) {
        this.hastaAd = hastaAd;
        this.hastaSoyad = hastaSoyad;
        this.tcNo = tcNo;
        this.doktor = doktor;
        this.saat = saat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHastaAd() { return hastaAd; }
    public String getHastaSoyad() { return hastaSoyad; }
    public String getTcNo() { return tcNo; }
    public Doktor getDoktor() { return doktor; }
    public String getSaat() { return saat; }
}