/*
 *  TFPColorrampCategorization.java 
 *
 *  Created by DFKI AV on 09.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.functions.ui;

import com.dfki.av.sudplan.vis.core.TFPanel;
import com.dfki.av.sudplan.vis.functions.ColorrampCategorization;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;

/**
 *
 * @author steffen
 */
public class TFPColorrampCategorization extends TFPanel {

    /**
     *
     */
    private ColorrampCategorization function;

    /**
     * Creates new form TFPColorrampCategorization
     */
    public TFPColorrampCategorization(final ColorrampCategorization f) {
        this.function = f;
        initComponents();
        jRadioButton1.setActionCommand(f.getClass().getName());
        jTextField1.setBackground(f.getStartColor());
        jTextField2.setBackground(f.getEndColor());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();

        jRadioButton1.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jRadioButton1.text")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jLabel2.text")); // NOI18N

        jTextField1.setColumns(6);
        jTextField1.setEditable(false);
        jTextField1.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jTextField1.text")); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
        });

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jLabel3.text")); // NOI18N

        jTextField2.setColumns(6);
        jTextField2.setEditable(false);
        jTextField2.setText(org.openide.util.NbBundle.getMessage(TFPColorrampCategorization.class, "TFPColorrampCategorization.jTextField2.text")); // NOI18N
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
        Color c = JColorChooser.showDialog(this, "Choose your color", function.getStartColor());
        if (c != null) {
            jTextField1.setBackground(c);
            function.setStartColor(c);
        }
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseClicked
        Color c = JColorChooser.showDialog(this, "Choose your color", function.getEndColor());
        if (c != null) {
            jTextField2.setBackground(c);
            function.setEndColor(c);
        }
    }//GEN-LAST:event_jTextField2MouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return (String) jComboBox1.getSelectedItem();
    }

    @Override
    public void setAttributes(List<String> attributes) {
        for (Iterator<String> it = attributes.iterator(); it.hasNext();) {
            String string = it.next();
            jComboBox1.addItem(string);
        }
    }

    @Override
    public void setButtonGroup(ButtonGroup bg) {
        bg.add(jRadioButton1);
    }

    @Override
    public void setActionListener(ActionListener l) {
        jRadioButton1.addActionListener(l);
    }

    @Override
    public void setSelected(boolean b) {
        jRadioButton1.setSelected(b);
    }
}
