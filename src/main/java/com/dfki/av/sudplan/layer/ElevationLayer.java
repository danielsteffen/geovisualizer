/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.io.dem.ElevationLoader;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.layer.texture.Texturable;
import com.dfki.av.sudplan.layer.texture.TexturableListener;
import com.dfki.av.sudplan.layer.texture.TextureProvider;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.dfki.av.sudplan.util.IconUtil;
import com.sun.j3d.loaders.Scene;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ElevationLayer extends FileBasedLayer implements
        FeatureLayer,
        Texturable,
        PropertyChangeListener {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:lookup of loader + generic
    private final static Logger logger = LoggerFactory.getLogger(ElevationLayer.class);
    private ElevationLoader loader;
    private Scene dataObject;
    public static final ImageIcon ELEVATION_ICON_24 = new javax.swing.ImageIcon(ElevationLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/filetype/elevation24.png"));
    public static final ImageIcon ELEVATION_ICON_12 = IconUtil.getHalfSizeIcon(ELEVATION_ICON_24);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: not the layer is texturable, but the object. Change
    private final ArrayList<TexturableListener> texturableListeners = new ArrayList<TexturableListener>();
    private final ArrayList<TextureProvider> textureProviders = new ArrayList<TextureProvider>();

    public ElevationLayer(String file) throws LayerIntialisationException {
        super(file);
    }

    public ElevationLayer(File file) throws LayerIntialisationException {
        super(file);
        setIcon(ELEVATION_ICON_12);
    }

    public ElevationLayer(URL url) throws LayerIntialisationException {
        super(url);
    }

    @Override
    public Object getDataObject() {
        return dataObject;
    }

    @Override
    public void setDataObject(final Object dataObject) {
        if (dataObject == null || dataObject instanceof Scene) {
            this.dataObject = (Scene) dataObject;
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:generics
    @Override
    protected void initialiseLayerFromFile() throws LayerIntialisationException {
        loader = new ElevationLoader();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("loader: " + loader + " file: " + file);
            }
            this.dataObject = loader.load(file);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:abstract will be the same for all layers
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:seems to be not correct // maybe this should be calculated by hand.
            setBoundingBox(new AdvancedBoundingBox(dataObject.getSceneGroup().getBounds()));
        } catch (Exception ex) {
            final String message = "Error while intialising layer.";
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            throw new LayerIntialisationException(message, ex);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:if not loaded not available. Redesign.
    public RawArcGrid getGrid() {
        return loader.getArcGrid();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: maybe better to register the listner on the object itself during the creation in the loader.
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Problem I want to have one (textureObject) per layer. If I added to the shapes the layer panel will explode.
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generell for scene based layers.
    public void setTextureRecursivley(final Texture texture, final Group branchGroup) {
        if (dataObject != null && dataObject.getSceneGroup() != null) {
            final BranchGroup allObjects = dataObject.getSceneGroup();
            Enumeration children = allObjects.getAllChildren();
            while (children.hasMoreElements()) {
                final Object currentChild = children.nextElement();
                if (currentChild instanceof Group) {
                    setTextureRecursivley(texture, (Group) currentChild);
                } else if (currentChild instanceof Shape3D) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("found shape, setting texture.");
                    }
                    ((Shape3D) currentChild).getAppearance().setTexture(texture);
                }
            }
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: generell for scene based layers.
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:simply wrong here we need mutltiple textures
    public void removeTextureRecursively(final Texture texture, final Group branchGroup) {
        if (dataObject != null && dataObject.getSceneGroup() != null) {
            final BranchGroup allObjects = dataObject.getSceneGroup();
            Enumeration children = allObjects.getAllChildren();
            while (children.hasMoreElements()) {
                final Object currentChild = children.nextElement();
                if (currentChild instanceof Group) {
                    setTextureRecursivley(texture, (Group) currentChild);
                } else if (currentChild instanceof Shape3D) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("found shape, removing texture.");
                    }
                    ((Shape3D) currentChild).getAppearance().setTexture(null);
                }
            }
        }
    }

    @Override
    public void addTextureProvider(final TextureProvider provider) {
        if (provider != null && !textureProviders.contains(provider)) {
            textureProviders.add(provider);
            provider.addPropertyChangeListener(this);
            addTexture(provider.getTexture());
        }
    }

    @Override
    public void removeTextureProvider(final TextureProvider provider) {
        if (textureProviders.contains(provider)) {
            textureProviders.remove(provider);
            provider.removePropertyChangeListener(this);
            removeTexture(provider.getTexture());
        }
    }

    @Override
    public TextureProvider getTextureProvider(final Texture texture) {
        if (texture != null) {
            for (TextureProvider textureProvider : textureProviders) {
                if (texture.equals(textureProvider.getTexture())) {
                    return textureProvider;
                }
            }
        }
        return null;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:this should not be on the layer    
    public void addTexture(Texture textureToAdd) {
        if (logger.isDebugEnabled()) {
            logger.debug("addTexture");
        }
        if (dataObject != null && dataObject.getSceneGroup() != null) {
            setTextureRecursivley(textureToAdd, dataObject.getSceneGroup());
        }
        for (TexturableListener texturableListener : texturableListeners) {
            texturableListener.textureAdded(this, textureToAdd);
        }
    }

    public List<Texture> getTextures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTextureIntersecting(final Texture textureToTest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeTexture(final Texture textureToRemove) {
        if (dataObject != null && dataObject.getSceneGroup() != null) {
            removeTextureRecursively(textureToRemove, dataObject.getSceneGroup());
        }
        for (TexturableListener texturableListener : texturableListeners) {
            texturableListener.textureRemoved(this, textureToRemove);
        }
    }

    public void removeTexture(final int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void replaceTexture(final int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTextureVisible(Texture texture) {
        return true;
    }

    @Override
    public void setTextureVisible(final int index, final boolean isVisible) {
        if (logger.isDebugEnabled()) {
            logger.debug("setTextureVisible: index:" + index + " isVisible: " + isVisible);
        }
    }

    @Override
    public void setTextureVisible(final Texture texture, final boolean isVisible) {
        if (logger.isDebugEnabled()) {
            logger.debug("setTextureVisible: texture:" + texture + " isVisible: " + isVisible);
        }
        if (isVisible && dataObject != null && dataObject.getSceneGroup() != null) {
            setTextureRecursivley(texture, dataObject.getSceneGroup());
        } else {
            removeTextureRecursively(texture, dataObject.getSceneGroup());
        }
        for (TexturableListener texturableListener : texturableListeners) {
            texturableListener.textureVisibilityChanged(this, texture, isVisible);
        }
    }

    @Override
    public void addTextureListener(final TexturableListener listener) {
        if (!texturableListeners.contains(listener)) {
            texturableListeners.add(listener);
        }
    }

    @Override
    public void reomveTextureListener(final TexturableListener listener) {
        if (texturableListeners.contains(listener)) {
            texturableListeners.remove(listener);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:redesign texturable concept I think it is overengineerd to complicated.
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (logger.isDebugEnabled()) {
            logger.debug("propertyChanged");
        }
        if (evt.getSource() instanceof TextureProvider) {
            final TextureProvider textureProvider = (TextureProvider) evt.getSource();
            if (logger.isDebugEnabled()) {
                logger.debug("TextureProvider " + textureProvider.getName() + " property: " + evt.getPropertyName() + " has changed.");
            }
            if (evt.getPropertyName().equals("visible")) {
                if (textureProvider.isVisible()) {
                    setTextureVisible(textureProvider.getTexture(), true);
                } else {
                    setTextureVisible(textureProvider.getTexture(), false);
                }
            }
        }
    }
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: move up in AbstractSceneLayer
}
