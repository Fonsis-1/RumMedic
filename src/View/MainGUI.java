package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("RumMedic 2026 - Hastane Randevu Sistemi");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ArkaPlanPanel panel = new ArkaPlanPanel();
        panel.setLayout(new GridBagLayout());
        setContentPane(panel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuHakkimizda = new JMenu("Hakkımızda");
        JMenu menuYardim = new JMenu("Yardım");
        JMenu menuCikis = new JMenu("Çıkış");

        JMenuItem itemHakkimizda = new JMenuItem("Sistem Bilgisi");
        itemHakkimizda.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "RumMedic 2026\n" +
                        "\n" +
                        "Geliştirici: RumMedic\n\n" +
                        "Bu sistem hastane randevu işlemlerini kolaylaştırmak amacıyla geliştirilmiştir.",
                "Hakkımızda", JOptionPane.INFORMATION_MESSAGE));

        JMenuItem itemYardim = new JMenuItem("Nasıl Kullanılır?");
        itemYardim.addActionListener(e -> JOptionPane.showMessageDialog(this,
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

        JMenuItem itemCikis = new JMenuItem("Uygulamayı Kapat");
        itemCikis.addActionListener(e -> System.exit(0));

        menuHakkimizda.add(itemHakkimizda);
        menuYardim.add(itemYardim);
        menuCikis.add(itemCikis);

        menuBar.add(menuHakkimizda);
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
            JPanel passPanel = new JPanel(new BorderLayout(5, 5));
            JLabel lblMesaj = new JLabel("Yönetici Şifresi Giriniz:");
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

            String[] options = {"Giriş", "İptal"};
            int result = JOptionPane.showOptionDialog(this, passPanel, "Giriş Yap",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (result == 0) {
                String sifre = new String(txtSifre.getPassword());
                if (sifre.equals("admin")) {
                    new DoktorGUI().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Hatalı Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
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
}