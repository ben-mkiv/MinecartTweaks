package ben_mkiv.minecarttweaks.cartCommands;

import ben_mkiv.minecarttweaks.capability.ICartCommand;
import ben_mkiv.minecarttweaks.capability.capability;
import ben_mkiv.minecarttweaks.util.EntityInventoryUtils;
import ben_mkiv.minecarttweaks.util.EntityTeleportUtils;
import ben_mkiv.minecarttweaks.util.MinecartUtils;
import ben_mkiv.minecarttweaks.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ccTeleport implements ICartCommand {

    DimensionType dimension = null;
    BlockPos position;

    public ccTeleport(DimensionType dimension, int x, int y, int z){
        this.dimension = dimension;
        this.position = new BlockPos(x, y, z);

        //System.out.println("new teleporter created to dim: " + dimension + ", " + position.toString());
    }

    public ccTeleport(){}

    @Override
    public boolean validate(String data){
        return data.contains("teleport") || data.contains("tp");
    }

    @Override
    public HashSet<EventType> getPhases(){
        return new HashSet<>(Arrays.asList(EventType.TICK, EventType.MOUNTED));
    }

    private void validateLocation(AbstractMinecartEntity entity){
        if(dimension == null)
            dimension = entity.getEntityWorld().getDimension().getType();

        if(position.getX() == Integer.MAX_VALUE)
            position = new BlockPos(entity.getPosition().getX(), position.getY(), position.getZ());

        if(position.getY() == Integer.MAX_VALUE)
            position = new BlockPos(position.getX(), entity.getPosition().getY(), position.getZ());

        if(position.getZ() == Integer.MAX_VALUE)
            position = new BlockPos(position.getX(), position.getY(), entity.getPosition().getZ());
    }

    @Override
    public void execute(AbstractMinecartEntity entity){
        if(entity.ticksExisted < 15)
            return;

        entity.getCapability(capability.CAPABILITY).ifPresent(cart ->
            cart.getCommands().commands.remove(this.getPhases(), this)
        );

        validateLocation(entity);

        if(!consumeFuel(entity)) {
            MinecartUtils.sendNotify(entity, 2, new StringTextComponent("no fuel found"));
            return;
        }

        EntityTeleportUtils.teleport(entity, dimension, position);
    }

    boolean consumedFuel = false;
    private boolean consumeFuel(AbstractMinecartEntity entity){
        // try to get fuel from mounted entity (usually a player)
        if(entity.getPassengers().size() > 0 && EntityInventoryUtils.consumeItemFromInventory(entity.getPassengers().get(0), Items.ENDER_PEARL, 1))
            return true;

        // try to get fuel from adjacent inventories
        int radius = 1;
        AxisAlignedBB area = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(radius).offset(entity.getPosition());
        consumedFuel = false;
        for(BlockPos pos : WorldUtils.getBlockPos(area)){
            if(!entity.getEntityWorld().isAreaLoaded(pos, 1))
                continue;

            BlockState state = entity.getEntityWorld().getBlockState(pos);
            Block block = state.getBlock();
            if(!block.hasTileEntity(state))
                continue;

            TileEntity tile = entity.getEntityWorld().getTileEntity(pos);

            if(tile == null)
                continue;

            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                    cap -> consumeFuel(cap)
            );

            if(consumedFuel)
                return true;
        }

        return false;
    }

    private void consumeFuel(IItemHandler itemHandler){
        for(int slot = 0; slot < itemHandler.getSlots(); slot++){
            if(!(itemHandler.getStackInSlot(slot).getItem() instanceof EnderPearlItem))
                continue;

            itemHandler.extractItem(slot, 1, false);
            consumedFuel = true;
            return;
        }
    }

    @Deprecated
    public ccTeleport create(ITextComponent[] signData){
        String data = "";

        for(ITextComponent text : signData)
            data+=text.getUnformattedComponentText().toLowerCase();

        DimensionType dim = null;

        int dimId = parseInteger("dim", data);
        if(dimId != Integer.MAX_VALUE)
            dim = getDimensionById(dimId);

        return new ccTeleport(dim, parseInteger("x", data), parseInteger("y", data), parseInteger("z", data));
    }

    private static DimensionType getDimensionById(int id){
        for(DimensionType dimensionType : DimensionType.getAll())
            if(dimensionType.getId() == id)
                return dimensionType;

        return null;
    }

    private int parseInteger(String expect, String data){
        Matcher matcher = Pattern.compile(expect+"([\\D]*)([\\d]+)").matcher(data);
        if (matcher.find()){
            Matcher subMatcher = Pattern.compile("[\\d,-]+").matcher(matcher.group(0));

            if (subMatcher.find())
                return Integer.valueOf(subMatcher.group(0));
        }

        return Integer.MAX_VALUE;
    }

}
