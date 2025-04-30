package net.limit.cubliminal.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.limit.cubliminal.Cubliminal;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JsonUtil {
    // Taken from: https://stackoverflow.com/questions/72750454/read-a-bunch-of-json-file-from-a-folder-in-java

    /**
     * Deserializes JSON files stored inside a directory from the "data" folder of this mod.
     *
     * @param ops      The ops to use instead of the default JSON ops
     * @param codec    The codec to use to deserialize the JSON
     * @param directory The directory from the "data" folder
     * @param <T>      The type of data to deserialize
     * @return A list with the deserialized JSON objects
     */
    public static <T> List<T> deserializeDataJsonArray(DynamicOps<JsonElement> ops, Codec<T> codec, Identifier directory) {
        List<T> results = new ArrayList<>();
        URL url = JsonUtil.class.getClassLoader().getResource("data/" + directory.getNamespace() + '/' + directory.getPath());
        try {
            Path dir = Paths.get(url.toURI());
            try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(dir, "*.json")) {
                for (Path file : fileStream) {
                    try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
                        DataResult<Pair<T, JsonElement>> decodeResult = codec.decode(ops, JsonHelper.deserialize(reader));

                        Optional<Pair<T, JsonElement>> result = decodeResult.result();
                        if (result.isPresent()) {
                            results.add(result.get().getFirst());
                        } else {
                            throw new IOException(decodeResult.error().get().message());
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            Cubliminal.LOGGER.error("Couldn't parse json in data folder {}", directory);
            throw new RuntimeException(e);
        }
        return results;
    }
}
