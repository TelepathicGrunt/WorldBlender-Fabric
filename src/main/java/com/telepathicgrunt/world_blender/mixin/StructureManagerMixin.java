package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

/**
 * From Modmuss in voyager
 * https://github.com/modmuss50/Voyager/blob/master/src/main/java/me/modmuss50/voyager/mixin/StructureManagerMixin.java
 *
 * Why isn't this in fapi?!
 */
@Mixin(StructureManager.class)
public class StructureManagerMixin {

	@Shadow
	@Final
	@Mutable
	private Map<Identifier, Structure> structures;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void init(CallbackInfo info) {
		structures = Collections.synchronizedMap(structures);
	}
}
