package ben_mkiv.minecarttweaks.capability;

import ben_mkiv.minecarttweaks.MinecartTweaks;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class capability implements ICapabilityProvider {
    public static final String NAME = "minecart_capability";
    public static ResourceLocation MINECART_CAPABILITY = new ResourceLocation(MinecartTweaks.MOD_ID, NAME);

    private LazyOptional<MinecartCapability> lazyInstance = LazyOptional.empty();

    MinecartCapability instance = null;

    @CapabilityInject(capability.class)
    public static Capability<IMinecartCapability> CAPABILITY = null;

    public capability(AbstractMinecartEntity cart){
        instance = new MinecartCapability(cart);
        lazyInstance = LazyOptional.of(() -> instance).cast();
    }


    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side){
        if(cap == capability.CAPABILITY)
            return LazyOptional.of(() -> instance).cast();
        else
            return LazyOptional.empty();
    }

    public static class Storage implements Capability.IStorage<capability> {

        @Override
        public INBT writeNBT(Capability<capability> capability, capability instance, Direction side) {
            // return an NBT tag
            return new CompoundNBT();
        }

        @Override
        public void readNBT(Capability<capability> capability, capability instance, Direction side, INBT nbt) {
            // load from the NBT tag
        }
    }

    public static class Factory implements Callable<capability> {
        @Override
        public capability call() throws Exception {
            return new capability(null);
        }
    }
}
