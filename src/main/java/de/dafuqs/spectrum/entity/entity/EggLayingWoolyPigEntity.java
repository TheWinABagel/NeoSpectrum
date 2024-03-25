package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EggLayingWoolyPigEntity extends Animal implements Shearable {
	
	private static final Ingredient FOOD = Ingredient.of(SpectrumBlocks.AMARANTH_BUSHEL);
	
	private static final int MAX_GRASS_TIMER = 40;
	private static final EntityDataAccessor<Byte> COLOR_AND_SHEARED = SynchedEntityData.defineId(EggLayingWoolyPigEntity.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> HATLESS = SynchedEntityData.defineId(EggLayingWoolyPigEntity.class, EntityDataSerializers.BOOLEAN);
	private static final Map<DyeColor, float[]> COLORS = new EnumMap<>(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), EggLayingWoolyPigEntity::getDyedColor)));
	private static final ResourceLocation SHEARING_LOOT_TABLE_ID = SpectrumCommon.locate("entities/egg_laying_wooly_pig_shearing");
	
	private int eatGrassTimer;
	private EatBlockGoal eatGrassGoal;
	public int eggLayTime;
	
	public EggLayingWoolyPigEntity(EntityType<? extends Animal> entityType, Level world) {
		super(entityType, world);
		this.eggLayTime = this.random.nextInt(12000) + 12000;
	}
	
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		Level world = this.level();
		ItemStack handStack = player.getItemInHand(hand);

		if (handStack.getItem() instanceof DyeItem dyeItem && isAlive() && getColor() != dyeItem.getDyeColor()) {
			world.playSound(player, this, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
			if (!world.isClientSide) {
				setColor(dyeItem.getDyeColor());
				handStack.shrink(1);
			}
			return InteractionResult.sidedSuccess(world.isClientSide);
		} else if (handStack.is(Items.BUCKET) && !this.isBaby()) {
			player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
			ItemStack itemStack2 = ItemUtils.createFilledResult(handStack, player, Items.MILK_BUCKET.getDefaultInstance());
			player.setItemInHand(hand, itemStack2);
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else if (handStack.is(ConventionalItemTags.SHEARS)) {
			if (!world.isClientSide() && this.readyForShearing()) {
				this.shear(SoundSource.PLAYERS);
				this.gameEvent(GameEvent.SHEAR, player);
				handStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.CONSUME;
			}
		} else {
			return super.mobInteract(player, hand);
		}
	}
	
	public static AttributeSupplier.Builder createEggLayingWoolyPigAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 12.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	public boolean isFood(ItemStack stack) {
		return FOOD.test(stack);
	}
	
	@Override
	protected void registerGoals() {
		this.eatGrassGoal = new EatBlockGoal(this);
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, FOOD, false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(5, this.eatGrassGoal);
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}
	
	@Override
	public void handleEntityEvent(byte status) {
		if (status == 10) {
			this.eatGrassTimer = MAX_GRASS_TIMER;
		} else {
			super.handleEntityEvent(status);
		}
	}
	
	@Override
	protected void customServerAiStep() {
		this.eatGrassTimer = this.eatGrassGoal.getEatAnimationTick();
		super.customServerAiStep();
	}
	
	@Override
	public void aiStep() {
		if (this.level().isClientSide()) {
			this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
		}
		
		if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.eggLayTime <= 0) {
			this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.spawnAtLocation(Items.EGG);
			this.eggLayTime = this.random.nextInt(6000) + 6000;
		}
		
		super.aiStep();
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(COLOR_AND_SHEARED, (byte) 0);
		this.entityData.define(HATLESS, false);
	}
	
	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		EggLayingWoolyPigEntity other = (EggLayingWoolyPigEntity) entity;
		EggLayingWoolyPigEntity child = SpectrumEntityTypes.EGG_LAYING_WOOLY_PIG.create(world);
		if (child != null) {
			child.setColor(this.getChildColor(this, other));
			if (world.random.nextInt(50) == 0) {
				child.setHatless(true);
			}
		}
		return child;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean("Sheared", this.isSheared());
		nbt.putBoolean("Hatless", this.isHatless());
		nbt.putByte("Color", (byte) this.getColor().getId());
		nbt.putInt("EggLayTime", this.eggLayTime);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.setSheared(nbt.getBoolean("Sheared"));
		this.setHatless(nbt.getBoolean("Hatless"));
		this.setColor(DyeColor.byId(nbt.getByte("Color")));
		if (nbt.contains("EggLayTime")) {
			this.eggLayTime = nbt.getInt("EggLayTime");
		}
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.PIG_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.PIG_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.PIG_DEATH;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
	}
	
	@Override
	public void ate() {
		this.setSheared(false);
		if (this.isBaby()) {
			this.ageUp(60);
		}
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.95F * dimensions.height;
	}
	
	public float getNeckAngle(float delta) {
		if (this.eatGrassTimer <= 0) {
			return 0.0F;
		} else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
			return 1.0F;
		} else {
			return this.eatGrassTimer < 4 ? ((float) this.eatGrassTimer - delta) / 4.0F : -((float) (this.eatGrassTimer - MAX_GRASS_TIMER) - delta) / 4.0F;
		}
	}
	
	public float getHeadAngle(float delta) {
		if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
			float f = ((float) (this.eatGrassTimer - 4) - delta) / 32.0F;
			return 0.62831855F + 0.21991149F * Mth.sin(f * 28.7F);
		} else {
			return this.eatGrassTimer > 0 ? 0.62831855F : this.getXRot() * 0.017453292F;
		}
	}
	
	@Override
	public void shear(SoundSource shearedSoundCategory) {
		var world = this.level();
		world.playSound(null, this, SoundEvents.SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		this.setSheared(true);
		
		for (ItemStack droppedStack : getShearedStacks((ServerLevel) world)) {
			ItemEntity itemEntity = this.spawnAtLocation(droppedStack, 1);
			if (itemEntity != null) {
				itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1F, this.random.nextFloat() * 0.05F, (this.random.nextFloat() - this.random.nextFloat()) * 0.1F));
			}
		}
	}
	
	public List<ItemStack> getShearedStacks(ServerLevel world) {
		var builder = (new LootParams.Builder(world))
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, this.position());
		
		LootTable lootTable = world.getServer().getLootData().getLootTable(SHEARING_LOOT_TABLE_ID);
		return lootTable.getRandomItems(builder.create(LootContextParamSets.GIFT));
	}
	
	@Override
	public boolean readyForShearing() {
		return this.isAlive() && !this.isSheared() && !this.isBaby();
	}
	
	public boolean isSheared() {
		return (this.entityData.get(COLOR_AND_SHEARED) & 16) != 0;
	}
	
	public void setSheared(boolean sheared) {
		byte color = this.entityData.get(COLOR_AND_SHEARED);
		if (sheared) {
			this.entityData.set(COLOR_AND_SHEARED, (byte) (color | 16));
		} else {
			this.entityData.set(COLOR_AND_SHEARED, (byte) (color & -17));
		}
	}
	
	public boolean isHatless() {
		return this.entityData.get(HATLESS);
	}
	
	public void setHatless(boolean hatless) {
		this.entityData.set(HATLESS, hatless);
	}
	
	// COLORING
	public static float[] getRgbColor(DyeColor dyeColor) {
		return COLORS.get(dyeColor);
	}
	
	private static float[] getDyedColor(DyeColor color) {
		if (color == DyeColor.WHITE) {
			return new float[]{1.0F, 1.0F, 1.0F};
		} else {
			float[] fs = color.getTextureDiffuseColors();
			return new float[]{fs[0], fs[1], fs[2]};
		}
	}
	
	public DyeColor getColor() {
		return DyeColor.byId(this.entityData.get(COLOR_AND_SHEARED) & 15);
	}
	
	public void setColor(DyeColor color) {
		byte b = this.entityData.get(COLOR_AND_SHEARED);
		this.entityData.set(COLOR_AND_SHEARED, (byte) (b & 240 | color.getId() & 15));
	}
	
	private DyeColor getChildColor(Animal firstParent, Animal secondParent) {
		Level world = this.level();
		DyeColor dyeColor = ((EggLayingWoolyPigEntity) firstParent).getColor();
		DyeColor dyeColor2 = ((EggLayingWoolyPigEntity) secondParent).getColor();
		TransientCraftingContainer craftingInventory = createDyeMixingCraftingInventory(dyeColor, dyeColor2);
		Optional<Item> optionalItem = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory, world).map((recipe) -> recipe.assemble(craftingInventory, world.registryAccess())).map(ItemStack::getItem);
		
		if (optionalItem.isPresent() && optionalItem.get() instanceof DyeItem dyeItem) {
			return dyeItem.getDyeColor();
		}
		return world.random.nextBoolean() ? dyeColor : dyeColor2;
	}
	
	private static TransientCraftingContainer createDyeMixingCraftingInventory(DyeColor firstColor, DyeColor secondColor) {
		TransientCraftingContainer craftingInventory = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
			@Override
			public ItemStack quickMoveStack(Player player, int index) {
				return ItemStack.EMPTY;
			}
			
			@Override
			public boolean stillValid(Player player) {
				return false;
			}
		}, 2, 1);
		craftingInventory.setItem(0, new ItemStack(DyeItem.byColor(firstColor)));
		craftingInventory.setItem(1, new ItemStack(DyeItem.byColor(secondColor)));
		return craftingInventory;
	}
	
}
