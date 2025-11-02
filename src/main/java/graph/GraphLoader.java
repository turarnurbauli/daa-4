package graph;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads graph data from JSON files.
 */
public class GraphLoader {
    private static final Gson gson = new Gson();

    /**
     * Loads a graph from a JSON file.
     * 
     * @param filename path to the JSON file
     * @return loaded Graph object
     * @throws IOException if file cannot be read
     */
    public static Graph loadFromJson(String filename) throws IOException {
        JsonObject json = gson.fromJson(new FileReader(filename), JsonObject.class);
        
        int n = json.get("n").getAsInt();
        Graph graph = new Graph(n);
        
        JsonArray edges = json.get("edges").getAsJsonArray();
        for (JsonElement edgeElem : edges) {
            JsonObject edge = edgeElem.getAsJsonObject();
            int u = edge.get("u").getAsInt();
            int v = edge.get("v").getAsInt();
            int w = edge.get("w").getAsInt();
            graph.addEdge(u, v, w);
        }
        
        return graph;
    }

    /**
     * Gets the source vertex from JSON file.
     */
    public static int getSourceFromJson(String filename) throws IOException {
        JsonObject json = gson.fromJson(new FileReader(filename), JsonObject.class);
        return json.get("source").getAsInt();
    }
}
