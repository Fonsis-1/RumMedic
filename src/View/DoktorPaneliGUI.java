package View;

import Controller.Doktor;
import Controller.Randevu;
import Model.VeriTabani;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DoktorPaneliGUI extends JFrame {

    private Doktor girisYapanDoktor;
    private DefaultTableModel model;

    public DoktorPaneliGUI(Doktor doktor) {
        this.girisYapanDoktor = doktor;

        setTitle("RumMedic 2026 - Doktor Paneli - " + doktor.getAdSoyad());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new MainGUI().setVisible(true);
                dispose();
            }
        });

        ArkaPlanPanel mainPanel = new ArkaPlanPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(mainPanel);

        JMenuBar menuBar = new JMenuBar();
        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setFocusPainted(false);
        btnCikis.setBorderPainted(false);
        btnCikis.setContentAreaFilled(false);
        btnCikis.setOpaque(true);
        btnCikis.setBackground(new Color(240, 240, 240));
        
        btnCikis.addActionListener(e -> {
            new MainGUI().setVisible(true);
            dispose();
        });

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(btnCikis);
        setJMenuBar(menuBar);

        JPanel pnlUst = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlUst.setOpaque(false);
        JLabel lblBaslik = new JLabel("Hoşgeldiniz, " + doktor.getAdSoyad());
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 20));
        pnlUst.add(lblBaslik);
        mainPanel.add(pnlUst, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // --- Randevu Listesi Sekmesi ---
        JPanel pnlRandevular = new JPanel(new BorderLayout());
        pnlRandevular.setOpaque(false);
        pnlRandevular.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Saat", "Hasta Adı", "Hasta Soyadı", "TC Kimlik No"};
        model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        pnlRandevular.add(scrollPane, BorderLayout.CENTER);

        // --- Tahlil Ekle Sekmesi (GÜNCELLENDİ) ---
        JPanel pnlTahlil = new JPanel(new GridLayout(5, 2, 10, 10));
        pnlTahlil.setOpaque(false);
        pnlTahlil.setBorder(new EmptyBorder(50, 50, 200, 50));

        JTextField txtTahlilTc = new JTextField();
        JTextField txtTahlilAdi = new JTextField();
        
        // Sonuç kısmı JComboBox yapıldı
        JComboBox<String> cmbTahlilSonuc = new JComboBox<>();
        cmbTahlilSonuc.addItem("Sonuç Seçiniz...");
        cmbTahlilSonuc.addItem("Pozitif");
        cmbTahlilSonuc.addItem("Negatif");
        
        JButton btnTahlilEkle = new JButton("Tahlil Sonucu Gönder");
        btnTahlilEkle.setBackground(new Color(60, 179, 113));
        btnTahlilEkle.setForeground(Color.WHITE);

        pnlTahlil.add(new JLabel("Hasta TC Kimlik No:"));
        pnlTahlil.add(txtTahlilTc);
        pnlTahlil.add(new JLabel("Tahlil Adı:"));
        pnlTahlil.add(txtTahlilAdi);
        pnlTahlil.add(new JLabel("Sonuç:"));
        pnlTahlil.add(cmbTahlilSonuc);
        pnlTahlil.add(new JLabel(""));
        pnlTahlil.add(btnTahlilEkle);

        btnTahlilEkle.addActionListener(e -> {
            String tc = txtTahlilTc.getText();
            String ad = txtTahlilAdi.getText();
            String sonuc = (String) cmbTahlilSonuc.getSelectedItem();
            String tarih = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

            if(tc.length() == 11 && !ad.isEmpty() && sonuc != null && !sonuc.equals("Sonuç Seçiniz...")) {
                VeriTabani.tahlilEkle(tc, ad, sonuc, tarih);
                JOptionPane.showMessageDialog(this, "Tahlil sonucu başarıyla gönderildi.");
                txtTahlilTc.setText(""); txtTahlilAdi.setText(""); cmbTahlilSonuc.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları eksiksiz doldurunuz.");
            }
        });

        // --- Rapor Ekle Sekmesi ---
        JPanel pnlRapor = new JPanel(new BorderLayout(10, 10));
        pnlRapor.setOpaque(false);
        pnlRapor.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlRaporUst = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlRaporUst.setOpaque(false);
        JTextField txtRaporTc = new JTextField(15);
        pnlRaporUst.add(new JLabel("Hasta TC Kimlik No:"));
        pnlRaporUst.add(txtRaporTc);

        JTextArea txtRaporIcerik = new JTextArea();
        txtRaporIcerik.setBorder(BorderFactory.createTitledBorder("Rapor İçeriği"));
        
        JButton btnRaporEkle = new JButton("Rapor Gönder");
        btnRaporEkle.setBackground(new Color(70, 130, 180));
        btnRaporEkle.setForeground(Color.WHITE);

        pnlRapor.add(pnlRaporUst, BorderLayout.NORTH);
        pnlRapor.add(new JScrollPane(txtRaporIcerik), BorderLayout.CENTER);
        pnlRapor.add(btnRaporEkle, BorderLayout.SOUTH);

        btnRaporEkle.addActionListener(e -> {
            String tc = txtRaporTc.getText();
            String icerik = txtRaporIcerik.getText();
            String tarih = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

            if(tc.length() == 11 && !icerik.isEmpty()) {
                VeriTabani.raporEkle(tc, icerik, tarih);
                JOptionPane.showMessageDialog(this, "Rapor başarıyla gönderildi.");
                txtRaporTc.setText(""); txtRaporIcerik.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen TC ve Rapor içeriğini giriniz.");
            }
        });

        tabbedPane.addTab("Randevularım", pnlRandevular);
        tabbedPane.addTab("Tahlil Sonucu Gir", pnlTahlil);
        tabbedPane.addTab("Rapor Yaz", pnlRapor);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        randevulariListele();
    }

    private void randevulariListele() {
        model.setRowCount(0);
        VeriTabani.randevuListesi.sort((r1, r2) -> r1.getSaat().compareTo(r2.getSaat()));

        for (Randevu r : VeriTabani.randevuListesi) {
            if (r.getDoktor().getId() == girisYapanDoktor.getId()) {
                model.addRow(new Object[]{
                        r.getSaat(),
                        r.getHastaAd(),
                        r.getHastaSoyad(),
                        r.getTcNo()
                });
            }
        }
    }
}