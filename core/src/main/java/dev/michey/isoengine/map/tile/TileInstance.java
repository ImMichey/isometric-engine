package dev.michey.isoengine.map.tile;

public class TileInstance {

    public int width = 1;
    public int height = 1;

    public void test() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < width; j++) {
                for(int k = 0; k < height; k++) {
                    float row_max   =   ((float) (i + j + 2)) / ((float) (width + width));
                    float row_base  =   ((float) (i + j)) / ((float) (width + width));
                    float row_diff  =   row_max - row_base;
                    float z         =   row_base + row_diff * (((float) k) / ((float) height));
                    System.out.println("W = " + width + ", H = " + height + " -> z = " + z + " (X,Z,Y) " + i + "," + j + "," + k);
                }
            }
        }
    }

}
