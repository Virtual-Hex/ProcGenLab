package jaredbgreat.procgenlab.viewer.logic;

/*
 * Copyright (C) Jared Blackburn 2016
 *
 * Currently under the Creative Commons Attribution License version 4.0:  
 * https://creativecommons.org/licenses/by/4.0/legalcode
*/

import jaredbgreat.procgenlab.exceptions.ImageCreationException;
import jaredbgreat.procgenlab.exceptions.InconsistentLayersException;
import jaredbgreat.procgenlab.api.IGenerator;
import jaredbgreat.procgenlab.api.IPalette;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represent / contains all the images created from 
 * data returned by the generator, including the names and 
 * graphic images to display.  Its primary purpose is to hold 
 * a set of results so it can be found and displayed by the GUI's
 * ViewPanel.
 * 
 * @author Jared Blackburn
 */
public final class WorldMap implements IWorldMap {
    private Image[] images;
    private String[] names;
    
    
    public WorldMap(IGenerator generator) {
        try {
            setData(generator);
        } catch (InconsistentLayersException ex) {
            Logger.getLogger(WorldMap.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.exit(1);
            
        } catch (ImageCreationException ex) {
            Logger.getLogger(WorldMap.class.getName())
                    .log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
    
    /**
     * This will request the current generation data 
     * of an generator (i.e., the data result from running
     * generate()) and uses it to create set of Images 
     * for displaying the results.  It should only be called 
     * after the generator has been run.
     * 
     * @param generator
     * @throws InconsistentLayersException
     * @throws ImageCreationException 
     */
    public void setData(IGenerator generator) 
                        throws InconsistentLayersException, 
                               ImageCreationException {
        int layers = generator.getNumLayers();
        names = generator.getLayerNames();
        if(names.length != layers) {
            if(names.length < layers) {
                throw new 
                    InconsistentLayersException("Not all layers are named"
                            + " (layers = " + layers + ", given = " 
                            + names.length + ")");
            } else {
                throw new 
                    InconsistentLayersException("More names given than layers;"
                            + " (layers = " + layers + ", given = " 
                            + names.length + ")");
            }
        }
        IPalette[] palettes = generator.getColorPaletes();
        if(palettes.length != layers) {
            if(palettes.length < layers) {
                throw new 
                    InconsistentLayersException("Not all layers have palettes.");
            } else {
                throw new 
                    InconsistentLayersException("More palettes than "
                            + "layers given.");                
            }
        }
        int[][] maps = generator.getData();
        if(maps.length != layers) {
            if(maps.length < layers) {
                throw new 
                    InconsistentLayersException("Not all layers have maps"
                            + " (layers = " + layers + ", given = " 
                            + maps.length + ")");
            } else {
                throw new 
                    InconsistentLayersException("More names given than maps"
                            + " (layers = " + layers + ", given = " 
                            + maps.length + ")");               
            }
        }
        images = new BufferedImage[layers];
        int w = generator.getWidth(), h = generator.getHeight();
        for(int i = 0; i < layers; i++) {
            images[i] = palettes[i].getImage(w, h, maps[i]);
        }
    }
    
    
    /**
     * Get the Image for a specific index.
     * 
     * @param number
     * @return 
     */
    @Override
    public Image getImage(int number) {
        return images[number];
    }
    
    
    /**
     * Get the name for the Image at the given index.
     * 
     * This is the human readable name to placed on tabs 
     * so that the specific map layers can be found.  I.e.,
     * it is for human use, not a technical part of processing.
     * 
     * @param number
     * @return 
     */
    @Override
    public String getImageName(int number) {
        return names[number];
    }
    
    
    public int getSize() {
        return images.length;
    }
    
}
