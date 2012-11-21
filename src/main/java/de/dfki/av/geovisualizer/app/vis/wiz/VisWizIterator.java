/*
 *  VisWizIterator.java 
 *
 *  Created by DFKI AV on 01.01.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis.wiz;

import java.awt.Component;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

public final class VisWizIterator implements WizardDescriptor.Iterator {

    // To invoke this wizard, copy-paste and run the following code, e.g. from
    // SomeAction.performAction():
    /*
     * WizardDescriptor.Iterator iterator = new VisWizIterator();
     * WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator); //
     * {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
     * // {1} will be replaced by WizardDescriptor.Iterator.name()
     * wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
     * wizardDescriptor.setTitle("Your wizard dialog title here"); Dialog dialog
     * = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
     * dialog.setVisible(true); dialog.toFront(); boolean cancelled =
     * wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION; if
     * (!cancelled) { // do something }
     */
    private int index;
    /**
     * Whether the {@link DataSourceSelectionPanel} should be included or not.
     */
    private boolean enableDataSourceSelection;
    /**
     * The 
     */
    private WizardDescriptor.Panel[] panels;

    /**
     * Constructor. Creates a VisWiz with {@link DataSourceSelectionPanel}.
     */
    public VisWizIterator() {
        this(true);
    }
    
    /**
     * 
     * @param enable 
     */
    public VisWizIterator(boolean enable){
        super();
        this.enableDataSourceSelection = enable;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            if (enableDataSourceSelection) {
                panels = new WizardDescriptor.Panel[]{
                    new DataSourceSelectionController(),
                    new AttributeSelectionController(),
                    new VisualizationSelectionController(),
                    new ParameterMappingController()
                };
            } else {
                panels = new WizardDescriptor.Panel[]{
                    new AttributeSelectionController(),
                    new VisualizationSelectionController(),
                    new ParameterMappingController()
                };
            }

            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.FALSE);
                }
            }
        }
        return panels;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
     * private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
     * // or can use ChangeSupport in NB 6.0 public final void
     * addChangeListener(ChangeListener l) { synchronized (listeners) {
     * listeners.add(l); } } public final void
     * removeChangeListener(ChangeListener l) { synchronized (listeners) {
     * listeners.remove(l); } } protected final void fireChangeEvent() {
     * Iterator<ChangeListener> it; synchronized (listeners) { it = new
     * HashSet<ChangeListener>(listeners).iterator(); } ChangeEvent ev = new
     * ChangeEvent(this); while (it.hasNext()) { it.next().stateChanged(ev); } }
     */
}
