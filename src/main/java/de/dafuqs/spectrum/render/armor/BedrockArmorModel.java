package de.dafuqs.spectrum.render.armor;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class BedrockArmorModel {
	public final ModelPart head;
	public final ModelPart body;
	public final ModelPart rightArm;
	public final ModelPart left_arm;
	public final ModelPart rightLeg;
	public final ModelPart leftLeg;
	
	public BedrockArmorModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightArm = root.getChild("right_arm");
		this.left_arm = root.getChild("left_arm");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}
	
	public static MeshDefinition getModelData() {
		MeshDefinition data = new MeshDefinition();
		var root = data.getRoot();
		
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		
		var head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		
		head.addOrReplaceChild(
				"armor_head",
				CubeListBuilder.create()
						.texOffs(0, 20)
						.addBox(-4.5F, -8.5F, -4.5F, 9.0F, 9.0F, 9.0F)
						.texOffs(0, 0)
						.addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F),
				PartPose.ZERO
		);
		
		var body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		
		body.addOrReplaceChild(
				"armor_body",
				CubeListBuilder.create()
						.texOffs(31, 33)
						.addBox(-4.5F, -0.5F, -2.5F, 9.0F, 13.0F, 5.0F)
						.texOffs(36, 20)
						.addBox(-5.0F, 0.0F, -3.0F, 10.0F, 10.0F, 3.0F),
				PartPose.ZERO
		);
		
		var rightArm = root.addOrReplaceChild(
				"right_arm",
				CubeListBuilder.create(),
				PartPose.ZERO
		);

		var armorRightArm = rightArm.addOrReplaceChild(
				"armor_right_arm",
				CubeListBuilder.create()
						.texOffs(22, 51)
						.addBox(-4.25F, -2.5F, -2.5F, 5.0F, 13.0F, 5.0F),
				PartPose.offset(1.0F, 0.0F, 0.0F)
		);

		armorRightArm.addOrReplaceChild(
				"armor_right_arm_extra",
				CubeListBuilder.create()
						.texOffs(57, 45)
						.addBox(-4.0F, -1.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.10F)),
				PartPose.offsetAndRotation(-1.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.4363F)
		);
		
		var leftArm = root.addOrReplaceChild(
				"left_arm",
				CubeListBuilder.create(),
				PartPose.ZERO
		);
		
		var armorLeftArm = leftArm.addOrReplaceChild(
				"armor_left_arm",
				CubeListBuilder.create()
						.texOffs(40, 0)
						.addBox(-1.5F, -2.5F, -2.5F, 5.0F, 13.0F, 5.0F),
				PartPose.ZERO
		);
		
		armorLeftArm.addOrReplaceChild(
				"armor_left_arm_extra",
				CubeListBuilder.create()
						.texOffs(62, 20)
						.addBox(-1.75F, -1.25F, -2.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.10F))
						.texOffs(54, 12)
						.addBox(-1.75F, -0.25F, -2.5F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.10F)),
				PartPose.offsetAndRotation(1.0F, -2.25F, -0.5F, 0.0F, 0.0F, 0.4363F)
		);
		
		var leftLeg = root.addOrReplaceChild(
				"left_leg",
				CubeListBuilder.create(),
				PartPose.ZERO
		);
		
		leftLeg.addOrReplaceChild(
				"armor_left_leg",
				CubeListBuilder.create()
						.texOffs(42, 51)
						.addBox(-2.5F, -0.15F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.15F)),
				PartPose.ZERO
		);
		
		leftLeg.addOrReplaceChild(
				"left_boot",
				CubeListBuilder.create()
						.texOffs(60, 0)
						.addBox(-2.5F, 9.15F, -2.5F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.25F)),
				PartPose.ZERO
		);
		
		var rightLeg = root.addOrReplaceChild(
				"right_leg",
				CubeListBuilder.create(),
				PartPose.ZERO
		);
		
		rightLeg.addOrReplaceChild(
				"armor_right_leg",
				CubeListBuilder.create()
						.texOffs(59, 28)
						.addBox(-2.5F, -0.15F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.149F)),
				PartPose.ZERO
		);
		
		rightLeg.addOrReplaceChild(
				"right_boot",
				CubeListBuilder.create()
						.texOffs(0, 61)
						.addBox(-2.5F, 9.15F, -2.5F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.249F)),
				PartPose.ZERO
		);
		
		return data;
		
	}
	
}
