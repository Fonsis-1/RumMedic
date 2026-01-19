package View;

import Model.VeriTabani;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class HastaGirisGUI extends JDialog {

    private JFrame parentFrame;

    public HastaGirisGUI(JFrame parent) {
        super(parent, "RumMedic 2026 - Hasta Girişi", true);
        this.parentFrame = parent;
        
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ArkaPlanPanel panel = new ArkaPlanPanel();
        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Giriş paneli
        JPanel pnlGiris = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlGiris.setOpaque(false);
        pnlGiris.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtGirisTc = new JTextField();
        txtGirisTc.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
                if (txtGirisTc.getText().length() >= 11 && c != KeyEvent.VK_BACK_SPACE) {
                    if (txtGirisTc.getSelectedText() == null || txtGirisTc.getSelectedText().isEmpty()) {
                        evt.consume();
                    }
                }
            }
        });

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
            
            if (tc.length() != 11) {
                JOptionPane.showMessageDialog(this, "TC Kimlik No 11 haneli olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String adSoyad = VeriTabani.hastaGiris(tc, sifre);
            if (adSoyad != null) {
                JOptionPane.showMessageDialog(this, "Hoşgeldiniz, " + adSoyad);
                

                new HastaGUI(tc, adSoyad).setVisible(true);
                

                dispose();
                

                if (parentFrame != null) {
                    parentFrame.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı TC veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Kayıt paneli
        JPanel pnlKayit = new JPanel(new GridLayout(5, 2, 10, 10));
        pnlKayit.setOpaque(false);
        pnlKayit.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtKayitTc = new JTextField();
        txtKayitTc.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
                if (txtKayitTc.getText().length() >= 11 && c != KeyEvent.VK_BACK_SPACE) {
                    if (txtKayitTc.getSelectedText() == null || txtKayitTc.getSelectedText().isEmpty()) {
                        evt.consume();
                    }
                }
            }
        });

        JTextField txtKayitAd = new JTextField();
        txtKayitAd.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        JTextField txtKayitSoyad = new JTextField();
        txtKayitSoyad.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });

        JPasswordField txtKayitSifre = new JPasswordField();
        JButton btnKayit = new JButton("Kayıt Ol");
        btnKayit.setBackground(new Color(70, 130, 180));
        btnKayit.setForeground(Color.WHITE);

        pnlKayit.add(new JLabel("TC Kimlik No:"));
        pnlKayit.add(txtKayitTc);
        pnlKayit.add(new JLabel("Ad:"));
        pnlKayit.add(txtKayitAd);
        pnlKayit.add(new JLabel("Soyad:"));
        pnlKayit.add(txtKayitSoyad);
        pnlKayit.add(new JLabel("Şifre Belirle:"));
        pnlKayit.add(txtKayitSifre);
        pnlKayit.add(new JLabel(""));
        pnlKayit.add(btnKayit);

        btnKayit.addActionListener(e -> {
            String tc = txtKayitTc.getText();
            String ad = txtKayitAd.getText().trim();
            String soyad = txtKayitSoyad.getText().trim();
            String sifre = new String(txtKayitSifre.getPassword());

            if (tc.length() != 11) {
                JOptionPane.showMessageDialog(this, "TC Kimlik No 11 haneli olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ad.isEmpty() || soyad.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.");
                return;
            }

            String tamAdSoyad = ad + " " + soyad;

            boolean basarili = VeriTabani.hastaKayit(tc, tamAdSoyad, sifre);
            if (basarili) {
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Giriş yapabilirsiniz.");
                txtKayitTc.setText("");
                txtKayitAd.setText("");
                txtKayitSoyad.setText("");
                txtKayitSifre.setText("");
                tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Bu TC ile zaten kayıt mevcut!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Giriş Yap", pnlGiris);
        tabbedPane.addTab("Kayıt Ol", pnlKayit);

        panel.add(tabbedPane, BorderLayout.CENTER);
    }
}