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
import java.util.HashSet;
import java.util.Set;

public class HastaGUI extends JFrame {

    private JComboBox<String> cmbBranslar;
    private JComboBox<Doktor> cmbDoktorlar;
    private JComboBox<String> cmbSaatler;
    private DefaultTableModel model;

    public HastaGUI() {
        setTitle("RumMedic 2026 - Hasta İşlemleri");
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
        JMenu menuCikis = new JMenu("Seçenekler");

        JMenuItem itemBilgi = new JMenuItem("Bu Sayfa Hakkında");
        itemBilgi.addActionListener(e -> JOptionPane.showMessageDialog(this, "Buradan randevu alabilir ve aktif randevularınızı görebilirsiniz."));

        JMenuItem itemCikis = new JMenuItem("Ana Menüye Dön");
        itemCikis.addActionListener(e -> {
            new MainGUI().setVisible(true);
            dispose();
        });

        menuYardim.add(itemBilgi);
        menuCikis.add(itemCikis);
        menuBar.add(menuYardim);
        menuBar.add(menuCikis);
        setJMenuBar(menuBar);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(9, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Randevu Al"));
        formPanel.setBackground(new Color(255, 255, 255, 200));

        JTextField txtAd = new JTextField();
        JTextField txtSoyad = new JTextField();
        JTextField txtTc = new JTextField();
        txtTc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
                if (txtTc.getText().length() >= 11 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    if (txtTc.getSelectedText() == null || txtTc.getSelectedText().isEmpty()) {
                        evt.consume();
                    }
                }
            }
        });

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

        formPanel.add(new JLabel("Adınız:"));
        formPanel.add(txtAd);
        formPanel.add(new JLabel("Soyadınız:"));
        formPanel.add(txtSoyad);
        formPanel.add(new JLabel("TC Kimlik No (11 Hane):"));
        formPanel.add(txtTc);
        formPanel.add(new JLabel("Branş Seçiniz:"));
        formPanel.add(cmbBranslar);
        formPanel.add(new JLabel("Doktor Seçiniz:"));
        formPanel.add(cmbDoktorlar);
        formPanel.add(new JLabel("Randevu Saati:"));
        formPanel.add(cmbSaatler);
        formPanel.add(new JLabel(""));
        formPanel.add(btnOnayla);

        JPanel tabloPanel = new JPanel(new BorderLayout());
        tabloPanel.setBorder(BorderFactory.createTitledBorder("Aktif Randevular"));
        tabloPanel.setBackground(new Color(255, 255, 255, 200));

        String[] kolanlar = {"Hasta Adı", "Soyadı", "Doktor", "Branş", "Saat"};
        model = new DefaultTableModel(kolanlar, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        tabloPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnIptal = new JButton("Seçili Randevuyu İptal Et");
        btnIptal.setBackground(new Color(220, 20, 60));
        btnIptal.setForeground(Color.WHITE);
        btnIptal.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String tc = JOptionPane.showInputDialog(this, "İptal işlemi için TC Kimlik No giriniz:");
                if (tc != null) {
                    Randevu r = VeriTabani.randevuListesi.get(selectedRow);
                    if (r.getTcNo().equals(tc)) {
                        VeriTabani.randevuIptal(selectedRow);
                        tabloYenile();
                        saatleriGuncelle();
                        JOptionPane.showMessageDialog(this, "Randevunuz başarıyla iptal edildi.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Hatalı TC Kimlik No! İptal işlemi gerçekleştirilemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal etmek istediğiniz randevuyu tablodan seçiniz.");
            }
        });

        tabloPanel.add(btnIptal, BorderLayout.SOUTH);

        tabloYenile();

        centerPanel.add(formPanel);
        centerPanel.add(tabloPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        btnOnayla.addActionListener(e -> {
            String ad = txtAd.getText();
            String soyad = txtSoyad.getText();
            String tc = txtTc.getText();
            Doktor secilenDr = (Doktor) cmbDoktorlar.getSelectedItem();
            String secilenSaat = (String) cmbSaatler.getSelectedItem();


            if (ad.isEmpty() || soyad.isEmpty() || tc.isEmpty() || secilenDr == null || secilenSaat == null) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!tc.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "TC Kimlik No 11 haneli rakamlardan oluşmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
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

            VeriTabani.randevuOlustur(ad, soyad, tc, secilenDr, secilenSaat);

            String fisMesaji = "RANDEVU ONAYLANDI\n\n" +
                    "Hasta: " + ad + " " + soyad + "\n" +
                    "TC: " + tc + "\n" +
                    "Doktor: " + secilenDr.getAdSoyad() + "\n" +
                    "Branş: " + secilenDr.getBrans() + "\n" +
                    "Saat: " + secilenSaat + "\n\n" +
                    "Lütfen randevu saatinden 15 dk önce geliniz.";

            JOptionPane.showMessageDialog(this, fisMesaji, "Randevu Fişi", JOptionPane.INFORMATION_MESSAGE);

            tabloYenile();
            saatleriGuncelle();
            txtAd.setText(""); txtSoyad.setText(""); txtTc.setText("");
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
            model.addRow(new Object[]{r.getHastaAd(), r.getHastaSoyad(), r.getDoktor().getAdSoyad(), r.getDoktor().getBrans(), r.getSaat()});
        }
    }
}