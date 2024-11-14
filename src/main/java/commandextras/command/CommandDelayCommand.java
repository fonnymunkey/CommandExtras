package commandextras.command;

import commandextras.handler.DelayedCommandHandler;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandDelayCommand extends CommandBase {
	
	@Override
	public String getName() {
		return "commanddelay";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "/commanddelay <ticks> <entity> <x> <y> <z> <command>";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 6) {
			throw new WrongUsageException("/commanddelay <ticks> <entity> <x> <y> <z> <command>");
		}
		else {
			int time = parseInt(args[0]);
			Entity entity = getEntity(server, sender, args[1], Entity.class);
			double d0 = parseDouble(entity.posX, args[2], false);
			double d1 = parseDouble(entity.posY, args[3], false);
			double d2 = parseDouble(entity.posZ, args[4], false);
			
			String s = buildString(args, 5);
			ICommandSender icommandsender = CommandSenderWrapper.create(sender).withEntity(entity, new Vec3d(d0, d1, d2)).withSendCommandFeedback(server.worlds[0].getGameRules().getBoolean("commandBlockOutput"));
			
			if(time > 0) {
				if(!s.isEmpty()) {
					DelayedCommandHandler.addDelayedCommandEntry(new DelayedCommandEntry(time + server.getTickCounter(), icommandsender, server, s));
				}
				else {
					throw new WrongUsageException("Command must not be empty");
				}
			}
			else {
				throw new NumberInvalidException("Time to execution can not be 0 or less");
			}
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if(args.length == 2) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		else if(args.length > 2 && args.length <= 5) {
			return getTabCompletionCoordinate(args, 2, targetPos);
		}
		else return Collections.emptyList();
	}
	
	public static class DelayedCommandEntry {
		
		private final int time;
		private final ICommandSender sender;
		private final MinecraftServer server;
		private final String command;
		
		public DelayedCommandEntry(int time, ICommandSender sender, MinecraftServer server, String command) {
			this.time = time;
			this.sender = sender;
			this.server = server;
			this.command = command;
		}
		
		public int getTime() {
			return this.time;
		}
		
		public ICommandSender getSender() {
			return this.sender;
		}
		
		public MinecraftServer getServer() {
			return this.server;
		}
		
		public String getCommand() {
			return this.command;
		}
	}
}