package es.furynocturn.viaviewer.entity.baby;

import es.furynocturn.viaviewer.entity.baby.Layers263.BabyArmadilloModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyAxolotlModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyBeeModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyCamelModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyCatModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyChickenModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyCowModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyDolphinModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyDonkeyModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyDrownedModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyFelineModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyFoxModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyGoatModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyHoglinModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyHorseModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyLlamaModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyPandaModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyPigModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyPiglinModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyPolarBearModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyRabbitModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabySheepModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabySquidModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyStriderModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyTurtleModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyVillagerModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyWolfModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyZombieModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyZombieVillagerModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.BabyZombifiedPiglinModel;
import es.furynocturn.viaviewer.entity.baby.Layers263.SniffletModel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

/** Que capa de cria de 1.21.11 se cuece con que geometria de 26.3 (como LayerDefinitions de 26.3). */
final class BabyLayers {

    private BabyLayers() {
    }

    static Map<ModelLayerLocation, Supplier<LayerDefinition>> all() {
        final Map<ModelLayerLocation, Supplier<LayerDefinition>> map = new HashMap<>();
        map.put(ModelLayers.ARMADILLO_BABY, BabyArmadilloModel::createBodyLayer);
        map.put(ModelLayers.AXOLOTL_BABY, BabyAxolotlModel::createBodyLayer);
        map.put(ModelLayers.BEE_BABY, BabyBeeModel::createBodyLayer);
        map.put(ModelLayers.CAMEL_BABY, BabyCamelModel::createBodyLayer);
        map.put(ModelLayers.CAT_BABY, BabyFelineModel::createBabyLayer);
        map.put(ModelLayers.CAT_BABY_COLLAR, () -> BabyFelineModel.createBabyLayer().apply(BabyCatModel.COLLAR_TRANSFORMER));
        map.put(ModelLayers.OCELOT_BABY, BabyFelineModel::createBabyLayer);
        map.put(ModelLayers.CHICKEN_BABY, BabyChickenModel::createBodyLayer);
        map.put(ModelLayers.COLD_CHICKEN_BABY, BabyChickenModel::createBodyLayer);
        map.put(ModelLayers.COW_BABY, BabyCowModel::createBodyLayer);
        map.put(ModelLayers.WARM_COW_BABY, BabyCowModel::createBodyLayer);
        map.put(ModelLayers.COLD_COW_BABY, BabyCowModel::createBodyLayer);
        map.put(ModelLayers.MOOSHROOM_BABY, BabyCowModel::createBodyLayer);
        map.put(ModelLayers.DOLPHIN_BABY, BabyDolphinModel::createBodyLayer);
        map.put(ModelLayers.DONKEY_BABY, BabyDonkeyModel::createBabyLayer);
        map.put(ModelLayers.MULE_BABY, BabyDonkeyModel::createBabyLayer);
        for (final ModelLayerLocation horse : new ModelLayerLocation[]{ModelLayers.HORSE_BABY, ModelLayers.SKELETON_HORSE_BABY, ModelLayers.ZOMBIE_HORSE_BABY}) {
            map.put(horse, () -> LayerDefinition.create(BabyHorseModel.createBabyMesh(CubeDeformation.NONE), 64, 64));
        }
        map.put(ModelLayers.DROWNED_BABY, () -> BabyDrownedModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.DROWNED_BABY_OUTER_LAYER, () -> BabyDrownedModel.createBodyLayer(new CubeDeformation(0.25F)));
        map.put(ModelLayers.FOX_BABY, BabyFoxModel::createBodyLayer);
        map.put(ModelLayers.GOAT_BABY, BabyGoatModel::createBodyLayer);
        map.put(ModelLayers.HOGLIN_BABY, BabyHoglinModel::createBodyLayer);
        map.put(ModelLayers.ZOGLIN_BABY, BabyHoglinModel::createBodyLayer);
        map.put(ModelLayers.LLAMA_BABY, () -> BabyLlamaModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.TRADER_LLAMA_BABY, () -> BabyLlamaModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.LLAMA_BABY_DECOR, () -> BabyLlamaModel.createBodyLayer(new CubeDeformation(0.2F)));
        map.put(ModelLayers.PANDA_BABY, BabyPandaModel::createBodyLayer);
        map.put(ModelLayers.PIG_BABY, () -> BabyPigModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.COLD_PIG_BABY, () -> BabyPigModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.PIGLIN_BABY, BabyPiglinModel::createBodyLayer);
        map.put(ModelLayers.ZOMBIFIED_PIGLIN_BABY, BabyZombifiedPiglinModel::createBodyLayer);
        map.put(ModelLayers.POLAR_BEAR_BABY, BabyPolarBearModel::createBodyLayer);
        map.put(ModelLayers.RABBIT_BABY, BabyRabbitModel::createBodyLayer);
        map.put(ModelLayers.SHEEP_BABY, BabySheepModel::createBodyLayer);
        map.put(ModelLayers.SHEEP_BABY_WOOL, BabySheepModel::createBodyLayer);
        // 26.3 no dibuja la lana de debajo en las crias
        map.put(ModelLayers.SHEEP_BABY_WOOL_UNDERCOAT, BabyLayers::emptyQuadruped);
        map.put(ModelLayers.SNIFFER_BABY, SniffletModel::createBodyLayer);
        map.put(ModelLayers.SQUID_BABY, BabySquidModel::createBodyLayer);
        map.put(ModelLayers.GLOW_SQUID_BABY, BabySquidModel::createBodyLayer);
        map.put(ModelLayers.STRIDER_BABY, BabyStriderModel::createBodyLayer);
        map.put(ModelLayers.TURTLE_BABY, BabyTurtleModel::createBodyLayer);
        map.put(ModelLayers.VILLAGER_BABY, () -> LayerDefinition.create(BabyVillagerModel.createBodyModel(), 64, 64));
        map.put(ModelLayers.VILLAGER_BABY_NO_HAT, () -> LayerDefinition.create(BabyVillagerModel.createNoHatModel(), 64, 64));
        map.put(ModelLayers.WOLF_BABY, BabyWolfModel::createBodyLayer);
        map.put(ModelLayers.ZOMBIE_BABY, () -> BabyZombieModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.HUSK_BABY, () -> BabyZombieModel.createBodyLayer(CubeDeformation.NONE));
        map.put(ModelLayers.ZOMBIE_VILLAGER_BABY, BabyZombieVillagerModel::createBodyLayer);
        map.put(ModelLayers.ZOMBIE_VILLAGER_BABY_NO_HAT, BabyZombieVillagerModel::createNoHatLayer);
        return map;
    }

    /** Las partes que pide QuadrupedModel, sin cubos. */
    private static LayerDefinition emptyQuadruped() {
        final MeshDefinition mesh = new MeshDefinition();
        for (final String part : new String[]{"head", "body", "right_hind_leg", "left_hind_leg", "right_front_leg", "left_front_leg"}) {
            mesh.getRoot().addOrReplaceChild(part, CubeListBuilder.create(), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 32);
    }
}
