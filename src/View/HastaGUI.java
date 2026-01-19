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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HastaGUI extends JFrame {

    private JComboBox<String> cmbBranslar;
    private JComboBox<Doktor> cmbDoktorlar;
    private JComboBox<String> cmbSaatler;
    private DefaultTableModel model;
    private DefaultTableModel tahlilModel;
    private DefaultTableModel raporModel;
    
    private String girisYapanTc;
    private String girisYapanAdSoyad;

    public HastaGUI(String tc, String adSoyad) {
        this.girisYapanTc = tc;
        this.girisYapanAdSoyad = adSoyad;
        
        setTitle("RumMedic 2026 - Hasta Paneli - " + adSoyad);
        setSize(1000, 650);
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
        JMenu menuYardim = new JMenu("Yardım");
        
        JButton btnAnaSayfa = new JButton("Ana Sayfaya Dön");
        btnAnaSayfa.setFocusPainted(false);
        btnAnaSayfa.setBorderPainted(false);
        btnAnaSayfa.setContentAreaFilled(false);
        btnAnaSayfa.setOpaque(true);
        btnAnaSayfa.setBackground(new Color(240, 240, 240));
        
        btnAnaSayfa.addActionListener(e -> {
            new MainGUI().setVisible(true);
            dispose();
        });

        JMenuItem itemBilgi = new JMenuItem("Bu Sayfa Hakkında");
        itemBilgi.addActionListener(e -> JOptionPane.showMessageDialog(this, "Buradan randevu alabilir ve aktif randevularınızı görebilirsiniz."));

        menuYardim.add(itemBilgi);
        menuBar.add(menuYardim);
        menuBar.add(btnAnaSayfa);
        
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Randevu Paneli
        JPanel pnlRandevu = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlRandevu.setOpaque(false);
        pnlRandevu.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(9, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Randevu Al"));
        formPanel.setBackground(new Color(255, 255, 255, 200));

        JTextField txtAdSoyad = new JTextField(girisYapanAdSoyad);
        txtAdSoyad.setEditable(false);
        
        JTextField txtTc = new JTextField(girisYapanTc);
        txtTc.setEditable(false);

        cmbBranslar = new JComboBox<>();
        cmbDoktorlar = new JComboBox<>();
        cmbSaatler = new JComboBox<>();

        Set<String> branslar = new HashSet<>();
        for (Doktor d : VeriTabani.doktorListesi) {
            branslar.add(d.getBrans());
        }
        cmbBranslar.addItem("Branş Seçiniz...");
        for (String b : branslar) {
            cmbBranslar.addItem(b);
        }

        cmbBranslar.addActionListener(e -> {
            cmbDoktorlar.removeAllItems();
            String secilenBrans = (String) cmbBranslar.getSelectedItem();
            if (secilenBrans != null && !secilenBrans.equals("Branş Seçiniz...")) {
                for (Doktor d : VeriTabani.doktorListesi) {
                    if (d.getBrans().equals(secilenBrans)) {
                        cmbDoktorlar.addItem(d);
                    }
                }
            }
        });

        cmbDoktorlar.addActionListener(e -> saatleriGuncelle());

        cmbDoktorlar.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doktor) {
                    Doktor d = (Doktor) value;
                    if (d.isIzinde()) {
                        setText("<html><strike>" + d.toString() + "</strike></html>");
                        setForeground(Color.RED);
                    } else {
                        setText(d.toString());
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JButton btnOnayla = new JButton("Randevuyu Onayla");
        btnOnayla.setBackground(new Color(60, 179, 113));
        btnOnayla.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Ad Soyad:"));
        formPanel.add(txtAdSoyad);
        formPanel.add(new JLabel("TC Kimlik No:"));
        formPanel.add(txtTc);
        formPanel.add(new JLabel("Branş Seçiniz:"));
        formPanel.add(cmbBranslar);
        formPanel.add(new JLabel("Doktor Seçiniz:"));
        formPanel.add(cmbDoktorlar);
        formPanel.add(new JLabel("Randevu Saati:"));
        formPanel.add(cmbSaatler);
        formPanel.add(new JLabel(""));
        formPanel.add(btnOnayla);
        formPanel.add(new JLabel(""));

        JPanel tabloPanel = new JPanel(new BorderLayout());
        tabloPanel.setBorder(BorderFactory.createTitledBorder("Aktif Randevularım"));
        tabloPanel.setBackground(new Color(255, 255, 255, 200));

        String[] kolanlar = {"ID", "Doktor", "Branş", "Saat"};
        
        model = new DefaultTableModel(kolanlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tabloPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnIptal = new JButton("Seçili Randevuyu İptal Et");
        btnIptal.setBackground(new Color(220, 20, 60));
        btnIptal.setForeground(Color.WHITE);
        btnIptal.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Randevuyu iptal etmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int id = (int) model.getValueAt(modelRow, 0);
                    
                    VeriTabani.randevuIptal(id);
                    tabloYenile();
                    saatleriGuncelle();
                    JOptionPane.showMessageDialog(this, "Randevunuz başarıyla iptal edildi.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal etmek istediğiniz randevuyu tablodan seçiniz.");
            }
        });

        tabloPanel.add(btnIptal, BorderLayout.SOUTH);
        pnlRandevu.add(formPanel);
        pnlRandevu.add(tabloPanel);

        // Tahlillerim Paneli
        JPanel pnlTahliller = new JPanel(new BorderLayout());
        pnlTahliller.setOpaque(false);
        pnlTahliller.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] tahlilKolonlar = {"Tahlil Adı", "Sonuç", "Tarih"};
        
        tahlilModel = new DefaultTableModel(tahlilKolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tahlilTable = new JTable(tahlilModel);
        
        ArrayList<String[]> tahliller = VeriTabani.tahlilGetir(girisYapanTc);
        for(String[] row : tahliller) {
            tahlilModel.addRow(row);
        }

        pnlTahliller.add(new JScrollPane(tahlilTable), BorderLayout.CENTER);

        // Raporlarım Paneli
        JPanel pnlRaporlar = new JPanel(new BorderLayout(10, 10));
        pnlRaporlar.setOpaque(false);
        pnlRaporlar.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] raporKolonlar = {"Rapor İçeriği", "Tarih"};
        
        raporModel = new DefaultTableModel(raporKolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable raporTable = new JTable(raporModel);
        raporTable.setRowHeight(30);

        raporTable.getColumnModel().getColumn(0).setPreferredWidth(600);
        raporTable.getColumnModel().getColumn(1).setPreferredWidth(100);

        ArrayList<String[]> raporlar = VeriTabani.raporGetir(girisYapanTc);
        for(String[] row : raporlar) {
            raporModel.addRow(row);
        }

        JPanel pnlRaporAlt = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlRaporAlt.setOpaque(false);
        
        JButton btnRaporIndir = new JButton("Seçili Raporu İndir (PDF/TXT)");
        btnRaporIndir.setFont(new Font("Arial", Font.BOLD, 14));
        btnRaporIndir.setBackground(new Color(70, 130, 180));
        btnRaporIndir.setForeground(Color.WHITE);
        btnRaporIndir.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Varsa disket ikonu

        btnRaporIndir.addActionListener(e -> {
            int selectedRow = raporTable.getSelectedRow();
            if (selectedRow != -1) {
                String icerik = (String) raporModel.getValueAt(selectedRow, 0);
                String tarih = (String) raporModel.getValueAt(selectedRow, 1);
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Raporu Kaydet");
                fileChooser.setSelectedFile(new File("Rapor_" + girisYapanAdSoyad.replace(" ", "_") + "_" + tarih + ".txt"));
                
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                        writer.write("RUMMEDIC HASTANE RAPORU");
                        writer.newLine();
                        writer.write("=======================");
                        writer.newLine();
                        writer.newLine();
                        writer.write("Hasta Adı Soyadı: " + girisYapanAdSoyad);
                        writer.newLine();
                        writer.write("TC Kimlik No: " + girisYapanTc);
                        writer.newLine();
                        writer.write("Tarih: " + tarih);
                        writer.newLine();
                        writer.newLine();
                        writer.write("RAPOR İÇERİĞİ:");
                        writer.newLine();
                        writer.write("--------------------------------------------------");
                        writer.newLine();
                        writer.write(icerik);
                        writer.newLine();
                        writer.write("--------------------------------------------------");
                        writer.newLine();
                        writer.write("Bu belge RumMedic sistemi tarafından oluşturulmuştur.");
                        
                        JOptionPane.showMessageDialog(this, "Rapor başarıyla kaydedildi:\n" + fileToSave.getAbsolutePath());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Dosya kaydedilirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen indirmek istediğiniz raporu listeden seçiniz.");
            }
        });

        pnlRaporAlt.add(btnRaporIndir);
        pnlRaporlar.add(new JScrollPane(raporTable), BorderLayout.CENTER);
        pnlRaporlar.add(pnlRaporAlt, BorderLayout.SOUTH);

        tabbedPane.addTab("Randevu Al", pnlRandevu);
        tabbedPane.addTab("Tahlillerim", pnlTahliller);
        tabbedPane.addTab("Raporlarım", pnlRaporlar);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        tabloYenile();

        btnOnayla.addActionListener(e -> {
            Doktor secilenDr = (Doktor) cmbDoktorlar.getSelectedItem();
            String secilenSaat = (String) cmbSaatler.getSelectedItem();

            if (secilenDr == null || secilenSaat == null) {
                JOptionPane.showMessageDialog(this, "Lütfen doktor ve saat seçiniz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (secilenDr.isIzinde()) {
                JOptionPane.showMessageDialog(this, "Seçilen doktor şu anda izindedir. Lütfen başka bir doktor seçiniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (VeriTabani.randevuSaatiDoluMu(secilenDr, secilenSaat)) {
                JOptionPane.showMessageDialog(this, "Bu saatte randevu doludur. Lütfen başka bir saat seçiniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                saatleriGuncelle();
                return;
            }
            
            String[] isimler = girisYapanAdSoyad.split(" ", 2);
            String ad = isimler[0];
            String soyad = (isimler.length > 1) ? isimler[1] : "";

            VeriTabani.randevuOlustur(ad, soyad, girisYapanTc, secilenDr, secilenSaat);

            String fisMesaji = "RANDEVU ONAYLANDI\n\n" +
                    "Hasta: " + girisYapanAdSoyad + "\n" +
                    "TC: " + girisYapanTc + "\n" +
                    "Doktor: " + secilenDr.getAdSoyad() + "\n" +
                    "Branş: " + secilenDr.getBrans() + "\n" +
                    "Saat: " + secilenSaat + "\n\n" +
                    "Lütfen randevu saatinden 15 dk önce geliniz.";

            JOptionPane.showMessageDialog(this, fisMesaji, "Randevu Fişi", JOptionPane.INFORMATION_MESSAGE);

            tabloYenile();
            saatleriGuncelle();
        });
    }

    private void saatleriGuncelle() {
        cmbSaatler.removeAllItems();
        Doktor secilenDr = (Doktor) cmbDoktorlar.getSelectedItem();
        if (secilenDr == null) return;

        String[] tumSaatler = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00"};

        for (String saat : tumSaatler) {
            if (!VeriTabani.randevuSaatiDoluMu(secilenDr, saat)) {
                cmbSaatler.addItem(saat);
            }
        }
    }

    private void tabloYenile() {
        model.setRowCount(0);
        for (Randevu r : VeriTabani.randevuListesi) {
            if (r.getTcNo().equals(girisYapanTc)) {
                model.addRow(new Object[]{r.getId(), r.getDoktor().getAdSoyad(), r.getDoktor().getBrans(), r.getSaat()});
            }
        }
    }
}