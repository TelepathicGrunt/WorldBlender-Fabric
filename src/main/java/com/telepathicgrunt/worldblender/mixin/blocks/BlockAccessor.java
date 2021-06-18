package com.telepathicgrunt.worldblender.mixin.blocks;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockAccessor {
    @Accessor("FACE_CULL_MAP")
    static ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> worldblender_getFACE_CULL_MAP() {
        throw new UnsupportedOperationException();
    }
}
