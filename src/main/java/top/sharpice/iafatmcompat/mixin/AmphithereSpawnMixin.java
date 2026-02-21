/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.world.level.ServerLevelAccessor;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AmphithereEntity.class)
public abstract class AmphithereSpawnMixin {

	@Inject(method = "canAmphithereSpawnOn", at = @At("RETURN"), cancellable = true, remap = false)
	private static void iaf_atm_compat$allowSandBase(EntityType<AmphithereEntity> type, ServerLevelAccessor world,
			MobSpawnType reason, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue() && pos != null) {
			// 允许在沙子上生成
			if (world.getBlockState(pos.below()).is(BlockTags.SAND)) {
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(method = "checkSpawnObstruction", at = @At("RETURN"), cancellable = true)
	private void iaf_atm_compat$allowSandCanSpawn(LevelReader world, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			AmphithereEntity self = (AmphithereEntity) (Object) this;

			// 允许在沙子上，且不卡墙、非水里生成
			if (world.getBlockState(self.blockPosition().below()).is(BlockTags.SAND) && world.isUnobstructed(self)
					&& !world.containsAnyLiquid(self.getBoundingBox())) {
				cir.setReturnValue(true);
			}
		}
	}
}
