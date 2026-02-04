package me.udnek.coreu.multiblockstructure;

import com.google.common.base.Preconditions;
import me.udnek.coreu.multiblockstructure.blockchoice.AnyBlockChoice;
import me.udnek.coreu.multiblockstructure.blockchoice.BlockChoice;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@org.jspecify.annotations.NullMarked public class MultiBlockStructure{

    BlockChoice[] blocks;
    public final int xSize;
    public final int ySize;
    public final int zSize;
    private final Vector center;

    protected MultiBlockStructure(int xSize, int ySize, int zSize, BlockChoice[] blockChoices, Vector center){
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        blocks = blockChoices;
        this.center = center;
    }


    public void build(Location location){
        final int xOffset = location.getBlockX() - center.getBlockX();
        final int yOffset = location.getBlockY() - center.getBlockY();
        final int zOffset = location.getBlockZ() - center.getBlockZ();

        World world = location.getWorld();
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    Block block = world.getBlockAt(xOffset + x, yOffset + y, zOffset + z);
                    block.setType(get(x, y, z).getExample());
                }
            }
        }
    }


    public boolean isStandingHere(Location location){
        final int xOffset = location.getBlockX() - center.getBlockX();
        final int yOffset = location.getBlockY() - center.getBlockY();
        final int zOffset = location.getBlockZ() - center.getBlockZ();

        World world = location.getWorld();
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    Block block = world.getBlockAt(xOffset + x, yOffset + y, zOffset + z);
                    if (!get(x, y, z).isAppropriate(block)) return false;
                }
            }
        }
        return true;
    }



    public Vector getCenter(){
        return center.clone();
    }

    public BlockChoice getAtCenter(){
        return get(center);
    }


    public BlockChoice get(Vector vector){
        return get(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public BlockChoice get(Location location){
        return get(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockChoice get(int x, int y, int z){
        return blocks[z*xSize*ySize + y*xSize + x];
    }

    public int getXSize() {return xSize;}
    public int getYSize() {return ySize;}
    public int getZSize() { return zSize;}
    public Vector getSize(){ return new Vector(xSize, ySize, zSize);}


    public static class Builder {

        BlockChoice[][][] blocks;
        private final int xSize;
        private int ySize;
        private final int zSize;

        private int layerNumber = 0;
        private Vector center = new Vector(0, 0, 0);

        public Builder(int xSize, int ySize, int zSize){
            Preconditions.checkArgument(xSize > 0, "xSize must be >0");
            Preconditions.checkArgument(ySize > 0, "ySize must be >0");
            Preconditions.checkArgument(zSize > 0, "zSize must be >0");
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
            blocks = new BlockChoice[xSize][ySize][zSize];
        }

        public void setCenter(Vector center) {
            this.center = center;
        }
        public void setCenter(int x, int y, int z){
            this.center = new Vector(x, y, z);
        }

        public BlockChoice[] flatten(){
            BlockChoice[] flatten = new BlockChoice[xSize*ySize*zSize];
            for (int x = 0; x < xSize; x++) {
                for (int y = 0; y < ySize; y++) {
                    for (int z = 0; z < zSize; z++) {
                        flatten[z*xSize*ySize + y*xSize + x] = blocks[x][y][z];
                    }
                }
            }
            return flatten;
        }

        public MultiBlockStructure build(){
            if (layerNumber < ySize) ySize = layerNumber;
            return new MultiBlockStructure(xSize, ySize, zSize, flatten(), center);
        }

        public void addLayer(BlockChoice[][] layer){
            Preconditions.checkArgument(layerNumber < ySize, "to much layers! " + layerNumber + "/" + ySize);
            Preconditions.checkArgument(layer.length == zSize, "not appropriate Z size");
            Preconditions.checkArgument(layer[0].length == xSize, "not appropriate X size");
/*            if () throw new Exception("to much layers!");
            if (layer.length != xSize) throw new Exception("not appropriate Z size");
            if (layer[0].length != zSize) throw new Exception("not appropriate X size");*/



            for (int z = 0; z < zSize; z++) {
                for (int x = 0; x < xSize; x++) {

                    if (layer[z][x] == null){
                        blocks[x][layerNumber][z] = new AnyBlockChoice();
                    }
                    else{
                        blocks[x][layerNumber][z] = layer[z][x];
                    }
                }
            }
            layerNumber += 1;
        }
    }
}















