package View;

import Controller.Doktor;
import Controller.Randevu;
import Model.VeriTabani;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DoktorGUI extends JFrame {

    private JLabel lblIstatistik;

    public DoktorGUI() {
        setTitle("RumMedic 2026 - Yönetici Paneli");
        setSize(1000, 700);
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
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuYardim = new JMenu("Yardım");
        JMenu menuCikis = new JMenu("Anasayfa");

        JMenuItem itemInfo = new JMenuItem("Panel Bilgisi");
        itemInfo.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Yönetici Paneli Bilgilendirme:\n\n" +
                        "- Doktor ekleyebilir, silebilir ve izin durumlarını yönetebilirsiniz.\n" +
                        "- Randevuları görüntüleyebilir ve iptal edebilirsiniz.\n" +
                        "- İstatistikleri takip edebilirsiniz.",
                "Panel Bilgisi", JOptionPane.INFORMATION_MESSAGE));

        JMenuItem itemLogout = new JMenuItem("Geri Dön");
        itemLogout.addActionListener(e -> {
            new MainGUI().setVisible(true);
            dispose();
        });

        menuYardim.add(itemInfo);
        menuCikis.add(itemLogout);
        menuBar.add(menuYardim);
        menuBar.add(menuCikis);
        setJMenuBar(menuBar);

        JPanel pnlUstBilgi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlUstBilgi.setBackground(new Color(230, 240, 255));
        pnlUstBilgi.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        lblIstatistik = new JLabel();
        lblIstatistik.setFont(new Font("Arial", Font.BOLD, 14));
        pnlUstBilgi.add(lblIstatistik);
        mainPanel.add(pnlUstBilgi, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel pnlDoktorlar = new JPanel(new BorderLayout());
        pnlDoktorlar.setOpaque(false);

        String[] drKolonlar = {"Doktor Adı", "Branşı", "Durum"};
        DefaultTableModel drModel = new DefaultTableModel(drKolonlar, 0);
        JTable drTable = new JTable(drModel);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(drModel);
        drTable.setRowSorter(sorter);

        JPanel pnlDrIslemler = new JPanel(new FlowLayout());
        pnlDrIslemler.setBackground(new Color(240, 240, 240));

        JTextField txtDrAd = new JTextField(15);
        JTextField txtDrBrans = new JTextField(15);
        JButton btnEkle = new JButton("Ekle");
        JButton btnSil = new JButton("Sil");
        JButton btnIzin = new JButton("İzin Durumu Değiştir");

        btnEkle.setBackground(new Color(60, 179, 113)); btnEkle.setForeground(Color.WHITE);
        btnSil.setBackground(new Color(220, 20, 60)); btnSil.setForeground(Color.WHITE);
        btnIzin.setBackground(new Color(70, 130, 180)); btnIzin.setForeground(Color.WHITE);

        btnEkle.addActionListener(e -> {
            if(!txtDrAd.getText().isEmpty() && !txtDrBrans.getText().isEmpty()){
                VeriTabani.doktorEkle(txtDrAd.getText(), txtDrBrans.getText());
                drListele(drModel);
                istatistikGuncelle();
                txtDrAd.setText(""); txtDrBrans.setText("");
                JOptionPane.showMessageDialog(this, "Doktor Eklendi.");
            }
        });

        btnSil.addActionListener(e -> {
            int[] selectedRows = drTable.getSelectedRows();
            if (selectedRows.length > 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Seçili " + selectedRows.length + " doktoru silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ArrayList<Integer> indicesToDelete = new ArrayList<>();
                    for (int row : selectedRows) {
                        indicesToDelete.add(drTable.convertRowIndexToModel(row));
                    }
                    VeriTabani.topluDoktorSil(indicesToDelete);
                    drListele(drModel);
                    istatistikGuncelle();
                    JOptionPane.showMessageDialog(this, "Seçili doktorlar silindi.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silinecek doktor(ları) seçiniz.");
            }
        });

        btnIzin.addActionListener(e -> {
            int selectedRow = drTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = drTable.convertRowIndexToModel(selectedRow);
                Doktor d = VeriTabani.doktorListesi.get(modelRow);
                d.setIzinde(!d.isIzinde());
                drListele(drModel);
                JOptionPane.showMessageDialog(this, "Durum güncellendi: " + (d.isIzinde() ? "İZİNDE" : "AKTİF"));
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir doktor seçiniz.");
            }
        });

        pnlDrIslemler.add(new JLabel("Ad Soyad:"));
        pnlDrIslemler.add(txtDrAd);
        pnlDrIslemler.add(new JLabel("Branş:"));
        pnlDrIslemler.add(txtDrBrans);
        pnlDrIslemler.add(btnEkle);
        pnlDrIslemler.add(btnSil);
        pnlDrIslemler.add(btnIzin);

        pnlDoktorlar.add(new JScrollPane(drTable), BorderLayout.CENTER);
        pnlDoktorlar.add(pnlDrIslemler, BorderLayout.SOUTH);

        JPanel pnlHastalar = new JPanel(new BorderLayout());
        String[] rKolonlar = {"Hasta Ad", "Hasta Soyad", "TCKN", "Doktor", "Branş", "Saat"};
        DefaultTableModel rModel = new DefaultTableModel(rKolonlar, 0);
        JTable rTable = new JTable(rModel);

        JPanel pnlRandevuIslem = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRandevuIptal = new JButton("Seçili Randevuyu İptal Et");
        btnRandevuIptal.setBackground(Color.RED);
        btnRandevuIptal.setForeground(Color.WHITE);

        btnRandevuIptal.addActionListener(e -> {
            int[] selectedRows = rTable.getSelectedRows();
            if (selectedRows.length > 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Seçili " + selectedRows.length + " randevuyu iptal etmek istiyor musunuz?", "Onay", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ArrayList<Integer> indicesToDelete = new ArrayList<>();
                    for (int row : selectedRows) {
                        indicesToDelete.add(row);
                    }
                    VeriTabani.topluRandevuSil(indicesToDelete);
                    randevuListele(rModel);
                    istatistikGuncelle();
                    JOptionPane.showMessageDialog(this, "Seçili randevular iptal edildi.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen iptal edilecek randevu(ları) seçiniz.");
            }
        });

        pnlRandevuIslem.add(btnRandevuIptal);
        pnlHastalar.add(new JScrollPane(rTable), BorderLayout.CENTER);
        pnlHastalar.add(pnlRandevuIslem, BorderLayout.SOUTH);

        tabbedPane.addTab("Doktor Yönetimi", pnlDoktorlar);
        tabbedPane.addTab("Randevu Yönetimi", pnlHastalar);

        tabbedPane.addChangeListener(e -> {
            drListele(drModel);
            randevuListele(rModel);
            istatistikGuncelle();
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        drListele(drModel);
        randevuListele(rModel);
        istatistikGuncelle();
    }

    private void drListele(DefaultTableModel model) {
        model.setRowCount(0);
        VeriTabani.doktorListesi.sort((d1, d2) -> d1.getBrans().compareToIgnoreCase(d2.getBrans()));

        for(Doktor d : VeriTabani.doktorListesi){
            model.addRow(new Object[]{d.getAdSoyad(), d.getBrans(), d.isIzinde() ? "İZİNDE" : "AKTİF"});
        }
    }

    private void randevuListele(DefaultTableModel model) {
        model.setRowCount(0);
        for (Randevu r : VeriTabani.randevuListesi) {
            String tc = r.getTcNo();
            String maskeliTc = tc;
            if (tc != null && tc.length() > 3) {
                StringBuilder sb = new StringBuilder();
                sb.append(tc.substring(0, 3));
                for (int i = 3; i < tc.length(); i++) {
                    sb.append("*");
                }
                maskeliTc = sb.toString();
            }
            model.addRow(new Object[]{r.getHastaAd(), r.getHastaSoyad(), maskeliTc, r.getDoktor().getAdSoyad(), r.getDoktor().getBrans(), r.getSaat()});
        }
    }

    private void istatistikGuncelle() {
        int toplamDr = VeriTabani.doktorListesi.size();
        int toplamRandevu = VeriTabani.randevuListesi.size();
        long aktifDr = VeriTabani.doktorListesi.stream().filter(d -> !d.isIzinde()).count();

        lblIstatistik.setText("   SİSTEM DURUMU:  Toplam Doktor: " + toplamDr + "  |  Aktif Doktor: " + aktifDr + "  |  Toplam Randevu: " + toplamRandevu);
    }
}