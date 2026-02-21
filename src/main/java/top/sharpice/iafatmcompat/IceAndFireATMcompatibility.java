/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package top.sharpice.iafatmcompat;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import top.sharpice.iafatmcompat.data.Register;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(IceAndFireATMcompatibility.MOD_ID)
public class IceAndFireATMcompatibility {
	public static final String IAF_MOD_ID = "iceandfire";
	public static final String MOD_ID = "iaf_atm_compat";

	public IceAndFireATMcompatibility(FMLModContainer container, IEventBus modBus, Dist dist) {
		Register.LOOT_FUNCTIONS.register(modBus);
	}
}
