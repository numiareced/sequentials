package test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.gml.GMLReader;

public class ParsingGML {

    public static void main(String[] args) throws Exception {
    	TinkerGraph graph = new TinkerGraph();
    	String sampleFileName = "C:/Users/alice/workspace/gmlParse/graph.gml";
        GMLReader.inputGraph(graph, sampleFileName);
/*        System.out.println("Vertices of " + graph);
        for (Vertex vertex : graph.getVertices()) {
          System.out.println(vertex);
        }
        System.out.println("Edges of " + graph);
        for (Edge edge : graph.getEdges()) {
          System.out.println(edge);
         }
        */
        
        for (Vertex vertex : graph.getVertices()) {
            System.out.println(vertex);
            for(Edge e : vertex.getEdges(Direction.OUT)) {
                System.out.println(e);
                System.out.println(e.getPropertyKeys().toString());
              }
          }
        //System.out.println("vertex " + a.getId() + " has name " + a.getProperty("name"));
       

    }
    

}
