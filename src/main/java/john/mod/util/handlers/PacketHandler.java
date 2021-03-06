package john.mod.util.handlers;

import john.mod.packets.PacketMaskUpdate;
import john.mod.packets.PacketMaskUse;
import john.mod.packets.PacketSendKey;
import john.mod.packets.PacketUpdateElement;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	private static int packetId = 0;

	public static SimpleNetworkWrapper INSTANCE = null;

	public PacketHandler()
	{
	}

	public static int nextID()
	{
		return packetId++;
	}

	public static void registerMessages(String channelName)
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}

	public static void registerMessages()
	{
		// Register messages which are sent from the client to the server here:
		INSTANCE.registerMessage(PacketSendKey.Handler.class, PacketSendKey.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketUpdateElement.Handler.class, PacketUpdateElement.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketMaskUse.Handler.class, PacketMaskUse.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketMaskUpdate.Handler.class, PacketMaskUpdate.class, nextID(), Side.CLIENT);
	}
}