package de.dafuqs.spectrum.entity.entity;

import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColorTags;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.api.entity.POIMemorized;
import de.dafuqs.spectrum.api.entity.PackEntity;
import de.dafuqs.spectrum.entity.SpectrumEntityTypes;
import de.dafuqs.spectrum.entity.SpectrumTrackedDataHandlerRegistry;
import de.dafuqs.spectrum.entity.variants.LizardFrillVariant;
import de.dafuqs.spectrum.entity.variants.LizardHornVariant;
import de.dafuqs.spectrum.registries.SpectrumItems;
import de.dafuqs.spectrum.registries.SpectrumPointOfInterestTypeTags;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// funny little creatures always out for trouble
public class LizardEntity extends TamableAnimal implements PackEntity<LizardEntity>, POIMemorized {

	protected static final EntityDataAccessor<LizardFrillVariant> FRILL_VARIANT = SynchedEntityData.defineId(LizardEntity.class, SpectrumTrackedDataHandlerRegistry.LIZARD_FRILL_VARIANT);
	protected static final EntityDataAccessor<LizardHornVariant> HORN_VARIANT = SynchedEntityData.defineId(LizardEntity.class, SpectrumTrackedDataHandlerRegistry.LIZARD_HORN_VARIANT);
	protected static final EntityDataAccessor<InkColor> COLOR = SynchedEntityData.defineId(LizardEntity.class, SpectrumTrackedDataHandlerRegistry.INK_COLOR);

	protected @Nullable LizardEntity leader;
	protected int groupSize = 1;

	protected int ticksLeftToFindPOI;
	protected @Nullable BlockPos poiPos;
	
	public LizardEntity(EntityType<? extends LizardEntity> entityType, Level world) {
		super(entityType, world);
		this.xpReward = 4;
	}
	
	public static AttributeSupplier.Builder createLizardAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 60.0D)
				.add(Attributes.ATTACK_DAMAGE, 16.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
				.add(Attributes.ARMOR, 6.0D)
				.add(Attributes.ARMOR_TOUGHNESS, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D)
				.add(Attributes.FOLLOW_RANGE, 12.0D);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new OcelotAttackGoal(this));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.2D));
		this.goalSelector.addGoal(4, new FollowClanLeaderGoal<>(this));
		this.goalSelector.addGoal(5, new FindPOIGoal(PoiTypes.LODESTONE, 32));
		this.goalSelector.addGoal(6, new ClanLeaderWanderAroundGoal(this, 0.8, 20, 8, 4));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, target -> !LizardEntity.this.isOwnedBy(target)));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, // different clans attacking each other
				target -> {
					if (target instanceof LizardEntity other) {
						return isDifferentPack(other);
					}
					return !target.isBaby();
				}));
	}

	@Override
	public float getLightLevelDependentMagicValue() {
		return 1.0F;
	}

	@Override
	public boolean isOwnedBy(LivingEntity entity) {
		return entity == this.getOwner() || this.leader != null && entity == this.leader.getOwner();
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		if (this.tickCount % 1200 == 0) {
			this.heal(1.0F);
		}
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(COLOR, InkColors.MAGENTA);
		this.entityData.define(FRILL_VARIANT, LizardFrillVariant.SIMPLE);
		this.entityData.define(HORN_VARIANT, LizardHornVariant.HORNY);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
		RandomSource random = world.getRandom();
		this.setFrills(SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.LIZARD_FRILL_VARIANT, LizardFrillVariant.NATURAL_VARIANT, random, LizardFrillVariant.SIMPLE));
		this.setHorns(SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.LIZARD_HORN_VARIANT, LizardHornVariant.NATURAL_VARIANT, random, LizardHornVariant.HORNY));
		this.setColor(SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.INK_COLORS, InkColorTags.ELEMENTAL_COLORS, random, InkColors.MAGENTA));

		return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("color", SpectrumRegistries.INK_COLORS.getKey(this.getColor()).toString());
		nbt.putString("frills", SpectrumRegistries.LIZARD_FRILL_VARIANT.getKey(this.getFrills()).toString());
		nbt.putString("horns", SpectrumRegistries.LIZARD_HORN_VARIANT.getKey(this.getHorns()).toString());
		writePOIPosToNbt(nbt);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		
		InkColor color = SpectrumRegistries.INK_COLORS.get(ResourceLocation.tryParse(nbt.getString("color")));
		this.setColor(color == null ? SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.INK_COLORS, InkColorTags.ELEMENTAL_COLORS, this.random, InkColors.CYAN) : color);

		LizardFrillVariant frills = SpectrumRegistries.LIZARD_FRILL_VARIANT.get(ResourceLocation.tryParse(nbt.getString("frills")));
		this.setFrills(frills == null ? SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.LIZARD_FRILL_VARIANT, LizardFrillVariant.NATURAL_VARIANT, this.random, LizardFrillVariant.SIMPLE) : frills);
		
		LizardHornVariant horns = SpectrumRegistries.LIZARD_HORN_VARIANT.get(ResourceLocation.tryParse(nbt.getString("horns")));
		this.setHorns(horns == null ? SpectrumRegistries.getRandomTagEntry(SpectrumRegistries.LIZARD_HORN_VARIANT, LizardHornVariant.NATURAL_VARIANT, this.random, LizardHornVariant.HORNY) : horns);
		
		readPOIPosFromNbt(nbt);
	}

	@Override
	public void aiStep() {
		Level world = this.level();
		super.aiStep();
		if (!world.isClientSide && this.ticksLeftToFindPOI > 0) {
			--this.ticksLeftToFindPOI;
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		Level world = this.level();
		ItemStack itemStack = player.getItemInHand(hand);
		if (this.isFood(itemStack)) {
			int i = this.getAge();
			if (!world.isClientSide && i == 0 && this.canFallInLove() && this.random.nextInt(5) == 0) {
				// yes, this also overrides the existing owner
				// there is no god besides the new god
				this.usePlayerItem(player, hand, itemStack);
				this.tame(player);
				this.setInLove(player);
				return InteractionResult.SUCCESS;
			}

			if (this.isBaby()) {
				this.usePlayerItem(player, hand, itemStack);
				this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
				return InteractionResult.sidedSuccess(world.isClientSide);
			}

			if (world.isClientSide) {
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public boolean canFallInLove() {
		return super.canFallInLove() || getOwner() != null;
	}
	
	public InkColor getColor() {
		return this.entityData.get(COLOR);
	}
	
	public void setColor(InkColor color) {
		this.entityData.set(COLOR, color);
	}
	
	public LizardFrillVariant getFrills() {
		return this.entityData.get(FRILL_VARIANT);
	}
	
	public void setFrills(LizardFrillVariant variant) {
		this.entityData.set(FRILL_VARIANT, variant);
	}
	
	public LizardHornVariant getHorns() {
		return this.entityData.get(HORN_VARIANT);
	}
	
	public void setHorns(LizardHornVariant variant) {
		this.entityData.set(HORN_VARIANT, variant);
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SpectrumSoundEvents.ENTITY_LIZARD_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SpectrumSoundEvents.ENTITY_LIZARD_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SpectrumSoundEvents.ENTITY_LIZARD_DEATH;
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.5F * dimensions.height;
	}

	// Breeding

	@Override
	public boolean isFood(ItemStack stack) {
		if (stack.is(SpectrumItems.LIZARD_MEAT)) {
			return false;
		}
		FoodProperties food = stack.getItem().getFoodProperties();
		return food != null && food.isMeat();
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		LizardEntity other = (LizardEntity) entity;
		LizardEntity child = SpectrumEntityTypes.LIZARD.create(world);
		if (child != null) {
			child.setColor(getChildColor(this, other));
			child.setFrills(getChildFrills(this, other));
			child.setHorns(getChildHorns(this, other));
		}
		return child;
	}
	
	private InkColor getChildColor(LizardEntity firstParent, LizardEntity secondParent) {
		Level world = firstParent.level();
		InkColor color1 = firstParent.getColor();
		InkColor color2 = secondParent.getColor();

		return InkColor.getRandomMixedColor(color1, color2, world.random);
	}

	private LizardFrillVariant getChildFrills(LizardEntity firstParent, LizardEntity secondParent) {
		Level world = this.level();
		return world.random.nextBoolean() ? firstParent.getFrills() : secondParent.getFrills();
	}

	private LizardHornVariant getChildHorns(LizardEntity firstParent, LizardEntity secondParent) {
		Level world = this.level();
		return world.random.nextBoolean() ? firstParent.getHorns() : secondParent.getHorns();
	}

	// PackEntity

	@Override
	public boolean hasOthersInGroup() {
		return this.groupSize > 1;
	}

	@Override
	public @Nullable LizardEntity getLeader() {
		return this.leader;
	}

	@Override
	public boolean isCloseEnoughToLeader() {
		return this.distanceToSqr(this.leader) <= 121.0;
	}

	@Override
	public void leaveGroup() {
		this.leader.decreaseGroupSize();
		this.leader = null;
	}

	@Override
	public void moveTowardLeader() {
		if (this.hasLeader()) {
			this.getNavigation().moveTo(this.leader, 1.0);
		}
	}

	@Override
	public int getMaxGroupSize() {
		return super.getMaxSpawnClusterSize();
	}

	@Override
	public void joinGroupOf(LizardEntity groupLeader) {
		this.leader = groupLeader;
		groupLeader.increaseGroupSize();
	}

	@Override
	public int getGroupSize() {
		return this.groupSize;
	}

	protected void increaseGroupSize() {
		++this.groupSize;
	}

	protected void decreaseGroupSize() {
		--this.groupSize;
	}

	// POIMemorized
	@Override
	public TagKey<PoiType> getPOITag() {
		return SpectrumPointOfInterestTypeTags.LIZARD_DENS;
	}

	@Override
	public @Nullable BlockPos getPOIPos() {
		return this.poiPos;
	}

	@Override
	public void setPOIPos(@Nullable BlockPos blockPos) {
		this.poiPos = blockPos;
	}

	// Goals
	protected class ClanLeaderWanderAroundGoal extends RandomStrollGoal {

		int chanceToNavigateToPOI;
		int maxDistanceFromPOI;

		public ClanLeaderWanderAroundGoal(PathfinderMob mob, double speed, int chance, int chanceToNavigateToPOI, int maxDistanceFromPOI) {
			super(mob, speed, chance);
			this.chanceToNavigateToPOI = chanceToNavigateToPOI;
			this.maxDistanceFromPOI = maxDistanceFromPOI;
		}

		@Override
		public boolean canUse() {
			return !LizardEntity.this.hasLeader() && super.canUse();
		}

		@Override
		protected @Nullable Vec3 getPosition() {
			// when we are away from our poi (their den) there is a chance they navigate back to it, so they always stay near
			if (random.nextFloat() < this.chanceToNavigateToPOI
					&& LizardEntity.this.isPOIValid((ServerLevel) LizardEntity.this.level())
					&& !LizardEntity.this.blockPosition().closerThan(LizardEntity.this.poiPos, this.maxDistanceFromPOI)) {

				return Vec3.atCenterOf(LizardEntity.this.poiPos);
			}

			return DefaultRandomPos.getPos(LizardEntity.this, 8, 7);
		}

	}

	private class FindPOIGoal extends Goal {

		FindPOIGoal(ResourceKey<PoiType> poiType, int maxDistance) {
			super();
		}

		@Override
		public boolean canUse() {
			return LizardEntity.this.hasOthersInGroup()
					&& LizardEntity.this.ticksLeftToFindPOI == 0
					&& !LizardEntity.this.isPOIValid((ServerLevel) LizardEntity.this.level());
		}

		@Override
		public void start() {
			LizardEntity.this.ticksLeftToFindPOI = 200;
			LizardEntity.this.poiPos = LizardEntity.this.findNearestPOI((ServerLevel) LizardEntity.this.level(), LizardEntity.this.blockPosition(), 40);
		}

	}

	@Override
	public EntityGetter level() {
		return this.level();
	}

}
