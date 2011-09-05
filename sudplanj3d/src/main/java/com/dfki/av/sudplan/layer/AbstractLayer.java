/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.media.j3d.BoundingBox;
import javax.swing.Icon;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public abstract class AbstractLayer implements Layer {

    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private Icon icon = new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/dummy.png"));
    private double transparency = 1.0;
    private boolean enabled = true;
    private boolean visible = true;
    private AdvancedBoundingBox boundingBox = null;
    private String name;

    @Override
    public AdvancedBoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public double getTransparency() {
        return transparency;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setBoundingBox(final AdvancedBoundingBox boundingBox) {
        final BoundingBox oldValue = this.boundingBox;
        this.boundingBox = boundingBox;
        changeSupport.firePropertyChange("boundingBox", oldValue, boundingBox);

    }

    @Override
    public void setEnabled(final boolean enabled) {
        final boolean oldValue = this.enabled;
        this.enabled = enabled;
        changeSupport.firePropertyChange("enabled", oldValue, enabled);
    }

    @Override
    public void setTransparency(final double transparency) {
        final double oldValue = this.transparency;
        this.transparency = transparency;
        changeSupport.firePropertyChange("transparency", oldValue, transparency);
    }

    @Override
    public void setVisible(final boolean isVisible) {
        final boolean oldValue = this.visible;
        this.visible = isVisible;
        changeSupport.firePropertyChange("visible", oldValue, visible);
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void setIcon(final Icon icon) {
        this.icon=icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        final String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange("name", oldValue, name);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(final String propertyName,final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(final String propertyName,final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    
}
