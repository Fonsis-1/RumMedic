package View;

import Model.VeriTabani;

import javax.swing.*;
import java.awt.*;

public class HastaGirisGUI extends JFrame {

    public HastaGirisGUI() {
        setTitle("RumMedic 2026 - Hasta Girişi");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ArkaPlanPanel panel = new ArkaPlanPanel();
        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- GİRİŞ YAP PANELİ ---
        JPanel pnlGiris = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlGiris.setOpaque(false);
        pnlGiris.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtGirisTc = new JTextField();
        JPasswordField txtGirisSifre = new JPasswordField();
        JButton btnGiris = new JButton("Giriş Yap");
        btnGiris.setBackground(new Color(60, 179, 113));
        btnGiris.setForeground(Color.WHITE);

        pnlGiris.add(new JLabel("TC Kimlik No:"));
        pnlGiris.add(txtGirisTc);
        pnlGiris.add(new JLabel("Şifre:"));
        pnlGiris.add(txtGirisSifre);
        pnlGiris.add(new JLabel(""));
        pnlGiris.add(btnGiris);

        btnGiris.addActionListener(e -> {
            String tc = txtGirisTc.getText();
            String sifre = new String(txtGirisSifre.getPassword());

            if (tc.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.");
                return;
            }

            String adSoyad = VeriTabani.hastaGiris(tc, sifre);
            if (adSoyad != null) {
                JOptionPane.showMessageDialog(this, "Hoşgeldiniz, " + adSoyad);
                new HastaGUI(tc, adSoyad).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı TC veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- KAYIT OL PANELİ ---
        JPanel pnlKayit = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlKayit.setOpaque(false);
        pnlKayit.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtKayitTc = new JTextField();
        JTextField txtKayitAdSoyad = new JTextField();
        JPasswordField txtKayitSifre = new JPasswordField();
        JButton btnKayit = new JButton("Kayıt Ol");
        btnKayit.setBackground(new Color(70, 130, 180));
        btnKayit.setForeground(Color.WHITE);

        pnlKayit.add(new JLabel("TC Kimlik No:"));
        pnlKayit.add(txtKayitTc);
        pnlKayit.add(new JLabel("Ad Soyad:"));
        pnlKayit.add(txtKayitAdSoyad);
        pnlKayit.add(new JLabel("Şifre Belirle:"));
        pnlKayit.add(txtKayitSifre);
        pnlKayit.add(new JLabel(""));
        pnlKayit.add(btnKayit);

        btnKayit.addActionListener(e -> {
            String tc = txtKayitTc.getText();
            String adSoyad = txtKayitAdSoyad.getText();
            String sifre = new String(txtKayitSifre.getPassword());

            if (tc.length() != 11 || !tc.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Geçersiz TC Kimlik No!");
                return;
            }
            if (adSoyad.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.");
                return;
            }

            boolean basarili = VeriTabani.hastaKayit(tc, adSoyad, sifre);
            if (basarili) {
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Giriş yapabilirsiniz.");
                txtKayitTc.setText("");
                txtKayitAdSoyad.setText("");
                txtKayitSifre.setText("");
                tabbedPane.setSelectedIndex(0); // Giriş sekmesine geç
            } else {
                JOptionPane.showMessageDialog(this, "Bu TC ile zaten kayıt mevcut!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Giriş Yap", pnlGiris);
        tabbedPane.addTab("Kayıt Ol", pnlKayit);

        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Pencere kapanınca ana menüye dön
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                new MainGUI().setVisible(true);
            }
        });
    }
}