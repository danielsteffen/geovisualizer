/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.io.shape.ShapeLoader;
import com.dfki.av.sudplan.util.IconUtil;
import com.sun.j3d.loaders.Scene;
import java.io.File;
import java.net.URL;
import javax.media.j3d.BoundingBox;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:generic file based layer ? --> only difference is the loader
public class ShapeLayer extends FileBasedLayer{

    private final static Logger logger = LoggerFactory.getLogger(ShapeLayer.class);
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:lookup of loader + generic
    private ShapeLoader loader;
    private Scene dataObject;
    public static final ImageIcon SHAPE_ICON_24 = new javax.swing.ImageIcon(ElevationLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/filetype/shape24.png"));
    public static final ImageIcon SHAPE_ICON_12 = IconUtil.getHalfSizeIcon(SHAPE_ICON_24);

    public ShapeLayer(String file) throws LayerIntialisationException{
        super(file);
    }

    public ShapeLayer(File file) throws LayerIntialisationException{
        super(file);
        setIcon(SHAPE_ICON_12);
    }

    public ShapeLayer(URL url) throws LayerIntialisationException{
        super(url);
    }

    @Override
    protected void initialiseLayerFromFile() throws LayerIntialisationException{
        loader = new ShapeLoader();
        try {
            this.dataObject = loader.load(file);
             setBoundingBox(new BoundingBox(dataObject.getSceneGroup().getBounds()));
        } catch (Exception ex) {
            final String message = "Error while intialising layer.";
            if (logger.isErrorEnabled()) {
                logger.error(message,ex);
            }            
            throw new LayerIntialisationException(message, ex);
        }
    }

    @Override
    public Scene getDataObject() {
         return dataObject;
    }

    @Override
    public void setDataObject(Scene dataObject) {
        this.dataObject = dataObject;
    }
}
