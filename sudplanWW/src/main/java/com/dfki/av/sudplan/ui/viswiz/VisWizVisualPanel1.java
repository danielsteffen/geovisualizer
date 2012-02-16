/*
 *  LayerFactory.java 
 *
 *  Created by DFKI AV on 09.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.ui.viswiz;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VisWizVisualPanel1 extends JPanel {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisWizVisualPanel1.class);
    
    /**
     * Creates new form VisWizVisualPanel1
     */
    public VisWizVisualPanel1() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Select visualization technique";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pFileChooser = new javax.swing.JPanel();
        lFile = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dfki/av/sudplan/ui/viswiz/Bundle"); // NOI18N
        pFileChooser.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("VisWizVisualPanel1.pFileChooser.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lFile, bundle.getString("VisWizVisualPanel1.lFile.text")); // NOI18N

        txtFile.setText(bundle.getString("VisWizVisualPanel1.txtFile.text")); // NOI18N
        txtFile.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(btnFile, bundle.getString("VisWizVisualPanel1.btnFile.text")); // NOI18N
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pFileChooserLayout = new javax.swing.GroupLayout(pFileChooser);
        pFileChooser.setLayout(pFileChooserLayout);
        pFileChooserLayout.setHorizontalGroup(
            pFileChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFileChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFile, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFile)
                .addContainerGap())
        );
        pFileChooserLayout.setVerticalGroup(
            pFileChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFileChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pFileChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lFile)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(327, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("ESRI Shape-zip", "zip"));
        int retValue = jfc.showOpenDialog(this);

        if (retValue == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            if (file != null) {
                txtFile.setText(file.getAbsolutePath());
//                ShapefileLoader shapefileLoader = new ShapefileLoader(file);
//                shapefileLoader.execute();
            }
        } else {
            log.debug("No shp file selected.");
        }
    }//GEN-LAST:event_btnFileActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFile;
    private javax.swing.JLabel lFile;
    private javax.swing.JPanel pFileChooser;
    private javax.swing.JTextField txtFile;
    // End of variables declaration//GEN-END:variables
}
