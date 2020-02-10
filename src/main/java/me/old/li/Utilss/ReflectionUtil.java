package me.old.li.Utilss;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.old.li.Main;

public class ReflectionUtil {

	public static Class<?> getOBCClass(String path) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + Main.nmsver + "." + path);
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "failed to get \'" + path + "\' OBCClass", t);
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String string, Class<?> argClazz) {
		try {
			return clazz.getDeclaredMethod(string, argClazz);
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "failed to get \'" + string + "\' method", t);
			return null;
		}
	}

	public static Class<?> getNMSClass(String string) {
		try {
			return Class.forName("net.minecraft.server." + Main.nmsver + "." + string);
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "failed to get \'" + string + "\' NMSClass", t);
			return null;
		}
	}

}
