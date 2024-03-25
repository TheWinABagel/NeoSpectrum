package de.dafuqs.spectrum.api.predicate.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

public class LightPredicate {
    public static final LightPredicate ANY = new LightPredicate(null, null);
	
	@Nullable
	private final MinMaxBounds.Ints blockLight;
	@Nullable
	private final MinMaxBounds.Ints skyLight;
	
	public LightPredicate(@Nullable MinMaxBounds.Ints blockLight, @Nullable MinMaxBounds.Ints skyLight) {
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}
	
	public boolean test(ServerLevel world, BlockPos pos) {
		if (this == ANY) {
			return true;
		}
		
		if (this.blockLight != null && !this.blockLight.matches(world.getBrightness(LightLayer.BLOCK, pos))) {
			return false;
		}
		if (this.skyLight != null && !this.skyLight.matches(world.getBrightness(LightLayer.SKY, pos))) {
			return false;
		}
		
		return true;
	}
	
	public static LightPredicate fromJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
            return ANY;
        }
		
        JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "light");
        
		MinMaxBounds.Ints blockLight = MinMaxBounds.Ints.fromJson(jsonObject.get("block"));
		MinMaxBounds.Ints skyLight = MinMaxBounds.Ints.fromJson(jsonObject.get("sky"));
        
		return new LightPredicate(blockLight, skyLight);
	}
}
