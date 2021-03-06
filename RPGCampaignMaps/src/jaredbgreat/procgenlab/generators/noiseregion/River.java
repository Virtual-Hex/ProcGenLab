package jaredbgreat.procgenlab.generators.noiseregion;

import java.util.Random;

/**
 * @author Jared Blackburn
 */
public class River {
    Map map;
    BasinNode begin, end;
    double dx, dy;
    double angle, da;
    double cx, cy, rx, ry;
    AS s;
    final ChangeQueue Q;
    private int oc;
    
    private enum AS {
        P2 (2,   0),
        P1 (3,  -1),
        Z0 (0, 0),
        N1 (-3,  1),
        N2 (-2,  0);        
        public double a, b;
        private static final AS[] vals = values();
        AS(double a, double b) {
            this.a = a;
            this.b = b;
        }
        public static AS getRandom(Random r) {
            return vals[r.nextInt(vals.length)];
        }
        public AS shift(Random r) {
            int v = this.ordinal() + (r.nextInt(3)) - 1;
            if(v < 0) {
                v = 1;
            } else if(v >= vals.length) {
                v = vals.length - 1;
            }
            return vals[v];
        }
    }    
    
    private class ChangeQueue {
        private final ChunkTile[] data = new ChunkTile[16];
        private int head = 0, tail = 0;
        public ChunkTile push(ChunkTile in) {
            data[head] = in;
            head = (head + 1) % data.length;
            if(head == tail) {
                tail = (tail + 1) % data.length;
                return data[head];
            } else {
                return null;
            }
        }
        public ChunkTile pop() {
            if(head == tail) {
                return null;
            } else {
                ChunkTile out = data[tail];
                tail = (tail + 1) % data.length;
                return out;
            }
        }
    }
    
    
    public River(BasinNode high, BasinNode low, Map mapIn) {
        Q = new ChangeQueue();
        map = mapIn;
        begin = high;
        end   = low;
        oc = 0;
        da = angle = 0; 
        cx = begin.x;
        cy = begin.y;
        double length = findLength(cx, cy, end.x, end.y);
        dx = (end.x - begin.x) / length;
        dy = (end.y - begin.y) / length;
        s = AS.Z0;
    }
    
    private double findLength(double x1, double y1, double x2, double y2) {
        double difX = x1 - x2;
        double difY = y1 - y2;
        return Math.sqrt((difX * difX) + (difY * difY));
    }
    
    public void build(Random r) {
        s = AS.getRandom(r);
        da = (r.nextDouble() * 9) - 4;
        double l, das, dac, f, p;
        do {
            // TODO: Optimize: Keep angle in Radians, don't convert 
            das = Math.sin(Math.toRadians(angle)); 
            l = Math.sqrt((das * das) + 1);
            f = 1 / l;
            p = das / l;
            incrementAngle(r);
            cx += (f * dx) + (p * dy);
            cy += (f * dy) + (p * dx);
            ChunkTile toChange = Q.push(map.getTile((int)cx, (int)cy));
            if(toChange != null) {
                makeRiver(toChange);
            }
        } while(!shouldEnd((int)cx, (int)cy));
        ChunkTile toChange;
        while((toChange = Q.pop()) != null) {
            makeRiver(toChange);
        }
    }
    
    private boolean shouldEnd(int x, int y) {
        ChunkTile t = map.getTile(x, y);
        if(t == null) {
            return true;
        } else {
            // This will be true if the biome is water
            if(t.rlBiome < 3) {
                oc++;
            }
            return t.rlBiome == 3 || ((t.rlBiome < 3) 
                    && ((t.val < 4) || oc > 8));
        }
    }
    
    private void incrementAngle(Random r) {        
        angle += da;
        // Do I need to worry about fixing the angle over 
        // the rivers relatively short run?  Possibly not.
        /*if(angle > 360) {
            angle -= 360;
        } else if(angle < 0) {
            angle += 360;
        }*/
        da += (s.a * r.nextDouble()) + s.b;
        if(r.nextBoolean()) {
            if(r.nextBoolean()) {
                s = AS.getRandom(r);
            } else {
                s = s.shift(r);
            }
        }        
    }
    
    private void makeRiver(ChunkTile t) {
        // TODO: Expand to effect chunks in a radius of r = 1
        int rb = BiomeType.RIVER.ordinal();
        t.rlBiome = rb;
        map.getTile(t.x + 1, t.z).rlBiome = rb;
        map.getTile(t.x + 1, t.z + 1).rlBiome = rb;
        map.getTile(t.x, t.z + 1).rlBiome = rb;
        
    }
    
}
