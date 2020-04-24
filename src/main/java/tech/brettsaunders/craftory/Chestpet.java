package tech.brettsaunders.craftory;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.EntityChicken;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.PathfinderGoalFollowEntity;
import net.minecraft.server.v1_15_R1.PathfinderGoalFollowParent;
import net.minecraft.server.v1_15_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_15_R1.SoundEffect;
import net.minecraft.server.v1_15_R1.SoundEffects;
import net.minecraft.server.v1_15_R1.World;

public class Chestpet extends EntityChicken {

  private int timesClicked;

  public Chestpet(EntityTypes<? extends EntityChicken> type, World world) {
    super(EntityTypes.CHICKEN, world);
  }

  public Chestpet(World world) {
    super(EntityTypes.CHICKEN, world);
  }

  protected void initPathfinder() {
    //PathfinderGoalSelector#a(entity, speed, range?, ???) taken from EntityWolf
    goalSelector.a(0,new PathfinderGoalMoveTowardsTarget(this, 2.0, 3f));
  }

  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.ARMOR).setValue(2D);
  }

  //Init stored data here (THAT IS SYNCED TO THE CLIENT, USELESS UNLESS YOU HAVE CLIENT SIDE MODS OR SOMETHING)
  @Override
  protected void initDatawatcher() {
    super.initDatawatcher();
  }

  //Load NBT data
  @Override
  public void a(NBTTagCompound nbtTagCompound) {
    super.a(nbtTagCompound);
    timesClicked = nbtTagCompound.getInt("timesClicked");
  }

  //Save NBT data
  @Override
  public void b(NBTTagCompound nbtTagCompound) {
    super.b(nbtTagCompound);
    nbtTagCompound.setInt("timesClicked", timesClicked);
  }

  //Called every tick, used for things like health.
  protected void mobTick() {
    super.mobTick();
  }

  //Plays block sound I think?
  protected void a(BlockPosition blockposition, IBlockData iblockdata) {
    this.a(SoundEffects.ENTITY_CREEPER_PRIMED, 0.15F, 1.0F);
  }

  protected SoundEffect getSoundAmbient() {
    return SoundEffects.ENTITY_CHICKEN_AMBIENT;
  }

  protected SoundEffect getSoundHurt(DamageSource damagesource) {
    return SoundEffects.ENTITY_WOLF_HURT;
  }

  protected SoundEffect getSoundDeath() {
    return SoundEffects.ENTITY_WOLF_DEATH;
  }

  protected float getSoundVolume() {
    return 0.4F;
  }

  //Called every tick the entity moves, used for movement particles.
  public void movementTick() {
    super.movementTick();
  }

  //Called every tick, used for animations.
  public void tick() {
    super.tick();
  }

  //Called on death
  public void die(DamageSource damagesource) {
    super.die(damagesource);
  }

  //Called when damaged
  public boolean damageEntity(DamageSource damagesource, float f) {
    if (this.isInvulnerable(damagesource)) {
      return false;
    } else {
      return super.damageEntity(damagesource, f);
    }
  }

  //Called when right clicked, return true to cancel item actions. REMEMBER CALLED TWICE FOR BOTH HANDS
  public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
    if(enumhand == EnumHand.MAIN_HAND) {
      timesClicked++;
      entityhuman.sendMessage(new ChatComponentText("You have clicked on this entity " + timesClicked + " times."));
      return true;
    }
    return false;
  }
}