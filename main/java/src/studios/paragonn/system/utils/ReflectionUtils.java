package studios.paragonn.system.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import studios.paragonn.system.Main;

/**
 * @author Mior
 * @version 1.0
 * @category utils
 */

public class ReflectionUtils {
	
	private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private static Method getHandle;
	private static Method sendPacket;
	private static Field playerConnectionField;
	
	public static void loadUtils() {
		try 
		{
			getHandle = getOBClass("entity.CraftPlayer").getMethod("getHandle");
			playerConnectionField = getNMSClass("EntityPlayer").getField("playerConnection");
			sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
		}
		catch (Throwable e) {}
	}
	
   	public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
   		return Class.forName("net.minecraft.server." + version + "." + name);
   	}
   	
   	public static Class<?> getOBClass(String name) throws ClassNotFoundException {
   		return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
   	}
   	
   	public static void sendPacket(Player player, Object packet) {
   		try {
   			
   			if (Main.getVersion().value < 17) {   				
   				Object entityPlayer = getHandle.invoke(player);
   				Object playerConnection = playerConnectionField.get(entityPlayer);
   				sendPacket.invoke(playerConnection, packet);
   			} else {
   	            Object handle = player.getClass().getMethod("getHandle").invoke(player);
   	            Object playerConnection = handle.getClass().getField("b").get(handle);
   	            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
   	            playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
   			}
   			
   		} catch (Throwable e) {
   			e.printStackTrace();
   		}
   	}

	public static void freezeEntity(org.bukkit.entity.Entity entity) {
		if (entity == null) return;
		
		try {
			entity.getClass().getMethod("setAI", boolean.class).invoke(entity, false);
			return;
		} catch (Throwable ignored) {}
		
		try {
			Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
			Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
			Object tag = nbtTagCompoundClass.newInstance();
			
			java.lang.reflect.Method readNbt = nmsEntity.getClass().getMethod("c", nbtTagCompoundClass);
			readNbt.invoke(nmsEntity, tag);
			
			java.lang.reflect.Method setByte = nbtTagCompoundClass.getMethod("setByte", String.class, byte.class);
			setByte.invoke(tag, "NoAI", (byte) 1);
			
			java.lang.reflect.Method writeNbt = nmsEntity.getClass().getMethod("f", nbtTagCompoundClass);
			writeNbt.invoke(nmsEntity, tag);
		} catch (Throwable t) {
			try {
				Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
				Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
				Object tag = nbtTagCompoundClass.newInstance();
				
				java.lang.reflect.Method readNbt = nmsEntity.getClass().getMethod("e", nbtTagCompoundClass);
				readNbt.invoke(nmsEntity, tag);
				
				java.lang.reflect.Method setByte = nbtTagCompoundClass.getMethod("setByte", String.class, byte.class);
				setByte.invoke(tag, "NoAI", (byte) 1);
				
				java.lang.reflect.Method writeNbt = nmsEntity.getClass().getMethod("a", nbtTagCompoundClass);
				writeNbt.invoke(nmsEntity, tag);
			} catch (Throwable ignored) {}
		}
	}
   	
	public static List<Class<?>> getProjectClasses(String pckg) {
		try {
			List<Class<?>> classes = new ArrayList<>();
			File directory = new File(Thread.currentThread().getContextClassLoader().getResource(pckg).getFile());
			for (File file : directory.listFiles()) {
				if (file.getName().endsWith(".class")) {
					classes.add(Class.forName(pckg.replace('/', '.') + '.' + file.getName().replace(".class", "")));
				} else {
					if (file.isDirectory()) {
						classes.addAll(getProjectClasses(pckg + "/" + file.getName()));
					}
				}
			}
			return classes;
		} catch (Throwable e) {
			return null;
		}
	}
   	
}