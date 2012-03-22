/*
 *  ParameterMappingPanel.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.core.IVisParameter;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

public final class ParameterMappingPanel extends JPanel {

    private IVisAlgorithm visAlgorithm;
    private String[] attributes;

    /**
     * Creates new form ParameterMappingPanel
     */
    public ParameterMappingPanel() {
        this.attributes = new String[]{};
        this.visAlgorithm = null;
        initComponents();
    }

    @Override
    public String getName() {
        return "Parameter Mapping";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        setPreferredSize(new java.awt.Dimension(621, 408));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ParameterMappingPanel.class, "ParameterMappingPanel.jLabel1.text")); // NOI18N
        add(jLabel1);
        add(jTabbedPane1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @return
     */
    public String[] getAttributes() {

        for (int tabid = 0, j = 0; tabid < jTabbedPane1.getTabCount(); tabid++) {
            Component c = jTabbedPane1.getComponent(tabid);
            if (c instanceof VisParameterPanel) {
                VisParameterPanel visParameterPanel = (VisParameterPanel) c;
                attributes[j] = visParameterPanel.getSelectedAttribute();
                j++;
            } else {
                System.out.println("No VisParameterPanel.");
            }
        }
        return this.attributes;
    }

    /**
     *
     * @param i
     * @param attributes
     */
    public void setSelectedVisualization(IVisAlgorithm i, List<String[]> attributes) {
        this.visAlgorithm = i;
        this.attributes = new String[visAlgorithm.getVisParameters().size()];
        jTabbedPane1.removeAll();
        if (visAlgorithm.getVisParameters().isEmpty()) {
            jTabbedPane1.setVisible(false);
            jLabel1.setVisible(true);
        } else {
            jTabbedPane1.setVisible(true);
            jLabel1.setVisible(false);
        }

        for (Iterator<IVisParameter> it = visAlgorithm.getVisParameters().iterator(); it.hasNext();) {
            IVisParameter p = it.next();
            VisParameterPanel panel = new VisParameterPanel(p, attributes);
            jTabbedPane1.add(p.getName(), panel);
        }
        this.updateUI();
    }
}
