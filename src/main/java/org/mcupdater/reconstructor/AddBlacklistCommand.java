package org.mcupdater.reconstructor;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			sender.addChatMessage(new TextComponentString("This command must be issued by an opped player in game"));
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) sender;
		if (player.getActiveItemStack() == null) {
			sender.addChatMessage(new TextComponentString("This command must be issued while holding the item to be blacklisted."));
			return;
		}
		Reconstructor.blacklist.add(player.getActiveItemStack().getUnlocalizedName());
		Reconstructor.blProperty.set(Reconstructor.blacklist.toArray(new String[Reconstructor.blacklist.size()]));
		Reconstructor.config.save();
	}

}
