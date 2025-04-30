package net.limit.cubliminal.event.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.limit.cubliminal.event.noclip.NoClipEngine;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Collection;

public class NoClipCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("noclip").requires(source -> source.hasPermissionLevel(2))
                .executes(context -> execute(context.getSource()))
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                        .then(CommandManager.literal("set").then(CommandManager.argument("ticks", IntegerArgumentType.integer(1))
                                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "ticks")))))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"),
                                        DimensionArgumentType.getDimensionArgument(context, "dimension"))))));
    }

    private static int execute(ServerCommandSource source) {
        if (source.getWorld().isClient()) return 0;
        NoClipEngine.noClip(source.getPlayer());

        source.sendFeedback(() -> Text.translatable("commands.noclip.success.single", source.getDisplayName()), true);

        return 1;
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {

        for (ServerPlayerEntity entity : targets) {
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

        for (ServerPlayerEntity entity : targets) {
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

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int ticks) {
        if (ticks < 1) {
            source.sendError(Text.translatable("commands.noclip.failed.invalid_range"));
            return 0;
        }

        for (ServerPlayerEntity entity : targets) {
            if (entity.getWorld().isClient()) return 0;
            NoClipEngine.setTimer(entity, ticks);
        }

        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.noclip.set.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.noclip.set.success.multiple", targets.size()), true);
        }

        return targets.size();
    }
}
