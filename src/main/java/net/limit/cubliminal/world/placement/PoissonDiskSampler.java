package net.limit.cubliminal.world.placement;

import io.github.jdiemke.triangulation.Vector2D;
import net.limit.cubliminal.util.BooleanPair;
import net.limit.cubliminal.world.room.Room;
import net.limit.cubliminal.world.room.RoomRegistry;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record PoissonDiskSampler(int width, int height, int maxSamples, Predicate<RegistryEntry<Biome>> filter) {

    public List<Vec2i> generate(List<Room.Instance> instances, boolean[][] grid, RegistryEntry<Biome>[][] biomeGrid, Random random) {
        boolean[][] activeCache = new boolean[width][height];
        List<Vec2i> points = new ArrayList<>(width * height);
        List<Vec2i> active = new ArrayList<>(width * height);
        // Begin at the approximate center of the grid
        active.add(new Vec2i(width / 2, height / 2));

        label0:
        while (!active.isEmpty()) {
            int activeIndex = random.nextInt(active.size());
            Vec2i activeCenter = active.get(activeIndex);
            // Get biome at active center
            RegistryKey<Biome> biome = biomeGrid[activeCenter.getX()][activeCenter.getY()].getKey().orElseThrow();
            boolean hasMapping = RoomRegistry.contains(biome);
            // Use a default room in case the biome doesn't have a mapping
            Room.Instance newInstance = (hasMapping ? RoomRegistry.forBiome(biome, random) : Room.DEFAULT).newInstance(random);
            byte roomWidth = newInstance.height();
            byte roomHeight = newInstance.width();
            double minSeparation = Math.max(roomWidth, roomHeight);

            for (int i = 0; i < 20; i++) {
                float angle = (float) (random.nextFloat() * Math.PI * 2);
                Vector2D dir = new Vector2D((float) Math.sin(angle), (float) Math.cos(angle));
                // Max separation is twice the minimum
                dir = dir.mult((1 + random.nextFloat()) * minSeparation);
                Vec2i newCenter = activeCenter.add(separate(dir.x), separate(dir.y));
                // Continue if the new room doesn't collide with another
                BooleanPair conditions = this.isValid(newCenter, roomWidth, roomHeight, activeCache, biomeGrid, hasMapping);
                if (conditions.first()) {
                    // Add it to the generation list if it had a mapping
                    if (hasMapping && conditions.second()) {
                        instances.add(newInstance);
                        points.add(newCenter);
                        this.placeRoom(newCenter, roomWidth, roomHeight, grid);
                    }
                    // Save it for later use to fill gaps
                    active.add(newCenter);
                    this.placeRoom(newCenter, roomWidth, roomHeight, activeCache);
                    continue label0;
                }
            }

            active.remove(activeIndex);
        }
        /*
        StringBuilder sb0 = new StringBuilder();
        for (int y = 0; y < biomeGrid[0].length; y++) {
            for (int x = 0; x < biomeGrid.length; x++) {
                sb0.append(biomeGrid[x][y].getKey().orElseThrow() == CubliminalBiomes.DEEP_AQUILA_SECTOR_BIOME ? "O  " : "·  ");
            }
            sb0.append('\n'); // new row
        }
        Cubliminal.LOGGER.info('\n' + "{}", sb0);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                sb.append(grid[x][y] ? "#  " : "·  ");
            }
            sb.append('\n'); // new row
        }
        Cubliminal.LOGGER.info('\n' + "{}", sb);
         */

        return points;
    }

    private int separate(double d) {
        return (int) (d >= 0 ? Math.ceil(d) : Math.floor(d));
    }

    private BooleanPair isValid(Vec2i candidate, byte roomWidth, byte roomHeight, boolean[][] grid, RegistryEntry<Biome>[][] biomeGrid, boolean hasMapping) {
        int startX = candidate.getX() - 1;
        int endX = candidate.getX() + roomWidth + 1;
        int startY = candidate.getY() - 1;
        int endY = candidate.getY() + roomHeight + 1;

        if (startX >= 0 && endX < this.width && startY >= 0 && endY < this.height) {
            boolean correctBiome = !hasMapping || this.correctBiomeStrict(biomeGrid, startX, endX, startY, endY);

            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    if (grid[x][y]) {
                        return BooleanPair.of(false, correctBiome);
                    }
                }
            }

            return BooleanPair.of(true, correctBiome);
        }

        return BooleanPair.of(false, false);
    }

    private boolean correctBiomeStrict(RegistryEntry<Biome>[][] biomeGrid, int startX, int endX, int startY, int endY) {
        return this.filter.test(biomeGrid[(startX + endX) / 2][(startY + endY) / 2]) &&
                this.filter.test(biomeGrid[startX][startY]) &&
                this.filter.test(biomeGrid[startX][endY]) &&
                this.filter.test(biomeGrid[endX][startY]) &&
                this.filter.test(biomeGrid[endX][endY]);
    }

    private boolean correctBiome(RegistryEntry<Biome>[][] biomeGrid, int startX, int endX, int startY, int endY) {
        return this.filter.test(biomeGrid[(startX + endX) / 2][(startY + endY) / 2]);
    }

    private void placeRoom(Vec2i candidate, byte roomWidth, byte roomHeight, boolean[][] grid) {
        for (int row = 0; row < roomWidth; row++) {
            for (int column = 0; column < roomHeight; column++) {
                grid[row + candidate.getX()][column + candidate.getY()] = true;
            }
        }
    }

}