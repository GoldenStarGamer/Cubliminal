package net.limit.cubliminal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "cubliminal")
public class CubliminalConfig implements ConfigData {
	public boolean disableAggressiveGraphics = false;

	private static List<String> trimList(List<String> inputList) {
		List<String> trimmedList = new ArrayList<>();
		int limit = Math.min(6, inputList.size());

		for (int i = 0; i < limit; i++) {
			trimmedList.add(inputList.get(i));
		}

		return trimmedList;
	}

	public static CubliminalConfig get() {
        return AutoConfig.getConfigHolder(CubliminalConfig.class).getConfig();
	}
}
