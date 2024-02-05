package de.dafuqs.spectrum.blocks.mob_head.models;

import de.dafuqs.spectrum.blocks.mob_head.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;

public class CowHeadModel extends SpectrumHeadModel {

    public CowHeadModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 6.0F), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 32);
    }

}