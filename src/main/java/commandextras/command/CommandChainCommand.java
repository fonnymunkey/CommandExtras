package commandextras.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandChainCommand extends CommandBase {
	
	@Override
	public String getName() {
		return "commandchain";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "/commandchain <entity> <x> <y> <z> <command1> &&0 <command2> &&0 ... (&&1 &&2 etc for nested chains)";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 5) {
			throw new WrongUsageException("/commandchain <entity> <x> <y> <z> <command1> &&0 <command2> &&0 ... (&&1 &&2 etc for nested chains)");
		}
		else {
			Entity entity = getEntity(server, sender, args[0], Entity.class);
			double d0 = parseDouble(entity.posX, args[1], false);
			double d1 = parseDouble(entity.posY, args[2], false);
			double d2 = parseDouble(entity.posZ, args[3], false);
			
			ICommandSender icommandsender = CommandSenderWrapper.create(sender).withEntity(entity, new Vec3d(d0, d1, d2)).withSendCommandFeedback(server.worlds[0].getGameRules().getBoolean("commandBlockOutput"));
			
			List<String> commands = parseCommands(buildString(args, 4));
			for(String command : commands) {
				command = command.trim();
				if(command.isEmpty()) continue;
				
				server.getCommandManager().executeCommand(icommandsender, command);
			}
		}
	}
	
	private static List<String> parseCommands(String raw) {
		//Split command string at &&0 (0 depth)
		String[] commands = raw.split("&&0");
		List<String> result = new ArrayList<>();
		
		for(String command : commands) {
			StringBuilder commBuild = new StringBuilder();
			for(int i = 0; i < command.length(); i++) {
				//Find each &&x remaining and decrease the value of x by 1, to handle nested chains
				if(i + 2 < command.length() && Character.isDigit(command.charAt(i + 2)) && command.startsWith("&&", i)) {
					i += 2;
					int x = Character.getNumericValue(command.charAt(i));
					commBuild.append("&&");
					commBuild.append(x - 1);
				}
				else {
					commBuild.append(command.charAt(i));
				}
			}
			result.add(commBuild.toString());
		}
		return result;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if(args.length == 1) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		else if(args.length > 1 && args.length <= 4) {
			return getTabCompletionCoordinate(args, 1, targetPos);
		}
		else return Collections.emptyList();
	}
}