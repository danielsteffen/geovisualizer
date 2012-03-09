/*
 *  VisParameterPanel.java 
 *
 *  Created by DFKI AV on 29.02.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis.wiz;

import com.dfki.av.sudplan.vis.algorithm.IVisParameter;
import com.dfki.av.sudplan.vis.algorithm.functions.*;
import com.dfki.av.sudplan.vis.wiz.tfpanels.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author steffen
 */
public class VisParameterPanel extends javax.swing.JPanel implements ActionListener {

    /*
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(VisParameterPanel.class);
    /**
     *
     */
    private IVisParameter visParameter;
    /**
     *
     */
    private Map<String, ITransferFunction> transferFunctionMap;
    /**
     *
     */
    private Map<String, JPanel> panelMap;
    /**
     *
     */
    private String selectedTransferFunction;

    /**
     * Creates new form VisParameterPanel
     */
    public VisParameterPanel(final IVisParameter param, final List<String> dataAttributes) {
        this.visParameter = param;
        this.transferFunctionMap = new HashMap<String, ITransferFunction>();
        this.panelMap = new HashMap<String, JPanel>();

        initComponents();

        //Create a panel for each transfer function.
        List<ITransferFunction> list = param.getTransferFunctions();
        for (Iterator<ITransferFunction> it = list.iterator(); it.hasNext();) {
            ITransferFunction function = it.next();
            if (function instanceof RedGreenColorrampTransferFunction) {
                RedGreenColorrampTransferFunction f = (RedGreenColorrampTransferFunction) function;
                JPanel panel = new TFPRedGreenColorrampTransferFunction(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else if (function instanceof ColorrampTransferFunction) {
                ColorrampTransferFunction f = (ColorrampTransferFunction) function;
                JPanel panel = new TFPColorrampTransferFunction(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else if (function instanceof ConstantColorTransferFunction) {
                ConstantColorTransferFunction f = (ConstantColorTransferFunction) function;
                JPanel panel = new TFPConstantColorTransferFunction(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else if (function instanceof IdentityFunction) {
                IdentityFunction f = (IdentityFunction) function;
                JPanel panel = new TFPIdentityFunction(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else if (function instanceof ScalarMultiplication) {
                ScalarMultiplication f = (ScalarMultiplication) function;
                JPanel panel = new TFPScalarMultiplication(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else if (function instanceof ConstantNumberTansferFunction) {
                ConstantNumberTansferFunction f = (ConstantNumberTansferFunction) function;
                JPanel panel = new TFPConstantNumberTransferFunction(f, dataAttributes, bgTransferFunctions, this);
                transferFunctionMap.put(f.getClass().getSimpleName(), f);
                panelMap.put(f.getClass().getSimpleName(), panel);
                this.add(panel);
                selectedTransferFunction = function.getClass().getSimpleName();
                visParameter.setSelectedTransferFunction(f);
            } else {
                log.debug("TransferFunction {} not supported by UI", function.getClass().getSimpleName());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTransferFunctions = new javax.swing.ButtonGroup();

        setAlignmentY(0.0F);
        setMinimumSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgTransferFunctions;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @return
     */
    public String getSelectedAttribute() {
//        log.debug("Selected transfer function {}", selectedTransferFunction);
        return ((TFPanel) panelMap.get(selectedTransferFunction)).getSelectedAttribute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ITransferFunction f = transferFunctionMap.get(e.getActionCommand());
        selectedTransferFunction = e.getActionCommand();
        log.debug("Setting transfer function {} of VisParameter {} ", selectedTransferFunction, visParameter.getName());
        visParameter.setSelectedTransferFunction(f);
    }
}
