/*
 * Copyright (C) Jared Blackburn 2016
 *
 * Currently under the Creative Commons Attribution License version 4.0:  
 * https://creativecommons.org/licenses/by/4.0/legalcode
 */
package jaredbgreat.procgenlab;

import jaredbgreat.procgenlab.viewer.MainWindow;

/**
 * TODO: Everything!!!
 * 
 * A program for procedure content generation experiments concerning world /
 * campaign maps.  This is an application for holding re-usable visualization 
 * code along with various map generation systems that plug into it so as to 
 * allow experimentation and development of map generators that can be used
 * else where.  The first likely use of one of these would most likely be a
 * Minecraft mod (RPG Campaign Mod) so as to see it in action in a real game 
 * without creating the whole game.
 * 
 * @author Jared Blackburn
 */
public class MapGenView {
    public static MainWindow win;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        init();
        winInit();
        run();
        cleanup();
    }
    
    
    private static void init() {
        
    }
    
    
    private static void winInit() {
        win = new MainWindow();
    }
    
    
    private static void run() {
        
    }
    
    
    private static void cleanup() {
        
    }
    
}
