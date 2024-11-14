package commandextras.handler;

import commandextras.CommandExtras;
import commandextras.command.CommandDelayCommand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DelayedCommandHandler {
	
	private static final List<CommandDelayCommand.DelayedCommandEntry> delayedEntries = new ArrayList<>();
	
	public static void addDelayedCommandEntry(CommandDelayCommand.DelayedCommandEntry entry) {
		delayedEntries.add(entry);
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase != TickEvent.Phase.END || delayedEntries.isEmpty()) return;
		
		for(Iterator<CommandDelayCommand.DelayedCommandEntry> iter = delayedEntries.iterator(); iter.hasNext();) {
			CommandDelayCommand.DelayedCommandEntry entry = iter.next();
			if(entry.getServer() == null || entry.getTime() <= entry.getServer().getTickCounter()) {
				if(entry.getSender() != null && entry.getServer() != null && !entry.getCommand().isEmpty()) {
					try {
						int j = entry.getServer().getCommandManager().executeCommand(entry.getSender(), entry.getCommand());
						
						if(j < 1) {
							CommandExtras.LOGGER.log(Level.WARN, "Delayed Command Failed Execution of: " + entry.getCommand());
						}
					}
					catch(Exception ex) {
						CommandExtras.LOGGER.log(Level.WARN, "Delayed Command Failed Execution of: " + entry.getCommand());
					}
				}
				iter.remove();
			}
		}
	}
}