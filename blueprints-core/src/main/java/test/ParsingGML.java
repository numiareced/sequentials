package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.gml.GMLReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Item;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Sequence;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.SequenceStatsGenerator;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.Predictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.pfv.spmf.test.MainTestPPM;

public class ParsingGML {
	
	public static List<Sequence> sequences = new ArrayList<Sequence>();
	static String sampleFileName = "C:/Users/alice/workspace/blueprints-master/blueprints-core/src/graph.gml";
	static List<Sequence> testData = new ArrayList<Sequence>();
	static List<Sequence> learningData = new ArrayList<Sequence>();
	static String russianAlphabet = "C:/Users/alice/workspace/blueprints-master/blueprints-core/src/russian.txt";
    public static void main(String[] args) throws Exception {
    	
    	TinkerGraph graph = new TinkerGraph();
        ParseRussian.readAlphabet(russianAlphabet);
        GMLReader.inputGraph(graph, sampleFileName);
        traverse(graph);
        System.out.println("done traversing with seq size= " + sequences.size());
        try{
    	    PrintWriter writer = new PrintWriter("sequences.txt", "UTF-8");
    	    for(Sequence sequence : sequences) {
    			writer.println(sequence.toString());
    	   }
    	    writer.close();
    	} catch (IOException e) {
    	   // do something
    	}
        splitData(sequences, 0.8);
        MarkovFirstOrderPredictor predictionModel = new MarkovFirstOrderPredictor("PPM");
		predictionModel.Train(learningData);    
		System.out.println("done training");
		testAlgorithm(predictionModel);
		
        
/*        System.out.println("Vertices of " + graph);
        for (Vertex vertex : graph.getVertices()) {
          
          String intent = vertex.getProperty("intent");
          System.out.println(intent);
        }*/
     /*   System.out.println("Edges of " + graph);
        for (Edge edge : graph.getEdges()) {
          System.out.println(edge);
         }*/
        
        
/*       for (Vertex vertex : graph.getVertices()) {
            System.out.println(vertex);
            for(Edge e : vertex.getEdges(Direction.OUT)) {
                System.out.println(e);
                String intent = e.getVertex(Direction.OUT).getProperty("intent");
                System.out.println("intent:" + intent);
              }
          }*/
        //PPMAlgr();

      /*traverse(graph);
      System.out.println("done traversing");
    */
//     
//       MarkovFirstOrderPredictor predictionModel = new MarkovFirstOrderPredictor("PPM");
//		predictionModel.Train(sequences);    			
//		Sequence sequence = new Sequence(0);
//		sequence.addItem(new Item(7));
//		sequence.addItem(new Item(7));
//		sequence.addItem(new Item(12));
//		// Then we perform the prediction
//		Sequence thePrediction = predictionModel.Predict(sequence);
//		System.out.println("For the sequence <(3),(14), (14)>, the prediction for the next symbol is: +" + thePrediction);
       // System.out.println("vertex " + a.getId() + " has name " + a.getProperty("name"));
    }
    
    static void traverse(TinkerGraph graph){
    	 for (Vertex vertex : graph.getVertices()) {
    		 String intent = vertex.getProperty("intent");
    		// System.out.println("vertex:" + vertex + " intent:" + intent);
    	    	Sequence seq = new Sequence(0);
             travel(vertex, seq);
             sequences.add(seq);
    	 }
    	 
    }
    
    static void testAlgorithm(Predictor predictionModel){
    	int rand = new Random().nextInt(testData.size()); 
    	Sequence testSeq = testData.get(rand);
    	ArrayList<Item> testItems =(ArrayList<Item>)testSeq.getItems();
		double precision; 
		int hit = 0; 
		int overall = 0; 
		Sequence thePrediction = predictionModel.Predict(testSeq);
		System.out.println("For the sequence "  + testData.get(rand)  + " the prediction for the next symbol is: " + thePrediction);
		
		for (Sequence seq : testData){
			if (findSubsequence(seq.getItems(), testSeq.getItems())){
				overall ++; 
				for (Item it : thePrediction.getItems()){
					testSeq.addItem(it);
				}
				if (findSubsequence(seq.getItems(),  testSeq.getItems())){
					hit++;
				}
			}
		}
		System.out.println("found subsequences:"  + overall  + " and hited: " + hit);
		
		
		
/*		for (Sequence seq : testData) {
			ArrayList<Item> currentItems = (ArrayList<Item>) seq.getItems();
			boolean hited = false; 
			for (Item testItem : testItems) {
				for (Item curItem : currentItems) {
					if (!curItem.equals(testItem))
						break;
				}
				hited = true; 
				hit ++; 
			}
		}
		System.out.println("hited:" + hit);*/

	}
    
    static boolean findSubsequence(List<Item> input, List<Item> seq){
    	for (Item item : input){
    		for (Item it2: seq){
    			if (!item.equals(it2)){
    				return false; 
    			}
    		}
    	}
    	return true; 
    }
    
    static void travel(Vertex vertex, Sequence seq){
    	String intentStr = vertex.getProperty("intent");

    	if(intentStr.length() > 0){
    	char intentChar = intentStr.charAt(0);
    	int value =ParseRussian.getIntValue(intentChar);
    	
    	seq.addItem(new Item((value)));
    	}
    	 for(Edge e : vertex.getEdges(Direction.OUT)) {
             //System.out.println(e);
             Vertex child = e.getVertex(Direction.IN);
             if (child == null){
            	 System.out.println("end");
            	 return;
             }
             else {
        		 String intent = child.getProperty("intent");
                 //System.out.println("child:" + child + " intent:" + intent);
            	 travel(child, seq);
             }
         }
    }
    
/*    static void PPMAlgr() throws IOException{
    	// Load the set of training sequences
    			String inputPath = fileToPath("BIBLE.txt");  
    			SequenceDatabase trainingSet = new SequenceDatabase();
    			trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    			
    			// Print the training sequences to the console
    			System.out.println("--- Training sequences ---");
    			for(Sequence sequence : trainingSet.getSequences()) {
    				System.out.println(sequence.toString());
    			}
    			System.out.println();
    			
    			// Print statistics about the training sequences
    			SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");
    			
    			// Train the prediction model
    			MarkovFirstOrderPredictor predictionModel = new MarkovFirstOrderPredictor("PPM");
    			predictionModel.Train(trainingSet.getSequences());
    			
    			// Now we will make a prediction.
    			// We want to predict what would occur after the sequence <1, 3>.
    			// We first create the sequence
    			Sequence sequence = new Sequence(0);
    			sequence.addItem(new Item(160));
    			sequence.addItem(new Item(662));
    			sequence.addItem(new Item(663));
    			// Then we perform the prediction
    			Sequence thePrediction = predictionModel.Predict(sequence);
    			System.out.println("For the sequence <(1),(4)>, the prediction for the next symbol is: +" + thePrediction);
    }*/
    
	static void splitData(List<Sequence> sequences, double range) {
		Random rand = new Random();
		for (Sequence seq : sequences) {
			double i = rand.nextDouble();
			if (i < range) {
				learningData.add(seq);
			} else {
				testData.add(seq);
			}
		}
		
		System.out.println("done splitting data. Learning = " + learningData.size() + " test = " + testData.size());
	}
    
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestPPM.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

}
