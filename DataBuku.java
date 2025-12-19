/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pkgclass;

import java.sql.DriverManager;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author niakr
 */
public class DataBuku extends javax.swing.JFrame {
    Connection conn;
    DefaultTableModel model;
    /**
     * Creates new form DataBuku
     */
    public DataBuku() {
        initComponents();
        koneksiDatabase();
        aturTable();
        loadData("");
    }

    private void koneksiDatabase(){
       try{
       java.lang.Class.forName("com.mysql.cj.jdbc.Driver");
       String url = "jdbc:mysql://localhost:3306/Aplikasi_Data_Buku";
       String user = "root";
       String pass = "";
       conn = DriverManager.getConnection(url, user, pass);
       System.out.println("koneksi berhasil");
    } catch (Exception e){
      JOptionPane.showMessageDialog(this, "koneksi gagal:" + e.getMessage());
} 
     }
       
    
    private void aturTable(){
        model = new DefaultTableModel();
        model.addColumn("kode_buku");
        model.addColumn("judul_buku");
        model.addColumn("penulis");
        model.addColumn("penerbit");
        model.addColumn("tahun_terbit");
        TBLdatabuku.setModel(model);
    }
       
    
    private void loadData(String key){
        model = (DefaultTableModel) TBLdatabuku.getModel();
        model.setRowCount(0);
        
        try{
        String sql ="SELECT * FROM Data_Buku WHERE kode_buku LIKE ? OR judul_buku LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%"+ key + "%");
        pst.setString(2,"%"+ key + "%" );
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("kode_buku"),
                rs.getString("judul_buku"),
                rs.getString("penulis"),
                rs.getString("penerbit"),
                rs.getString("tahun_terbit")
            });
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
    }
  }
    
    
    private void simpanData() {
        try {
            String sql = "INSERT INTO Data_Buku (kode_buku, judul_buku, penulis, penerbit, tahun_terbit) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, TFkode.getText());
            pst.setString(2, TFjudul.getText());
            pst.setString(3, TFpenulis.getText());
            pst.setString(4, TFpenerbit.getText());
            pst.setString(5, CBtahun.getSelectedItem().toString());
           
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            loadData("");
            
            TFkode.setText("");
            TFjudul.setText("");
            TFpenulis.setText("");
            TFpenerbit.setText("");
            CBtahun.getSelectedIndex();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }
    
    private void editData(){
        try{
            String sql = "UPDATE Data_Buku set judul_buku=?, penulis=?, penerbit=?, tahun_terbit=? WHERE kode_buku=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, TFkode.getText());
            pst.setString(2, TFjudul.getText());
            pst.setString(3, TFpenulis.getText());
            pst.setString(4, TFpenerbit.getText());
            pst.setString(5, CBtahun.getSelectedItem().toString());
          
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!"); 
            loadData("");
        }catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }
    
    private void hapusData(){
        try{
           String sql = "DELETE FROM Data_Buku WHERE kode_buku=?";
           PreparedStatement pst = conn.prepareStatement(sql);
           pst.setString(1, TFkode.getText());
           pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!"); 
        loadData("");
        
        }catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
        } 
    }
    
    private void kosongkanField(){
        TFkode.setText("");
        TFjudul.setText("");
        TFpenulis.setText("");
        TFpenerbit.setText("");
        CBtahun.getSelectedIndex();
    }
    
    private void exportPDF() {
    try {
        // Format waktu untuk nama file
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String timestamp = sdf.format(new java.util.Date());

        // Default nama file
        String defaultFileName = "Data_Buku" + timestamp + ".pdf";

        // JFileChooser untuk memilih lokasi penyimpanan
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Simpan File PDF");
        chooser.setSelectedFile(new java.io.File(defaultFileName));

        int userSelection = chooser.showSaveDialog(this);

        if (userSelection != javax.swing.JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Export dibatalkan.");
            return;
        }

        java.io.File fileToSave = chooser.getSelectedFile();

        // Pastikan ext PDF
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
        }

        // Buat dokumen PDF
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(doc,
                new java.io.FileOutputStream(fileToSave));

        doc.open();

        // Buat tabel PDF
        com.itextpdf.text.pdf.PdfPTable pdfTable =
                new com.itextpdf.text.pdf.PdfPTable(model.getColumnCount());

        // Header
        for (int i = 0; i < model.getColumnCount(); i++) {
            pdfTable.addCell(model.getColumnName(i));
        }

        // Data rows
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                pdfTable.addCell(value == null ? "" : value.toString());
            }
        }

        // Masukkan tabel ke dokumen
        doc.add(pdfTable);
        doc.close();

        JOptionPane.showMessageDialog(this, "Export PDF Berhasil!\nTersimpan di:\n" + fileToSave);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan saat export PDF:\n" + e.getMessage());
    }
}
      
      
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TFkode = new javax.swing.JTextField();
        TFjudul = new javax.swing.JTextField();
        TFpenulis = new javax.swing.JTextField();
        TFpenerbit = new javax.swing.JTextField();
        CBtahun = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBLdatabuku = new javax.swing.JTable();
        BTNsimpan = new javax.swing.JButton();
        BTNhapus = new javax.swing.JButton();
        BTNreset = new javax.swing.JButton();
        BTNexit = new javax.swing.JButton();
        BTNedit = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtcari = new javax.swing.JTextField();
        BTNcetakpdf = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Aplikasi Pengolahaan Data Buku");

        jLabel2.setText("Kode Buku");

        jLabel3.setText(" Judul Buku");

        jLabel4.setText("Penulis");

        jLabel5.setText("Penerbit");

        jLabel6.setText("Tahun Terbit");

        CBtahun.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Tahun Terbit", "2021", "2022", "2023", "2024", "2025" }));

        TBLdatabuku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kode_Buku", "Judul_Buku", "Penulis", "Penerbit", "Tahun_Terbit"
            }
        ));
        TBLdatabuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLdatabukuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TBLdatabuku);

        BTNsimpan.setText("SIMPAN");
        BTNsimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNsimpanActionPerformed(evt);
            }
        });

        BTNhapus.setText("HAPUS");
        BTNhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNhapusActionPerformed(evt);
            }
        });

        BTNreset.setText("RESET");
        BTNreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNresetActionPerformed(evt);
            }
        });

        BTNexit.setText("EXIT");
        BTNexit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNexitActionPerformed(evt);
            }
        });

        BTNedit.setText("EDIT");
        BTNedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNeditActionPerformed(evt);
            }
        });

        jLabel7.setText("Cari");

        txtcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcariKeyReleased(evt);
            }
        });

        BTNcetakpdf.setText(" CETAK PDF");
        BTNcetakpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTNcetakpdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel4)
                                .addGap(97, 97, 97))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(77, 77, 77)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(TFpenulis)
                                        .addGap(62, 62, 62))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(TFpenerbit, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TFjudul, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(BTNhapus, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(BTNedit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TFkode)
                                    .addComponent(CBtahun, 0, 163, Short.MAX_VALUE)
                                    .addComponent(txtcari))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(BTNcetakpdf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BTNexit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(302, 302, 302)
                                .addComponent(BTNsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(316, 316, 316)
                                .addComponent(BTNreset, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(65, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(170, 170, 170))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TFkode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNsimpan))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(BTNedit)
                    .addComponent(TFjudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TFpenulis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNhapus))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TFpenerbit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNreset))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(CBtahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNexit))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTNcetakpdf))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BTNexitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNexitActionPerformed
        // TODO add your handling code here:
        int keluar = JOptionPane.showConfirmDialog(this,
            "Apakah yakin akan keluar?", "konfirmasi", JOptionPane.YES_NO_OPTION);
        if (keluar == JOptionPane.NO_OPTION);{
        System.exit(0);
    }
    }//GEN-LAST:event_BTNexitActionPerformed

    private void BTNsimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNsimpanActionPerformed
        // TODO add your handling code here:
        simpanData();
    }//GEN-LAST:event_BTNsimpanActionPerformed

    private void BTNeditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNeditActionPerformed
        // TODO add your handling code here:
        editData();
    }//GEN-LAST:event_BTNeditActionPerformed

    private void BTNhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNhapusActionPerformed
        // TODO add your handling code here:
        hapusData();
    }//GEN-LAST:event_BTNhapusActionPerformed

    private void BTNresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNresetActionPerformed
        // TODO add your handling code here:
          TFkode.setText("");
          TFjudul.setText("");
          TFpenulis.setText("");
          TFpenerbit.setText("");
          CBtahun.getSelectedIndex();
    }//GEN-LAST:event_BTNresetActionPerformed

    private void TBLdatabukuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLdatabukuMouseClicked
        // TODO add your handling code here:
        int row = TBLdatabuku.getSelectedRow();
        TFkode.setText(model.getValueAt(row, 0).toString());
        TFjudul.setText(model.getValueAt(row, 1).toString());
        TFpenulis.setText(model.getValueAt(row, 2).toString());
        TFpenerbit.setText(model.getValueAt(row, 3).toString());
        CBtahun.setSelectedItem(model.getValueAt(row, 4));
    }//GEN-LAST:event_TBLdatabukuMouseClicked

    private void txtcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcariKeyReleased
        // TODO add your handling code here:
        loadData(txtcari.getText());
    }//GEN-LAST:event_txtcariKeyReleased

    private void BTNcetakpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTNcetakpdfActionPerformed
        // TODO add your handling code here:
        exportPDF();
    }//GEN-LAST:event_BTNcetakpdfActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataBuku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTNcetakpdf;
    private javax.swing.JButton BTNedit;
    private javax.swing.JButton BTNexit;
    private javax.swing.JButton BTNhapus;
    private javax.swing.JButton BTNreset;
    private javax.swing.JButton BTNsimpan;
    private javax.swing.JComboBox<String> CBtahun;
    private javax.swing.JTable TBLdatabuku;
    private javax.swing.JTextField TFjudul;
    private javax.swing.JTextField TFkode;
    private javax.swing.JTextField TFpenerbit;
    private javax.swing.JTextField TFpenulis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtcari;
    // End of variables declaration//GEN-END:variables
}
