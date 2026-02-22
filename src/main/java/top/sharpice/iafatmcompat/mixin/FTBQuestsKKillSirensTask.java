/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat.mixin;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Inject(method = "kill", at = @At("HEAD"), cancellable = true)
	private void iaf_atm_compat$redirectSirenKill(TeamData teamData, LivingEntity e, CallbackInfo ci) {
		// 确保当前做的任务是击杀`ars_elemental:siren_entity`的任务
		if (this.entityTypeId != null && "ars_elemental:siren_entity".equals(this.entityTypeId.toString())) {

			KillTask self = (KillTask) (Object) this;
			dev.ftb.mods.ftbquests.quest.Quest parentQuest = self.getQuest();

			if (parentQuest != null) {
				// 获取 Quest 原始 NBT
				CompoundTag nbt = new CompoundTag();
				parentQuest.writeData(nbt, e.level().registryAccess());

				// 检查任务图标是否包含冰火传说的塞壬特征
				if (nbt.contains("icon")) {
					String iconString = nbt.get("icon").getAsString().toLowerCase();

					if (iconString.contains("iceandfire:siren")) {
						ResourceLocation killedId = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());

						// 如果杀掉的是冰火传说的塞壬，则强制推送进度
						if (killedId != null && "iceandfire".equals(killedId.getNamespace())
								&& "siren".equals(killedId.getPath())) {

							if (!teamData.isCompleted(self)) {
								teamData.addProgress(self, 1L);
							}
							ci.cancel();
						}
					}
				}
			}
		}
	}
}
