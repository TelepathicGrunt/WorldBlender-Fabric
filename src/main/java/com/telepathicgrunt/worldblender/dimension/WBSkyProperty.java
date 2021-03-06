package com.telepathicgrunt.worldblender.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class WBSkyProperty extends SkyProperties {
    public WBSkyProperty() {
        super(155, true, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return color.multiply(sunHeight * 0.85F + 0.06F, sunHeight * 0.90F + 0.06F, sunHeight * 0.89F + 0.10F);
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }

}
