package dev.michey.isoengine.map;

import com.badlogic.gdx.Gdx;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.map.tile.TileType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapLoader {

    public Map loadMap(String internalPathName) {
        List<String> lines;

        if(!internalPathName.startsWith("maps/")) {
            internalPathName = "maps" + File.separator + internalPathName;
        }

        String raw = Gdx.files.internal(internalPathName).readString();
        lines = new ArrayList<>(Arrays.asList(raw.split("\n")));

        String mapVersion = (String) findValue(lines, "map_version");

        String[] tileDim = ((String) findValue(lines, "tile_size")).split("x");
        int tileSizeX = Integer.parseInt(tileDim[0]);
        int tileSizeY = Integer.parseInt(tileDim[1]);

        String[] mapDim = ((String) findValue(lines, "map_dim")).split("x");
        int mapDimX = Integer.parseInt(mapDim[0]);
        int mapDimY = Integer.parseInt(mapDim[1]);

        HashMap<String, TileType> definitions = readMapDefinitions(lines);
        System.out.println("TS, MD = " + tileSizeX + "," + tileSizeY + "," + mapDimX + "," + mapDimY);

        Map map = new Map(mapVersion, tileSizeX, tileSizeY, mapDimX, mapDimY);

        // Loading first base layer of tiles here
        Layer layer = new Layer(map, 0);
        ArrayList<Tile> tiles = readLayerData(layer, mapDimX, mapDimY, lines, definitions);

        for(Tile tile : tiles) {
            tile.updateWorldPosition();
            map.getBaseTileList().add(tile);
            layer.addTile(tile);
        }

        map.getLayers().add(layer);
        return map;
    }

    private ArrayList<Tile> readLayerData(Layer layer, int mapDimX, int mapDimY, List<String> lines, HashMap<String, TileType> definitions) {
        ArrayList<Tile> map = new ArrayList<>(mapDimX * mapDimY);
        boolean activeSearch = false;

        int y = 0;

        for(String line : lines) {
            if(y == mapDimY) break;

            if(line.equals("[layer_" + layer.layerIndex + "_start]")) {
                activeSearch = true;
                continue;
            }

            if(line.equals("[layer_" + layer.layerIndex + "_end]")) {
                return map;
            }

            if(activeSearch) {
                String[] individualTiles = line.split(";");

                for(int i = 0; i < mapDimX; i++) {
                    map.add(new Tile(layer, definitions.get(individualTiles[i]), i, y, i + y * mapDimX));
                }

                y++;
            }
        }

        return map;
    }

    private HashMap<String, TileType> readMapDefinitions(List<String> lines) {
        HashMap<String, TileType> definitions = new HashMap<>();
        boolean activeSearch = false;

        for(String line : lines) {
            if(line.equals("[definition_start]")) {
                activeSearch = true;
                continue;
            }

            if(line.equals("[definition_end]")) {
                return definitions;
            }

            if(activeSearch) {
                Object[] extracted = extract(line);
                definitions.put((String) extracted[0], TileType.valueOf((String) extracted[1]));
            }
        }

        return definitions;
    }

    private Object findValue(List<String> lines, String key) {
        for(String s : lines) {
            if(s.startsWith(key)) {
                return extractValue(s);
            }
        }

        return null;
    }

    private Object[] extract(String line) {
        String[] split = line.split("=");
        return new Object[] {split[0], split[1]};
    }

    private Object extractValue(String line) {
        return line.split("=")[1];
    }

}
