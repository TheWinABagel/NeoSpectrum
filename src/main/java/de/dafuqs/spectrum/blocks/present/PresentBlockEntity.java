package de.dafuqs.spectrum.blocks.present;

import de.dafuqs.spectrum.api.block.PlayerOwned;
import de.dafuqs.spectrum.api.block.PlayerOwnedWithName;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.registries.SpectrumBlockEntities;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class PresentBlockEntity extends BlockEntity implements PlayerOwnedWithName {
	
	protected final NonNullList<ItemStack> stacks = NonNullList.withSize(PresentItem.MAX_STORAGE_STACKS, ItemStack.EMPTY);
	protected Map<DyeColor, Integer> colors = new HashMap<>();
	
	private UUID ownerUUID;
	private String ownerName;
	private UUID openerUUID;
	
	protected int openingTicks = 0;
	
	public PresentBlockEntity(BlockPos pos, BlockState state) {
		super(SpectrumBlockEntities.PRESENT, pos, state);
	}
	
	public void setDataFromPresentStack(ItemStack stack) {
		List<ItemStack> s = PresentItem.getBundledStacks(stack).toList();
		for (int i = 0; i < PresentItem.MAX_STORAGE_STACKS && i < s.size(); i++) {
			this.stacks.set(i, s.get(i));
		}
		this.colors = PresentItem.getColors(stack);
		
		Optional<Tuple<UUID, String>> wrapper = PresentItem.getWrapper(stack);
		if (wrapper.isPresent()) {
			this.ownerUUID = wrapper.get().getA();
			this.ownerName = wrapper.get().getB();
		}
		this.setChanged();
	}
	
	public void triggerAdvancement() {
		UUID openerUUID = getOpenerUUID();
		if (openerUUID != null) {
			Player opener = PlayerOwned.getPlayerEntityIfOnline(openerUUID);
			if (opener != null) {
				Support.grantAdvancementCriterion((ServerPlayer) opener, "gift_or_open_present", "gifted_or_opened_present");
			}
		}
		
		UUID wrapperUUID = getOwnerUUID();
		if (wrapperUUID != null) {
			Player wrapper = PlayerOwned.getPlayerEntityIfOnline(wrapperUUID);
			if (wrapper != null) {
				Support.grantAdvancementCriterion((ServerPlayer) wrapper, "gift_or_open_present", "gifted_or_opened_present");
			}
		}
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ContainerHelper.loadAllItems(nbt, this.stacks);
		this.colors = PresentItem.getColors(nbt);
		this.ownerUUID = PlayerOwned.readOwnerUUID(nbt);
		this.ownerName = PlayerOwned.readOwnerName(nbt);
		if (nbt.contains("OpenerUUID")) {
			this.openerUUID = nbt.getUUID("OpenerUUID");
		} else {
			this.openerUUID = null;
		}
		if (nbt.contains("OpeningTick", Tag.TAG_ANY_NUMERIC)) {
			this.openingTicks = nbt.getInt("OpeningTick");
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (!this.stacks.isEmpty()) {
			ContainerHelper.saveAllItems(nbt, this.stacks);
		}
		if (!this.colors.isEmpty()) {
			PresentItem.setColors(nbt, this.colors);
		}
		PlayerOwned.writeOwnerUUID(nbt, this.ownerUUID);
		PlayerOwned.writeOwnerName(nbt, this.ownerName);
		if (this.openerUUID != null) {
			nbt.putUUID("OpenerUUID", this.openerUUID);
		}
		if (this.openingTicks > 0) {
			nbt.putInt("OpeningTick", this.openingTicks);
		}
	}
	
	public int openingTick() {
		openingTicks++;
		setChanged();
		return this.openingTicks;
	}
	
	@Override
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}
	
	@Override
	public String getOwnerName() {
		return this.ownerName;
	}
	
	@Override
	public void setOwner(Player playerEntity) {
		this.ownerUUID = playerEntity.getUUID();
		this.ownerName = playerEntity.getName().getString();
		setChanged();
	}
	
	public void setOpenerUUID(Player opener) {
		this.openerUUID = opener.getUUID();
		setChanged();
	}
	
	public UUID getOpenerUUID() {
		return this.openerUUID;
	}
	
	public ItemStack retrievePresent(PresentBlock.WrappingPaper wrappingPaper) {
		ItemStack presentStack = SpectrumBlocks.PRESENT.asItem().getDefaultInstance();
		for (ItemStack contentStack : this.stacks) {
			PresentItem.addToPresent(presentStack, contentStack);
		}
		PresentItem.wrap(presentStack, wrappingPaper, this.colors);
		if (this.ownerUUID != null && this.ownerName != null) {
			PresentItem.setWrapper(presentStack, this.ownerUUID, this.ownerName);
		}
		return presentStack;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < PresentItem.MAX_STORAGE_STACKS; i++) {
			if (!stacks.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
}
