package View;

import Controller.Doktor;
import Model.VeriTabani;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("RumMedic 2026 - Hastane Randevu Sistemi");
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cikisIslemi();
            }
        });

        ArkaPlanPanel panel = new ArkaPlanPanel();
        panel.setLayout(new GridBagLayout());
        setContentPane(panel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuYardim = new JMenu("Yardım");
        
        JMenu menuCikis = new JMenu("Çıkış");
        menuCikis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cikisIslemi();
            }
        });

        JMenuItem itemSistemBilgi = new JMenuItem("Sistem Bilgisi");
        itemSistemBilgi.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "RumMedic 2026\n" +
                        "\n" +
                        "Geliştirici: RumMedic\n\n" +
                        "Bu sistem hastane randevu işlemlerini kolaylaştırmak amacıyla geliştirilmiştir.",
                "Sistem Bilgisi", JOptionPane.INFORMATION_MESSAGE));

        JMenuItem itemNasil = new JMenuItem("Nasıl Kullanılır?");
        itemNasil.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "RumMedic Kullanım Kılavuzu:\n\n" +
                        "1. Yönetici Girişi:\n" +
                        "   - Doktor ekleme, düzenleme ve izin durumlarını yönetme.\n" +
                        "   - Tüm randevuları görüntüleme.\n" +
                        "   - Giriş için yönetici şifresi gereklidir.\n\n" +
                        "2. Hasta İşlemleri:\n" +
                        "   - Yeni randevu oluşturma.\n" +
                        "   - Aktif randevuları görüntüleme.\n" +
                        "   - Doktorların izin durumlarını kontrol etme.",
                "Yardım", JOptionPane.QUESTION_MESSAGE));

        menuYardim.add(itemSistemBilgi);
        menuYardim.add(itemNasil);

        menuBar.add(menuYardim);
        menuBar.add(menuCikis);
        setJMenuBar(menuBar);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setOpaque(false);

        JButton btnYonetici = new JButton("YÖNETİCİ GİRİŞİ");
        JButton btnHasta = new JButton("HASTA İŞLEMLERİ");

        Font btnFont = new Font("Arial", Font.BOLD, 18);
        btnYonetici.setFont(btnFont);
        btnHasta.setFont(btnFont);
        
        btnYonetici.setBackground(new Color(70, 130, 180));
        btnYonetici.setForeground(Color.WHITE);
        
        btnHasta.setBackground(new Color(60, 179, 113));
        btnHasta.setForeground(Color.WHITE);

        btnYonetici.setPreferredSize(new Dimension(250, 150));
        btnHasta.setPreferredSize(new Dimension(250, 150));

        btnYonetici.addActionListener(e -> {
            String[] roller = {"Admin", "Personel (Doktor)"};
            int secim = JOptionPane.showOptionDialog(this, "Giriş Türünü Seçiniz:", "Yönetici Girişi",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, roller, roller[0]);

            if (secim == 0) { // Admin Girişi
                JPanel passPanel = new JPanel(new BorderLayout(5, 5));
                JLabel lblMesaj = new JLabel("Admin Şifresi Giriniz:");
                JPasswordField txtSifre = new JPasswordField(15);
                JCheckBox chkGoster = new JCheckBox("Göster");

                char defaultChar = txtSifre.getEchoChar();
                chkGoster.addActionListener(ev -> {
                    if (chkGoster.isSelected()) {
                        txtSifre.setEchoChar((char) 0);
                    } else {
                        txtSifre.setEchoChar(defaultChar);
                    }
                });

                JPanel inputPanel = new JPanel(new BorderLayout());
                inputPanel.add(txtSifre, BorderLayout.CENTER);
                inputPanel.add(chkGoster, BorderLayout.EAST);

                passPanel.add(lblMesaj, BorderLayout.NORTH);
                passPanel.add(inputPanel, BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(this, passPanel, "Admin Girişi", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String sifre = new String(txtSifre.getPassword());
                    if (sifre.equals("admin")) {
                        new DoktorGUI().setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Hatalı Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (secim == 1) { // Personel (Doktor) Girişi
                JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField txtAdSoyad = new JTextField();
                JPasswordField txtSifre = new JPasswordField();
                
                loginPanel.add(new JLabel("Ad Soyad:"));
                loginPanel.add(txtAdSoyad);
                loginPanel.add(new JLabel("Şifre:"));
                loginPanel.add(txtSifre);
                
                int result = JOptionPane.showConfirmDialog(this, loginPanel, "Personel Girişi", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    String adSoyad = txtAdSoyad.getText();
                    String sifre = new String(txtSifre.getPassword());
                    
                    Doktor d = VeriTabani.doktorGiris(adSoyad, sifre);
                    if (d != null) {
                        new DoktorPaneliGUI(d).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Hatalı Ad Soyad veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnHasta.addActionListener(e -> {
            new HastaGirisGUI().setVisible(true);
            this.dispose();
        });

        buttonPanel.add(btnYonetici);
        buttonPanel.add(btnHasta);

        JLabel titleLabel = new JLabel("RUMMEDIC 2026", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(new EmptyBorder(0, 0, 50, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(buttonPanel, gbc);
    }

    private void cikisIslemi() {
        int response = JOptionPane.showConfirmDialog(this,
                "Uygulamadan çıkmak istiyor musunuz?",
                "Çıkış Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}