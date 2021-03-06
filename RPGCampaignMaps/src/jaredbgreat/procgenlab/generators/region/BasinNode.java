/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaredbgreat.procgenlab.generators.region;

/**
 *
 * @author jared
 */
public class BasinNode {
    final int x, y, value;
    final double decay;
    private static final double[] logtable = makeLogTable();
    
    
    public BasinNode(int x, int y, int value, double decay) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.decay = decay;
    }
    
    
    public double getRelativeWeakness(int range) {
        double effect = ((range) * decay);
        return range * range;
    }
    
    
    public double getWeaknessAt(int atx, int aty) {
        double xdisplace = ((double)(x - atx) * decay);
        double ydisplace = ((double)(y - aty) * decay);
        return Math.min((xdisplace * xdisplace) + (ydisplace * ydisplace), 1.0);
    }
    
    
    public static int summateEffect(BasinNode[] n, Tile t) {
        double effect = 0.0;
        double sum    = 0.0;
        double power, weakness;
        for(int i = 0; i < n.length; i++) {
            if((n[i].x == t.x) && (n[i].y == t.y)) {
                return (int)n[i].value;
            }
            weakness = n[i].getWeaknessAt(t.x, t.y);
            power = 1.0 / (weakness * weakness);
            sum += power;
            effect += Math.max(((double)n[i].value) * power, 0);
        }
        //System.out.println((int)(effect / sum));
        return (int)(effect / sum);
    }
    
    
    public static int summateEffect(BasinNode[] n, Tile t, double noise) {
        double effect = 0.0;
        double sum    = 0.0;
        double power, weakness;
        for(int i = 0; i < n.length; i++) {
            if((n[i].x == t.x) && (n[i].y == t.y)) {
                return (int)n[i].value;
            }
            weakness = n[i].getWeaknessAt(t.x, t.y);
            power = 1.0 / (weakness * weakness);
            sum += power;
            effect += Math.max(((double)n[i].value) * power, 0);
        }
        //System.out.println((int)(effect / sum));
        return (int)((effect / sum) + noise);
    }
    
    
    private static double[] makeLogTable() {
        double[] out = new double[21];
            for(int i = 0; i < out.length; i++) {
                out[i] = Math.pow(10, ((double)(i - 10)) / 10);
            }
        return out;
    }
    
    
    public static double getLogScaled(int in) {
        return logtable[in + 10];
    }
    
    
    
    
}
