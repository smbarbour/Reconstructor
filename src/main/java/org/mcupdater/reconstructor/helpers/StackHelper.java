/**
 * Copyright (c) SpaceToad, 2011 http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License
 * 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 *
 * This code is directly from Buildcraft, the package name has been modified and unused functions removed.
 */
package org.mcupdater.reconstructor.helpers;

import net.minecraft.item.ItemStack;

public class StackHelper {

	private static StackHelper instance;

	public static StackHelper instance() {
		if (instance == null) {
			instance = new StackHelper();
		}
		return instance;
	}

	protected StackHelper() {
	}

	/* STACK MERGING */
	/**
	 * Checks if two ItemStacks are identical enough to be merged
	 *
	 * @param stack1 - The first stack
	 * @param stack2 - The second stack
	 * @return true if stacks can be merged, false otherwise
	 */
	public boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null)
			return false;
		if (!stack1.isItemEqual(stack2))
			return false;
		if (!ItemStack.areItemStackTagsEqual(stack1, stack2))
			return false;
		return true;

	}

}
