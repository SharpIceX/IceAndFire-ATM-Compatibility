/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat.data;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.sharpice.iafatmcompat.data.function.CustomizeToDragon;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import static top.sharpice.iafatmcompat.IceAndFireATMcompatibility.IAF_MOD_ID;

public class Register {
	public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister
			.create(Registries.LOOT_FUNCTION_TYPE, IAF_MOD_ID);

	// 注册自定义 Loot 函数
	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CustomizeToDragon>> DRAGON_LOOT = LOOT_FUNCTIONS
			.register("customize_to_dragon", () -> new LootItemFunctionType<>(CustomizeToDragon.CODEC));
}
