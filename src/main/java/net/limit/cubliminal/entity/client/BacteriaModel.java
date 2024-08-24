package net.limit.cubliminal.entity.client;

import net.limit.cubliminal.entity.custom.BacteriaEntity;
import net.limit.cubliminal.init.CubliminalAnimations;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class BacteriaModel<T extends BacteriaEntity> extends SinglePartEntityModel<T> {
	private final ModelPart bacteria;
	private final ModelPart head;

	public BacteriaModel(ModelPart root) {
		this.bacteria = root.getChild("bacteria");
		this.head = bacteria.getChild("body").getChild("torso").getChild("upper_torso").getChild("head");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bacteria = modelPartData.addChild("bacteria", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = bacteria.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = body.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(3.105F, -18.765F, -0.27F));

		ModelPartData cube_r1 = left_leg.addChild("cube_r1", ModelPartBuilder.create().uv(2, 2).cuboid(-0.0065F, -1.3166F, 2.0912F, 0.945F, 6.885F, 0.945F, new Dilation(0.0F)), ModelTransform.of(0.27F, 0.945F, -2.565F, 0.0394F, -0.0971F, -0.4382F));

		ModelPartData cube_r2 = left_leg.addChild("cube_r2", ModelPartBuilder.create().uv(2, 2).cuboid(0.6088F, -1.2326F, -0.1464F, 0.405F, 1.485F, 0.405F, new Dilation(0.0F)), ModelTransform.of(3.375F, 2.7F, 0.0F, -0.0188F, -0.1271F, 2.7164F));

		ModelPartData cube_r3 = left_leg.addChild("cube_r3", ModelPartBuilder.create().uv(2, 2).cuboid(-0.0662F, -2.5826F, -0.1464F, 0.405F, 1.35F, 0.405F, new Dilation(0.0F)), ModelTransform.of(3.375F, 2.7F, 0.0F, -0.08F, -0.1006F, -3.0403F));

		ModelPartData cube_r4 = left_leg.addChild("cube_r4", ModelPartBuilder.create().uv(2, 2).cuboid(-0.5465F, -1.1816F, 2.2262F, 0.81F, 3.51F, 0.945F, new Dilation(0.0F)), ModelTransform.of(0.27F, 0.945F, -2.565F, 0.0218F, -0.1024F, -0.2629F));

		ModelPartData cube_r5 = left_leg.addChild("cube_r5", ModelPartBuilder.create().uv(2, 2).cuboid(0.2038F, 0.6574F, -0.1464F, 0.405F, 1.08F, 0.405F, new Dilation(0.0F)), ModelTransform.of(3.375F, 2.7F, 0.0F, 0.0751F, -0.1043F, 1.9434F));

		ModelPartData left_knee = left_leg.addChild("left_knee", ModelPartBuilder.create(), ModelTransform.pivot(2.835F, 5.805F, 0.405F));

		ModelPartData cube_r6 = left_knee.addChild("cube_r6", ModelPartBuilder.create().uv(2, 2).cuboid(-0.0662F, -4.4726F, -0.1464F, 0.405F, 3.24F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-2.7F, 2.565F, -1.62F, -0.291F, -0.3761F, 0.6317F));

		ModelPartData cube_r7 = left_knee.addChild("cube_r7", ModelPartBuilder.create().uv(2, 2).cuboid(0.6088F, -1.2326F, -0.1464F, 0.405F, 1.485F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-2.7F, 2.565F, -1.62F, -0.0532F, -0.4684F, 0.0651F));

		ModelPartData cube_r8 = left_knee.addChild("cube_r8", ModelPartBuilder.create().uv(2, 2).cuboid(0.2038F, 0.6574F, -0.1464F, 0.405F, 3.375F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-2.7F, 2.565F, -1.62F, 0.304F, -0.3659F, -0.7722F));

		ModelPartData cube_r9 = left_knee.addChild("cube_r9", ModelPartBuilder.create().uv(2, 2).cuboid(5.265F, -14.175F, -0.135F, 0.27F, 7.425F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-5.94F, 16.2F, -3.645F, -0.062F, -0.4674F, 0.0846F));

		ModelPartData cube_r10 = left_knee.addChild("cube_r10", ModelPartBuilder.create().uv(2, 2).cuboid(7.02F, -16.2F, -0.405F, 0.945F, 8.775F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-5.94F, 16.2F, -3.645F, 0.0F, -0.4712F, -0.0524F));

		ModelPartData left_ankle = left_knee.addChild("left_ankle", ModelPartBuilder.create(), ModelTransform.pivot(0.27F, 8.235F, -0.135F));

		ModelPartData cube_r11 = left_ankle.addChild("cube_r11", ModelPartBuilder.create().uv(3, 3).cuboid(6.885F, -7.56F, -1.89F, 1.08F, 2.7F, 0.81F, new Dilation(0.0F)), ModelTransform.of(-6.21F, 7.965F, -3.51F, -0.192F, -0.4712F, -0.0524F));

		ModelPartData left_foot = left_ankle.addChild("left_foot", ModelPartBuilder.create(), ModelTransform.pivot(0.405F, 2.295F, -0.54F));

		ModelPartData cube_r12 = left_foot.addChild("cube_r12", ModelPartBuilder.create().uv(3, 3).cuboid(6.885F, 0.54F, -4.05F, 0.945F, 1.485F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-6.615F, 5.67F, -2.97F, -1.1781F, -0.4712F, -0.0524F));

		ModelPartData cube_r13 = left_foot.addChild("cube_r13", ModelPartBuilder.create().uv(3, 3).cuboid(6.885F, -3.915F, -3.915F, 0.945F, 2.7F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-6.615F, 5.67F, -2.97F, -0.6981F, -0.4712F, -0.0524F));

		ModelPartData torso = body.addChild("torso", ModelPartBuilder.create(), ModelTransform.pivot(0.945F, -30.645F, 0.135F));

		ModelPartData cube_r14 = torso.addChild("cube_r14", ModelPartBuilder.create().uv(2, 2).cuboid(-7.3583F, -0.7451F, 0.1758F, 0.945F, 4.185F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-0.945F, 4.725F, -0.675F, -0.017F, -0.02F, -1.5543F));

		ModelPartData cube_r15 = torso.addChild("cube_r15", ModelPartBuilder.create().uv(2, 2).cuboid(-5.7383F, 2.6299F, 0.1758F, 0.945F, 1.755F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-0.945F, 4.725F, -0.675F, -0.0208F, -0.0161F, -1.3448F));

		ModelPartData upper_torso = torso.addChild("upper_torso", ModelPartBuilder.create(), ModelTransform.pivot(0.405F, 11.745F, -0.135F));

		ModelPartData cube_r16 = upper_torso.addChild("cube_r16", ModelPartBuilder.create().uv(2, 2).cuboid(0.3996F, -2.3161F, -0.5998F, 0.27F, 2.7F, 0.675F, new Dilation(0.0F)), ModelTransform.of(7.83F, -21.6F, -0.54F, -0.0118F, 0.051F, -1.798F));

		ModelPartData cube_r17 = upper_torso.addChild("cube_r17", ModelPartBuilder.create().uv(2, 2).cuboid(-0.5456F, -4.6111F, -0.7277F, 0.81F, 5.805F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-1.485F, -21.465F, -0.27F, -0.1466F, 0.0269F, -1.751F));

		ModelPartData cube_r18 = upper_torso.addChild("cube_r18", ModelPartBuilder.create().uv(2, 2).cuboid(0.3996F, -1.7761F, -0.4648F, 0.135F, 1.755F, 0.405F, new Dilation(0.0F)), ModelTransform.of(7.83F, -21.6F, -0.54F, -0.0205F, 0.0482F, -1.9727F));

		ModelPartData cube_r19 = upper_torso.addChild("cube_r19", ModelPartBuilder.create().uv(2, 2).cuboid(-4.32F, -0.1561F, -0.9397F, 0.81F, 6.75F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-0.27F, -25.515F, -0.54F, 0.0F, 0.0524F, -1.5708F));

		ModelPartData cube_r20 = upper_torso.addChild("cube_r20", ModelPartBuilder.create().uv(3, 3).cuboid(-2.9224F, -3.8878F, -1.4709F, 1.215F, 1.89F, 0.54F, new Dilation(0.0F))
		.uv(3, 3).cuboid(-1.9774F, -4.4278F, -1.3359F, 0.54F, 3.645F, 0.27F, new Dilation(0.0F)), ModelTransform.of(1.08F, -16.74F, 0.405F, -0.0161F, 0.0385F, 0.5054F));

		ModelPartData cube_r21 = upper_torso.addChild("cube_r21", ModelPartBuilder.create().uv(3, 3).cuboid(0.7135F, -4.7506F, -0.4265F, 0.27F, 4.455F, 0.54F, new Dilation(0.0F))
		.uv(3, 3).cuboid(1.9285F, -8.3956F, -0.4265F, 0.675F, 3.78F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.405F, -11.745F, 0.135F, 0.0579F, 0.0224F, -0.4012F));

		ModelPartData cube_r22 = upper_torso.addChild("cube_r22", ModelPartBuilder.create().uv(3, 3).cuboid(-0.6365F, -7.4506F, -0.4265F, 0.27F, 3.51F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.405F, -11.745F, 0.135F, 0.0621F, 0.0013F, -0.0528F));

		ModelPartData cube_r23 = upper_torso.addChild("cube_r23", ModelPartBuilder.create().uv(3, 3).cuboid(1.3885F, -5.6956F, -0.5615F, 0.27F, 3.78F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.405F, -11.745F, 0.135F, 0.0612F, 0.011F, -0.2095F));

		ModelPartData cube_r24 = upper_torso.addChild("cube_r24", ModelPartBuilder.create().uv(3, 3).cuboid(0.5785F, -2.4556F, -0.5615F, 0.27F, 3.78F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.405F, -11.745F, 0.135F, 0.0608F, -0.0127F, 0.1737F));

		ModelPartData cube_r25 = upper_torso.addChild("cube_r25", ModelPartBuilder.create().uv(3, 3).cuboid(-0.3665F, -2.7256F, -0.5615F, 0.405F, 4.59F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.405F, -11.745F, 0.0F, 0.0621F, 0.0013F, -0.0528F));

		ModelPartData cube_r26 = upper_torso.addChild("cube_r26", ModelPartBuilder.create().uv(3, 3).cuboid(3.4226F, 5.2922F, -0.6609F, 0.405F, 3.105F, 0.54F, new Dilation(0.0F)), ModelTransform.of(1.08F, -16.74F, 0.405F, -0.0041F, 0.0066F, 0.8024F));

		ModelPartData cube_r27 = upper_torso.addChild("cube_r27", ModelPartBuilder.create().uv(3, 3).cuboid(-7.7824F, 4.6172F, -0.5259F, 0.405F, 4.995F, 0.54F, new Dilation(0.0F)), ModelTransform.of(1.08F, -16.74F, 0.405F, -0.0417F, 0.0021F, -0.6201F));

		ModelPartData cube_r28 = upper_torso.addChild("cube_r28", ModelPartBuilder.create().uv(3, 3).cuboid(-1.5724F, -4.8328F, -1.2009F, 0.81F, 4.995F, 0.27F, new Dilation(0.0F)), ModelTransform.of(1.08F, -16.74F, 0.405F, -0.0113F, 0.0402F, 0.6276F));

		ModelPartData cube_r29 = upper_torso.addChild("cube_r29", ModelPartBuilder.create().uv(3, 3).cuboid(-1.0324F, -5.7778F, -1.3359F, 0.405F, 6.345F, 0.54F, new Dilation(0.0F)), ModelTransform.of(1.08F, -16.74F, 0.405F, -0.0041F, 0.0415F, 0.8023F));

		ModelPartData cube_r30 = upper_torso.addChild("cube_r30", ModelPartBuilder.create().uv(3, 3).cuboid(3.645F, -11.1375F, 1.6819F, 0.405F, 4.455F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-3.78F, 6.21F, -2.295F, -0.0318F, 0.027F, 0.034F));

		ModelPartData cube_r31 = upper_torso.addChild("cube_r31", ModelPartBuilder.create().uv(3, 3).cuboid(0.0F, -11.9475F, 1.6819F, 0.405F, 5.13F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-3.78F, 6.21F, -2.295F, -0.0243F, 0.0339F, 0.2784F));

		ModelPartData cube_r32 = upper_torso.addChild("cube_r32", ModelPartBuilder.create().uv(2, 2).cuboid(-0.27F, -10.125F, -0.1342F, 0.81F, 10.8F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-0.27F, -12.015F, -0.54F, 0.0524F, 0.0F, 0.0F));

		ModelPartData cube_r33 = upper_torso.addChild("cube_r33", ModelPartBuilder.create().uv(2, 2).cuboid(0.81F, -30.24F, -0.135F, 0.81F, 10.8F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-1.35F, 18.9F, 0.0F, 0.0175F, 0.0F, 0.0F));

		ModelPartData cube_r34 = upper_torso.addChild("cube_r34", ModelPartBuilder.create().uv(2, 2).cuboid(-1.0856F, -5.0161F, -0.5927F, 0.405F, 6.21F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-1.485F, -21.465F, -0.27F, -0.1491F, 0.0011F, -1.9236F));

		ModelPartData head = upper_torso.addChild("head", ModelPartBuilder.create().uv(1, 1).cuboid(-1.62F, -0.405F, 0.945F, 2.565F, 0.54F, 0.405F, new Dilation(0.0F))
		.uv(1, 1).cuboid(-1.755F, -0.405F, 0.27F, 2.835F, 0.54F, 0.675F, new Dilation(0.0F))
		.uv(1, 1).cuboid(-2.16F, -0.405F, -0.405F, 3.645F, 0.54F, 0.675F, new Dilation(0.0F))
		.uv(1, 1).cuboid(-2.295F, -0.405F, -1.08F, 3.915F, 0.54F, 0.675F, new Dilation(0.0F))
		.uv(1, 1).cuboid(-2.43F, -0.405F, -6.48F, 4.185F, 0.54F, 3.78F, new Dilation(0.0F))
		.uv(1, 1).cuboid(-2.295F, -0.405F, -2.7F, 3.915F, 0.54F, 1.62F, new Dilation(0.0F)), ModelTransform.pivot(-0.135F, -22.275F, -0.675F));

		ModelPartData cube_r35 = head.addChild("cube_r35", ModelPartBuilder.create().uv(3, 3).cuboid(-0.4616F, -2.5344F, -0.1525F, 0.405F, 2.835F, 0.54F, new Dilation(0.0F)), ModelTransform.of(2.7F, -2.97F, 1.62F, 0.164F, -1.0185F, -1.9578F));

		ModelPartData cube_r36 = head.addChild("cube_r36", ModelPartBuilder.create().uv(3, 3).cuboid(1.674F, -3.015F, -1.1821F, 0.405F, 0.945F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.4F, -0.945F, -0.54F, -0.5783F, -0.9045F, -1.0729F));

		ModelPartData cube_r37 = head.addChild("cube_r37", ModelPartBuilder.create().uv(3, 3).cuboid(0.5265F, -2.88F, -1.0471F, 0.405F, 0.945F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.4F, -0.945F, -0.54F, -0.8621F, -0.6072F, -0.5931F));

		ModelPartData cube_r38 = head.addChild("cube_r38", ModelPartBuilder.create().uv(3, 3).cuboid(0.0645F, -2.34F, -0.5071F, 0.405F, 0.945F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.4F, -0.945F, -0.54F, -0.6808F, -0.4287F, -0.4314F));

		ModelPartData cube_r39 = head.addChild("cube_r39", ModelPartBuilder.create().uv(3, 3).cuboid(-0.6105F, -1.53F, -0.1021F, 0.405F, 0.945F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.4F, -0.945F, -0.54F, -0.5843F, -0.2001F, -0.0177F));

		ModelPartData cube_r40 = head.addChild("cube_r40", ModelPartBuilder.create().uv(3, 3).cuboid(-0.8805F, -0.315F, 0.0329F, 0.405F, 1.215F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.4F, -0.945F, -0.54F, -0.2456F, 0.0511F, 0.6064F));

		ModelPartData cube_r41 = head.addChild("cube_r41", ModelPartBuilder.create().uv(3, 3).cuboid(-5.3524F, -4.9678F, -1.3359F, 0.405F, 4.455F, 0.54F, new Dilation(0.0F)), ModelTransform.of(1.215F, 5.535F, 1.08F, 0.0854F, -0.0048F, 1.3026F));

		ModelPartData cube_r42 = head.addChild("cube_r42", ModelPartBuilder.create().uv(3, 3).cuboid(-3.3103F, -3.9117F, 0.5717F, 0.27F, 1.485F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-2.295F, 0.405F, 0.81F, 1.0426F, -0.8275F, -0.1598F));

		ModelPartData cube_r43 = head.addChild("cube_r43", ModelPartBuilder.create().uv(3, 3).cuboid(-1.5553F, -3.9117F, 0.1667F, 0.27F, 1.08F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-2.295F, 0.405F, 0.81F, 1.0817F, -0.3738F, -0.5255F));

		ModelPartData cube_r44 = head.addChild("cube_r44", ModelPartBuilder.create().uv(3, 3).cuboid(0.0647F, -3.2367F, 0.1667F, 0.27F, 0.945F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-2.295F, 0.405F, 0.81F, 1.1364F, 0.0513F, -0.8552F));

		ModelPartData cube_r45 = head.addChild("cube_r45", ModelPartBuilder.create().uv(3, 3).cuboid(0.0647F, -2.5617F, -1.0483F, 0.27F, 1.62F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-2.295F, 0.405F, 0.81F, 0.6477F, 0.0513F, -0.8552F));

		ModelPartData cube_r46 = head.addChild("cube_r46", ModelPartBuilder.create().uv(3, 3).cuboid(9.7585F, -4.4806F, -0.0215F, 0.27F, 2.565F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-0.27F, 10.53F, 0.81F, 0.2233F, 0.0606F, -1.3788F));

		ModelPartData cube_r47 = head.addChild("cube_r47", ModelPartBuilder.create().uv(2, 2).cuboid(-0.945F, -3.78F, 0.243F, 1.215F, 0.945F, 0.0F, new Dilation(0.0F))
		.uv(2, 2).cuboid(-1.755F, -3.24F, 0.189F, 0.945F, 0.405F, 0.0F, new Dilation(0.0F))
		.uv(2, 2).cuboid(-2.16F, -2.835F, 0.189F, 1.215F, 1.08F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 0.9425F));

		ModelPartData cube_r48 = head.addChild("cube_r48", ModelPartBuilder.create().uv(2, 2).cuboid(2.835F, -2.565F, 0.189F, 1.08F, 0.675F, 0.0F, new Dilation(0.0F))
		.uv(2, 2).cuboid(3.105F, -2.025F, 0.2295F, 0.945F, 1.485F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 0.0349F));

		ModelPartData cube_r49 = head.addChild("cube_r49", ModelPartBuilder.create().uv(2, 2).cuboid(-0.27F, -3.78F, 0.189F, 0.675F, 2.295F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 1.3439F));

		ModelPartData cube_r50 = head.addChild("cube_r50", ModelPartBuilder.create().uv(2, 2).cuboid(-0.675F, -2.025F, 0.2295F, 0.81F, 1.89F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 1.1868F));

		ModelPartData cube_r51 = head.addChild("cube_r51", ModelPartBuilder.create().uv(2, 2).cuboid(-1.35F, -0.27F, 0.189F, 0.945F, 1.08F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 1.309F));

		ModelPartData cube_r52 = head.addChild("cube_r52", ModelPartBuilder.create().uv(2, 2).cuboid(-2.16F, -3.105F, 0.2295F, 1.08F, 0.81F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 0.6458F));

		ModelPartData cube_r53 = head.addChild("cube_r53", ModelPartBuilder.create().uv(2, 2).cuboid(-1.755F, -3.105F, 0.135F, 0.405F, 2.295F, 0.0F, new Dilation(0.0F))
		.uv(2, 2).cuboid(-1.35F, -3.375F, 0.135F, 0.405F, 2.97F, 0.0F, new Dilation(0.0F))
		.uv(2, 2).cuboid(-1.08F, -0.54F, 0.2295F, 1.08F, 0.675F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 0.0F, -6.75F, 0.0F, 0.0F, 0.3142F));

		ModelPartData cube_r54 = head.addChild("cube_r54", ModelPartBuilder.create().uv(0, 0).cuboid(4.1972F, -2.7675F, 3.2052F, 0.54F, 3.24F, 1.215F, new Dilation(0.0F))
		.uv(0, 0).cuboid(4.1972F, -2.4975F, 4.4202F, 0.54F, 2.43F, 1.215F, new Dilation(0.0F))
		.uv(0, 0).cuboid(4.1972F, -3.3075F, -2.4648F, 0.54F, 3.375F, 5.67F, new Dilation(0.0F)), ModelTransform.of(0.54F, -7.56F, -4.05F, -0.0741F, 0.0462F, 1.9182F));

		ModelPartData cube_r55 = head.addChild("cube_r55", ModelPartBuilder.create().uv(0, 0).cuboid(-1.917F, -2.43F, -3.564F, 0.54F, 0.405F, 1.89F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-1.917F, -2.025F, -3.699F, 0.54F, 0.945F, 2.7F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-1.917F, -1.08F, -3.699F, 0.54F, 1.35F, 2.97F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, 0.0F, 1.5882F, 0.0F));

		ModelPartData cube_r56 = head.addChild("cube_r56", ModelPartBuilder.create().uv(0, 0).cuboid(-4.752F, -0.81F, -2.1562F, 0.54F, 1.08F, 2.565F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, 0.2052F, 0.3269F, 0.575F));

		ModelPartData cube_r57 = head.addChild("cube_r57", ModelPartBuilder.create().uv(0, 0).cuboid(-0.0476F, -2.025F, -0.4455F, 0.54F, 1.08F, 2.4975F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, 0.1341F, -0.3609F, -0.3648F));

		ModelPartData cube_r58 = head.addChild("cube_r58", ModelPartBuilder.create().uv(0, 0).cuboid(-4.212F, -2.295F, -2.1562F, 0.54F, 1.215F, 2.565F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, -0.0071F, 0.3839F, -0.0188F));

		ModelPartData cube_r59 = head.addChild("cube_r59", ModelPartBuilder.create().uv(0, 0).cuboid(-0.861F, -4.725F, -1.4769F, 0.27F, 0.27F, 3.645F, new Dilation(0.0F)), ModelTransform.of(-0.27F, -7.965F, -5.13F, -0.0162F, -0.1229F, -2.5841F));

		ModelPartData cube_r60 = head.addChild("cube_r60", ModelPartBuilder.create().uv(0, 0).cuboid(4.2107F, -0.3375F, -2.4648F, 0.5265F, 1.35F, 5.67F, new Dilation(0.0F)), ModelTransform.of(0.54F, -7.56F, -4.05F, 0.083F, 0.0462F, 1.9182F));

		ModelPartData cube_r61 = head.addChild("cube_r61", ModelPartBuilder.create().uv(0, 0).cuboid(-0.996F, -6.075F, -1.6119F, 0.54F, 1.485F, 5.67F, new Dilation(0.0F)), ModelTransform.of(-0.27F, -7.965F, -5.13F, -0.0514F, -0.0706F, -2.5813F));

		ModelPartData cube_r62 = head.addChild("cube_r62", ModelPartBuilder.create().uv(0, 0).cuboid(-2.4178F, -6.75F, -2.5998F, 0.54F, 1.35F, 5.67F, new Dilation(0.0F)), ModelTransform.of(-0.675F, 4.185F, -4.05F, 0.0F, 0.0873F, 0.0F));

		ModelPartData cube_r63 = head.addChild("cube_r63", ModelPartBuilder.create().uv(0, 0).cuboid(3.189F, -6.1425F, -1.6119F, 0.54F, 1.35F, 5.67F, new Dilation(0.0F)), ModelTransform.of(0.135F, 4.32F, -5.13F, 0.0091F, -0.0868F, -0.2447F));

		ModelPartData cube_r64 = head.addChild("cube_r64", ModelPartBuilder.create().uv(0, 0).cuboid(-4.077F, -1.755F, -2.1562F, 0.54F, 1.35F, 2.565F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, -0.0631F, 0.379F, -0.1692F));

		ModelPartData cube_r65 = head.addChild("cube_r65", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5876F, -1.08F, -0.4455F, 0.54F, 1.35F, 2.4975F, new Dilation(0.0F)), ModelTransform.of(1.89F, -0.405F, -0.405F, -0.0735F, -0.3772F, 0.1973F));

		ModelPartData cube_r66 = head.addChild("cube_r66", ModelPartBuilder.create().uv(0, 0).cuboid(0.219F, -6.21F, -1.6119F, 0.54F, 1.35F, 5.67F, new Dilation(0.0F)), ModelTransform.of(-0.675F, 4.05F, -5.13F, -0.037F, -0.0791F, 0.4378F));

		ModelPartData cube_r67 = head.addChild("cube_r67", ModelPartBuilder.create().uv(0, 0).cuboid(-0.2578F, -5.94F, -2.5998F, 0.54F, 1.35F, 5.67F, new Dilation(0.0F)), ModelTransform.of(-0.675F, 4.185F, -4.05F, -0.0328F, 0.0809F, -0.3853F));

		ModelPartData right_arm = upper_torso.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(-5.6217F, -20.3415F, 0.7054F));

		ModelPartData cube_r68 = right_arm.addChild("cube_r68", ModelPartBuilder.create().uv(2, 2).cuboid(-1.5078F, -4.0383F, -0.5221F, 0.135F, 2.97F, 0.81F, new Dilation(0.0F)), ModelTransform.of(0.0867F, -0.4485F, -0.4354F, -0.0556F, -0.116F, -2.7127F));

		ModelPartData cube_r69 = right_arm.addChild("cube_r69", ModelPartBuilder.create().uv(2, 2).cuboid(0.5172F, -4.4433F, -0.6571F, 0.135F, 4.32F, 0.945F, new Dilation(0.0F)), ModelTransform.of(0.0867F, -0.4485F, -0.4354F, -0.0191F, -0.1272F, -3.0114F));

		ModelPartData cube_r70 = right_arm.addChild("cube_r70", ModelPartBuilder.create().uv(2, 2).cuboid(-6.6035F, 9.8612F, -3.4083F, 0.27F, 1.215F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-12.4683F, 27.9015F, 0.7796F, 0.1715F, -0.1852F, -2.9357F));

		ModelPartData cube_r71 = right_arm.addChild("cube_r71", ModelPartBuilder.create().uv(2, 2).cuboid(-0.067F, -2.1221F, -0.0833F, 0.54F, 2.7F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-3.6933F, 16.9665F, -1.3804F, 0.1623F, -0.1882F, -2.7509F));

		ModelPartData cube_r72 = right_arm.addChild("cube_r72", ModelPartBuilder.create().uv(2, 2).cuboid(-0.13F, -9.214F, -0.6382F, 0.81F, 6.75F, 0.945F, new Dilation(0.0F))
		.uv(4, 4).cuboid(0.005F, -2.464F, -0.5032F, 0.54F, 4.05F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-1.2633F, 7.6515F, -0.4354F, 0.0571F, -0.125F, -2.9235F));

		ModelPartData cube_r73 = right_arm.addChild("cube_r73", ModelPartBuilder.create().uv(2, 2).cuboid(-0.5628F, -6.6033F, -0.6571F, 0.81F, 6.75F, 0.945F, new Dilation(0.0F)), ModelTransform.of(0.0867F, -0.4485F, -0.4354F, -0.0301F, -0.125F, -2.9235F));

		ModelPartData right_elbow = right_arm.addChild("right_elbow", ModelPartBuilder.create(), ModelTransform.pivot(-4.6383F, 18.5865F, -1.2454F));

		ModelPartData cube_r74 = right_elbow.addChild("cube_r74", ModelPartBuilder.create().uv(2, 2).cuboid(-5.6585F, 5.5412F, -3.4083F, 0.27F, 2.025F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-7.83F, 9.315F, 2.025F, 0.1616F, -0.1939F, -2.8832F));

		ModelPartData cube_r75 = right_elbow.addChild("cube_r75", ModelPartBuilder.create().uv(2, 2).cuboid(-6.8735F, 0.9512F, -3.4083F, 0.27F, 2.025F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-7.83F, 9.315F, 2.025F, 0.2085F, -0.1422F, 3.1216F));

		ModelPartData cube_r76 = right_elbow.addChild("cube_r76", ModelPartBuilder.create().uv(2, 2).cuboid(-0.2275F, -7.054F, -0.8108F, 0.54F, 9.45F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-0.405F, 2.43F, -0.135F, 0.1977F, -0.1506F, -2.96F));

		ModelPartData right_hand = right_elbow.addChild("right_hand", ModelPartBuilder.create(), ModelTransform.pivot(-2.025F, 9.045F, -1.89F));

		ModelPartData cube_r77 = right_hand.addChild("cube_r77", ModelPartBuilder.create().uv(2, 2).cuboid(0.9766F, -1.7417F, -0.1247F, 0.81F, 2.295F, 0.675F, new Dilation(0.0F)), ModelTransform.of(1.35F, 0.54F, -0.405F, 0.234F, -0.0827F, 3.0143F));

		ModelPartData cube_r78 = right_hand.addChild("cube_r78", ModelPartBuilder.create().uv(2, 2).cuboid(-0.9459F, -3.2071F, -0.0502F, 0.27F, 3.24F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-0.27F, 1.62F, -0.405F, 0.2209F, -0.0636F, 2.8301F));

		ModelPartData cube_r79 = right_hand.addChild("cube_r79", ModelPartBuilder.create().uv(2, 2).cuboid(-0.135F, -2.9237F, -0.3857F, 0.27F, 3.105F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-0.54F, 2.835F, -0.405F, 0.0656F, -0.1684F, -2.9727F));

		ModelPartData cube_r80 = right_hand.addChild("cube_r80", ModelPartBuilder.create().uv(2, 2).cuboid(-0.2075F, -3.1298F, -0.1854F, 0.27F, 3.51F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-1.485F, 2.43F, -0.405F, -0.031F, -0.2028F, -2.6542F));

		ModelPartData cube_r81 = right_hand.addChild("cube_r81", ModelPartBuilder.create().uv(2, 2).cuboid(0.8416F, -1.8767F, -0.1247F, 0.54F, 2.7F, 0.675F, new Dilation(0.0F)), ModelTransform.of(1.35F, 0.54F, -0.405F, 0.2463F, -0.0285F, 2.7936F));

		ModelPartData cube_r82 = right_hand.addChild("cube_r82", ModelPartBuilder.create().uv(2, 2).cuboid(-0.2275F, -9.754F, -0.8108F, 0.54F, 2.7F, 0.675F, new Dilation(0.0F)), ModelTransform.of(1.62F, -6.615F, 1.755F, 0.1977F, -0.1506F, -2.96F));

		ModelPartData cube_r83 = right_hand.addChild("cube_r83", ModelPartBuilder.create().uv(2, 2).cuboid(0.4041F, -1.1821F, -0.3202F, 0.405F, 2.7F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-0.27F, 1.62F, -0.405F, 0.1435F, -0.2028F, -2.6542F));

		ModelPartData left_arm = upper_torso.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(6.075F, -21.465F, -0.945F));

		ModelPartData cube_r84 = left_arm.addChild("cube_r84", ModelPartBuilder.create().uv(2, 2).cuboid(-1.485F, 9.7036F, -0.3375F, 0.27F, 4.59F, 0.27F, new Dilation(0.0F)), ModelTransform.of(2.43F, 14.175F, -0.27F, 0.0402F, -0.1237F, 2.8055F));

		ModelPartData cube_r85 = left_arm.addChild("cube_r85", ModelPartBuilder.create().uv(2, 2).cuboid(1.08F, 3.3586F, -0.3375F, 0.27F, 6.345F, 0.27F, new Dilation(0.0F)), ModelTransform.of(2.43F, 14.175F, -0.27F, 0.0067F, -0.1299F, 3.0693F));

		ModelPartData cube_r86 = left_arm.addChild("cube_r86", ModelPartBuilder.create().uv(2, 2).cuboid(0.54F, -2.8514F, -0.3375F, 0.27F, 6.345F, 0.27F, new Dilation(0.0F)), ModelTransform.of(2.43F, 14.175F, -0.27F, 0.027F, -0.1273F, 2.911F));

		ModelPartData cube_r87 = left_arm.addChild("cube_r87", ModelPartBuilder.create().uv(2, 2).cuboid(-0.405F, -5.5514F, -0.4725F, 0.81F, 6.21F, 0.945F, new Dilation(0.0F)), ModelTransform.of(2.43F, 14.175F, -0.27F, 0.0961F, -0.1358F, 3.0514F));

		ModelPartData cube_r88 = left_arm.addChild("cube_r88", ModelPartBuilder.create().uv(2, 2).cuboid(-12.0378F, -15.2433F, -0.1171F, 0.81F, 14.31F, 0.945F, new Dilation(0.0F)), ModelTransform.of(-11.61F, 0.675F, 1.215F, 0.0203F, -0.127F, 2.9639F));

		ModelPartData left_elbow = left_arm.addChild("left_elbow", ModelPartBuilder.create(), ModelTransform.pivot(3.105F, 19.845F, -0.54F));

		ModelPartData cube_r89 = left_elbow.addChild("cube_r89", ModelPartBuilder.create().uv(2, 2).cuboid(0.135F, -15.2714F, -0.3375F, 0.27F, 9.045F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-0.675F, -5.67F, 0.27F, 0.1053F, -0.1288F, 2.9814F));

		ModelPartData cube_r90 = left_elbow.addChild("cube_r90", ModelPartBuilder.create().uv(2, 2).cuboid(-0.405F, -15.8114F, -0.3375F, 0.27F, 4.05F, 0.675F, new Dilation(0.0F))
		.uv(2, 2).cuboid(-0.135F, -9.0614F, -0.3375F, 0.27F, 3.51F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-0.675F, -5.67F, 0.27F, 0.0961F, -0.1358F, 3.0514F));

		ModelPartData cube_r91 = left_elbow.addChild("cube_r91", ModelPartBuilder.create().uv(2, 2).cuboid(0.0525F, -1.8061F, -0.2404F, 0.27F, 3.51F, 0.675F, new Dilation(0.0F)), ModelTransform.of(0.27F, 4.32F, -0.81F, 0.114F, -0.1212F, 2.9114F));

		ModelPartData left_hand = left_elbow.addChild("left_hand", ModelPartBuilder.create(), ModelTransform.pivot(0.675F, 9.72F, -1.08F));

		ModelPartData cube_r92 = left_hand.addChild("cube_r92", ModelPartBuilder.create().uv(2, 2).cuboid(-1.3351F, -3.1453F, 0.0939F, 0.27F, 1.215F, 0.405F, new Dilation(0.0F)), ModelTransform.of(0.54F, 2.835F, -0.405F, -0.197F, -0.1585F, 3.114F));

		ModelPartData cube_r93 = left_hand.addChild("cube_r93", ModelPartBuilder.create().uv(2, 2).cuboid(-0.9301F, -2.8753F, 0.2289F, 0.27F, 0.54F, 0.405F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.16F, -0.81F, -0.0857F, -0.1365F, -2.8633F));

		ModelPartData cube_r94 = left_hand.addChild("cube_r94", ModelPartBuilder.create().uv(2, 2).cuboid(-0.6601F, -2.2003F, 0.2289F, 0.27F, 4.995F, 0.405F, new Dilation(0.0F)), ModelTransform.of(0.54F, 2.835F, -0.405F, -0.055F, -0.1846F, 2.7962F));

		ModelPartData cube_r95 = left_hand.addChild("cube_r95", ModelPartBuilder.create().uv(2, 2).cuboid(-0.2551F, -2.6053F, 0.2289F, 0.27F, 3.105F, 0.405F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.16F, -0.81F, -0.0445F, -0.1548F, 3.1383F));

		ModelPartData cube_r96 = left_hand.addChild("cube_r96", ModelPartBuilder.create().uv(2, 2).cuboid(-19.9175F, 9.6952F, 4.1346F, 0.27F, 0.81F, 0.405F, new Dilation(0.0F)), ModelTransform.of(-23.625F, 1.62F, -0.27F, -0.031F, -0.2028F, -2.6542F));

		ModelPartData cube_r97 = left_hand.addChild("cube_r97", ModelPartBuilder.create().uv(2, 2).cuboid(0.2681F, -0.8594F, -0.4104F, 0.54F, 1.485F, 0.675F, new Dilation(0.0F)), ModelTransform.of(0.675F, 0.81F, -0.27F, 0.2396F, -0.0643F, 2.9377F));

		ModelPartData cube_r98 = left_hand.addChild("cube_r98", ModelPartBuilder.create().uv(2, 2).cuboid(-19.4409F, 9.6179F, 2.1098F, 0.54F, 2.025F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-22.41F, 0.81F, -0.27F, 0.1435F, -0.2028F, -2.6542F));

		ModelPartData right_leg = body.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-0.675F, -18.9F, -0.27F));

		ModelPartData cube_r99 = right_leg.addChild("cube_r99", ModelPartBuilder.create().uv(3, 3).cuboid(-0.27F, -6.8175F, 1.4119F, 0.405F, 2.295F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 6.21F, -2.025F, -0.0917F, 0.0355F, 0.3482F));

		ModelPartData cube_r100 = right_leg.addChild("cube_r100", ModelPartBuilder.create().uv(3, 3).cuboid(-1.751F, -4.2888F, 1.4119F, 0.405F, 1.755F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 6.21F, -2.025F, -0.0752F, 0.0634F, 0.6791F));

		ModelPartData cube_r101 = right_leg.addChild("cube_r101", ModelPartBuilder.create().uv(3, 3).cuboid(-2.831F, -1.5888F, 1.4119F, 0.405F, 1.755F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 6.21F, -2.025F, -0.0382F, 0.0907F, 1.151F));

		ModelPartData cube_r102 = right_leg.addChild("cube_r102", ModelPartBuilder.create().uv(3, 3).cuboid(-0.3469F, -4.0427F, -1.9382F, 0.81F, 6.75F, 0.81F, new Dilation(0.0F)), ModelTransform.of(-1.485F, 3.78F, 0.27F, -0.5492F, 1.4206F, 0.1686F));

		ModelPartData cube_r103 = right_leg.addChild("cube_r103", ModelPartBuilder.create().uv(3, 3).cuboid(-4.32F, -5.8725F, 1.5469F, 0.405F, 2.295F, 0.54F, new Dilation(0.0F)), ModelTransform.of(-1.755F, 6.21F, -2.025F, -0.0358F, 0.0916F, 1.1772F));

		ModelPartData right_knee = right_leg.addChild("right_knee", ModelPartBuilder.create(), ModelTransform.of(-4.185F, 4.86F, -0.135F, 0.0873F, -0.3316F, 0.0F));

		ModelPartData cube_r104 = right_knee.addChild("cube_r104", ModelPartBuilder.create().uv(3, 3).cuboid(-4.995F, -16.875F, 0.54F, 0.405F, 1.755F, 0.54F, new Dilation(0.0F)), ModelTransform.of(3.645F, 15.525F, -5.67F, -0.1933F, 0.6F, -0.1383F));

		ModelPartData cube_r105 = right_knee.addChild("cube_r105", ModelPartBuilder.create().uv(3, 3).cuboid(-6.345F, -15.795F, 0.54F, 0.405F, 1.755F, 0.54F, new Dilation(0.0F))
		.uv(3, 3).cuboid(-7.155F, -15.795F, 0.405F, 0.81F, 10.8F, 0.81F, new Dilation(0.0F)), ModelTransform.of(3.645F, 15.525F, -5.67F, -0.0841F, 0.6218F, 0.0517F));

		ModelPartData cube_r106 = right_knee.addChild("cube_r106", ModelPartBuilder.create().uv(2, 2).cuboid(0.0903F, -1.6302F, -1.9992F, 0.27F, 1.62F, 0.675F, new Dilation(0.0F)), ModelTransform.of(0.81F, 2.7F, 1.35F, 0.1043F, -0.1304F, 2.9989F));

		ModelPartData cube_r107 = right_knee.addChild("cube_r107", ModelPartBuilder.create().uv(2, 2).cuboid(-12.9485F, -2.6938F, -0.1683F, 0.27F, 2.295F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-12.015F, 2.7F, 1.35F, 0.0722F, -0.1504F, -3.056F));

		ModelPartData cube_r108 = right_knee.addChild("cube_r108", ModelPartBuilder.create().uv(2, 2).cuboid(-13.2185F, -1.0738F, -0.1683F, 0.27F, 2.295F, 0.675F, new Dilation(0.0F)), ModelTransform.of(-12.015F, 2.7F, 1.35F, 0.0247F, -0.1649F, -2.7559F));

		ModelPartData right_ankle = right_knee.addChild("right_ankle", ModelPartBuilder.create(), ModelTransform.pivot(-0.945F, 10.26F, -0.675F));

		ModelPartData cube_r109 = right_ankle.addChild("cube_r109", ModelPartBuilder.create().uv(3, 3).cuboid(-7.83F, -4.32F, -1.215F, 0.81F, 2.7F, 0.81F, new Dilation(0.0F)), ModelTransform.of(4.59F, 5.265F, -4.995F, -0.3639F, 0.6236F, 0.2275F));

		ModelPartData right_foot = right_ankle.addChild("right_foot", ModelPartBuilder.create(), ModelTransform.pivot(-0.945F, 1.62F, -0.675F));

		ModelPartData cube_r110 = right_foot.addChild("cube_r110", ModelPartBuilder.create().uv(3, 3).cuboid(-7.83F, 1.62F, -1.215F, 0.81F, 1.485F, 0.54F, new Dilation(0.0F)), ModelTransform.of(5.13F, 2.025F, -5.4F, -1.2883F, 0.7309F, 0.0716F));

		ModelPartData cube_r111 = right_foot.addChild("cube_r111", ModelPartBuilder.create().uv(2, 2).cuboid(-7.83F, -1.08F, -1.62F, 0.81F, 2.7F, 0.945F, new Dilation(0.0F)), ModelTransform.of(5.13F, 2.025F, -5.4F, -1.0788F, 0.7309F, 0.0716F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(BacteriaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, headPitch);
		this.animateMovement(CubliminalAnimations.BACTERIA_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.updateAnimation(entity.idleAnimationState, CubliminalAnimations.BACTERIA_IDLE, ageInTicks, 1f);
		this.updateAnimation(entity.attackAnimationState, CubliminalAnimations.BACTERIA_ATTACK, ageInTicks, 1f);
	}
	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
		headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bacteria.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return bacteria;
	}
}
