import java.io.*;
import java.util.*;

public class GraphTraversal {
    static class Node {
        int id;
        String name;
        List<Node> children = new ArrayList<>();
        int pendingParents = 0;

        Node(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Map<Integer, Node> graph = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        int n = Integer.parseInt(br.readLine().trim());
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().trim().split(":");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            graph.put(id, new Node(id, name));
        }

        int m = Integer.parseInt(br.readLine().trim());
        for (int i = 0; i < m; i++) {
            String[] parts = br.readLine().trim().split(":");
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);
            Node parent = graph.get(from);
            Node child = graph.get(to);
            parent.children.add(child);
            child.pendingParents++;
        }

        Queue<Node> queue = new LinkedList<>();
        for (Node node : graph.values()) {
            if (node.pendingParents == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (visited.contains(current.id)) continue;

            visited.add(current.id);
            System.out.println(current.name);

            for (Node child : current.children) {
                child.pendingParents--;
                if (child.pendingParents == 0) {
                    queue.add(child);
                }
            }
        }

        System.out.println(n);
    }
}
