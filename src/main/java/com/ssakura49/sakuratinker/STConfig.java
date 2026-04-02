package com.ssakura49.sakuratinker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.ssakura49.sakuratinker.utils.RegisterAccessUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import static com.ssakura49.sakuratinker.STConfig.Common.capacityCache;
import static com.ssakura49.sakuratinker.STConfig.Common.lootTableEnergyCostMap;

public class STConfig {
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("sakuratinker/treasure_modifier.json");
    private static final String DEFAULT_JSON_PATH = "/data/sakuratinker/config/treasure_modifier.json";

    @Mod.EventBusSubscriber(modid = SakuraTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Client {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec.ConfigValue<Boolean> NO_MOD_RENDER;
        public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_COSMIC_RENDERER;
        public static final ForgeConfigSpec.ConfigValue<Double> COSMIC_SCALE;
        public static final ForgeConfigSpec.ConfigValue<Double> COSMIC_SPEED_MULTIPLIER;
        public static final ForgeConfigSpec.ConfigValue<String> WRAPPED_COSMIC_LOCATION;
        private static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_TIME_STOP_BALL_RENDERER;

        public static boolean no_mod_render;
        public static boolean enable_cosmic_renderer;
        public static double cosmic_scale;
        public static double cosmic_speed_multiplier;
        public static String wrapped_texture_location;
        public static boolean enable_time_stop_ball_renderer;

        public static final ForgeConfigSpec SPEC;

        static {
            {
                BUILDER.push("Render");
                NO_MOD_RENDER = BUILDER.comment("Disable CIL(CustomInLevel) render.").define("noModRender", false);
                ENABLE_TIME_STOP_BALL_RENDERER = BUILDER.comment("Render TimeStop shock wave effect.").define("timestopBall", true);
                {
                    BUILDER.push("CosmicShader");
                    ENABLE_COSMIC_RENDERER = BUILDER.comment("Enable cosmic renderer").define("cosmicRenderer", true);
                    COSMIC_SCALE = BUILDER.comment("cosmic shader scale").define("cosmicScale", 1.0D);
                    WRAPPED_COSMIC_LOCATION = BUILDER.comment("cosmic texture covering the item").define("wrappedTextureLocation", "sakuratinker:textures/shader/star2.png");
                    COSMIC_SPEED_MULTIPLIER = BUILDER.comment("cosmic render speed multiplier").defineInRange("cosmicSpeedMultiplier", 1.0F, 0F, 100F);
                    BUILDER.pop();
                }
                BUILDER.pop();
            }
            SPEC = BUILDER.build();
        }

        @SubscribeEvent
        public static void onLoad(ModConfigEvent event) {
            update();
        }

        public static void update() {
            no_mod_render = NO_MOD_RENDER.get();
            enable_cosmic_renderer = ENABLE_COSMIC_RENDERER.get();
            cosmic_scale = COSMIC_SCALE.get();
            cosmic_speed_multiplier = COSMIC_SPEED_MULTIPLIER.get();
            wrapped_texture_location = WRAPPED_COSMIC_LOCATION.get();
            enable_time_stop_ball_renderer = ENABLE_TIME_STOP_BALL_RENDERER.get();
        }
    }

    @Mod.EventBusSubscriber(modid = SakuraTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Common {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        // 配方相关
        public static final ForgeConfigSpec.ConfigValue<String> SLIME_CRYSTAL_EARTH;
        public static final ForgeConfigSpec.ConfigValue<String> SLIME_CRYSTAL_SKY;
        public static final ForgeConfigSpec.ConfigValue<String> SLIME_CRYSTAL_NETHER;
        public static final ForgeConfigSpec.ConfigValue<String> GOLD_BLOCK_ITEM;

        // 护符配置
        public static final ForgeConfigSpec.BooleanValue CHARMS_ALLOW_MULTIPLE;

        // 罪孽根源配置
        public static final ForgeConfigSpec.DoubleValue RUINATION_DAMAGE_FACTOR;

        // 生命之尺配置
        public static final ForgeConfigSpec.DoubleValue LIFE_RATIO_PERCENT;

        // 伤害吸收配置
        public static final ForgeConfigSpec.IntValue TICK_INTERVAL;
        public static final ForgeConfigSpec.DoubleValue ABSORPTION_PER_TICK;

        // 下克上配置
        public static final ForgeConfigSpec.DoubleValue SHITAKUSO_BONUS;
        public static final ForgeConfigSpec.DoubleValue SHITAKUSO_MAX_BONUS;

        // 万剑归一掉落配置
//        public static final ForgeConfigSpec.DoubleValue BLADE_CONVERGENCE_DROP_CHANCE;
//        public static final ForgeConfigSpec.ConfigValue<String> BLADE_CONVERGENCE_DROP_MOBS;

        // 虚空珍珠掉落配置
        public static final ForgeConfigSpec.DoubleValue VOID_PEARL_DROP_CHANCE;
        public static final ForgeConfigSpec.ConfigValue<String> VOID_PEARL_DROP_MOBS;

        // 折磨效果配置
        public static final ForgeConfigSpec.DoubleValue TORTURE_BASE_DAMAGE_THRESHOLD;
        public static final ForgeConfigSpec.DoubleValue TORTURE_DAMAGE_MULTIPLIER;

        // 死神祝福配置
        public static final ForgeConfigSpec.DoubleValue REAPERS_BLESSING_MAX_BONUS;
        public static final ForgeConfigSpec.DoubleValue REAPERS_BLESSING_BONUS_PER_HEALTH;
        // 高频结界配置
        public static final ForgeConfigSpec.DoubleValue REDUCTION_PER_HIT;
        public static final ForgeConfigSpec.DoubleValue MAX_REDUCTION;
        public static final ForgeConfigSpec.IntValue RESET_TICK;
        // 凋零之心配置
        public static final ForgeConfigSpec.DoubleValue WITHER_HEART_DROP_CHANCE;
        public static final ForgeConfigSpec.ConfigValue<String> WITHER_HEART_DROP_MOBS;
        // 厄运人偶黑名单配置
        public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEBUFF_BLACKLIST;
        // 工匠秘典配置
        public static final ForgeConfigSpec.BooleanValue ENABLE_TINKER_SPELL_BOOK_ATK_BONUS;
        // 吸血鬼刀配置
        public static final ForgeConfigSpec.DoubleValue LIFE_STEAL_CHANCE;
        public static final ForgeConfigSpec.DoubleValue LIFE_STEAL_PERCENT;
        public static final ForgeConfigSpec.BooleanValue ENABLE_MAX_HEALTH_HEAL;
        public static final ForgeConfigSpec.DoubleValue MAX_HEALTH_HEAL_AMOUNT;
        // 工匠箭矢伤害修正
        public static final ForgeConfigSpec.DoubleValue TINKER_ARROW_FIXES;
        // 聚宝特性配置
        public static TreasureModifierConfig TREASURE_CONFIG = new TreasureModifierConfig();
        public static final ForgeConfigSpec.DoubleValue TREASURE_MODIFIER_ENERGY_COST;

        public static double treasureEnergyCost;
        public static final Map<Pair<ResourceLocation, ResourceLocation>, Integer> energyValueCache = new HashMap<>();
        public static final Map<ResourceLocation, Integer> capacityCache = new HashMap<>();
        public static final Map<ResourceLocation, List<TreasureModifierConfig.LootTableEntry>> dimensionLootTablesMap = new HashMap<>();
        public static final Map<ResourceLocation, Integer> lootTableEnergyCostMap = new HashMap<>();

        public static final ForgeConfigSpec.IntValue LASER_GUN_BASE_ENERGY;
        public static final ForgeConfigSpec.IntValue LASER_GUN_ENERGY_PER_TICK;
        public static final ForgeConfigSpec.DoubleValue LASER_GUN_DAMAGE_PER_TICK;
        public static final ForgeConfigSpec.IntValue LASER_GUN_C_MOD_NORMAL_DAMAGE_BONUS;
        public static final ForgeConfigSpec.BooleanValue LASER_GUN_MODIFIER_EFFECT;

        public static final ForgeConfigSpec.DoubleValue ALCHEMIACAL_FLUID_CONSUME;
        //工匠黑暗手杖
        public static final ForgeConfigSpec.BooleanValue TINKER_DARK_WAND_ATTACK_BONUS;
        public static final ForgeConfigSpec.BooleanValue TinkerDarkWandTriggerModifier;

        //幻金，饰品，地之精华
        public static final ForgeConfigSpec.DoubleValue ESSENCE_EARTH_MODIFIER_BONUS;

        //goety blacklist
        public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MODIFIER_BLACKLIST;

        //虚金的计算公式
        public enum GrowthMode {
            LINEAR,
            LOG,
            SOFTCAP
        }
        public static final ForgeConfigSpec.EnumValue<GrowthMode> growthMode;
        //线性
        public static final ForgeConfigSpec.DoubleValue linearPerKill;
        //对数
        public static final ForgeConfigSpec.DoubleValue logFactor;
        //指数趋近
        public static final ForgeConfigSpec.DoubleValue capMax;
        public static final ForgeConfigSpec.DoubleValue capScale;

        public static String slimeCrystalEarth;
        public static String slimeCrystalSky;
        public static String slimeCrystalNether;
        public static String goldBlockItem;
        public static boolean charmsAllowMultiple;
        public static double ruinationDamageFactor;
        public static double lifeRatioPercent;
        public static int tickInterval;
        public static double absorptionPerTick;
        public static double shitakusoBonus;
        public static double bladeConvergenceDropChance;
        public static String bladeConvergenceDropMobs;
        public static double voidPearlDropChance;
        public static String voidPearlDropMobs;
        public static double tortureBaseDamageThreshold;
        public static double tortureDamageMultiplier;
        public static double reapersBlessingMaxBonus;
        public static double reapersBlessingBonusPerHealth;
        public static double reductionPerHit;
        public static double maxReduction;
        public static int resetTick;
        public static double witherHeartDropChance;
        public static String witherHeartDropMobs;
        public static List<? extends String> jinxDollBlackList;
        public static boolean enableTinkerBookATKBonus;
        public static double lifeStealChance;
        public static double lifeStealPercent;
        public static boolean enableMaxHealthHeal;
        public static double maxHealthHealAmount;
        public static double tinkerArrowDamageFixes;

        public static final ForgeConfigSpec SPEC;

        static {
            //================== 兼容配置 ==================//
            BUILDER.push("兼容配置");
            {
                BUILDER.push("modifier");
                MODIFIER_BLACKLIST = BUILDER
                        .comment("Blacklist of modifiers (by ID) to skip in damage calculation")
                        .comment("Modifier黑名单")
                        .defineList("blacklist",
                                List.of("cloudertinker:steelbone"),
                                obj -> obj instanceof String);

                BUILDER.pop();
            }
            BUILDER.pop();
            //================== Recipe 配置 ==================//
            BUILDER.push("recipe");
            {
                BUILDER.comment("魂樱刻印配方配置").push("sakura_core");
                SLIME_CRYSTAL_EARTH = BUILDER
                        .comment("大地史莱姆水晶 (default: sakuratinker:slime_crystal_earth)")
                        .define("slimeCrystalEarth", "sakuratinker:slime_crystal_earth");
                SLIME_CRYSTAL_SKY = BUILDER
                        .comment("天空史莱姆水晶 (default: sakuratinker:slime_crystal_sky)")
                        .define("slimeCrystalSky", "sakuratinker:slime_crystal_sky");
                SLIME_CRYSTAL_NETHER = BUILDER
                        .comment("下界史莱姆水晶 (default: sakuratinker:slime_crystal_nether)")
                        .define("slimeCrystalNether", "sakuratinker:slime_crystal_nether");
                GOLD_BLOCK_ITEM = BUILDER
                        .comment("金块 (default: minecraft:gold_block)")
                        .define("goldBlock", "minecraft:gold_block");
                BUILDER.pop();
            }
            BUILDER.pop();

            //==================  工具配置 ==================//
            BUILDER.push("工具配置");
            {
                BUILDER.comment("护符设置").push("charms");
                CHARMS_ALLOW_MULTIPLE = BUILDER
                        .comment("设为true允许装备多件护符")
                        .define("allowMultipleCharms", false);
                BUILDER.pop();

                BUILDER.comment("工匠秘典").push("Tinker spell book");
                ENABLE_TINKER_SPELL_BOOK_ATK_BONUS = BUILDER
                        .comment("设为true允许秘典攻击力计算进法术伤害")
                        .define("enableTinkerBookATKBonus", true);
                BUILDER.pop();

                BUILDER.comment("吸血鬼刀").push("Vampire knives");
                LIFE_STEAL_CHANCE = BUILDER
                        .comment("吸血概率")
                        .defineInRange("lifeStealChance", 0.5, 0.0, 1.0);
                LIFE_STEAL_PERCENT = BUILDER
                        .comment("一次攻击伤害中的吸取量")
                        .defineInRange("lifeStealPercent", 0.1,0.0,10.0);
                ENABLE_MAX_HEALTH_HEAL = BUILDER
                        .comment("是否开启最大生命值恢复")
                        .define("enableMaxHealthHeal", true);
                MAX_HEALTH_HEAL_AMOUNT = BUILDER
                        .comment("最大生命值恢复系数")
                        .defineInRange("maxHealthHealAmount", 0.05 ,0.0, 10.0);
                BUILDER.pop();

                BUILDER.comment("工匠箭矢").push("Tinker Arrow");
                TINKER_ARROW_FIXES = BUILDER
                        .comment("箭矢伤害修正系数，最小为1.0")
                        .defineInRange("enableTinkerBookATKBonus",1.2 ,1.0, Integer.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("战术刺刀激光步枪").push("Laser Gun");
                LASER_GUN_BASE_ENERGY = BUILDER
                        .comment("激光普通模式能量消耗")
                        .defineInRange("laserBaseEnergy", 500, 0, Integer.MAX_VALUE);
                LASER_GUN_ENERGY_PER_TICK = BUILDER
                        .comment("激光连发模式每tick增加的能量消耗")
                        .defineInRange("continuousModePerTickEnergyCost", 50,0,Integer.MAX_VALUE);
                LASER_GUN_DAMAGE_PER_TICK = BUILDER
                        .comment("激光连发模式每tick伤害增幅")
                        .defineInRange("continuousModePerTickDamageIncrease", 0.05,0, Double.MAX_VALUE);
                LASER_GUN_C_MOD_NORMAL_DAMAGE_BONUS = BUILDER
                        .comment("激光连发模式伤害增幅持平普通模式所需要的tick大小")
                        .defineInRange("continuousModeDamageToBaseBonus", 40, 1, Integer.MAX_VALUE);
                LASER_GUN_MODIFIER_EFFECT = BUILDER
                        .comment("激光枪是否会受到Modifier的影响，默认为true")
                        .define("laserGunModifierEffect", true);
                BUILDER.pop();

                BUILDER.comment("炼金手套").push("Alchemical Gloves");
                ALCHEMIACAL_FLUID_CONSUME = BUILDER
                        .comment("每次投掷消耗的流体数量，默认250")
                        .comment("The amount of fluid consumed per throw is 250 by default.")
                        .defineInRange("fluidConsume",50 ,0, Double.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("工匠黑暗法杖").push("Tinker Dark Wand");
                TINKER_DARK_WAND_ATTACK_BONUS = BUILDER
                        .comment("聚晶伤害是否会受到工具攻击力影响。默认为false")
                        .comment("Does polycrystalline damage get affected by the tool's attack power? The default is false.")
                        .define("attackBonus",false);
                TinkerDarkWandTriggerModifier = BUILDER
                        .define("disabled trigger modifier", true);
                BUILDER.pop();
            }
            BUILDER.pop();

            //================== 掉落配置 ==================//
            BUILDER.push("drop");
            {
//                BUILDER.comment("万剑归一掉落配置").push("blade_convergence");
//                BLADE_CONVERGENCE_DROP_CHANCE = BUILDER
//                        .comment("万剑归一的掉落概率 (0.0 - 1.0)")
//                        .defineInRange("dropChance", 0.1, 0.0, 1.0);
//                BLADE_CONVERGENCE_DROP_MOBS = BUILDER
//                        .comment("可以掉落万剑归一的生物 (用逗号分隔)")
//                        .define("dropMobs", "minecraft:ender_dragon");
//                BUILDER.pop();

                BUILDER.comment("虚空珍珠掉落配置").push("void_pearl");
                VOID_PEARL_DROP_CHANCE = BUILDER
                        .comment("掉落概率 (0.0 - 1.0)")
                        .defineInRange("dropChance", 0.01, 0.0, 1.0);
                VOID_PEARL_DROP_MOBS = BUILDER
                        .comment("可以掉落虚空珍珠的生物 (用逗号分隔)")
                        .define("dropMobs", "minecraft:ender_dragon");
                BUILDER.pop();

                BUILDER.comment("凋零之心掉落配置").push("wither_heart");
                WITHER_HEART_DROP_CHANCE = BUILDER
                        .comment("掉落概率 (0.0 - 1.0)")
                        .defineInRange("dropChance", 1.0, 0.0, 1.0);
                WITHER_HEART_DROP_MOBS = BUILDER
                        .comment("可以掉落凋零之心的生物 (用逗号分隔)")
                        .define("dropMobs", "minecraft:wither");
                BUILDER.pop();
            }
            BUILDER.pop();

            //================== Modifier 配置 ==================//
            BUILDER.push("modifiers");
            {
                BUILDER.comment("罪孽根源配置").push("ruination");
                RUINATION_DAMAGE_FACTOR = BUILDER
                        .comment("罪孽根源的伤害系数")
                        .defineInRange("damageFactor", 0.08, 0.0, 1.0);
                BUILDER.pop();

                BUILDER.comment("生命之尺配置").push("life_ratio");
                LIFE_RATIO_PERCENT = BUILDER
                        .comment("生命之尺的增幅系数")
                        .defineInRange("bonusPercent", 0.1, 0.0, 1.0);
                BUILDER.pop();

                BUILDER.comment("伤害吸收配置").push("absorption");
                TICK_INTERVAL = BUILDER
                        .comment("每tick恢复的间隔 (default: 5)")
                        .defineInRange("tickInterval", 5, 1, 1000);
                ABSORPTION_PER_TICK = BUILDER
                        .comment("每tick恢复的生命值 (default: 0.2)")
                        .defineInRange("absorptionPerTick", 0.2, 0.0, 100.0);
                BUILDER.pop();

                BUILDER.comment("下克上配置").push("shitakuso");
                SHITAKUSO_BONUS = BUILDER
                        .comment("根据生命比例缩放伤害的系数 (default: 0.1)")
                        .defineInRange("bonusMultiplier", 0.1, 0.0, 10.0);
                SHITAKUSO_MAX_BONUS = BUILDER
                        .comment("下克上最大增幅 (default: 10)")
                        .defineInRange("bonusMultiplier", 10, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("折磨效果配置").push("torture");
                TORTURE_BASE_DAMAGE_THRESHOLD = BUILDER
                        .comment("基础移动多少米触发伤害(1.0 = 1格)")
                        .defineInRange("baseDamageThreshold", 1.0, 0.1 , Integer.MAX_VALUE);
                TORTURE_DAMAGE_MULTIPLIER = BUILDER
                        .comment("每等级对阈值的影响系数(0.2 = 每等级减少20%阈值)")
                        .defineInRange("damageMultiplier", 0.2, 0.0 , 1.0);
                BUILDER.pop();

                BUILDER.comment("死神祝福配置").push("reapers_blessing");
                REAPERS_BLESSING_MAX_BONUS = BUILDER
                        .comment("最大加成 (百分比, 例如 0.4 是 40%)")
                        .defineInRange("maxBonus", 0.4, 0.0, 1.0);
                REAPERS_BLESSING_BONUS_PER_HEALTH = BUILDER
                        .comment("每百分比的生命加成")
                        .defineInRange("bonusPerHealth", 0.01, 0.0, 0.1);
                BUILDER.pop();

                BUILDER.comment("高频结界配置").push("high_frequency_barrier");
                REDUCTION_PER_HIT = BUILDER
                        .comment("每次受到伤害时增加的伤害减免比例 (0.0 - 1.0)")
                        .defineInRange("reductionPerHit", 0.05, 0.0, 1.0);
                MAX_REDUCTION = BUILDER
                        .comment("最大伤害减免比例 (0.0 - 1.0)")
                        .defineInRange("maxReduction", 0.50, 0.0, 1.0);
                RESET_TICK = BUILDER
                        .comment("多少tick未受攻击后重置层数 (0表示不重置)")
                        .defineInRange("resetTicks", 100, 0, Integer.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("厄运人偶配置").push("jinx_doll");
                DEBUFF_BLACKLIST = BUILDER
                        .comment("厄运人偶消除buff黑名单")
                        .defineListAllowEmpty(
                                "debuffBlacklist",
                                List.of("youkaishomecoming:youkaified"),
                                o -> o instanceof String
                        );
                BUILDER.pop();

                BUILDER.comment("聚宝特性配置").push("treasure_modifier");
                TREASURE_MODIFIER_ENERGY_COST = BUILDER
                        .comment("每次生成战利品消耗的能量")
                        .defineInRange("energy_cost", 50.0, 0.01, Integer.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("地之精华配置").push("essence_earth_modifier");
                ESSENCE_EARTH_MODIFIER_BONUS = BUILDER
                        .comment("最大加成，默认2.0")
                        .defineInRange("bonus", 2.0, 0.0, Integer.MAX_VALUE);
                BUILDER.pop();

                BUILDER.comment("噬魂者配置").push("soul_devourer");
                growthMode = BUILDER
                        .comment("计算模式: LINEAR / LOG / SOFTCAP （线性，对数，指数趋近）")
                        .comment("Damage growth mode: LINEAR / LOG / SOFTCAP")
                        .defineEnum("growth_mode", GrowthMode.LOG);
                linearPerKill = BUILDER
                        .comment("线性增长")
                        .comment("Linear bonus per kill")
                        .defineInRange("linear_per_kill", 0.0001D, 0D, 1D);
                logFactor = BUILDER
                        .comment("对数增长")
                        .comment("Logarithmic growth factor")
                        .defineInRange("log_factor", 0.1D, 0D, 10D);
                capMax = BUILDER
                        .comment("指数趋近最大值")
                        .comment("Softcap max bonus")
                        .defineInRange("softcap_max", 1.5D, 0D, 100D);
                capScale = BUILDER
                        .comment("指数趋近速率（越大越慢）")
                        .comment("Softcap scale (bigger = slower)")
                        .defineInRange("softcap_scale", 5000D, 1D, 1_000_000D);
                BUILDER.pop();
            }
            BUILDER.pop();
            //================== 构建配置 ==================//
            SPEC = BUILDER.build();
        }

        @SubscribeEvent
        public static void onLoad(ModConfigEvent event) {
//            loadTreasureConfig();
            if (event.getConfig().getSpec() != SPEC) return;
            if (FMLEnvironment.production) {
                return;
            }
            init();
            STConfig.loadOrCreateTreasureConfig();
        }

        public static void init() {
            treasureEnergyCost = TREASURE_MODIFIER_ENERGY_COST.get();
            slimeCrystalEarth = SLIME_CRYSTAL_EARTH.get();
            slimeCrystalSky = SLIME_CRYSTAL_SKY.get();
            slimeCrystalNether = SLIME_CRYSTAL_NETHER.get();
            goldBlockItem = GOLD_BLOCK_ITEM.get();
            charmsAllowMultiple = CHARMS_ALLOW_MULTIPLE.get();
            ruinationDamageFactor = RUINATION_DAMAGE_FACTOR.get();
            lifeRatioPercent = LIFE_RATIO_PERCENT.get();
            tickInterval = TICK_INTERVAL.get();
            absorptionPerTick = ABSORPTION_PER_TICK.get();
            shitakusoBonus = SHITAKUSO_BONUS.get();
//            bladeConvergenceDropChance = BLADE_CONVERGENCE_DROP_CHANCE.get();
//            bladeConvergenceDropMobs = BLADE_CONVERGENCE_DROP_MOBS.get();
            voidPearlDropChance = VOID_PEARL_DROP_CHANCE.get();
            voidPearlDropMobs = VOID_PEARL_DROP_MOBS.get();
            tortureBaseDamageThreshold = TORTURE_BASE_DAMAGE_THRESHOLD.get();
            tortureDamageMultiplier = TORTURE_DAMAGE_MULTIPLIER.get();
            reapersBlessingMaxBonus = REAPERS_BLESSING_MAX_BONUS.get();
            reapersBlessingBonusPerHealth = REAPERS_BLESSING_BONUS_PER_HEALTH.get();
            reductionPerHit = REDUCTION_PER_HIT.get();
            maxReduction = MAX_REDUCTION.get();
            resetTick = RESET_TICK.get();
            witherHeartDropChance = WITHER_HEART_DROP_CHANCE.get();
            witherHeartDropMobs = WITHER_HEART_DROP_MOBS.get();
            jinxDollBlackList = DEBUFF_BLACKLIST.get();
            enableTinkerBookATKBonus = ENABLE_TINKER_SPELL_BOOK_ATK_BONUS.get();
            lifeStealChance = LIFE_STEAL_CHANCE.get();
            lifeStealPercent = LIFE_STEAL_PERCENT.get();
            enableMaxHealthHeal = ENABLE_MAX_HEALTH_HEAL.get();
            maxHealthHealAmount = MAX_HEALTH_HEAL_AMOUNT.get();
            tinkerArrowDamageFixes = TINKER_ARROW_FIXES.get();
        }
    }

    @Mod.EventBusSubscriber(modid = SakuraTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Server {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec SPEC;

        static {
            SPEC = BUILDER.build();
        }

        @SubscribeEvent
        public static void onLoad(ModConfigEvent event) {
        }

        public static void init() {
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class LoadConfig {
        @SubscribeEvent
        public static void onLevelInit(LevelEvent.Load event) {
            Common.init();
        }
    }

    public static void registerAll(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Common.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, Client.SPEC);
        context.registerConfig(ModConfig.Type.SERVER, Server.SPEC);
    }

    private static Supplier<Item> getConfiguredItem(ForgeConfigSpec.ConfigValue<String> config, Supplier<Item> fallback) {
        return () -> {
            try {
                String id = config.get();
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id));
                return item != null ? item : fallback.get();
            } catch (Exception e) {
                SakuraTinker.LOGGER.error("配置中物品ID无效: {}", config.get(), e);
                return fallback.get();
            }
        };
    }

    public static Supplier<Item> slime_crystal_earth() {
        return getConfiguredItem(Common.SLIME_CRYSTAL_EARTH, () -> ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "slime_crystal_earth")));
    }

    public static Supplier<Item> slime_crystal_sky() {
        return getConfiguredItem(Common.SLIME_CRYSTAL_SKY, () -> ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "slime_crystal_sky")));
    }

    public static Supplier<Item> slime_crystal_nether() {
        return getConfiguredItem(Common.SLIME_CRYSTAL_NETHER, () -> ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID, "slime_crystal_nether")));
    }

    public static Supplier<Item> getGoldBlock() {
        return getConfiguredItem(Common.GOLD_BLOCK_ITEM, () -> Items.GOLD_BLOCK);
    }

    public static boolean allowMultipleCharms() {
        return Common.CHARMS_ALLOW_MULTIPLE.get();
    }

    public static boolean isBlacklisted(MobEffect effect, RegistryAccess registryAccess) {
        if (effect == null || registryAccess == null) return false;
        ResourceLocation id = RegisterAccessUtil.getMobEffectId(registryAccess, effect);
        if (id == null) {
            SakuraTinker.LOGGER.warn("MobEffect 未注册，跳过黑名单判断: {}", effect);
            return false;
        }
        return Common.jinxDollBlackList.contains(id.toString());
    }

    public static class TreasureModifierConfig {
        public List<String> dimensions = List.of();
        public List<EntityEnergyConfig> entity_energy_values = List.of();
        public List<DimensionCapacityConfig> dimension_capacities = List.of();
        public List<DimensionLootTableConfig> dimension_loot_tables = List.of();

        public static class EntityEnergyConfig {
            public String entity;
            public String dimension;
            public int value;
        }

        public static class DimensionCapacityConfig {
            public String dimension;
            public int capacity;
        }

        public static class DimensionLootTableConfig {
            public String dimension;
            public List<LootTableEntry> tables;
        }

        public static class LootTableEntry {
            public String id;
            public int cost;
        }
    }
    public static boolean isValidDimension(ResourceLocation dimId) {
        return Common.TREASURE_CONFIG.dimensions.contains(dimId.toString());
    }

    public static int getEnergyValueFromJson(ResourceLocation entityId, ResourceLocation dimId) {
        return Common.energyValueCache.getOrDefault(Pair.of(entityId, dimId), 0);
    }

    public static int getCapacityFromJson(ResourceLocation dimId) {
        return capacityCache.getOrDefault(dimId, 0);
    }
    // 获取指定维度的战利品表列表（包含cost）
    public static List<TreasureModifierConfig.LootTableEntry> getLootTablesForDimension(ResourceLocation dim) {
        return Common.dimensionLootTablesMap.getOrDefault(dim, List.of());
    }

    // 获取指定战利品表的能量消耗
    public static int getLootTableCost(ResourceLocation lootTable) {
        return lootTableEnergyCostMap.getOrDefault(lootTable, 0);
    }


    public static List<ResourceLocation> getLootTablesFromJson(ResourceLocation dimId) {
        for (TreasureModifierConfig.DimensionLootTableConfig dimLoot : Common.TREASURE_CONFIG.dimension_loot_tables) {
            if (dimLoot.dimension.equals(dimId.toString())) {
                // 加载能量消耗映射
                for (TreasureModifierConfig.LootTableEntry entry : dimLoot.tables) {
                    lootTableEnergyCostMap.put(ResourceLocation.parse(entry.id), entry.cost);
                }
                // 返回该维度所有表的 ResourceLocation 列表
                return dimLoot.tables.stream().map(e -> ResourceLocation.parse(e.id)).toList();
            }
        }
        return List.of();
    }
    public static void rebuildTreasureConfigCache(TreasureModifierConfig config) {
        Common.energyValueCache.clear();
        for (var e : config.entity_energy_values) {
            Common.energyValueCache.put(Pair.of(ResourceLocation.parse(e.entity), ResourceLocation.parse(e.dimension)), e.value);
        }
        capacityCache.clear();
        for (var c : config.dimension_capacities) {
            capacityCache.put(ResourceLocation.parse(c.dimension), c.capacity);
        }
        Common.dimensionLootTablesMap.clear();
        lootTableEnergyCostMap.clear();
        for (var dimLoot : config.dimension_loot_tables) {
            ResourceLocation dim = ResourceLocation.parse(dimLoot.dimension);
            Common.dimensionLootTablesMap.put(dim, dimLoot.tables);
            for (var table : dimLoot.tables) {
                lootTableEnergyCostMap.put(ResourceLocation.parse(table.id), table.cost);
            }
        }
    }

    public static void loadOrCreateTreasureConfig() {
        Path file = CONFIG_PATH;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            // 如果文件不存在，先创建文件
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());

                try (InputStream in = SakuraTinker.class.getResourceAsStream(DEFAULT_JSON_PATH)) {
                    if (in == null) {
                        System.err.println("[SakuraTinker] 未找到默认的 treasure_modifier.json");
                        return;
                    }
                    Files.copy(in, file);
                    System.out.println("[SakuraTinker] 已生成默认的 treasure_modifier.json");
                }
            }

            // 加载JSON内容
            String json = Files.readString(file);
            Common.TREASURE_CONFIG = gson.fromJson(json, TreasureModifierConfig.class);
            STConfig.rebuildTreasureConfigCache(Common.TREASURE_CONFIG);

            System.out.println("[SakuraTinker] 成功加载 treasure_modifier.json");
        } catch (IOException e) {
            System.err.println("[SakuraTinker] 加载 treasure_modifier.json 时出错");
            e.printStackTrace();
        }
    }


}
