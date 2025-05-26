package net.limit.cubliminal.world.placement;

import io.github.jdiemke.triangulation.Vector2D;
import net.limit.cubliminal.init.CubliminalBiomes;
import net.limit.cubliminal.world.room.Room;
import net.limit.cubliminal.world.room.RoomRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class PoissonDiskSampler {

    private final float radius;
    private final float cellSize;
    private final int width;
    private final int height;
    private final int maxSamples;

    public PoissonDiskSampler(float radius, int width, int height, int maxSamples) {
        this.radius = radius;
        this.cellSize = (float) (radius/Math.sqrt(2));
        this.width = width;
        this.height = height;
        this.maxSamples = maxSamples;
    }

    public List<Vec2f> generateF(Random random) {
        int[][] grid = new int[(int) Math.ceil(height/cellSize)][(int) Math.ceil(width/cellSize)];
        List<Vec2f> points = new ArrayList<>(grid.length * grid[0].length);
        List<Vec2f> spawnPoints = new ArrayList<>(grid.length * grid[0].length);

        spawnPoints.add(new Vec2f((float) height / 2, (float) width / 2));
        label0:
        while (!spawnPoints.isEmpty()) {
            int spawnIndex = random.nextInt(spawnPoints.size());
            Vec2f spawnCenter = spawnPoints.get(spawnIndex);

            for (int i = 0; i < maxSamples; i++) {
                float angle = (float) (random.nextFloat() * Math.PI * 2);
                Vec2f dir = new Vec2f((float) Math.sin(angle), (float) Math.cos(angle));
                Vec2f candidate = spawnCenter.add(dir.multiply((1 + random.nextFloat()) * radius));
                if (this.isValid(candidate, cellSize, radius, points, grid)) {
                    points.add(candidate);
                    spawnPoints.add(candidate);
                    grid[(int)(candidate.x/cellSize)][(int)(candidate.y/cellSize)] = points.size();
                    continue label0;
                }
            }

            spawnPoints.remove(spawnIndex);
        }

        return points;
    }

    public List<Vector2D> oldGenerate(List<Room.Instance> placedRooms, Random random) {

        boolean[][] grid = new boolean[height][width];
        List<Vector2D> points = new ArrayList<>(height * width);
        List<Vector2D> active = new ArrayList<>(height * width);
        /*
        active.add(new Vector2D(height / 2, width / 2));

        label0:
        while (!active.isEmpty()) {
            int activeIndex = random.nextInt(active.size());
            Vector2D activeCenter = active.get(activeIndex);
            RoomVariant variant = availableRooms.get(random.nextInt(availableRooms.size())).randomInstance(random);
            byte roomWidth = variant.width();
            byte roomHeight = variant.height();
            double minSeparation = Math.max(roomHeight, roomWidth) + 1;
            for (int i = 0; i < this.maxSamples; i++) {
                float angle = (float) (random.nextFloat() * Math.PI * 2);
                Vector2D dir = new Vector2D((float) Math.sin(angle), (float) Math.cos(angle));
                dir = dir.mult((1 + random.nextFloat()) * minSeparation);
                Vector2D newCenter = activeCenter.add(new Vector2D(separate(dir.x), separate(dir.y)));
                if (this.isValid(newCenter, roomWidth, roomHeight, grid)) {
                    placedRooms.add(variant.withPosIndex(points.size()));
                    points.add(newCenter);
                    active.add(newCenter);
                    this.placeRoom(newCenter, roomWidth, roomHeight, grid);
                    continue label0;
                }
            }

            active.remove(activeIndex);
        }


         */
        return points;
    }

    public List<Vector2D> generate(List<Room.Instance> instances, RegistryKey<Biome>[][] biomeGrid, Random random) {
        boolean[][] grid = new boolean[height][width];
        List<Vector2D> points = new ArrayList<>(height * width);
        List<Vector2D> active = new ArrayList<>(height * width);
        active.add(new Vector2D(height / 2, width / 2));

        label0:
        while (!active.isEmpty()) {
            int activeIndex = random.nextInt(active.size());
            Vector2D activeCenter = active.get(activeIndex);
            RegistryKey<Biome> biome = //biomeGrid[(int) activeCenter.x][(int) activeCenter.y];
                    CubliminalBiomes.AQUILA_SECTOR_BIOME;
            Room.Instance newInstance = RoomRegistry.forBiome(biome, random).newInstance(random);
            byte roomWidth = newInstance.width();
            byte roomHeight = newInstance.height();
            double minSeparation = Math.max(roomHeight, roomWidth) + 1;
            for (int i = 0; i < this.maxSamples; i++) {
                float angle = (float) (random.nextFloat() * Math.PI * 2);
                Vector2D dir = new Vector2D((float) Math.sin(angle), (float) Math.cos(angle));
                dir = dir.mult((1 + random.nextFloat()) * minSeparation);
                Vector2D newCenter = activeCenter.add(new Vector2D(separate(dir.x), separate(dir.y)));
                if (this.isValid(newCenter, roomWidth, roomHeight, grid)) {
                    instances.add(newInstance);
                    points.add(newCenter);
                    active.add(newCenter);
                    this.placeRoom(newCenter, roomWidth, roomHeight, grid);
                    continue label0;
                }
            }

            active.remove(activeIndex);
        }

        return points;
    }

    private int separate(double d) {
        return (int) (d >= 0 ? Math.ceil(d) : Math.floor(d));
    }

    private boolean isValid(Vector2D candidate, byte roomWidth, byte roomHeight, boolean[][] grid) {
        int startX = (int) (candidate.x - 1);
        int endX = (int) (candidate.x + roomHeight + 1);
        int startY = (int) (candidate.y - 1);
        int endY = (int) (candidate.y + roomWidth + 1);

        if (startX >= 0 && endX < this.height && startY >= 0 && endY < this.width) {
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    if (grid[x][y]) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    private void placeRoom(Vector2D candidate, byte roomWidth, byte roomHeight, boolean[][] grid) {
        for (int row = 0; row < roomHeight; row++) {
            for (int column = 0; column < roomWidth; column++) {
                grid[(int) (row + candidate.x)][(int) (column + candidate.y)] = true;
            }
        }
    }

    private boolean isValid(Vec2f candidate, float cellSize, float radius, List<Vec2f> points, int[][] grid) {
        if (candidate.x >= 0 && candidate.x < height && candidate.y >= 0 && candidate.y < width) {
            int cellX = (int) (candidate.x/cellSize);
            int cellY = (int) (candidate.y/cellSize);
            int startX = Math.max(0, cellX - 2);
            int endX = Math.min(cellX + 2, grid.length - 1);
            int startY = Math.max(0, cellY - 2);
            int endY = Math.min(cellY + 2, grid[0].length - 1);

            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    int pointIndex = grid[x][y] - 1;
                    if (pointIndex != -1) {
                        double sqrDst = candidate.distanceSquared(points.get(pointIndex));
                        if (sqrDst < radius * radius) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }
}
