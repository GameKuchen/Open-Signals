package com.troblecodings.signals.tileentitys;

import com.mojang.blaze3d.vertex.PoseStack;
import com.troblecodings.signals.config.ConfigHandler;
import com.troblecodings.signals.core.RenderAnimationInfo;
import com.troblecodings.signals.core.RenderOverlayInfo;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class SignalSpecialRenderer implements BlockEntityRenderer<SignalTileEntity> {

    private final BlockEntityRendererProvider.Context context;

    public SignalSpecialRenderer(final BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(final SignalTileEntity tile, final float tick, final PoseStack stack,
            final MultiBufferSource source, final int rand1, final int rand2) {
        if (tile.hasCustomName()) {
            tile.renderOverlay(new RenderOverlayInfo(stack, 0, 0, 0, context.getFont()));
        }
        if (tile.getSignal().hasAnimation()) {
            tile.getAnimationHandler().render(new RenderAnimationInfo(stack,
                    context.getBlockRenderDispatcher(), source, rand1, rand2).with(tile));
        }
    }

    @Override
    public boolean shouldRender(final SignalTileEntity tile, final Vec3 pos) {
        if (tile.getSignal().hasAnimation())
            return Vec3.atCenterOf(tile.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(
                    pos.multiply(1.0D, 0.0D, 1.0D), ConfigHandler.CLIENT.renderDistance.get());
        return BlockEntityRenderer.super.shouldRender(tile, pos);
    }
}