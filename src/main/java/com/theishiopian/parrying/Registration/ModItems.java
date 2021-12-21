package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Items.APItem;
import com.theishiopian.parrying.Items.DaggerItem;
import com.theishiopian.parrying.Items.FlailItem;
import com.theishiopian.parrying.Items.SpearItem;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is used to register custom items. Mostly weapons.
 */
@SuppressWarnings("unused")//yes, they are, just not here. trust me
public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParryingMod.MOD_ID);
    public static final float MACE_AP = 0.35f;
    public static final float HAMMER_AP = 0.65f;
    public static final float FLAIL_AP = 0.15f;

    public static final float MACE_SPEED = -2.7f;
    public static final float HAMMER_SPEED = -3.2f;
    public static final float FLAIL_SPEED = -2.3f;
    public static final float SPEAR_SPEED = -2.8f;
    public static final float DAGGER_SPEED = -1.4f;

    public static final int MACE_DMG = 3;
    public static final int HAMMER_DMG = 5;
    public static final int FLAIL_DMG = 2;
    public static final int SPEAR_DMG = 2;
    public static final int DAGGER_DMG = 1;

    public static final RegistryObject<Item> WOODEN_MACE = ITEMS.register("wooden_mace", () -> new APItem(Tiers.WOOD, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_MACE = ITEMS.register("stone_mace", () -> new APItem(Tiers.STONE, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_MACE = ITEMS.register("iron_mace", () -> new APItem(Tiers.IRON, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_MACE = ITEMS.register("golden_mace", () -> new APItem(Tiers.GOLD, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_MACE = ITEMS.register("diamond_mace", () -> new APItem(Tiers.DIAMOND, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_MACE = ITEMS.register("netherite_mace", () -> new APItem(Tiers.NETHERITE, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).fireResistant().tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new APItem(Tiers.WOOD, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_HAMMER = ITEMS.register("stone_hammer", () -> new APItem(Tiers.STONE, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new APItem(Tiers.IRON, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new APItem(Tiers.GOLD, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer", () -> new APItem(Tiers.DIAMOND, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer", () -> new APItem(Tiers.NETHERITE, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).fireResistant().tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> WOODEN_DAGGER = ITEMS.register("wooden_dagger", () -> new DaggerItem(Tiers.WOOD, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_DAGGER = ITEMS.register("stone_dagger", () -> new DaggerItem(Tiers.STONE, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_DAGGER = ITEMS.register("iron_dagger", () -> new DaggerItem(Tiers.IRON, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_DAGGER = ITEMS.register("golden_dagger", () -> new DaggerItem(Tiers.GOLD, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_DAGGER = ITEMS.register("diamond_dagger", () -> new DaggerItem(Tiers.DIAMOND, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger", () -> new DaggerItem(Tiers.NETHERITE, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).fireResistant().tab(CreativeModeTab.TAB_COMBAT)));

    //initialized separately for item override purposes
    private static final FlailItem WoodFlail = new FlailItem(Tiers.WOOD, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT));
    private static final FlailItem StoneFlail = new FlailItem(Tiers.STONE, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT));
    private static final FlailItem IronFlail = new FlailItem(Tiers.IRON, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT));
    private static final FlailItem GoldFlail = new FlailItem(Tiers.GOLD, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT));
    private static final FlailItem DiamondFlail = new FlailItem(Tiers.DIAMOND, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT));
    private static final FlailItem NetheriteFlail = new FlailItem(Tiers.NETHERITE, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).fireResistant().tab(CreativeModeTab.TAB_COMBAT));

    public static final RegistryObject<Item> WOOD_FLAIL = ITEMS.register("wood_flail", () -> WoodFlail);
    public static final RegistryObject<Item> STONE_FLAIL = ITEMS.register("stone_flail", () -> StoneFlail);
    public static final RegistryObject<Item> IRON_FLAIL = ITEMS.register("iron_flail", () -> IronFlail);
    public static final RegistryObject<Item> GOLD_FLAIL = ITEMS.register("gold_flail", () -> GoldFlail);
    public static final RegistryObject<Item> DIAMOND_FLAIL = ITEMS.register("diamond_flail", () -> DiamondFlail);
    public static final RegistryObject<Item> NETHERITE_FLAIL = ITEMS.register("netherite_flail", () -> NetheriteFlail);

    //initialized separately for item override purposes
    private static final SpearItem WoodSpear = new SpearItem(Tiers.WOOD, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    private static final SpearItem StoneSpear = new SpearItem(Tiers.STONE, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    private static final SpearItem IronSpear = new SpearItem(Tiers.IRON, SPEAR_DMG, SPEAR_SPEED, 1,  (new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    private static final SpearItem GoldSpear = new SpearItem(Tiers.GOLD, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    private static final SpearItem DiamondSpear = new SpearItem(Tiers.DIAMOND, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    private static final SpearItem NetheriteSpear = new SpearItem(Tiers.NETHERITE, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().fireResistant().tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<SpearItem> WOOD_SPEAR = ITEMS.register("wood_spear", () -> WoodSpear);
    public static final RegistryObject<SpearItem> STONE_SPEAR = ITEMS.register("stone_spear", () -> StoneSpear);
    public static final RegistryObject<SpearItem> IRON_SPEAR = ITEMS.register("iron_spear", () -> IronSpear);
    public static final RegistryObject<SpearItem> GOLD_SPEAR = ITEMS.register("gold_spear", () -> GoldSpear);
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = ITEMS.register("diamond_spear", () -> DiamondSpear);
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = ITEMS.register("netherite_spear", () -> NetheriteSpear);

    /**
     * This method is used to register item overrides for any items that need them. This allows models to be swapped out on the fly, such as
     * for the flail animation or for spears pointing the right way when about to be thrown.
     */
    public static void RegisterOverrides()
    {
        FlailItem[] flails =
        {
            WoodFlail,
            StoneFlail,
            IronFlail,
            GoldFlail,
            DiamondFlail,
            NetheriteFlail
        };

        SpearItem[] spears =
        {
            WoodSpear,
            StoneSpear,
            IronSpear,
            GoldSpear,
            DiamondSpear,
            NetheriteSpear
        };

        //if null pointers get thrown in the item render, look at these rascals
        for (SpearItem spear:spears)
        {
            ItemProperties.register(spear, new ResourceLocation("throwing"), (stack, world, user, seed) ->
                    (user != null && user.isUsingItem() && user.getMainHandItem().equals(stack)) ? 1 : 0);
        }

        //if null pointers get thrown in the item render, look at these rascals
        for (FlailItem flail:flails)
        {
            ItemProperties.register(flail, new ResourceLocation("swing"), (stack, world, user, seed)-> user != null ? user.attackAnim : 0);
            ItemProperties.register(flail, new ResourceLocation("swinging"), (stack, world, user, seed)->
            {
                boolean mainHand = false;
                boolean offHand = false;
                if(user != null)
                {
                    mainHand = user.getMainHandItem().equals(stack) && user.swingingArm == InteractionHand.MAIN_HAND;
                    offHand = user.getOffhandItem().equals(stack) && user.swingingArm == InteractionHand.OFF_HAND;
                }
                return (user != null && user.attackAnim > 0 && (mainHand || offHand)) ? 1 : 0;
            });
        }
    }
}