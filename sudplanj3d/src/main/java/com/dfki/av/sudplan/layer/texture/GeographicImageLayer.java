/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer.texture;

import com.dfki.av.sudplan.io.texture.GeographicTexture;
import com.dfki.av.sudplan.io.texture.GeographicTextureLoader;
import com.dfki.av.sudplan.layer.FileBasedLayer;
import com.dfki.av.sudplan.layer.LayerIntialisationException;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class GeographicImageLayer extends FileBasedLayer implements ImageLayer{

    private final static Logger logger = LoggerFactory.getLogger(GeographicImageLayer.class);
    public static ImageIcon IMAGE_ICON_12 = new javax.swing.ImageIcon(GeographicImageLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/filetype/image12.png"));
    GeographicTexture texture;

    public GeographicImageLayer(String file) throws LayerIntialisationException {
        super(file);
    }

    public GeographicImageLayer(File file) throws LayerIntialisationException {
        super(file);
    }

    public GeographicImageLayer(URL url) throws LayerIntialisationException {
        super(url);
    }

    @Override
    protected void initialiseLayerFromFile() throws LayerIntialisationException {
        try {
            texture = (GeographicTexture) new GeographicTextureLoader().load(file);
            texture.setName(getName());
            setBoundingBox(texture.getBoundingBox());
        } catch (final Exception ex) {
            final String message = "Error during initialisation of texture layer.";
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            throw new LayerIntialisationException(message, ex);
        }
    }

    @Override
    public GeographicTexture getTexture() {
        return texture;
    }
}
