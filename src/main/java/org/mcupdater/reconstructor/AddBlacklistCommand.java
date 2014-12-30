package org.mcupdater.reconstructor;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class AddBlacklistCommand extends CommandBase
{
	@Override
	public String getCommandName() {
		return "recon_blacklist";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		if (!(sender instanceof EntityPlayerMP)) {
			sender.addChatMessage(new ChatComponentText("This command must be issued by an opped player in game"));
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) sender;
		if (player.getCurrentEquippedItem() == null) {
			sender.addChatMessage(new ChatComponentText("This command must be issued while holding the item to be blacklisted."));
			return;
		}
		Reconstructor.blacklist.add(player.getCurrentEquippedItem().getUnlocalizedName());
		Reconstructor.blProperty.set(Reconstructor.blacklist.toArray(new String[Reconstructor.blacklist.size()]));
	}

}
