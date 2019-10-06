package ben_mkiv.minecarttweaks.capability;

import ben_mkiv.minecarttweaks.cartCommands.*;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CartCommands {
    static HashSet<ICartCommand> validCommands = new HashSet<>();

    public HashMap<HashSet<ICartCommand.EventType>, ICartCommand> commands = new HashMap<>();

    AbstractMinecartEntity entity;

    static {
        register(new ccDropCartAsItem());
        register(new ccDropCartToPlayer());
        register(new ccDropItem());
        register(new ccDropStack());
        register(new ccEjectPassangers());
        register(new ccTeleport());
        register(new ccVoidItem());
        register(new ccVoidStack());


        register(new ccDimensionList());
    }

    CartCommands(AbstractMinecartEntity cart){
        entity = cart;
    }

    public static void register(ICartCommand command){
        System.out.println("registered MinecartTweaks command from " + command.getClass().getSimpleName());
        validCommands.add(command);
    }

    public void parseSign(World world, BlockPos position){
        SignTileEntity tile = (SignTileEntity) entity.getEntityWorld().getTileEntity(position);

        for(ITextComponent text : tile.signText)
            if(text.getUnformattedComponentText().length() > 0)
                for(ICartCommand c : validCommands)
                    if(c.validate(text.getUnformattedComponentText().trim().toLowerCase()))
                        commands.put(c.getPhases(), c.create(tile.signText));

    }

    public void event(ICartCommand.EventType eventType){
        HashSet<ICartCommand> executeCommands = new HashSet<>();
        for(Map.Entry<HashSet<ICartCommand.EventType>, ICartCommand> entry : commands.entrySet())
            if(entry.getKey().contains(eventType))
                executeCommands.add(entry.getValue());

        for(ICartCommand command : executeCommands)
            command.execute(entity);
    }

}
