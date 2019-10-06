package ben_mkiv.minecarttweaks;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> ProtectBlocks;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            ProtectBlocks = builder
                    .comment("protect blocks")
                    .translation("config.protectblocks")
                    .define("protectBlocks", true);

            builder.pop();
        }
    }
}