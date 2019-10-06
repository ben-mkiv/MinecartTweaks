package ben_mkiv.minecarttweaks;

import ben_mkiv.minecarttweaks.capability.IMinecartCapability;
import ben_mkiv.minecarttweaks.capability.capability;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = MinecartTweaks.MOD_ID)
@Mod(value = MinecartTweaks.MOD_ID)
public class MinecartTweaks {

    public static final String MOD_ID = "minecarttweaks";
    public static final String MOD_NAME = "MinecartTweaks";
    public static final String VERSION = "1.0";


    public MinecartTweaks(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(
                (FMLCommonSetupEvent event) -> preInit()
        );

    }

    private void preInit(){
        CapabilityManager.INSTANCE.register(capability.class, new capability.Storage(), new capability.Factory());
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent event) {
        if(!(event.getObject() instanceof AbstractMinecartEntity))
            return;

        AbstractMinecartEntity cart = (AbstractMinecartEntity) event.getObject();

        if(cart.getEntityWorld().isRemote)
            return;

        if(cart.getCapability(capability.CAPABILITY, null).equals(LazyOptional.empty())) {
            event.addCapability(capability.MINECART_CAPABILITY, new capability(cart));
        }
    }


    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        if(!(event.getEntity() instanceof AbstractMinecartEntity))
            return;

        if(event.getEntity().getEntityWorld().isRemote)
            return;

        LazyOptional<IMinecartCapability> cap = event.getEntity().getCapability(capability.CAPABILITY, null);

        cap.ifPresent(foo -> foo.onSpawn());
    }


    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event){
        if(!(event.getEntityBeingMounted() instanceof AbstractMinecartEntity))
            return;

        if(event.getEntityBeingMounted().getEntityWorld().isRemote)
            return;

        LazyOptional<IMinecartCapability> cap = event.getEntityBeingMounted().getCapability(capability.CAPABILITY, null);

        cap.ifPresent(foo -> foo.onMount(event.getEntityMounting()));
    }


}
