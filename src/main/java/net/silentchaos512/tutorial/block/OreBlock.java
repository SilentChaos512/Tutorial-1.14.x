package net.silentchaos512.tutorial.block;

import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

/**
 * Extension of {@link BlockOre} which allows the dropped item to be easily set. This will likely be
 * obsolete when we update to 1.14.x (probably around part 6). I referenced a class from Silent's
 * Gems to create this, but this class is simpler. You can find the class here:
 * https://github.com/SilentChaos512/SilentGems/blob/1.13/src/main/java/net/silentchaos512/gems/block/OreBlockSG.java
 */
public class OreBlock extends BlockOre {
    private final IItemProvider droppedItem;

    public OreBlock(IItemProvider droppedItem) {
        this(droppedItem, Properties.create(Material.ROCK).hardnessAndResistance(3, 3));
    }

    public OreBlock(IItemProvider droppedItem, Properties builder) {
        super(builder);
        this.droppedItem = droppedItem;
    }

    @Override
    public int getExpDrop(IBlockState state, IWorldReader reader, BlockPos pos, int fortune) {
        World world = reader instanceof World ? (World) reader : null;
        // Note we check the dropped item is not the block, because we don't drop XP when using silk touch
        if (world == null || this.getItemDropped(state, world, pos, fortune) != this) {
            return MathHelper.nextInt(RANDOM, 1, 5);
        }
        return 0;
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        // FIXME: Not working?
        return 2;
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return droppedItem;
    }
}
