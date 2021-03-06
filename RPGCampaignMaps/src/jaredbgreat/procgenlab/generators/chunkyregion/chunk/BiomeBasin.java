/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaredbgreat.procgenlab.generators.chunkyregion.chunk;

/**
 *
 * @author jared
 */
public class BiomeBasin {
    int x, z, value;
    double strength;
    
//*********************************************************************************/
//                         DEBUGGING / PROFILING                                   /
//*********************************************************************************/
public static volatile long num = 0;
@Override
public void finalize() throws Throwable {
	num--;
	super.finalize();
}
//*********************************************************************************/

    public BiomeBasin(int x, int z, int value, double strength) {
        this.x = x;
        this.z = z;
        this.value = value;
        this.strength = strength;
        // Profile
        num++;
    }
    
    
    public double getWeaknessAt(int atx, int aty) {
        double xdisplace = ((double)(x - atx));
        double ydisplace = ((double)(z - aty));
        return (xdisplace * xdisplace) + (ydisplace * ydisplace);
    }
    
    
    public static int summateEffect(BiomeBasin[][] n, ChunkTile t, int scale) {
        double effect = 0.0;
        int indexx = 0;
        int indexy = 0;
        double power;
        for(int i = 0; i < n.length; i++) 
            for(int j = 0; j < n[i].length; j++) {
                power = n[i][j].strength / n[i][j].getWeaknessAt(t.x, t.z);
                if(effect < power) {
                    effect = power;
                    indexx = i;
                    indexy = j;
            }
        }
        return n[indexx][indexy].value;
    }
    
}
