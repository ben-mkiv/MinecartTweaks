package ben_mkiv.minecarttweaks.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;

public class WorldUtils {

    public static HashSet<BlockPos> getBlockPos(AxisAlignedBB area){
        HashSet<BlockPos> ret = new HashSet<>();

        for(double x = area.minX; x < area.maxX; x++)
            for(double z = area.minZ; z < area.maxZ; z++)
                for(double y = area.minY; y < (area.maxY-1); y++)
                    ret.add(new BlockPos(x, y, z));

        return ret;
    }
}
