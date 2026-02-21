/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat.data.function;

import java.util.List;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import top.sharpice.iafatmcompat.data.Register;
import net.minecraft.resources.ResourceLocation;
import com.iafenvoy.iceandfire.loot.DragonLootFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CustomizeToDragon extends LootItemConditionalFunction {
	private static final ResourceLocation OLD_BONE_ID = ResourceLocation.fromNamespaceAndPath("iceandfire",
			"dragon_bone");
	private static final ResourceLocation NEW_BONE_ID = ResourceLocation.fromNamespaceAndPath("iceandfire",
			"dragonbone");

	public static final MapCodec<CustomizeToDragon> CODEC = RecordCodecBuilder
			.mapCodec(instance -> commonFields(instance).apply(instance, CustomizeToDragon::new));

	private final DragonLootFunction delegate;

	public CustomizeToDragon(List<LootItemCondition> predicates) {
		super(predicates);
		this.delegate = new DragonLootFunction(predicates);
	}

	@Override
	public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
		return Register.DRAGON_LOOT.get();
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		if (stack.isEmpty())
			return stack;

		ResourceLocation currentItemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

		// 如果是旧版龙骨 ID 则改为新版 ID
		if (OLD_BONE_ID.equals(currentItemId)) {
			var newItem = BuiltInRegistries.ITEM.get(NEW_BONE_ID);
			ItemStack newStack = new ItemStack(newItem, stack.getCount());
			newStack.applyComponents(stack.getComponents());
			return this.delegate.apply(newStack, context);
		}

		return this.delegate.apply(stack, context);
	}
}
