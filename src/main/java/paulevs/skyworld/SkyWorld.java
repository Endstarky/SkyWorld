package paulevs.skyworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class SkyWorld implements ModInitializer
{
	public static final String MOD_ID = "skyworld";
	
	@Override
	public void onInitialize()
	{
		SkyChunkGenerator.register();
		SkyWorldType.register();
		StructureFeatures.register();
	}
	
	public static Identifier getID(String id)
	{
		return new Identifier(MOD_ID, id);
	}
}
