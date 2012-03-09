/*
 *  TFPIdentityFunction.java 
 *
 *  Created by DFKI AV on 01.03.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz.tfpanels;

import com.dfki.av.sudplan.vis.algorithm.functions.IdentityFunction;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;

/**
 *
 * @author steffen
 */
public class TFPIdentityFunction extends javax.swing.JPanel implements TFPanel{

    /**
     * Creates new form TFPIdentityFunction
     */
    public TFPIdentityFunction(final IdentityFunction f, final List<String> attributes, final ButtonGroup bg, ActionListener l) {
        
        initComponents();
        for (Iterator<String> it = attributes.iterator(); it.hasNext();) {
            String string = it.next();
            jComboBox1.addItem(string);
        }
        // Finally, add the jRadioButton to the buttongroup
        jRadioButton.setActionCommand(f.getClass().getSimpleName());
        jRadioButton.addActionListener(l);
//        jRadioButton.setSelected(true);
        bg.add(jRadioButton);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jRadioButton = new javax.swing.JRadioButton();

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jRadioButton.setText(org.openide.util.NbBundle.getMessage(TFPIdentityFunction.class, "TFPIdentityFunction.jRadioButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(197, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        
    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JRadioButton jRadioButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getSelectedAttribute() {
        return (String)jComboBox1.getSelectedItem();
    }
}
