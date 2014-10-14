/*
 * TFPAffineTransformation.java 
 *
 * Created by DFKI AV on 01.03.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.core.functions.ui;

import de.dfki.av.geovisualizer.core.AbstractTransferFunctionPanel;
import de.dfki.av.geovisualizer.core.functions.AffineTransformation;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class TFPAffineTransformation extends AbstractTransferFunctionPanel {

    /*
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TFPAffineTransformation.class);
    /**
     * The {@link AffineTransformation} for the UI.
     */
    private AffineTransformation function;

    /**
     * Creates new form TFPAffineTransformation
     */
    public TFPAffineTransformation(AffineTransformation function) {
        this.function = function;
        initComponents();
        jTextField1.setText(function.getScaleValue().toString());
        jTextField2.setText(function.getConstantValue().toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TFPAffineTransformation.class, "TFPAffineTransformation.jLabel1.text")); // NOI18N

        jTextField1.setColumns(10);
        jTextField1.setText(org.openide.util.NbBundle.getMessage(TFPAffineTransformation.class, "TFPAffineTransformation.jTextField1.text")); // NOI18N
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TFPAffineTransformation.class, "TFPAffineTransformation.jLabel2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TFPAffineTransformation.class, "TFPAffineTransformation.jLabel3.text")); // NOI18N

        jTextField2.setColumns(10);
        jTextField2.setText(org.openide.util.NbBundle.getMessage(TFPAffineTransformation.class, "TFPAffineTransformation.jTextField2.text")); // NOI18N
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s = jTextField1.getText();
        Double d = Double.parseDouble(s);
        LOG.debug("Setting scale value to {}", d.toString());
        function.setScaleValue(d);
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        String s = jTextField1.getText();
        Double d = Double.parseDouble(s);
        LOG.debug("Setting constant value to {}", d.toString());
        function.setConstantValue(d);
    }//GEN-LAST:event_jTextField2FocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return (String) jComboBox1.getSelectedItem();
    }

    @Override
    public boolean setAttributes(List<String[]> attributes) {
        for (Iterator<String[]> it = attributes.iterator(); it.hasNext();) {
            String[] attribute = it.next();
            if (!attribute[1].equalsIgnoreCase("String")) {
                jComboBox1.addItem(attribute[0]);
            }
        }
        return jComboBox1.getItemCount() > 0 ? true : false;
    }
}
