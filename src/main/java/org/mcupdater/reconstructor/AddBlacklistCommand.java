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
	public String getName() {
		return "recon_blacklist";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			sender.sendMessage(new TextComponentString("This command must be issued by an opped player in game"));
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) sender;
		if (player.getActiveItemStack() == null) {
			sender.sendMessage(new TextComponentString("This command must be issued while holding the item to be blacklisted."));
			return;
		}
		Config.blacklist.add(player.getActiveItemStack().getUnlocalizedName());
		Config.blProperty.set(Config.blacklist.toArray(new String[Config.blacklist.size()]));
		Config.config.save();
	}

}
