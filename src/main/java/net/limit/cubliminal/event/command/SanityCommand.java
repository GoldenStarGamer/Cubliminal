package net.limit.cubliminal.event.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.limit.cubliminal.util.IEntityDataSaver;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SanityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("sanity").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 10))
                                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "amount")))))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {

        if (source.getPlayer() == null) return 0;
        StringBuilder message = new StringBuilder();
        for (ServerPlayerEntity entity : targets) {
            if (entity.getWorld().isClient()) return 0;
            message.append(entity.getNameForScoreboard()).append(": ")
                    .append(IEntityDataSaver.cast(entity).getInt("sanity")).append("\n");
        }
        source.getPlayer().sendMessage(Text.literal(message.toString()));

        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.sanity.get.success.single", targets.iterator().next().getDisplayName()), false);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.sanity.get.success.multiple", targets.size()), false);
        }

        return targets.size();
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int amount) {
        if (amount < 0 || amount > 10) {
            source.sendError(Text.translatable("commands.sanity.failed.invalid_range"));
            return 0;
        }

        for (ServerPlayerEntity entity : targets) {
            NbtCompound nbt = IEntityDataSaver.cast(entity);
            nbt.putInt("sanity", amount);
        }

        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.sanity.success.single", targets.iterator().next().getDisplayName()), false);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.sanity.success.multiple", targets.size()), false);
        }

        return targets.size();
    }
}
