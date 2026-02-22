/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.resources.ResourceLocation;
import dev.ftb.mods.ftbquests.quest.task.KillTask;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KillTask.class, remap = false)
public class FTBQuestsKKillSirensTask {
	@Shadow
	private ResourceLocation entityTypeId;

	@Unique
	private boolean iaf_atm_compat$isSirenPatchNeeded = false;

	/**
	 * 编辑模式下
	 */
	@Inject(method = "readData", at = @At("RETURN"))
	private void iaf_atm_compat$identifySirenTask(CompoundTag nbt, HolderLookup.Provider provider, CallbackInfo ci) {
		this.iaf_atm_compat$isSirenPatchNeeded = false;

		// 仅当前任务是击杀 `ars_elemental:siren_entity` 实体
		if (this.entityTypeId != null && "ars_elemental:siren_entity".equals(this.entityTypeId.toString())) {
			String nbtString = nbt.toString().toLowerCase();

			// 如果 NBT 使用了有关冰火传说的数据（例如图标），则表示当前任务设置出错
			if (nbtString.contains("iceandfire:siren")) {
				this.iaf_atm_compat$isSirenPatchNeeded = true;
			}
		}
	}

	/**
	 * 击杀判定
	 */
	@Inject(method = "kill", at = @At("HEAD"), cancellable = true)
	private void iaf_atm_compat$redirectSirenKill(TeamData teamData, LivingEntity e, CallbackInfo ci) {
		if (this.iaf_atm_compat$isSirenPatchNeeded) {
			ResourceLocation killedId = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());

			// 杀掉的是不是 `iceandfire：siren`
			if (killedId != null && "iceandfire".equals(killedId.getNamespace())
					&& "siren".equals(killedId.getPath())) {
				KillTask self = (KillTask) (Object) this;

				// 手动推送进度
				if (!teamData.isCompleted(self)) {
					teamData.addProgress(self, 1L);
				}

				ci.cancel();
			}
		}
	}
}
