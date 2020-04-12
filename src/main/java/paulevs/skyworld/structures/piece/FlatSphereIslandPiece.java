package paulevs.skyworld.structures.piece;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.math.SDF;
import paulevs.skyworld.structures.StructureTypes;

public class FlatSphereIslandPiece extends CustomPiece
{
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private BlockPos center;
	private int radius;
	private int octaves;
	private float noisePower;
	private float noiseScale;
	
	public FlatSphereIslandPiece(BlockPos center, int radius, Random random)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, random.nextInt());
		this.center = center;
		this.radius = radius;
		initValues();
		makeBoundingBox();
	}
	
	public FlatSphereIslandPiece(StructureManager manager, CompoundTag tag)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, tag);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos)
	{
		int minY = Math.max(box.minY, center.getY() - radius);
		int maxY = Math.min(box.maxY, center.getY() + radius);
		for (int y = maxY; y >= minY; y--)
		{
			B_POS.setY(y);
			for (int x = box.minX; x <= box.maxX; x++)
			{
				B_POS.setX(x);

				for (int z = box.minZ; z <= box.maxZ; z++)
				{
					B_POS.setZ(z);
					float d = SDF.sphereSDF(B_POS, center, radius);
					if (d < 0)
					{
						//d += MHelper.noise(B_POS, 0.07) * 15 + MHelper.noise(B_POS, 0.12) * 7 + SDF.gradient(B_POS, center) * 2;
						d += MHelper.noise(B_POS, noiseScale, octaves) * noisePower + SDF.gradient(B_POS, center) * 2;
						if (d < 0)
						{
							world.setBlockState(B_POS, world.isAir(B_POS.up()) ? GRASS : STONE, 0);
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void toNbt(CompoundTag tag)
	{
		tag.putInt("radius", radius);
		tag.put("center", NbtHelper.fromBlockPos(center));
	}
	
	@Override
	protected void fromNbt(CompoundTag tag)
	{
		this.radius = tag.getInt("radius");
		this.center = NbtHelper.toBlockPos(tag.getCompound("center"));
		initValues();
	}

	@Override
	protected void makeBoundingBox()
	{
		int x1 = center.getX() - radius;
		int y1 = center.getY() - radius;
		int z1 = center.getZ() - radius;
		int x2 = center.getX() + radius;
		int y2 = center.getY() + radius;
		int z2 = center.getZ() + radius;
		this.boundingBox = new BlockBox(x1, y1, z1, x2, y2, z2);
	}
	
	protected void initValues()
	{
		this.octaves = (int) Math.round(Math.log(this.radius));
		this.noisePower = (float) Math.log(this.radius) * 7F;
		this.noiseScale = 0.15F / (float) Math.log(this.radius);
	}
}
