package com.telepathicgrunt.world_blender.mixin;

import net.minecraft.util.collection.WeightedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(WeightedList.class)
public class WeightedListMixin<U> {

	@Mutable
	@Final
	@Shadow
	protected List<WeightedList.Entry<U>> entries;

	@Inject(method = "<init>",
			at = @At(value = "RETURN"))
	private void synchronizedlist(CallbackInfo ci) {
		entries = Collections.synchronizedList(entries);
	}
}
