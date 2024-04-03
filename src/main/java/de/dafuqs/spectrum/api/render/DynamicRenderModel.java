package de.dafuqs.spectrum.api.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class DynamicRenderModel extends BakedModelWrapper<BakedModel> /*ForwardingBakedModel*/ implements UnbakedModel {
    public BakedModel getWrappedModel() { //todoforge replace this
        return this.originalModel;
    }

    private static class WrappingOverridesList extends ItemOverrides {
        private final ItemOverrides wrapped;
        private WrappingOverridesList(ItemOverrides orig) {
            super(null, null, List.of());
            this.wrapped = orig;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            BakedModel newModel = wrapped.resolve(model, stack, world, entity, seed);
            return newModel == model ? model : new DynamicRenderModel(newModel);
        }
    }
    // only used pre-bake
    private UnbakedModel baseUnbaked;

    // could be used again if pre-bake model problems get figured out
//    public DynamicRenderModel(UnbakedModel base) {
//        super(base);
//        this.baseUnbaked = base;
//    }

    // post-bake post-override constructor
    public DynamicRenderModel(BakedModel base) {
        super(base);
//        this.originalModel = base instanceof DynamicRenderModel fm ? fm.originalModel : base; //todoforge what is happening here
    }

    // avoid FAPI builtin model lookup
    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    private DynamicRenderModel wrap(BakedModel model) { //also what is happening here
//        this.originalModel = model instanceof DynamicRenderModel fm ? fm.originalModel : model;
        return this;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.baseUnbaked.getDependencies();
    }

    // override so wrap persists over override
    // ensures that renderer is called
    @Override
    public ItemOverrides getOverrides() {
        return new WrappingOverridesList(super.getOverrides());
    }

    // return empty transform to prevent double apply in render
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelLoader) {
        this.baseUnbaked.resolveParents(modelLoader);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer, ResourceLocation modelId) {
        return this.wrap(this.baseUnbaked.bake(baker, textureGetter, rotationContainer, modelId));
    }
}
