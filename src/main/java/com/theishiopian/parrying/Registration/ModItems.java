package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Items.*;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemTier;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static final RegistryObject<Item> WOODEN_MACE = ITEMS.register("wooden_mace", () -> new APItem(ItemTier.WOOD, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_MACE = ITEMS.register("stone_mace", () -> new APItem(ItemTier.STONE, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_MACE = ITEMS.register("iron_mace", () -> new APItem(ItemTier.IRON, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_MACE = ITEMS.register("golden_mace", () -> new APItem(ItemTier.GOLD, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_MACE = ITEMS.register("diamond_mace", () -> new APItem(ItemTier.DIAMOND, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_MACE = ITEMS.register("netherite_mace", () -> new APItem(ItemTier.NETHERITE, MACE_DMG, MACE_SPEED, MACE_AP, (new Item.Properties()).fireResistant().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<Item> WOODEN_HAMMER = ITEMS.register("wooden_hammer", () -> new APItem(ItemTier.WOOD, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_HAMMER = ITEMS.register("stone_hammer", () -> new APItem(ItemTier.STONE, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new APItem(ItemTier.IRON, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_HAMMER = ITEMS.register("golden_hammer", () -> new APItem(ItemTier.GOLD, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer", () -> new APItem(ItemTier.DIAMOND, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer", () -> new APItem(ItemTier.NETHERITE, HAMMER_DMG, HAMMER_SPEED, HAMMER_AP, (new Item.Properties()).fireResistant().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<Item> WOODEN_DAGGER = ITEMS.register("wooden_dagger", () -> new DaggerItem(ItemTier.WOOD, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> STONE_DAGGER = ITEMS.register("stone_dagger", () -> new DaggerItem(ItemTier.STONE, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> IRON_DAGGER = ITEMS.register("iron_dagger", () -> new DaggerItem(ItemTier.IRON, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> GOLDEN_DAGGER = ITEMS.register("golden_dagger", () -> new DaggerItem(ItemTier.GOLD, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> DIAMOND_DAGGER = ITEMS.register("diamond_dagger", () -> new DaggerItem(ItemTier.DIAMOND, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));
    public static final RegistryObject<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger", () -> new DaggerItem(ItemTier.NETHERITE, DAGGER_DMG, DAGGER_SPEED, (new Item.Properties()).fireResistant().tab(ItemGroup.TAB_COMBAT)));

    //initialized separately for item override purposes
    private static final FlailItem WoodFlail = new FlailItem(ItemTier.WOOD, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT));
    private static final FlailItem StoneFlail = new FlailItem(ItemTier.STONE, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT));
    private static final FlailItem IronFlail = new FlailItem(ItemTier.IRON, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT));
    private static final FlailItem GoldFlail = new FlailItem(ItemTier.GOLD, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT));
    private static final FlailItem DiamondFlail = new FlailItem(ItemTier.DIAMOND, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT));
    private static final FlailItem NetheriteFlail = new FlailItem(ItemTier.NETHERITE, FLAIL_DMG, FLAIL_SPEED, FLAIL_AP, 0.5f, (new Item.Properties()).fireResistant().tab(ItemGroup.TAB_COMBAT));

    public static final RegistryObject<Item> WOOD_FLAIL = ITEMS.register("wood_flail", () -> WoodFlail);
    public static final RegistryObject<Item> STONE_FLAIL = ITEMS.register("stone_flail", () -> StoneFlail);
    public static final RegistryObject<Item> IRON_FLAIL = ITEMS.register("iron_flail", () -> IronFlail);
    public static final RegistryObject<Item> GOLD_FLAIL = ITEMS.register("gold_flail", () -> GoldFlail);
    public static final RegistryObject<Item> DIAMOND_FLAIL = ITEMS.register("diamond_flail", () -> DiamondFlail);
    public static final RegistryObject<Item> NETHERITE_FLAIL = ITEMS.register("netherite_flail", () -> NetheriteFlail);

    //initialized separately for item override purposes
    private static final SpearItem WoodSpear = new SpearItem(ItemTier.WOOD, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
    private static final SpearItem StoneSpear = new SpearItem(ItemTier.STONE, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
    private static final SpearItem IronSpear = new SpearItem(ItemTier.IRON, SPEAR_DMG, SPEAR_SPEED, 1,  (new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
    private static final SpearItem GoldSpear = new SpearItem(ItemTier.GOLD, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
    private static final SpearItem DiamondSpear = new SpearItem(ItemTier.DIAMOND, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
    private static final SpearItem NetheriteSpear = new SpearItem(ItemTier.NETHERITE, SPEAR_DMG, SPEAR_SPEED, 1, (new Item.Properties().fireResistant().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<SpearItem> WOOD_SPEAR = ITEMS.register("wood_spear", () -> WoodSpear);
    public static final RegistryObject<SpearItem> STONE_SPEAR = ITEMS.register("stone_spear", () -> StoneSpear);
    public static final RegistryObject<SpearItem> IRON_SPEAR = ITEMS.register("iron_spear", () -> IronSpear);
    public static final RegistryObject<SpearItem> GOLD_SPEAR = ITEMS.register("gold_spear", () -> GoldSpear);
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = ITEMS.register("diamond_spear", () -> DiamondSpear);
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = ITEMS.register("netherite_spear", () -> NetheriteSpear);

    public static final RegistryObject<Item> FIRECRACKER = ITEMS.register("firecracker", () -> new FirecrackerItem(new Item.Properties()));

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
            ItemModelsProperties.register(spear, new ResourceLocation("throwing"), (stack, world, user) ->
                    (user != null && user.isUsingItem() && user.getMainHandItem().equals(stack)) ? 1 : 0);
        }

        //if null pointers get thrown in the item render, look at these rascals
        for (FlailItem flail:flails)
        {
            ItemModelsProperties.register(flail, new ResourceLocation("swing"), (stack, world, user)-> user != null ? user.attackAnim : 0);
            ItemModelsProperties.register(flail, new ResourceLocation("swinging"), (stack, world, user)->
            {
                boolean mainHand = false;
                boolean offHand = false;
                if(user != null)
                {
                    mainHand = user.getMainHandItem().equals(stack) && user.swingingArm == Hand.MAIN_HAND;
                    offHand = user.getOffhandItem().equals(stack) && user.swingingArm == Hand.OFF_HAND;
                }
                return (user != null && user.attackAnim > 0 && (mainHand || offHand)) ? 1 : 0;
            });
        }
    }
}