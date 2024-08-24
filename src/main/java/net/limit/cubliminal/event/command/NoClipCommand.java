package net.limit.cubliminal.event.command;

import com.mojang.brigadier.CommandDispatcher;
import net.limit.cubliminal.util.NoClipEngine;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;

public class NoClipCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("noclip").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"),
                                        DimensionArgumentType.getDimensionArgument(context, "dimension")))
                        ))
        );
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        Iterator<ServerPlayerEntity> var2 = targets.iterator();

        while (var2.hasNext()) {
            ServerPlayerEntity entity = var2.next();
            if (entity.getWorld().isClient()) return 0;
            NoClipEngine.noClip(entity);
        }

        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.noclip.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.noclip.success.multiple", targets.size()), true);
        }

        return targets.size();
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, ServerWorld world) {
        Iterator<ServerPlayerEntity> var2 = targets.iterator();

        while (var2.hasNext()) {
            ServerPlayerEntity entity = var2.next();
            if (entity.getWorld().isClient()) return 0;
            NoClipEngine.noClip(entity, world.getRegistryKey());
        }

        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.noclip.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.noclip.success.multiple", targets.size()), true);
        }

        return targets.size();
    }
}
