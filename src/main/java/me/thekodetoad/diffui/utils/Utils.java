package me.thekodetoad.diffui.utils;

import java.io.File;

public class Utils {
	
	public static OperatingSystem getOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("nix") || osName.contains("nux") || osName.contains("aix") || osName.contains("sunos")) {
			return OperatingSystem.LINUXLIKE;
		}
		else if(osName.contains("win")) {
			return OperatingSystem.WINDOWS;
		}
		else if(osName.contains("mac")) {
			return OperatingSystem.MAC;
		}
		return OperatingSystem.OTHER;
	}
	
	public static File getHomeDirectory() {
		return new File(System.getProperty("user.home"));
	}
	
}
