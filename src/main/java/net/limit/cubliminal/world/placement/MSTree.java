package net.limit.cubliminal.world.placement;

import com.google.common.collect.SetMultimap;
import io.github.jdiemke.triangulation.Edge2D;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import net.limit.cubliminal.world.room.Room.DoorInstance;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class MSTree {

    public static List<Edge2D> buildCorridors(Collection<Vector2D> nodes, SetMultimap<Vec2i, DoorInstance> doors, List<Triangle2D> triangleSoup, Random random) {
        // Build unique edges
        List<Edge2D> edges = new ArrayList<>();
        for (Triangle2D triangle : triangleSoup) {
            Vector2D a = triangle.a;
            Vector2D b = triangle.b;
            Vector2D c = triangle.c;
            addEdge(a, b, nodes, edges, doors, random);
            addEdge(b, c, nodes, edges, doors, random);
            addEdge(c, a, nodes, edges, doors, random);
        }
        // Sort edges by length
        edges.sort(Comparator.comparingDouble(MSTree::sqEdgeLength));

        List<Edge2D> mst = new ArrayList<>(nodes.size() - 1);
        UnionFind uf = new UnionFind(nodes);
        // Iterate from the shortest to the longest edges
        for (Edge2D edge : edges) {
            Vector2D u = edge.a;
            Vector2D v = edge.b;
            // Connect both sets if their root is different to form no loops
            if (!uf.find(u).equals(uf.find(v))) {
                mst.add(edge);
                uf.union(u, v);
            }
        }

        return mst;
    }

    public static void addEdge(Vector2D a, Vector2D b, Collection<Vector2D> nodes, List<Edge2D> edges, SetMultimap<Vec2i, DoorInstance> doors, Random random) {
        DoorInstance doorA = doors.get(new Vec2i((int) a.x, (int) a.y)).stream().toList().getFirst();
        DoorInstance doorB = doors.get(new Vec2i((int) b.x, (int) b.y)).stream().toList().getFirst();
        if (!doorA.roomPos().equals(doorB.roomPos())) {
            add(a, b, edges);
        } else {
            List<Vector2D> list = nodes.stream().toList();
            add(a, list.get(random.nextInt(list.size())), edges);
            add(b, list.get(random.nextInt(list.size())), edges);
        }
    }

    public static void add(Vector2D a, Vector2D b, List<Edge2D> edges) {
        if (!edges.contains(new Edge2D(a, b))) {
            edges.add(new Edge2D(a, b));
        } else if (!edges.contains(new Edge2D(b, a))) {
            edges.add(new Edge2D(b, a));
        }
    }

    public static double sqEdgeLength(Edge2D edge) {
        Vector2D dif = edge.a.sub(edge.b);
        return dif.x * dif.x + dif.y * dif.y;
    }

    private static class UnionFind {

        private final Map<Vector2D, Vector2D> parent;

        public UnionFind(Collection<Vector2D> nodes) {
            this.parent = new HashMap<>(nodes.size());
            for (Vector2D vec : nodes) {
                this.parent.put(vec, vec);
            }
        }

        public Vector2D find(Vector2D vec) {
            Vector2D root = this.parent.get(vec);
            if (!vec.equals(root)) {
                root = this.find(root);
                this.parent.put(vec, root);
            }
            return root;
        }

        public void union(Vector2D a, Vector2D b) {
            Vector2D rA = this.find(a);
            Vector2D rB = this.find(b);
            if (!rA.equals(rB)) {
                this.parent.put(rA, rB);
            }
        }
    }
}
