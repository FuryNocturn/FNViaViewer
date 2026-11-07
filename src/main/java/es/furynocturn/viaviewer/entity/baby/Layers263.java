package es.furynocturn.viaviewer.entity.baby;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometria de las crias de 26.3, copiada tal cual de las clases Baby*Model (y SniffletModel) del
 * cliente 26.3. Solo datos: cubos, pivotes y tamanos de textura. Sacado del cliente 26.3 decompilado con Vineflower.
 */
@SuppressWarnings("unused")
final class Layers263 {

    private Layers263() {
    }

    /** BabyArmadilloModel de 26.3. */
    static final class BabyArmadilloModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.5F, -2.0F, -3.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.3F))
                .texOffs(0, 11)
                .addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 6.0F),
             PartPose.offset(0.0F, 20.0F, 0.5F)
          );
          PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.4F));
          tail.addOrReplaceChild(
             "right_ear_cube",
             CubeListBuilder.create().texOffs(22, 11).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F),
             PartPose.offsetAndRotation(0.0F, 1.5F, 1.0F, -1.0472F, 0.0F, 0.0F)
          );
          PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -3.2F));
          PartDefinition headGroup = head.addOrReplaceChild(
             "head_cube",
             CubeListBuilder.create().texOffs(20, 17).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 4.0F),
             PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7417649F, 0.0F, 0.0F)
          );
          headGroup.addOrReplaceChild(
             "right_ear",
             CubeListBuilder.create().texOffs(28, 8).mirror().addBox(-1.8F, -2.0F, 0.0F, 2.0F, 3.0F, 0.0F).mirror(false),
             PartPose.offsetAndRotation(-1.0F, -2.0F, -0.3F, -0.4363F, -0.1134F, 0.0524F)
          );
          headGroup.addOrReplaceChild(
             "left_ear",
             CubeListBuilder.create().texOffs(28, 8).addBox(-0.2F, -2.0F, 0.0F, 2.0F, 3.0F, 0.0F),
             PartPose.offsetAndRotation(1.0F, -2.0F, -0.3F, -0.4363F, 0.1134F, -0.0524F)
          );
          root.addOrReplaceChild(
             "right_hind_leg",
             CubeListBuilder.create().texOffs(20, 27).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F).mirror(false),
             PartPose.offset(-1.5F, 22.0F, 2.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(20, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(1.5F, 22.0F, 2.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(20, 23).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(1.5F, 22.0F, -1.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg",
             CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F).mirror(false),
             PartPose.offset(-1.5F, 22.0F, -1.5F)
          );
          root.addOrReplaceChild(
             "cube",
             CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.3F)),
             PartPose.offset(0.0F, 20.7F, 0.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyAxolotlModel de 26.3. */
    static final class BabyAxolotlModel {
       private static float MAX_WALK_ANIMATION_SPEED = 15.0F;


       private static float WALK_ANIMATION_SCALE_FACTOR = 30.0F;


       public static LayerDefinition createBodyLayer() {
          MeshDefinition meshdefinition = new MeshDefinition();
          PartDefinition partdefinition = meshdefinition.getRoot();
          PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.0F, -0.75F, -2.75F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 12)
                .addBox(0.0F, -1.75F, -2.75F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -1.25F, 1.75F)
          );
          body.addOrReplaceChild(
             "right_front_leg",
             CubeListBuilder.create().texOffs(20, 16).addBox(-3.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-2.0F, 0.25F, -1.25F)
          );
          PartDefinition right_leg = body.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 0.25F, 1.75F, 0.0F, 1.5708F, 1.5708F)
          );
          right_leg.addOrReplaceChild(
             "right_leg_r1",
             CubeListBuilder.create().texOffs(20, 14).addBox(0.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 1.5708F)
          );
          body.addOrReplaceChild(
             "left_front_leg",
             CubeListBuilder.create().texOffs(20, 13).addBox(0.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.0F, 0.25F, -1.25F)
          );
          body.addOrReplaceChild(
             "left_hind_leg",
             CubeListBuilder.create().texOffs(20, 14).addBox(0.0F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.0F, 0.25F, 1.75F)
          );
          body.addOrReplaceChild(
             "tail",
             CubeListBuilder.create().texOffs(10, 9).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -0.25F, 3.25F)
          );
          PartDefinition head = body.addOrReplaceChild(
             "head",
             CubeListBuilder.create().texOffs(0, 8).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 0.25F, -2.75F)
          );
          head.addOrReplaceChild(
             "left_gills",
             CubeListBuilder.create().texOffs(20, 8).addBox(0.0F, -3.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(3.0F, -0.5F, -2.0F)
          );
          head.addOrReplaceChild(
             "right_gills",
             CubeListBuilder.create().texOffs(20, 3).addBox(-3.0F, -3.5F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-3.0F, -0.5F, -2.0F)
          );
          head.addOrReplaceChild(
             "top_gills",
             CubeListBuilder.create().texOffs(20, 0).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -2.0F, -2.0F)
          );
          return LayerDefinition.create(meshdefinition, 32, 32);
       }
    }

    /** BabyBeeModel de 26.3. */
    static final class BabyBeeModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition bone = root.addOrReplaceChild(
             "bone",
             CubeListBuilder.create()
                .texOffs(6, 12)
                .addBox(1.0F, -1.6667F, -2.1633F, 1.0F, 2.0F, 2.0F)
                .texOffs(0, 12)
                .addBox(-2.0F, -1.6667F, -2.1933F, 1.0F, 2.0F, 2.0F),
             PartPose.offset(0.0F, 19.6667F, -1.8567F)
          );
          PartDefinition body = bone.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.5F, 4.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 1.3333F, 2.3567F)
          );
          body.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(13, 2).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 0.5F, 2.5F));
          bone.addOrReplaceChild(
             "right_wing",
             CubeListBuilder.create().texOffs(3, 9).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F),
             PartPose.offsetAndRotation(-1.0F, -0.6667F, 0.8567F, 0.2182F, 0.3491F, 0.0F)
          );
          bone.addOrReplaceChild(
             "left_wing",
             CubeListBuilder.create().texOffs(-3, 9).mirror().addBox(0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F).mirror(false),
             PartPose.offsetAndRotation(1.0F, -0.6667F, 0.8567F, 0.2182F, -0.3491F, 0.0F)
          );
          bone.addOrReplaceChild(
             "front_legs", CubeListBuilder.create().texOffs(13, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F), PartPose.offset(0.0F, 3.3333F, 1.8567F)
          );
          bone.addOrReplaceChild(
             "middle_legs", CubeListBuilder.create().texOffs(13, 1).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F), PartPose.offset(0.0F, 3.3333F, 2.8567F)
          );
          bone.addOrReplaceChild(
             "back_legs", CubeListBuilder.create().texOffs(13, 2).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 0.0F), PartPose.offset(0.0F, 3.3333F, 3.8567F)
          );
          return LayerDefinition.create(mesh, 32, 32);
       }
    }

    /** BabyCamelModel de 26.3. */
    static final class BabyCamelModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition body = root.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 7.0F, 0.0F)
          );
          body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(50, 38).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 9.0F, 0.0F), PartPose.offset(0.0F, -1.5F, 8.05F));
          PartDefinition head = body.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(20, 0)
                .addBox(-2.5F, -3.0F, -7.5F, 5.0F, 5.0F, 7.0F)
                .texOffs(0, 0)
                .addBox(-2.5F, -12.0F, -7.5F, 5.0F, 9.0F, 5.0F)
                .texOffs(0, 14)
                .addBox(-2.5F, -12.0F, -10.5F, 5.0F, 4.0F, 3.0F),
             PartPose.offset(0.0F, 1.0F, -7.5F)
          );
          head.addOrReplaceChild(
             "right_ear", CubeListBuilder.create().texOffs(37, 0).addBox(-3.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-2.5F, -11.0F, -4.0F)
          );
          head.addOrReplaceChild(
             "left_ear", CubeListBuilder.create().texOffs(47, 0).addBox(0.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(2.5F, -11.0F, -4.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(36, 14).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(-3.0F, 11.5F, -5.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(48, 14).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(3.0F, 11.5F, -5.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 38).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(3.0F, 11.5F, 5.5F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 13.0F, 3.0F), PartPose.offset(-3.0F, 11.5F, 5.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyChickenModel de 26.3. */
    static final class BabyChickenModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.25F, -0.75F, 4.0F, 4.0F, 4.0F).texOffs(10, 8).addBox(-1.0F, -0.25F, -1.75F, 2.0F, 1.0F, 1.0F),
             PartPose.offset(0.0F, 20.25F, -1.25F)
          );
          root.addOrReplaceChild(
             "left_leg",
             CubeListBuilder.create().texOffs(2, 2).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F).texOffs(0, 1).addBox(-0.5F, 2.0F, -1.0F, 1.0F, 0.0F, 1.0F),
             PartPose.offset(1.0F, 22.0F, 0.5F)
          );
          root.addOrReplaceChild(
             "right_leg",
             CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F).texOffs(0, 0).addBox(-0.5F, 2.0F, -1.0F, 1.0F, 0.0F, 1.0F),
             PartPose.offset(-1.0F, 22.0F, 0.5F)
          );
          root.addOrReplaceChild(
             "right_wing", CubeListBuilder.create().texOffs(6, 8).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F), PartPose.offset(2.0F, 20.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_wing", CubeListBuilder.create().texOffs(4, 8).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F), PartPose.offset(-2.0F, 20.0F, 0.0F)
          );
          return LayerDefinition.create(mesh, 16, 16);
       }
    }

    /** BabyCowModel de 26.3. */
    static final class BabyCowModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 18)
                .addBox(-3.0F, -4.569F, -4.8333F, 6.0F, 6.0F, 5.0F)
                .texOffs(8, 29)
                .addBox(3.0F, -5.569F, -3.8333F, 1.0F, 2.0F, 1.0F)
                .texOffs(4, 29)
                .mirror()
                .addBox(-4.0F, -5.569F, -3.8333F, 1.0F, 2.0F, 1.0F)
                .mirror(false)
                .texOffs(12, 29)
                .addBox(-2.0F, -1.569F, -5.8333F, 4.0F, 3.0F, 1.0F),
             PartPose.offset(0.0F, 13.569F, -5.1667F)
          );
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, -1.0F, 8.0F, 6.0F, 12.0F), PartPose.offset(3.0F, 19.0F, -5.0F));
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(22, 18).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(-2.5F, 18.0F, -3.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(34, 18).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(2.5F, 18.0F, -3.5F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(22, 27).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(-2.5F, 18.0F, 3.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(34, 27).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(2.5F, 18.0F, 3.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyDolphinModel de 26.3. */
    static final class BabyDolphinModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(20, 0).addBox(-3.0F, -2.5F, -4.0F, 6.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 21.5F, 0.0F)
          );
          PartDefinition head = body.addOrReplaceChild(
             "head",
             CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.5F, -4.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 1.0F, -4.0F)
          );
          head.addOrReplaceChild(
             "nose",
             CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 0.5F, -4.0F)
          );
          body.addOrReplaceChild(
             "left_fin",
             CubeListBuilder.create().texOffs(34, 18).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(1.8F, 0.85F, -2.6F, 0.8727F, 0.0F, 1.7017F)
          );
          body.addOrReplaceChild(
             "right_fin",
             CubeListBuilder.create().texOffs(48, 18).mirror().addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
             PartPose.offsetAndRotation(-1.8F, 0.85F, -2.6F, 0.8727F, 0.0F, -1.7017F)
          );
          PartDefinition tail = body.addOrReplaceChild(
             "tail",
             CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -1.5F, 0.0F, 4.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 1.0F, 4.0F)
          );
          tail.addOrReplaceChild(
             "tail_fin",
             CubeListBuilder.create().texOffs(22, 13).addBox(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 0.0F, 6.0F)
          );
          body.addOrReplaceChild(
             "back_fin",
             CubeListBuilder.create().texOffs(42, 0).addBox(-0.5F, -1.0F, 1.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, -1.0F, -2.7F, 0.8727F, 0.0F, 0.0F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyDonkeyModel de 26.3. */
    static final class BabyDonkeyModel {
       public static LayerDefinition createBabyLayer() {
          MeshDefinition meshdefinition = new MeshDefinition();
          PartDefinition partdefinition = meshdefinition.getRoot();
          PartDefinition body = partdefinition.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -3.0F, -7.0F, 8.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)),
             PartPose.offset(1.0F, 14.0F, 0.0F)
          );
          PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 6.5F));
          tail.addOrReplaceChild(
             "tail_r1",
             CubeListBuilder.create().texOffs(24, 33).addBox(-2.5F, -1.0F, -0.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7418F, 0.0F, 0.0F)
          );
          body.addOrReplaceChild(
             "left_hind_leg",
             CubeListBuilder.create().texOffs(12, 44).addBox(-2.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.25F, 3.5F, 5.25F)
          );
          body.addOrReplaceChild(
             "right_hind_leg",
             CubeListBuilder.create().texOffs(0, 44).addBox(-2.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-2.4F, 3.5F, 5.4F)
          );
          body.addOrReplaceChild(
             "left_front_leg",
             CubeListBuilder.create().texOffs(12, 33).addBox(-2.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.4F, 3.5F, -5.3F)
          );
          body.addOrReplaceChild(
             "right_front_leg",
             CubeListBuilder.create().texOffs(0, 33).addBox(-2.5F, -1.5F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-2.4F, 3.5F, -5.4F)
          );
          PartDefinition neck = body.addOrReplaceChild("head_parts", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -5.0F));
          neck.addOrReplaceChild(
             "neck_r1",
             CubeListBuilder.create().texOffs(30, 9).addBox(-3.0F, -6.0F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
          );
          PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -3.0F));
          head.addOrReplaceChild(
             "head_r1",
             CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.6F, -8.4F, 6.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.3927F, 0.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "left_ear",
             CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.5F, -0.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(2.0F, -3.5F, -1.0F, 0.48F, 0.0F, 0.48F)
          );
          head.addOrReplaceChild(
             "right_ear",
             CubeListBuilder.create().texOffs(22, 0).mirror().addBox(-2.0F, -6.5F, -0.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
             PartPose.offsetAndRotation(-2.0F, -3.5F, -1.0F, 0.48F, 0.0F, -0.48F)
          );
          body.addOrReplaceChild("right_chest", CubeListBuilder.create(), PartPose.offset(-1.0F, 10.0F, 0.0F));
          body.addOrReplaceChild("left_chest", CubeListBuilder.create(), PartPose.offset(-1.0F, 10.0F, 0.0F));
          return LayerDefinition.create(meshdefinition, 64, 64);
       }
    }

    /** BabyHorseModel de 26.3. */
    static final class BabyHorseModel {
       public static MeshDefinition createBabyMesh(CubeDeformation g) {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition Body = root.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -3.5F, -7.0F, 8.0F, 7.0F, 14.0F, g), PartPose.offset(0.0F, 12.5F, 0.0F)
          );
          Body.addOrReplaceChild(
             "tail",
             CubeListBuilder.create().texOffs(24, 34).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 8.0F, g),
             PartPose.offsetAndRotation(0.0F, -1.0F, 7.0F, -0.7418F, 0.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 46).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, g), PartPose.offset(2.4F, 16.0F, 5.4F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 46).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, g), PartPose.offset(-2.4F, 16.0F, 5.4F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(12, 34).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, g), PartPose.offset(2.4F, 16.0F, -5.4F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 9.0F, 3.0F, g), PartPose.offset(-2.4F, 16.0F, -5.4F)
          );
          PartDefinition neck = root.addOrReplaceChild(
             "head_parts",
             CubeListBuilder.create().texOffs(30, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 8.0F, 4.0F, g),
             PartPose.offsetAndRotation(0.0F, 10.0F, -6.0F, 0.6109F, 0.0F, 0.0F)
          );
          PartDefinition head = neck.addOrReplaceChild(
             "head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.9484F, -6.705F, 6.0F, 4.0F, 9.0F, g), PartPose.offset(0.0F, -6.0516F, -0.2951F)
          );
          head.addOrReplaceChild(
             "left_ear",
             CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, -2.5F, -0.8F, 2.0F, 3.0F, 1.0F, g),
             PartPose.offsetAndRotation(2.0F, -4.2484F, 1.9451F, 0.0F, 0.0F, 0.2618F)
          );
          head.addOrReplaceChild(
             "right_ear",
             CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.5F, -0.5F, 2.0F, 3.0F, 1.0F, g),
             PartPose.offsetAndRotation(-2.0F, -4.2484F, 1.645F, 0.0F, 0.0F, -0.2618F)
          );
          return mesh;
       }
    }

    /** BabyCatModel de 26.3. */
    static final class BabyCatModel {
       public static MeshTransformer COLLAR_TRANSFORMER = MeshTransformer.scaling(1.01F);

    }

    /** BabyFelineModel de 26.3. */
    static final class BabyFelineModel {
       public static LayerDefinition createBabyLayer() {
          MeshDefinition meshdefinition = new MeshDefinition();
          PartDefinition partdefinition = meshdefinition.getRoot();
          partdefinition.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.5F, -3.0F, -2.875F, 5.0F, 4.0F, 4.0F)
                .texOffs(18, 0)
                .addBox(-2.0F, -4.0F, -0.875F, 1.0F, 1.0F, 2.0F)
                .texOffs(24, 0)
                .addBox(1.0F, -4.0F, -0.875F, 1.0F, 1.0F, 2.0F)
                .texOffs(18, 3)
                .addBox(-1.5F, -1.0F, -3.875F, 3.0F, 2.0F, 1.0F),
             PartPose.offset(0.0F, 20.0F, -3.125F)
          );
          partdefinition.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(18, 18).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(1.0F, 22.0F, -1.5F)
          );
          partdefinition.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(12, 18).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(-1.0F, 22.0F, -1.5F)
          );
          partdefinition.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(18, 22).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(1.0F, 22.0F, 2.5F)
          );
          partdefinition.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -1.5F, -3.5F, 4.0F, 3.0F, 7.0F), PartPose.offset(0.0F, 20.5F, 0.5F)
          );
          partdefinition.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(12, 22).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(-1.0F, 22.0F, 2.5F)
          );
          partdefinition.addOrReplaceChild(
             "tail1",
             CubeListBuilder.create().texOffs(0, 18).addBox(-0.5F, -0.107F, 0.0849F, 1.0F, 1.0F, 5.0F),
             PartPose.offsetAndRotation(0.0F, 19.107F, 3.9151F, -0.567232F, 0.0F, 0.0F)
          );
          partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.ZERO);
          return LayerDefinition.create(meshdefinition, 32, 32);
       }
    }

    /** BabyFoxModel de 26.3. */
    static final class BabyFoxModel {
       private static float MAX_WALK_ANIMATION_SPEED = 1.0F;


       private static float WALK_ANIMATION_SCALE_FACTOR = 2.5F;


       public static LayerDefinition createBodyLayer() {
          MeshDefinition meshdefinition = new MeshDefinition();
          PartDefinition root = meshdefinition.getRoot();
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3.0F, -2.125F, -5.125F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(18, 20)
                .addBox(-1.0F, 0.875F, -7.125F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 8)
                .addBox(-3.0F, -4.125F, -4.125F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 11)
                .addBox(1.0F, -4.125F, -4.125F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 18.125F, 0.125F)
          );
          root.addOrReplaceChild(
             "right_hind_leg",
             CubeListBuilder.create().texOffs(22, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-1.5F, 22.0F, 4.0F)
          );
          root.addOrReplaceChild(
             "left_hind_leg",
             CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(1.5F, 22.0F, 4.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg",
             CubeListBuilder.create().texOffs(22, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-1.5F, 22.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_front_leg",
             CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(1.5F, 22.0F, 0.0F)
          );
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 20.0F, 2.0F)
          );
          body.addOrReplaceChild(
             "tail",
             CubeListBuilder.create().texOffs(0, 20).addBox(-1.5F, -1.48F, -1.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -0.5F, 3.0F)
          );
          return LayerDefinition.create(meshdefinition, 32, 32);
       }
    }

    /** BabyGoatModel de 26.3. */
    static final class BabyGoatModel {
       private static String HEAD_MAIN = "HeadMain";


       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(29, 12).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(1.5F, 19.5F, 3.0F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(21, 12).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(-1.5F, 19.5F, 3.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(21, 5).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(-1.5F, 19.5F, -2.0F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(29, 5).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(1.5F, 19.5F, -2.0F)
          );
          root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -2.3F, -4.5F, 6.0F, 5.0F, 9.0F).texOffs(0, 24).addBox(-2.5F, -2.2F, -4.0F, 5.0F, 4.0F, 8.0F),
             PartPose.offset(0.0F, 17.8F, 0.0F)
          );
          PartDefinition head = root.addOrReplaceChild(
             "head",
             CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.8126F, -5.1548F, 4.0F, 4.0F, 6.0F),
             PartPose.offsetAndRotation(0.0F, 15.5F, -3.0F, 0.4363F, 0.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "right_horn",
             CubeListBuilder.create().texOffs(24, 0).mirror().addBox(0.0F, -4.5F, 0.0F, 1.0F, 2.0F, 1.0F).mirror(false),
             PartPose.offsetAndRotation(-1.5F, -1.5F, -1.0F, (float) (-Math.PI / 8), 0.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "left_horn",
             CubeListBuilder.create().texOffs(24, 0).mirror().addBox(2.0F, -4.5F, 0.0F, 1.0F, 2.0F, 1.0F).mirror(false),
             PartPose.offsetAndRotation(-1.5F, -1.5F, -1.0F, (float) (-Math.PI / 8), 0.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "right_ear",
             CubeListBuilder.create().texOffs(0, 12).mirror().addBox(-2.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F).mirror(false),
             PartPose.offsetAndRotation(-1.7F, -2.3126F, 0.1452F, 0.0F, -0.5236F, 0.0F)
          );
          head.addOrReplaceChild(
             "left_ear",
             CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F),
             PartPose.offsetAndRotation(1.7F, -2.3126F, 0.1452F, 0.0F, 0.5236F, 0.0F)
          );
          head.addOrReplaceChild(
             "HeadMain", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.5F, -4.0F, 4.0F, 4.0F, 6.0F), PartPose.offset(0.0F, -1.3126F, -1.1548F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyLlamaModel de 26.3. */
    static final class BabyLlamaModel {
       public static LayerDefinition createBodyLayer(CubeDeformation g) {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3.0F, -9.0F, -4.0F, 6.0F, 11.0F, 4.0F, g)
                .texOffs(0, 15)
                .addBox(-1.5F, -7.0F, -7.0F, 3.0F, 3.0F, 3.0F, g)
                .texOffs(20, 4)
                .addBox(0.5F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, g)
                .texOffs(20, 0)
                .addBox(-2.5F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, g),
             PartPose.offset(0.0F, 12.0F, -4.0F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 45).addBox(-1.4F, -0.5F, -1.5F, 3.0F, 8.0F, 3.0F, g), PartPose.offset(-2.5F, 16.5F, 4.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 45).addBox(-1.6F, -0.5F, -1.5F, 3.0F, 8.0F, 3.0F, g), PartPose.offset(2.5F, 16.5F, 4.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-1.4F, -0.5F, -1.5F, 3.0F, 8.0F, 3.0F, g), PartPose.offset(-2.5F, 16.5F, -3.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(12, 34).addBox(-1.6F, -0.5F, -1.5F, 3.0F, 8.0F, 3.0F, g), PartPose.offset(2.5F, 16.5F, -3.5F)
          );
          root.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -3.0F, -8.5F, 8.0F, 6.0F, 13.0F, g), PartPose.offset(0.0F, 14.0F, 2.5F)
          );
          root.addOrReplaceChild(
             "right_chest",
             CubeListBuilder.create().texOffs(45, 28).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, g),
             PartPose.offsetAndRotation(-8.5F, 4.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
          );
          root.addOrReplaceChild(
             "left_chest",
             CubeListBuilder.create().texOffs(45, 41).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, g),
             PartPose.offsetAndRotation(5.5F, 4.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyPandaModel de 26.3. */
    static final class BabyPandaModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-4.5F, -3.5F, -5.5F, 9.0F, 7.0F, 11.0F), PartPose.offset(0.0F, 18.5F, 2.5F));
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3.5F, -3.0F, -5.0F, 7.0F, 6.0F, 5.0F)
                .texOffs(24, 6)
                .addBox(-2.0F, 1.0F, -6.0F, 4.0F, 2.0F, 1.0F)
                .texOffs(24, 0)
                .addBox(-4.5F, -4.0F, -3.5F, 3.0F, 3.0F, 1.0F)
                .texOffs(33, 0)
                .addBox(1.5F, -4.0F, -3.5F, 3.0F, 3.0F, 1.0F),
             PartPose.offset(0.0F, 19.0F, -3.0F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(-3.0F, 22.0F, 6.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 34).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(3.0F, 22.0F, 6.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(-3.0F, 22.0F, -1.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(12, 29).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(3.0F, 22.0F, -1.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyPigModel de 26.3. */
    static final class BabyPigModel {
       public static LayerDefinition createBodyLayer(CubeDeformation g) {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -4.5F, 7.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 19.0F, 0.5F)
          );
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 15)
                .addBox(-3.5F, -5.0F, -5.0F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.025F))
                .texOffs(6, 27)
                .addBox(-1.5F, -1.975F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.015F)),
             PartPose.offset(0.0F, 19.0F, -2.0F)
          );
          root.addOrReplaceChild(
             "left_front_leg",
             CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.5F, 22.0F, -3.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg",
             CubeListBuilder.create().texOffs(23, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-2.5F, 22.0F, -3.0F)
          );
          root.addOrReplaceChild(
             "left_hind_leg",
             CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(2.5F, 22.0F, 4.0F)
          );
          root.addOrReplaceChild(
             "right_hind_leg",
             CubeListBuilder.create().texOffs(23, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-2.5F, 22.0F, 4.0F)
          );
          return LayerDefinition.create(mesh, 32, 32);
       }
    }

    /** BabyPolarBearModel de 26.3. */
    static final class BabyPolarBearModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-4.0F, -3.5F, -6.0F, 8.0F, 7.0F, 12.0F), PartPose.offset(0.0F, 17.5F, 0.0F));
          root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-3.0F, -2.625F, -4.25F, 6.0F, 5.0F, 4.0F)
                .texOffs(20, 3)
                .addBox(-2.0F, 0.375F, -6.25F, 4.0F, 2.0F, 2.0F)
                .texOffs(20, 0)
                .addBox(-4.0F, -3.625F, -2.75F, 2.0F, 2.0F, 1.0F)
                .texOffs(26, 0)
                .addBox(2.0F, -3.625F, -2.75F, 2.0F, 2.0F, 1.0F),
             PartPose.offset(0.0F, 18.625F, -5.75F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offset(-2.5F, 21.5F, 4.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 34).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offset(2.5F, 21.5F, 4.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 28).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offset(-2.5F, 21.5F, -4.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(12, 28).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offset(2.5F, 21.5F, -4.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyRabbitModel de 26.3. */
    static final class BabyRabbitModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 1.6F));
          body.addOrReplaceChild(
             "body_r1",
             CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 3.0F, 6.0F),
             PartPose.offsetAndRotation(0.0F, -2.0F, -1.6F, -0.5236F, 0.0F, 0.0F)
          );
          PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.2F, 2.0F));
          tail.addOrReplaceChild(
             "tail_r1",
             CubeListBuilder.create().texOffs(0, 21).addBox(-1.4F, -2.0268F, -1.0177F, 3.0F, 3.0F, 3.0F),
             PartPose.offsetAndRotation(-0.1F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F)
          );
          PartDefinition head = body.addOrReplaceChild(
             "head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 4.0F, 4.0F), PartPose.offset(0.0F, -5.0F, -2.6F)
          );
          head.addOrReplaceChild(
             "right_ear", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -3.5F, -0.5F, 2.0F, 4.0F, 1.0F), PartPose.offset(-1.5F, -3.5F, -0.5F)
          );
          head.addOrReplaceChild(
             "left_ear", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -3.5F, -0.5F, 2.0F, 4.0F, 1.0F), PartPose.offset(1.5F, -3.5F, -0.5F)
          );
          PartDefinition frontLegs = body.addOrReplaceChild("frontlegs", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, -2.6F));
          PartDefinition leftFrontLeg = frontLegs.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 1.0F, -0.5F, 0.3927F, 0.0F, 0.0F)
          );
          leftFrontLeg.addOrReplaceChild(
             "left_front_leg_r1",
             CubeListBuilder.create().texOffs(18, 8).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F),
             PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
          );
          PartDefinition rightFrontLeg = frontLegs.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 1.0F, -0.5F, 0.3927F, 0.0F, 0.0F)
          );
          rightFrontLeg.addOrReplaceChild(
             "right_front_leg_r1",
             CubeListBuilder.create().texOffs(14, 8).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F),
             PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
          );
          PartDefinition backLegs = root.addOrReplaceChild("backlegs", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 2.0F));
          PartDefinition leftBackLeg = backLegs.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, 0.5F, 0.5F, 0.0F, 3.1416F, 0.0F)
          );
          leftBackLeg.addOrReplaceChild(
             "left_haunch",
             CubeListBuilder.create().texOffs(10, 17).addBox(-2.0F, -0.5F, 0.0F, 2.0F, 1.0F, 3.0F),
             PartPose.offsetAndRotation(1.0F, 0.0F, 0.5F, 0.0F, -0.7854F, 0.0F)
          );
          PartDefinition rightBackLeg = backLegs.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, 0.5F, 0.5F, 0.0F, 3.1416F, 0.0F)
          );
          rightBackLeg.addOrReplaceChild(
             "right_haunch",
             CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, -0.5F, 0.0F, 2.0F, 1.0F, 3.0F),
             PartPose.offsetAndRotation(0.5F, 0.0F, -0.9F, 0.0F, 0.7854F, 0.0F)
          );
          return LayerDefinition.create(mesh, 32, 32);
       }
    }

    /** BabySheepModel de 26.3. */
    static final class BabySheepModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -2.0F, -4.5F, 6.0F, 4.0F, 9.0F), PartPose.offset(0.0F, 17.0F, 0.5F));
          root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -4.5F, -3.5F, 5.0F, 5.0F, 5.0F), PartPose.offset(0.0F, 15.5F, -2.5F));
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(-2.0F, 19.0F, 3.0F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(24, 12).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(2.0F, 19.0F, 3.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(8, 23).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(-2.0F, 19.0F, -2.0F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(24, 5).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F), PartPose.offset(2.0F, 19.0F, -2.0F)
          );
          return LayerDefinition.create(mesh, 64, 32);
       }
    }

    /** BabySquidModel de 26.3. */
    static final class BabySquidModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition meshdefinition = new MeshDefinition();
          PartDefinition root = meshdefinition.getRoot();
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 13.0F, 0.0F)
          );
          int tentacleCount = 8;
          CubeListBuilder tentacle = CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 6.0F, 2.0F);

          for (int i = 0; i < 8; i++) {
             double angle = i * Math.PI * 2.0 / 8.0;
             float x = (float)Math.cos(angle) * 3.0F;
             float y = 18.5F;
             float z = (float)Math.sin(angle) * 3.0F;
             angle = i * Math.PI * -2.0 / 8.0 + (Math.PI / 2);
             float yRot = (float)angle;
             root.addOrReplaceChild("tentacle" + i, tentacle, PartPose.offsetAndRotation(x, 18.5F, z, 0.0F, yRot, 0.0F));
          }

          return LayerDefinition.create(meshdefinition, 32, 32);
       }
    }

    /** BabyTurtleModel de 26.3. */
    static final class BabyTurtleModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F), PartPose.offset(0.0F, 22.9F, 1.0F));
          root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, -2.0F, -3.0F, 3.0F, 3.0F, 3.0F), PartPose.offset(0.0F, 22.9F, -1.0F));
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(-1, 0).addBox(-2.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F), PartPose.offset(-2.0F, 23.9F, 2.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(-1, 1).addBox(0.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F), PartPose.offset(2.0F, 23.9F, 2.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(8, 6).addBox(-2.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F), PartPose.offset(-2.0F, 23.9F, -0.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(8, 7).addBox(0.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F), PartPose.offset(2.0F, 23.9F, -0.5F)
          );
          return LayerDefinition.create(mesh, 16, 16);
       }
    }

    /** BabyWolfModel de 26.3. */
    static final class BabyWolfModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition head = root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 12)
                .addBox(-2.99F, -3.25F, -3.0F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.025F))
                .texOffs(17, 12)
                .addBox(-1.5F, -0.24F, -5.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 18.25F, -4.0F)
          );
          head.addOrReplaceChild(
             "right_ear", CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(-2.0F, -4.25F, -0.5F)
          );
          head.addOrReplaceChild(
             "left_ear", CubeListBuilder.create().texOffs(20, 5).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(2.0F, -4.25F, -0.5F)
          );
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 4.0F, 8.0F), PartPose.offset(0.0F, 19.0F, 0.0F));
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-1.5F, 21.0F, 3.0F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(8, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(1.5F, 21.0F, 3.0F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-1.5F, 21.0F, -3.0F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(20, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(1.5F, 21.0F, -3.0F)
          );
          PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 19.0F, 3.0F, -0.5236F, 0.0F, 0.0F));
          tail.addOrReplaceChild(
             "tail_r1",
             CubeListBuilder.create().texOffs(22, 16).addBox(-1.0F, -5.7F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offsetAndRotation(0.0F, -0.6F, 0.2F, -3.1F, 0.0F, 0.0F)
          );
          return LayerDefinition.create(mesh, 32, 32);
       }
    }

    /** BabyHoglinModel de 26.3. */
    static final class BabyHoglinModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition head = root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-5.0F, -2.2605F, -10.547F, 10.0F, 4.0F, 12.0F)
                .texOffs(44, 29)
                .addBox(-7.0F, -4.0981F, -8.4879F, 2.0F, 5.0F, 2.0F)
                .texOffs(52, 29)
                .addBox(5.0F, -4.0981F, -8.4879F, 2.0F, 5.0F, 2.0F),
             PartPose.offsetAndRotation(0.0F, 13.0F, -7.0F, 0.8727F, 0.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "body",
             CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-4.0F, -14.0F, -7.0F, 8.0F, 8.0F, 14.0F, new CubeDeformation(0.02F))
                .texOffs(24, 39)
                .addBox(0.0F, -18.0F, -8.0F, 0.0F, 6.0F, 11.0F, new CubeDeformation(0.02F)),
             PartPose.offset(0.0F, 24.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "right_ear",
             CubeListBuilder.create().texOffs(32, 5).addBox(-5.1F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F),
             PartPose.offsetAndRotation(-5.0F, -1.0F, -1.5F, 0.0F, 0.0F, -0.8727F)
          );
          head.addOrReplaceChild(
             "left_ear",
             CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-0.9F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F).mirror(false),
             PartPose.offsetAndRotation(5.0F, -1.0F, -1.5F, 0.0F, 0.0F, 0.8727F)
          );
          root.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(-2.5F, 18.0F, 4.5F)
          );
          root.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(12, 47).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(2.5F, 18.0F, 4.5F)
          );
          root.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(-2.5F, 18.0F, -4.5F)
          );
          root.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(12, 38).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offset(2.5F, 18.0F, -4.5F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyPiglinModel de 26.3. */
    static final class BabyPiglinModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 5.0F, 3.0F), PartPose.offset(0.0F, 18.0F, -0.5F));
          PartDefinition head = root.addOrReplaceChild(
             "head",
             CubeListBuilder.create().texOffs(21, 30).addBox(-1.5F, -3.0F, -4.5F, 3.0F, 3.0F, 1.0F).texOffs(0, 0).addBox(-4.5F, -6.0F, -3.5F, 9.0F, 6.0F, 7.0F),
             PartPose.offset(0.0F, 15.0F, 0.0F)
          );
          head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
          PartDefinition leftear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(4.2F, -4.0F, 0.0F));
          leftear.addOrReplaceChild(
             "left_ear_r1",
             CubeListBuilder.create().texOffs(0, 21).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F),
             PartPose.offsetAndRotation(1.0F, 1.75F, 0.0F, 0.0F, 0.0F, -0.6109F)
          );
          PartDefinition rightear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-4.2F, -4.0F, 0.0F));
          rightear.addOrReplaceChild(
             "right_ear_r1",
             CubeListBuilder.create().texOffs(18, 13).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F),
             PartPose.offsetAndRotation(-1.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.6109F)
          );
          root.addOrReplaceChild(
             "left_arm", CubeListBuilder.create().texOffs(28, 13).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F), PartPose.offset(4.0F, 15.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_arm", CubeListBuilder.create().texOffs(10, 30).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F), PartPose.offset(-4.0F, 15.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_leg", CubeListBuilder.create().texOffs(22, 23).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(-1.5F, 20.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_leg", CubeListBuilder.create().texOffs(10, 23).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(1.5F, 20.0F, 0.0F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyZombifiedPiglinModel de 26.3. */
    static final class BabyZombifiedPiglinModel {
       public static LayerDefinition createBodyLayer() {
          return BabyPiglinModel.createBodyLayer();
       }
    }

    /** BabyStriderModel de 26.3. */
    static final class BabyStriderModel {
       private static String FRONT_BRISTLE = "bristle2";


       private static String MIDDLE_BRISTLE = "bristle1";


       private static String BACK_BRISTLE = "bristle0";


       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition body = root.addOrReplaceChild(
             "body",
             CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.75F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, 16.75F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_leg",
             CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-1.5F, 20.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_leg",
             CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(1.5F, 20.0F, 0.0F)
          );
          body.addOrReplaceChild(
             "bristle0",
             CubeListBuilder.create().texOffs(0, 21).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -4.25F, 2.0F)
          );
          body.addOrReplaceChild(
             "bristle1",
             CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -4.25F, 0.0F)
          );
          body.addOrReplaceChild(
             "bristle2",
             CubeListBuilder.create().texOffs(0, 15).addBox(-3.5F, -2.5F, 0.0F, 7.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
             PartPose.offset(0.0F, -4.25F, -2.0F)
          );
          return LayerDefinition.create(mesh, 32, 32);
       }
    }

    /** BabyDrownedModel de 26.3. */
    static final class BabyDrownedModel {
       public static LayerDefinition createBodyLayer(CubeDeformation g) {
          return BabyZombieModel.createBodyLayer(g);
       }
    }

    /** BabyZombieModel de 26.3. */
    static final class BabyZombieModel {
       public static LayerDefinition createBodyLayer(CubeDeformation g) {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 2.0F, g), PartPose.offset(0.0F, 17.5F, 0.0F)
          );
          PartDefinition head = root.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(3, 3)
                .addBox(-3.0F, -6.25F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(35, 3)
                .addBox(-3.0F, -6.15F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.25F)),
             PartPose.offset(0.0F, 15.25F, 0.0F)
          );
          head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
          root.addOrReplaceChild(
             "right_arm", CubeListBuilder.create().texOffs(36, 16).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, g), PartPose.offset(-3.0F, 15.5F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_arm", CubeListBuilder.create().texOffs(28, 16).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, g), PartPose.offset(3.0F, 15.5F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_leg", CubeListBuilder.create().texOffs(8, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, g), PartPose.offset(-1.0F, 20.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, g), PartPose.offset(1.0F, 20.0F, 0.0F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }
    }

    /** BabyZombieVillagerModel de 26.3. */
    static final class BabyZombieVillagerModel {
       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          root.addOrReplaceChild(
             "body",
             CubeListBuilder.create()
                .texOffs(0, 15)
                .addBox(-2.0F, -2.75F, -1.5F, 4.0F, 5.0F, 3.0F)
                .texOffs(16, 22)
                .addBox(-2.0F, -2.75F, -1.5F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.1F)),
             PartPose.offset(0.0F, 18.75F, 0.0F)
          );
          PartDefinition head = root.addOrReplaceChild(
             "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.5F, 8.0F, 8.0F, 7.0F), PartPose.offset(0.0F, 16.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "hat",
             CubeListBuilder.create().texOffs(0, 31).addBox(-4.0F, -4.0F, -3.5F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.3F)),
             PartPose.offset(0.0F, -4.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "hat_rim", CubeListBuilder.create().texOffs(0, 46).addBox(-7.0F, -0.5F, -6.0F, 14.0F, 1.0F, 12.0F), PartPose.offset(0.0F, -4.5F, 0.0F)
          );
          head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(23, 0).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.0F, -4.0F));
          PartDefinition right_arm = root.addOrReplaceChild(
             "right_arm",
             CubeListBuilder.create().texOffs(24, 15).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(-3.0F, 15.5F, 0.0F)
          );
          PartDefinition left_arm = root.addOrReplaceChild(
             "left_arm",
             CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
             PartPose.offset(3.0F, 15.5F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_leg", CubeListBuilder.create().texOffs(8, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-1.0F, 21.5F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_leg", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(1.0F, 21.5F, 0.0F)
          );
          return LayerDefinition.create(mesh, 64, 64);
       }

       //#if MC >= 12109
       public static LayerDefinition createNoHatLayer() {
          return createBodyLayer().apply(mesh -> {
             mesh.getRoot().clearChild("head").clearRecursively();
             return mesh;
          });
       }
       //#endif
    }

    /** BabyVillagerModel de 26.3. */
    static final class BabyVillagerModel {
       //#if MC >= 12109
       public static MeshDefinition createNoHatModel() {
          MeshDefinition mesh = createBodyModel();
          mesh.getRoot().clearChild("head").clearRecursively();
          return mesh;
       }
       //#endif

       public static MeshDefinition createBodyModel() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition arms = root.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 17.5F, 0.0F));
          arms.addOrReplaceChild(
             "right_hand",
             CubeListBuilder.create()
                .texOffs(36, 15)
                .addBox(-1.0F, -2.4925F, -1.8401F, 2.0F, 4.0F, 2.0F)
                .texOffs(16, 15)
                .addBox(5.0F, -2.4925F, -1.8401F, 2.0F, 4.0F, 2.0F),
             PartPose.offsetAndRotation(-3.0F, 1.4025F, -0.9599F, -1.0472F, 0.0F, 0.0F)
          );
          arms.addOrReplaceChild(
             "middlearm_r1",
             CubeListBuilder.create().texOffs(24, 17).addBox(-2.0F, -0.9924F, -0.9825F, 4.0F, 2.0F, 2.0F),
             PartPose.offsetAndRotation(0.0F, 0.9024F, -1.8175F, -1.0472F, 0.0F, 0.0F)
          );
          root.addOrReplaceChild(
             "right_leg", CubeListBuilder.create().texOffs(8, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-1.0F, 21.5F, 0.0F)
          );
          root.addOrReplaceChild(
             "left_leg", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(1.0F, 21.5F, 0.0F)
          );
          PartDefinition head = root.addOrReplaceChild(
             "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.5F, 8.0F, 8.0F, 7.0F), PartPose.offset(0.0F, 16.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "hat",
             CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, -4.0F, -3.5F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.3F)),
             PartPose.offset(0.0F, -4.0F, 0.0F)
          );
          head.addOrReplaceChild(
             "hat_rim", CubeListBuilder.create().texOffs(0, 45).addBox(-7.0F, -0.5F, -6.0F, 14.0F, 1.0F, 12.0F), PartPose.offset(0.0F, -4.5F, 0.0F)
          );
          head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(23, 0).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -2.0F, -4.0F));
          root.addOrReplaceChild(
             "body", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.75F, -1.5F, 4.0F, 5.0F, 3.0F), PartPose.offset(0.0F, 18.75F, 0.0F)
          );
          root.addOrReplaceChild(
             "bb_main",
             CubeListBuilder.create().texOffs(16, 21).addBox(-2.5F, -8.0F, -1.5F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.2F)),
             PartPose.offset(0.5F, 24.0F, 0.0F)
          );
          return mesh;
       }
    }

    /** SniffletModel de 26.3. */
    static final class SniffletModel {
       private static String LOWER_BEAK = "lower_beak";


       public static LayerDefinition createBodyLayer() {
          MeshDefinition mesh = new MeshDefinition();
          PartDefinition root = mesh.getRoot();
          PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
          PartDefinition body = bone.addOrReplaceChild(
             "body",
             CubeListBuilder.create()
                .texOffs(0, 35)
                .addBox(-13.0F, -14.0F, -0.5F, 14.0F, 14.0F, 20.0F, new CubeDeformation(0.25F))
                .texOffs(0, 0)
                .addBox(-13.0F, -14.0F, -0.5F, 14.0F, 15.0F, 20.0F)
                .texOffs(68, 0)
                .addBox(-13.0F, 0.0F, -0.5F, 14.0F, 0.0F, 20.0F),
             PartPose.offset(6.0F, -3.0F, -9.5F)
          );
          PartDefinition head = body.addOrReplaceChild(
             "head",
             CubeListBuilder.create()
                .texOffs(68, 20)
                .addBox(-5.0F, -4.25F, -7.5F, 10.0F, 9.0F, 9.0F)
                .texOffs(88, 20)
                .addBox(-5.0F, 3.75F, -7.5F, 10.0F, 0.0F, 9.0F),
             PartPose.offset(-6.0F, -4.75F, 0.0F)
          );
          head.addOrReplaceChild(
             "left_ear", CubeListBuilder.create().texOffs(104, 38).addBox(0.0F, 0.0F, -2.0F, 1.0F, 11.0F, 3.0F), PartPose.offset(5.0F, -4.25F, -1.5F)
          );
          head.addOrReplaceChild(
             "right_ear", CubeListBuilder.create().texOffs(96, 38).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 11.0F, 3.0F), PartPose.offset(-5.0F, -4.25F, -1.5F)
          );
          head.addOrReplaceChild(
             "nose", CubeListBuilder.create().texOffs(68, 47).addBox(-5.0F, -3.0F, -2.0F, 10.0F, 3.0F, 4.0F), PartPose.offset(0.0F, -1.25F, -9.5F)
          );
          head.addOrReplaceChild(
             "lower_beak", CubeListBuilder.create().texOffs(68, 38).addBox(-5.0F, -2.5F, -2.0F, 10.0F, 5.0F, 4.0F), PartPose.offset(0.0F, 1.25F, -9.5F)
          );
          bone.addOrReplaceChild(
             "right_front_leg", CubeListBuilder.create().texOffs(0, 69).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(-4.0F, -4.0F, -7.0F)
          );
          bone.addOrReplaceChild(
             "right_mid_leg", CubeListBuilder.create().texOffs(0, 78).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(-4.0F, -4.0F, 0.0F)
          );
          bone.addOrReplaceChild(
             "right_hind_leg", CubeListBuilder.create().texOffs(0, 87).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(-4.0F, -4.0F, 7.0F)
          );
          bone.addOrReplaceChild(
             "left_front_leg", CubeListBuilder.create().texOffs(16, 69).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(4.0F, -4.0F, -7.0F)
          );
          bone.addOrReplaceChild(
             "left_mid_leg", CubeListBuilder.create().texOffs(16, 78).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(4.0F, -4.0F, 0.0F)
          );
          bone.addOrReplaceChild(
             "left_hind_leg", CubeListBuilder.create().texOffs(16, 87).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 4.0F), PartPose.offset(4.0F, -4.0F, 7.0F)
          );
          return LayerDefinition.create(mesh, 128, 128);
       }
    }
}
