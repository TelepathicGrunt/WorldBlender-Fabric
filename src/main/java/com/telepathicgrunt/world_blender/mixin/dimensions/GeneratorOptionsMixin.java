package com.telepathicgrunt.world_blender.mixin.dimensions;

import com.telepathicgrunt.world_blender.utils.WorldSeedHolder;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {

	/**
	 * World seed for worldgen when not specified by JSON by Haven King
	 * https://github.com/Hephaestus-Dev/seedy-behavior/blob/master/src/main/java/dev/hephaestus/seedy/mixin/world/gen/GeneratorOptionsMixin.java
	 */
	@Inject(method = "<init>(JZZLnet/minecraft/util/registry/SimpleRegistry;)V",
			at = @At(value = "RETURN"))
	private void giveUsRandomSeeds(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry, CallbackInfo ci) {
		WorldSeedHolder.setSeed(seed);
	}

	@Inject(method = "<init>(JZZLnet/minecraft/util/registry/SimpleRegistry;Ljava/util/Optional;)V",
			at = @At(value = "RETURN"))
	private void giveUsRandomSeeds2(long seed, boolean generateStructures, boolean bonusChest, SimpleRegistry<DimensionOptions> simpleRegistry, Optional<String> legacyCustomOptions, CallbackInfo ci) {
		WorldSeedHolder.setSeed(seed);
	}
}