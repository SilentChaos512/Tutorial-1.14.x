package net.silentchaos512.tutorial.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

/**
 * Extension of {@link OreBlock} which allows the dropped item to be easily set. This will likely be
 * obsolete when we update to 1.14.x (probably around part 6). I referenced a class from Silent's
 * Gems to create this, but this class is simpler. You can find the class here:
 * https://github.com/SilentChaos512/SilentGems/blob/1.13/src/main/java/net/silentchaos512/gems/block/OreBlockSG.java
 */
public class OreBlock extends net.minecraft.block.OreBlock {
    public OreBlock() {
        this(Properties.create(Material.ROCK).hardnessAndResistance(3, 3));
    }

    public OreBlock(Properties builder) {
        super(builder);
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? MathHelper.nextInt(RANDOM, 1, 5) : 0;
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        // FIXME: Not working?
        return 2;
    }
}
