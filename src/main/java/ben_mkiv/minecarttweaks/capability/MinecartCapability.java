package ben_mkiv.minecarttweaks.capability;

import ben_mkiv.minecarttweaks.util.MinecartUtils;
import ben_mkiv.minecarttweaks.util.WorldUtils;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// todo:
// mob filters?
// custom speed cap

public class MinecartCapability implements IMinecartCapability {
    private AbstractMinecartEntity entity = null;
    private boolean isMoving = false;
    private int updateSigns = 1;

    CartCommands commands;


    MinecartCapability(AbstractMinecartEntity cart){
        entity = cart;
        commands = new CartCommands(cart);
    }

    @Override
    public void onSpawn(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onStopMoving(){
        checkSigns(1);
        commands.event(ICartCommand.EventType.STOPPED);
    }

    private void onStartMoving(){
        commands.event(ICartCommand.EventType.STARTED);
    }

    @Override
    public void onMount(Entity mountingEntity){
        checkSigns(1);
        commands.event(ICartCommand.EventType.MOUNTED);
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event){
        if(!event.phase.equals(TickEvent.Phase.END))
            return;

        if(entity == null || !entity.isAlive()){
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }

        if(!entity.world.equals(event.world))
            return;

        if(updateSigns > 0) {
            checkSigns(updateSigns);
            return;
        }

        commands.event(ICartCommand.EventType.TICK);

        double speed = entity.getMotion().squareDistanceTo(new Vec3d(0, 0, 0));

        if(speed == 0 && isMoving)
            onStopMoving();
        else if(speed > 0 && !isMoving)
            onStartMoving();


        isMoving = speed > 0;
    }

    public CartCommands getCommands(){
        return commands;
    }

    private void checkSigns(int radius){
        updateSigns = radius;

        if(entity.ticksExisted < 10)
            return;

        AxisAlignedBB area = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(radius).offset(entity.getPosition());

        for(BlockPos pos : WorldUtils.getBlockPos(area)){
            if(!entity.getEntityWorld().isAreaLoaded(pos, 1))
                continue;

            BlockState state = entity.getEntityWorld().getBlockState(pos);
            Block block = state.getBlock();
            if(block instanceof AbstractSignBlock)
                commands.parseSign(entity.getEntityWorld(), pos);
        }


        MinecartUtils.sendNotify(entity, 4, new StringTextComponent("MinecartTweaks: " + commands.commands.size() + " commands."));

        updateSigns = 0;
    }

}
