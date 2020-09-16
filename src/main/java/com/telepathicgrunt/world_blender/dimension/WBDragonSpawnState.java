package net.telepathicgrunt.worldblender.dimension;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;


public enum WBDragonSpawnState
{
	START
	{
		@Override
		public void process(ServerWorld p_186079_1_, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_)
		{
			BlockPos blockpos = new BlockPos(0, 128, 0);

			for (EnderCrystalEntity endercrystalentity : p_186079_3_)
			{
				endercrystalentity.setBeamTarget(blockpos);
			}

			p_186079_2_.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
		}
	},
	PREPARING_TO_SUMMON_PILLARS
	{
		@Override
		public void process(ServerWorld p_186079_1_, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_)
		{
			if (p_186079_4_ < 100)
			{
				if (p_186079_4_ == 0 || p_186079_4_ == 50 || p_186079_4_ == 51 || p_186079_4_ == 52 || p_186079_4_ >= 95)
				{
					p_186079_1_.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
				}
			}
			else
			{
				p_186079_2_.setRespawnState(SUMMONING_PILLARS);
			}

		}
	},
	SUMMONING_PILLARS
	{
		@Override
		public void process(ServerWorld world, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_)
		{
			boolean flag = p_186079_4_ % 40 == 0;
			boolean flag1 = p_186079_4_ % 40 == 39;
			if (flag || flag1)
			{
				List<EndSpikeFeature.Spike> list = EndSpikeFeature.getSpikes(world);
				int j = p_186079_4_ / 40;
				if (j < list.size())
				{
					EndSpikeFeature.Spike endspikefeature$endspike = list.get(j);
					if (flag)
					{
						for (EnderCrystalEntity endercrystalentity : p_186079_3_)
						{
							endercrystalentity.setBeamTarget(new BlockPos(endspikefeature$endspike.getCenterX(), endspikefeature$endspike.getHeight() + 1, endspikefeature$endspike.getCenterZ()));
						}
					}
					else
					{
						for (BlockPos blockpos : BlockPos.iterate(new BlockPos(endspikefeature$endspike.getCenterX() - 10, endspikefeature$endspike.getHeight() - 10, endspikefeature$endspike.getCenterZ() - 10), new BlockPos(endspikefeature$endspike.getCenterX() + 10, endspikefeature$endspike.getHeight() + 10, endspikefeature$endspike.getCenterZ() + 10)))
						{
							world.removeBlock(blockpos, false);
						}

						world.createExplosion((Entity) null, endspikefeature$endspike.getCenterX() + 0.5F, endspikefeature$endspike.getHeight(), endspikefeature$endspike.getCenterZ() + 0.5F, 5.0F, Explosion.DestructionType.DESTROY);
						EndSpikeFeatureConfig endspikefeatureconfig = new EndSpikeFeatureConfig(true, ImmutableList.of(endspikefeature$endspike), new BlockPos(0, 128, 0));
						Feature.END_SPIKE.configure(endspikefeatureconfig).generate(world, world.getChunkManager().getChunkGenerator(), new Random(), new BlockPos(endspikefeature$endspike.getCenterX(), 45, endspikefeature$endspike.getCenterZ()));
					}
				}
				else if (flag)
				{
					p_186079_2_.setRespawnState(SUMMONING_DRAGON);
				}
			}

		}
	},
	SUMMONING_DRAGON
	{
		@Override
		public void process(ServerWorld p_186079_1_, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_)
		{
			if (p_186079_4_ >= 100)
			{
				p_186079_2_.setRespawnState(END);
				p_186079_2_.resetSpikeCrystals();

				for (EnderCrystalEntity endercrystalentity : p_186079_3_)
				{
					endercrystalentity.setBeamTarget((BlockPos) null);
					p_186079_1_.createExplosion(endercrystalentity, endercrystalentity.getX(), endercrystalentity.getY(), endercrystalentity.getZ(), 6.0F, Explosion.DestructionType.NONE);
					endercrystalentity.remove();
				}
			}
			else if (p_186079_4_ >= 80)
			{
				p_186079_1_.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
			}
			else if (p_186079_4_ == 0)
			{
				for (EnderCrystalEntity endercrystalentity1 : p_186079_3_)
				{
					endercrystalentity1.setBeamTarget(new BlockPos(0, 128, 0));
				}
			}
			else if (p_186079_4_ < 5)
			{
				p_186079_1_.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
			}

		}
	},
	END
	{
		@Override
		public void process(ServerWorld p_186079_1_, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_)
		{
		}
	};

	private WBDragonSpawnState() 
	{
	}

	public abstract void process(ServerWorld p_186079_1_, WBDragonManager p_186079_2_, List<EnderCrystalEntity> p_186079_3_, int p_186079_4_, BlockPos p_186079_5_);
}