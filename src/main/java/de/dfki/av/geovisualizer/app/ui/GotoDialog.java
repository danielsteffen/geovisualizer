/*
 *  GotoDialog.java 
 *
 *  Created by DFKI AV on 29.11.2012.
 *  Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import de.dfki.av.geovisualizer.app.Configuration;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class GotoDialog extends javax.swing.JDialog {

    /*
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(GotoDialog.class);
    /**
     * The {@link WorldWindow}.
     */
    private final WorldWindow worldWindow;

    /**
     * Creates new form GotoDialog
     */
    public GotoDialog(java.awt.Frame parent, final WorldWindow worldWindow) {
        super(parent, false);
        this.worldWindow = worldWindow;
        this.setIconImage(Configuration.GEOVISUALIZER_ICON);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        latitudeLabel = new javax.swing.JLabel();
        longitudeLabel = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.title")); // NOI18N

        latitudeLabel.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.latitudeLabel.text")); // NOI18N

        longitudeLabel.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.longitudeLabel.text")); // NOI18N

        txtLatitude.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.txtLatitude.text")); // NOI18N

        txtLongitude.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.txtLongitude.text")); // NOI18N

        okButton.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(GotoDialog.class, "GotoDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(latitudeLabel)
                            .addComponent(longitudeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLongitude, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                            .addComponent(txtLatitude)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(latitudeLabel)
                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(longitudeLabel)
                    .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            double lat = Double.parseDouble(txtLatitude.getText());
            double lon = Double.parseDouble(txtLongitude.getText());
            Position pos = Position.fromDegrees(lat, lon);
            View view = worldWindow.getView();
            view.goTo(pos, 5000.0);            
        } catch (NumberFormatException nfe) {
            String msg = "No valid value for \"latitude\" and \"longitude\".\n "
                    + "Must be a number value.";
            log.error(msg);
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);            
        }        
        resetDialog();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        resetDialog();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Reset the dialog fields.
     */
    private void resetDialog(){
        this.txtLatitude.setText("");
        this.txtLongitude.setText("");
        this.setVisible(false);        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel latitudeLabel;
    private javax.swing.JLabel longitudeLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    // End of variables declaration//GEN-END:variables
}
