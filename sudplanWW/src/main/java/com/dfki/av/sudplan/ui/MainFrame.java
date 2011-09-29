/*
 * MainFrame.java
 *
 * Created on 14.09.2011, 15:27:50
 */
package com.dfki.av.sudplan.ui;

import com.dfki.av.sudplan.vis.LayerAction;
import com.dfki.av.sudplan.vis.VisualizationPanel;
import gov.nasa.worldwind.layers.Layer;
import java.awt.Dimension;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class MainFrame extends javax.swing.JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private Dimension canvasSize;
    protected VisualizationPanel wwPanel;

    /** Creates new form MainFrame */
    public MainFrame() {
        this.canvasSize = new Dimension(1200, 800);
        this.wwPanel = new VisualizationPanel(canvasSize);
        this.wwPanel.setPreferredSize(canvasSize);
        initComponents();
        this.fill();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dGoTo = new javax.swing.JDialog();
        pGoTo = new javax.swing.JPanel();
        lLatitude = new javax.swing.JLabel();
        lLongitude = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        btnCancelGoToDialoag = new javax.swing.JButton();
        btnGo = new javax.swing.JButton();
        jopAddServer = new javax.swing.JOptionPane();
        tbMain = new javax.swing.JToolBar();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnHome = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnGoStockholm = new javax.swing.JButton();
        btnGoLinz = new javax.swing.JButton();
        btnGoWuppertal = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        pVisualization = new javax.swing.JPanel();
        mbMain = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        mLoad = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mEdit = new javax.swing.JMenu();
        miGoto = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miAddServer = new javax.swing.JMenuItem();
        mView = new javax.swing.JMenu();
        mLayers = new javax.swing.JMenu();
        mHelp = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle"); // NOI18N
        dGoTo.setTitle(bundle.getString("MainFrame.dGoTo.title")); // NOI18N
        dGoTo.setAlwaysOnTop(true);
        dGoTo.setMinimumSize(new java.awt.Dimension(230, 150));
        dGoTo.setResizable(false);

        pGoTo.setMaximumSize(new java.awt.Dimension(200, 100));

        lLatitude.setLabelFor(txtLatitude);
        lLatitude.setText(bundle.getString("MainFrame.lLatitude.text")); // NOI18N

        lLongitude.setLabelFor(txtLongitude);
        lLongitude.setText(bundle.getString("MainFrame.lLongitude.text")); // NOI18N

        txtLatitude.setText(bundle.getString("MainFrame.txtLatitude.text")); // NOI18N

        txtLongitude.setText(bundle.getString("MainFrame.txtLongitude.text")); // NOI18N

        javax.swing.GroupLayout pGoToLayout = new javax.swing.GroupLayout(pGoTo);
        pGoTo.setLayout(pGoToLayout);
        pGoToLayout.setHorizontalGroup(
            pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lLongitude)
                    .addComponent(lLatitude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLongitude, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                .addContainerGap())
        );
        pGoToLayout.setVerticalGroup(
            pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lLatitude)
                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lLongitude)
                    .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancelGoToDialoag.setText(bundle.getString("MainFrame.btnCancelGoToDialoag.text")); // NOI18N
        btnCancelGoToDialoag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelGoToDialoagActionPerformed(evt);
            }
        });

        btnGo.setText(bundle.getString("MainFrame.btnGo.text")); // NOI18N
        btnGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dGoToLayout = new javax.swing.GroupLayout(dGoTo.getContentPane());
        dGoTo.getContentPane().setLayout(dGoToLayout);
        dGoToLayout.setHorizontalGroup(
            dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dGoToLayout.createSequentialGroup()
                        .addComponent(btnGo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelGoToDialoag))
                    .addComponent(pGoTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dGoToLayout.setVerticalGroup(
            dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pGoTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelGoToDialoag)
                    .addComponent(btnGo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        tbMain.setFloatable(false);
        tbMain.setRollover(true);
        tbMain.add(filler3);

        btnHome.setText(bundle.getString("MainFrame.btnHome.text")); // NOI18N
        btnHome.setFocusable(false);
        btnHome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        tbMain.add(btnHome);
        tbMain.add(filler1);

        btnGoStockholm.setText(bundle.getString("MainFrame.btnGoStockholm.text")); // NOI18N
        btnGoStockholm.setToolTipText(bundle.getString("MainFrame.btnGoStockholm.toolTipText")); // NOI18N
        btnGoStockholm.setFocusable(false);
        btnGoStockholm.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoStockholm.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoStockholm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoStockholmActionPerformed(evt);
            }
        });
        tbMain.add(btnGoStockholm);

        btnGoLinz.setText(bundle.getString("MainFrame.btnGoLinz.text")); // NOI18N
        btnGoLinz.setToolTipText(bundle.getString("MainFrame.btnGoLinz.toolTipText")); // NOI18N
        btnGoLinz.setFocusable(false);
        btnGoLinz.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoLinz.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoLinz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoLinzActionPerformed(evt);
            }
        });
        tbMain.add(btnGoLinz);

        btnGoWuppertal.setText(bundle.getString("MainFrame.btnGoWuppertal.text")); // NOI18N
        btnGoWuppertal.setToolTipText(bundle.getString("MainFrame.btnGoWuppertal.toolTipText")); // NOI18N
        btnGoWuppertal.setFocusable(false);
        btnGoWuppertal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoWuppertal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoWuppertal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoWuppertalActionPerformed(evt);
            }
        });
        tbMain.add(btnGoWuppertal);
        tbMain.add(filler2);

        pMain.setBackground(new java.awt.Color(102, 102, 255));
        pMain.setPreferredSize(new java.awt.Dimension(800, 600));

        jButton1.setText(bundle.getString("MainFrame.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MainFrame.jPanel2.border.title"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 249, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        pVisualization.setLayout(new java.awt.BorderLayout());

        pVisualization.add(wwPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(pVisualization);

        javax.swing.GroupLayout pMainLayout = new javax.swing.GroupLayout(pMain);
        pMain.setLayout(pMainLayout);
        pMainLayout.setHorizontalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        );
        pMainLayout.setVerticalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );

        mFile.setText(bundle.getString("MainFrame.mFile.text")); // NOI18N

        mLoad.setText(bundle.getString("MainFrame.mLoad.text")); // NOI18N
        mLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mLoadActionPerformed(evt);
            }
        });
        mFile.add(mLoad);
        mFile.add(jSeparator2);

        miExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        miExit.setText(bundle.getString("MainFrame.miExit.text")); // NOI18N
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        mFile.add(miExit);

        mbMain.add(mFile);

        mEdit.setText(bundle.getString("MainFrame.mEdit.text")); // NOI18N

        miGoto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        miGoto.setText(bundle.getString("MainFrame.miGoto.text")); // NOI18N
        miGoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoActionPerformed(evt);
            }
        });
        mEdit.add(miGoto);
        mEdit.add(jSeparator1);

        miAddServer.setText(bundle.getString("MainFrame.miAddServer.text")); // NOI18N
        miAddServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddServerActionPerformed(evt);
            }
        });
        mEdit.add(miAddServer);

        mbMain.add(mEdit);

        mView.setText(bundle.getString("MainFrame.mView.text")); // NOI18N

        mLayers.setText(bundle.getString("MainFrame.mLayers.text")); // NOI18N
        mView.add(mLayers);

        mbMain.add(mView);

        mHelp.setText(bundle.getString("MainFrame.mHelp.text")); // NOI18N

        miAbout.setText(bundle.getString("MainFrame.miAbout.text")); // NOI18N
        miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAboutActionPerformed(evt);
            }
        });
        mHelp.add(miAbout);

        mbMain.add(mHelp);

        setJMenuBar(mbMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
            .addComponent(tbMain, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbMain, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pMain, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoStockholmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoStockholmActionPerformed
        wwPanel.goTo(59.328, 18.047, 20000.0, true);
    }//GEN-LAST:event_btnGoStockholmActionPerformed

    private void btnGoLinzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoLinzActionPerformed
        wwPanel.goTo(48.2323, 14.3350, 20000.0, true);
    }//GEN-LAST:event_btnGoLinzActionPerformed

    private void btnGoWuppertalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoWuppertalActionPerformed
        wwPanel.goTo(51.2665, 7.1832, 20000.0, true);
    }//GEN-LAST:event_btnGoWuppertalActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void miGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGotoActionPerformed
        dGoTo.setVisible(true);
    }//GEN-LAST:event_miGotoActionPerformed

    private void btnCancelGoToDialoagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelGoToDialoagActionPerformed
        dGoTo.setVisible(false);
    }//GEN-LAST:event_btnCancelGoToDialoagActionPerformed

    private void btnGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoActionPerformed
        try {
            double lat = Double.parseDouble(txtLatitude.getText());
            double lon = Double.parseDouble(txtLongitude.getText());
            wwPanel.goTo(lat, lon, 20000.0, true);
            dGoTo.setVisible(false);
        } catch (NumberFormatException nfe) {
            if (log.isWarnEnabled()) {
                log.warn("The content of the \"latitude\" and \"longitude\" component must be a double value." );
            }
        } 
    }//GEN-LAST:event_btnGoActionPerformed

    private void miAddServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddServerActionPerformed
        // TODO: i18n
        String ret = JOptionPane.showInputDialog(this, "Server URL", "Add Server ...", JOptionPane.QUESTION_MESSAGE);
        if (ret != null && !ret.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Connecting to {}", ret);
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("The input for the server URL is empty or null.");
            }
        }
    }//GEN-LAST:event_miAddServerActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        wwPanel.goToHome();
    }//GEN-LAST:event_btnHomeActionPerformed

    private void miAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAboutActionPerformed
        JOptionPane.showMessageDialog(this, "This is the sudplan application. ;)", "About Sudplan", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_miAboutActionPerformed

    private void mLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLoadActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("ESRI Shapefile (*.shp)", "shp"));
        int ret = fc.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {

            return;
        }

//        Thread t = new WorkerThread(fc.getSelectedFile(), this);
//        t.start();
//        wwPanel.getWwd().setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }//GEN-LAST:event_mLoadActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("ESRI Shapefile (*.shp)", "shp"));
        int ret = fc.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void fill() {
        
        for (Layer layer : wwPanel.getWwd().getModel().getLayers()) {
            LayerAction action = new LayerAction(layer, wwPanel.getWwd(), layer.isEnabled());
            JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(action);
            cbmi.setSelected(action.isSelected());
            cbmi.setName(layer.getName());
            this.mLayers.add(cbmi);
        }
    }

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelGoToDialoag;
    private javax.swing.JButton btnGo;
    private javax.swing.JButton btnGoLinz;
    private javax.swing.JButton btnGoStockholm;
    private javax.swing.JButton btnGoWuppertal;
    private javax.swing.JButton btnHome;
    private javax.swing.JDialog dGoTo;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JOptionPane jopAddServer;
    private javax.swing.JLabel lLatitude;
    private javax.swing.JLabel lLongitude;
    private javax.swing.JMenu mEdit;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mLayers;
    private javax.swing.JMenuItem mLoad;
    private javax.swing.JMenu mView;
    private javax.swing.JMenuBar mbMain;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miAddServer;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miGoto;
    private javax.swing.JPanel pGoTo;
    private javax.swing.JPanel pMain;
    private javax.swing.JPanel pVisualization;
    private javax.swing.JToolBar tbMain;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    // End of variables declaration//GEN-END:variables
}