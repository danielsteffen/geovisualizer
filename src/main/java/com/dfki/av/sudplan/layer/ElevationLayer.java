/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dfki.av.sudplan.layer;

import com.dfki.av.sudplan.io.dem.ElevationLoader;
import com.dfki.av.sudplan.io.dem.RawArcGrid;
import com.dfki.av.sudplan.util.IconUtil;
import com.sun.j3d.loaders.Scene;
import java.io.File;
import java.net.URL;
import javax.media.j3d.BoundingBox;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ElevationLayer extends FileBasedLayer {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:lookup of loader + generic
    private final static Logger logger = LoggerFactory.getLogger(ElevationLayer.class);
    private ElevationLoader loader;
    private Scene dataObject;
    public static final ImageIcon ELEVATION_ICON_24 = new javax.swing.ImageIcon(ElevationLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/filetype/elevation24.png"));
    public static final ImageIcon ELEVATION_ICON_12 = IconUtil.getHalfSizeIcon(ELEVATION_ICON_24);

    public ElevationLayer(String file) throws LayerIntialisationException{
        super(file);
    }

    public ElevationLayer(File file) throws LayerIntialisationException{
        super(file);        
        setIcon(ELEVATION_ICON_12);
    }

    public ElevationLayer(URL url) throws LayerIntialisationException{
        super(url);
    }     

    @Override
    public Scene getDataObject() {
        return dataObject;
    }

    @Override
    public void setDataObject(final Scene dataObject) {
        this.dataObject = dataObject;
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:generics
    @Override
    protected void initialiseLayerFromFile() throws LayerIntialisationException{
        loader = new ElevationLoader();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("loader: "+loader+" file: "+file);
            }
            this.dataObject = loader.load(file);
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:abstract will be the same for all layers
            setBoundingBox(new BoundingBox(dataObject.getSceneGroup().getBounds()));
        } catch (Exception ex) {
            final String message="Error while intialising layer.";
            if (logger.isErrorEnabled()) {
                logger.error(message,ex);
            }
            throw new LayerIntialisationException(message, ex);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:if not loaded not available. Redesign.
     public RawArcGrid getGrid(){
         return loader.getArcGrid();
     }


}
