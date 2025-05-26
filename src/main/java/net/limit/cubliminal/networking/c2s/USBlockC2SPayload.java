package net.limit.cubliminal.networking.c2s;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.limit.cubliminal.Cubliminal;
import net.limit.cubliminal.block.entity.USBlockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.nio.charset.StandardCharsets;

public class USBlockC2SPayload implements CustomPayload {
    public static final Codec<USBlockC2SPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructureBlockContext.CODEC.fieldOf("structure_block_context").forGetter(USBlockC2SPayload::context)
    ).apply(instance, USBlockC2SPayload::new));

    private final StructureBlockContext context;

    public static Identifier USBLOCK_UPDATE = Cubliminal.id("usblock_update");

    public static final CustomPayload.Id<USBlockC2SPayload> ID = new CustomPayload.Id<>(USBLOCK_UPDATE);

    public static final PacketCodec<RegistryByteBuf, USBlockC2SPayload> PAYLOAD_CODEC = PacketCodecs.unlimitedRegistryCodec(CODEC);

    public USBlockC2SPayload(BlockPos pos, USBlockBlockEntity.Action action, StructureBlockMode mode, String templateName, BlockPos offset, Vec3i size, BlockMirror mirror, BlockRotation rotation, String metadata, boolean ignoreEntities, boolean showAir, boolean showBoundingBox, float integrity, long seed) {
        int i = 0;
        if (ignoreEntities) {
            i |= 1;
        }

        if (showAir) {
            i |= 2;
        }

        if (showBoundingBox) {
            i |= 4;
        }
        this.context = new StructureBlockContext(pos, action, mode, templateName, offset, size, mirror, rotation, metadata, i, integrity, seed);
    }

    public USBlockC2SPayload(StructureBlockContext context) {
        this.context = context;
    }

    public StructureBlockContext context() {
        return this.context;
    }

    public BlockPos getPos() {
        return context.pos();
    }

    public USBlockBlockEntity.Action getAction() {
        return context.action();
    }

    public StructureBlockMode getMode() {
        return context.mode();
    }

    public String getTemplateName() {
        return context.templateName();
    }

    public BlockPos getOffset() {
        return context.offset();
    }

    public Vec3i getSize() {
        return context.size();
    }

    public BlockMirror getMirror() {
        return context.mirror();
    }

    public BlockRotation getRotation() {
        return context.rotation();
    }

    public String getMetadata() {
        return context.metadata();
    }

    public boolean shouldIgnoreEntities() {
        return context.shouldIgnoreEntities();
    }

    public boolean shouldShowAir() {
        return context.shouldShowAir();
    }

    public boolean shouldShowBoundingBox() {
        return context.shouldShowBoundingBox();
    }

    public float getIntegrity() {
        return context.integrity();
    }

    public long getSeed() {
        return context.seed();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(USBlockC2SPayload payload, ServerPlayNetworking.Context context) {
        if (context.player().isCreativeLevelTwoOp()) {
            BlockPos blockPos = payload.getPos();
            BlockState blockState = context.player().getWorld().getBlockState(blockPos);
            BlockEntity blockEntity = context.player().getWorld().getBlockEntity(blockPos);
            if (blockEntity instanceof USBlockBlockEntity usBlockBlockEntity) {
                usBlockBlockEntity.setMode(payload.getMode());
                usBlockBlockEntity.setTemplateName(payload.getTemplateName());
                usBlockBlockEntity.setOffset(payload.getOffset());
                usBlockBlockEntity.setSize(payload.getSize());
                usBlockBlockEntity.setMirror(payload.getMirror());
                usBlockBlockEntity.setRotation(payload.getRotation());
                usBlockBlockEntity.setMetadata(payload.getMetadata());
                usBlockBlockEntity.setIgnoreEntities(payload.shouldIgnoreEntities());
                usBlockBlockEntity.setShowAir(payload.shouldShowAir());
                usBlockBlockEntity.setShowBoundingBox(payload.shouldShowBoundingBox());
                usBlockBlockEntity.setIntegrity(payload.getIntegrity());
                usBlockBlockEntity.setSeed(payload.getSeed());
                if (usBlockBlockEntity.hasStructureName()) {
                    String string = usBlockBlockEntity.getTemplateName();
                    if (payload.getAction() == USBlockBlockEntity.Action.SAVE_AREA) {
                        if (usBlockBlockEntity.saveStructure()) {
                            context.player().sendMessage(Text.translatable("structure_block.save_success", string), false);
                        } else {
                            context.player().sendMessage(Text.translatable("structure_block.save_failure", string), false);
                        }
                    } else if (payload.getAction() == USBlockBlockEntity.Action.LOAD_AREA) {
                        if (!usBlockBlockEntity.isStructureAvailable()) {
                            context.player().sendMessage(Text.translatable("structure_block.load_not_found", string), false);
                        } else if (usBlockBlockEntity.loadAndTryPlaceStructure(context.player().getServerWorld())) {
                            context.player().sendMessage(Text.translatable("structure_block.load_success", string), false);
                        } else {
                            context.player().sendMessage(Text.translatable("structure_block.load_prepare", string), false);
                        }
                    } else if (payload.getAction() == USBlockBlockEntity.Action.SCAN_AREA) {
                        if (usBlockBlockEntity.detectStructureSize()) {
                            context.player().sendMessage(Text.translatable("structure_block.size_success", string), false);
                        } else {
                            context.player().sendMessage(Text.translatable("structure_block.size_failure"), false);
                        }
                    }
                } else {
                    context.player().sendMessage(Text.translatable("structure_block.invalid_structure_name", payload.getTemplateName()), false);
                }

                usBlockBlockEntity.markDirty();
                context.player().getWorld().updateListeners(blockPos, blockState, blockState, 3);
            }
        }
    }

    public record StructureBlockContext(BlockPos pos, USBlockBlockEntity.Action action, StructureBlockMode mode, String templateName, BlockPos offset, Vec3i size, BlockMirror mirror, BlockRotation rotation, String metadata, int booleans, float integrity, long seed) {
        public static final Codec<StructureBlockContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(StructureBlockContext::pos),
                StringIdentifiable.BasicCodec.STRING.fieldOf("action").xmap(USBlockBlockEntity.Action::valueOf, USBlockBlockEntity.Action::toString).forGetter(StructureBlockContext::action),
                StringIdentifiable.BasicCodec.STRING.fieldOf("mode").xmap(StructureBlockMode::valueOf, StructureBlockMode::toString).forGetter(StructureBlockContext::mode),
                Codec.STRING.fieldOf("templateName").forGetter(StructureBlockContext::templateName),
                BlockPos.CODEC.fieldOf("offset").forGetter(StructureBlockContext::offset),
                Vec3i.CODEC.fieldOf("size").forGetter(StructureBlockContext::size),
                BlockMirror.CODEC.fieldOf("mirror").forGetter(StructureBlockContext::mirror),
                BlockRotation.CODEC.fieldOf("rotation").forGetter(StructureBlockContext::rotation),
                Codec.STRING.fieldOf("metadata").forGetter(StructureBlockContext::metadata),
                Codec.INT.fieldOf("booleans").forGetter(StructureBlockContext::booleans),
                Codec.FLOAT.fieldOf("integrity").forGetter(StructureBlockContext::integrity),
                Codec.LONG.fieldOf("seed").forGetter(StructureBlockContext::seed)
        ).apply(instance, StructureBlockContext::new));

        public StructureBlockContext(BlockPos pos, USBlockBlockEntity.Action action, StructureBlockMode mode, String templateName, BlockPos offset, Vec3i size, BlockMirror mirror, BlockRotation rotation, String metadata, int booleans, float integrity, long seed) {
            this.pos = pos;
            this.action = action;
            this.mode = mode;
            this.templateName = templateName;
            this.offset = new BlockPos(
                    MathHelper.clamp(offset.getX(), -USBlockBlockEntity.structureSizeLimit(), USBlockBlockEntity.structureSizeLimit()),
                    MathHelper.clamp(offset.getY(), -USBlockBlockEntity.structureSizeLimit(), USBlockBlockEntity.structureSizeLimit()),
                    MathHelper.clamp(offset.getZ(), -USBlockBlockEntity.structureSizeLimit(), USBlockBlockEntity.structureSizeLimit()));
            this.size = new Vec3i(
                    MathHelper.clamp(size.getX(), 0, USBlockBlockEntity.structureSizeLimit()),
                    MathHelper.clamp(size.getY(), 0, USBlockBlockEntity.structureSizeLimit()),
                    MathHelper.clamp(size.getZ(), 0, USBlockBlockEntity.structureSizeLimit()));
            this.mirror = mirror;
            this.rotation = rotation;
            this.metadata = decode(metadata, 128);
            this.booleans = booleans;
            this.integrity = MathHelper.clamp(integrity, 0.0F, 1.0F);
            this.seed = seed;
        }

        public static String decode(String packet, int maxLength) {
            int i = ByteBufUtil.utf8MaxBytes(maxLength);
            int j = packet.getBytes(StandardCharsets.UTF_8).length;
            if (j > i) {
                throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i + ")");
            } else {
                if (packet.length() > maxLength) {
                    int var10002 = packet.length();
                    throw new DecoderException("The received string length is longer than maximum allowed (" + var10002 + " > " + maxLength + ")");
                } else {
                    return packet;
                }
            }
        }

        public boolean shouldIgnoreEntities() {
            return (booleans & 1) != 0;
        }

        public boolean shouldShowAir() {
            return (booleans & 2) != 0;
        }

        public boolean shouldShowBoundingBox() {
            return (booleans & 4) != 0;
        }
    }
}
