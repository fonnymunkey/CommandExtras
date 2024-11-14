package commandextras;

import commandextras.command.CommandChainCommand;
import commandextras.command.CommandDelayCommand;
import commandextras.handler.DelayedCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CommandExtras.MODID, version = CommandExtras.VERSION, name = CommandExtras.NAME, acceptableRemoteVersions = "*")
public class CommandExtras {
	
    public static final String MODID = "commandextras";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "CommandExtras";
    public static final Logger LOGGER = LogManager.getLogger();
	
	@Instance(MODID)
	public static CommandExtras instance;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new DelayedCommandHandler());
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandChainCommand());
		event.registerServerCommand(new CommandDelayCommand());
	}
}