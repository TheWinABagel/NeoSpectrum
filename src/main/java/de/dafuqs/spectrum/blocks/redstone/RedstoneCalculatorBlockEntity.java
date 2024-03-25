package de.dafuqs.spectrum.blocks.redstone;

import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneCalculatorBlockEntity extends BlockEntity {
	
	private int outputSignal;
	
	public RedstoneCalculatorBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.REDSTONE_CALCULATOR, pos, state);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("output_signal", this.outputSignal);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.outputSignal = nbt.getInt("output_signal");
	}
	
	public int getOutputSignal() {
		return this.outputSignal;
	}
	
	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
	
}
