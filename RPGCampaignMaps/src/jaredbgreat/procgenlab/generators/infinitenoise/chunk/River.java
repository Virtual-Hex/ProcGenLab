package jaredbgreat.procgenlab.generators.infinitenoise.chunk;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * @author Jared Blackburn
 */
public class River {
    final int MAX;
    MapMaker map;
    BasinNode begin, end;
    double dx, dy;
    double angle, da;
    double cx, cy, rx, ry;
    AS s;
    final Deque<ChunkTile> Q;
    //final ChangeQueue Q;
    private int oc;
    //private ChunkTile last;
    
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
    
//    private class ChangeQueue {
//        private final ChunkTile[] data = new ChunkTile[16];
//        private int head = 0, tail = 0;
//        public ChunkTile push(ChunkTile in) {
//            data[head] = in;
//            head = (head + 1) % data.length;
//            if(head == tail) {
//                tail = (tail + 1) % data.length;
//                return data[head];
//            } else {
//                return null;
//            }
//        }
//        public ChunkTile pop() {
//            if(head == tail) {
//                return null;
//            } else {
//                ChunkTile out = data[tail];
//                tail = (tail + 1) % data.length;
//                return out;
//            }
//        }
//    }  
    
    
    public River(BasinNode high, BasinNode low, MapMaker mapIn) {
        MAX = MapMaker.RSIZE -2;
        Q = new ArrayDeque<>();
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
            Q.push(map.getTile((int)cx, (int)cy));
            //ChunkTile toChange = Q.peek();
//            if(toChange != null) {
//                makeRiver(toChange);
//            }
        } while(!shouldEnd((int)cx, (int)cy));
        ChunkTile toChange;
        while(!Q.isEmpty() && (toChange = Q.pop()) != null) {
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
            return t.rlBiome == 3 
                    || ((t.rlBiome < 3) && (oc > 4) 
                        && ((t.val < 4) || oc > 8)) 
                    || outOfBounds(x, y);
        }
    }
    
    
    private boolean outOfBounds(int x, int z) {        
        return (x <= 1) || (z <= 1) 
                ||(x >= MAX) || (z >= MAX);
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
