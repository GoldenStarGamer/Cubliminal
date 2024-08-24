package net.limit.cubliminal.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ParalyzingEntries {
    public static Set<Object> PARALYZING_ENTRIES = new HashSet<>();
}
