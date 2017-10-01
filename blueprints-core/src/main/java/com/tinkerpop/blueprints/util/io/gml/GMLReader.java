package com.tinkerpop.blueprints.util.io.gml;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.charset.Charset;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

/**
 * A reader for the Graph Modelling Language (GML).
 *
 * GML definition taken from
 * (http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-documentation.tar.gz)
 *
 * It's not clear that all node have to have id's or that they have to be integers - we assume that this is the case. We
 * also assume that only one graph can be defined in a file.
 *
 * @author Stuart Hendren (http://stuarthendren.net)
 * @author Stephen Mallette
 */
public class GMLReader {

    public static final String DEFAULT_LABEL = "undefined";
    
    public static final String DEFAULT_INTENT = "undefined";

    private static final int DEFAULT_BUFFER_SIZE = 1000;

    private Graph graph;

    private final String defaultEdgeLabel;
    
    private final String defaultIntentLabel;

    private String vertexIdKey;

    private String edgeIdKey;

    private String edgeLabelKey = GMLTokens.LABEL;
    
    private String intentLabelKey = GMLTokens.INTENT;

    /**
     * Create a new GML reader
     *
     * (Uses default edge label DEFAULT_LABEL)
     *
     * @param graph the graph to load data into
     */
    public GMLReader(Graph graph) {
        this(graph, DEFAULT_LABEL, DEFAULT_INTENT);
    }

    /**
     * Create a new GML reader
     *
     * @param graph            the graph to load data into
     * @param defaultEdgeLabel the default edge label to be used if the GML edge does not define a label
     */
    public GMLReader(Graph graph, String defaultEdgeLabel, String defaultIntentLabel) {
        this.graph = graph;
        this.defaultEdgeLabel = defaultEdgeLabel;
        this.defaultIntentLabel = defaultIntentLabel;
    }

    /**
     * @param vertexIdKey gml property to use as id for verticies
     */
    public void setVertexIdKey(String vertexIdKey) {
        this.vertexIdKey = vertexIdKey;
    }

    /**
     * @param edgeIdKey gml property to use as id for edges
     */
    public void setEdgeIdKey(String edgeIdKey) {
        this.edgeIdKey = edgeIdKey;
    }

    /**
     * @param edgeLabelKey gml property to assign edge Labels to
     */
    public void setEdgeLabelKey(String edgeLabelKey) {
        this.edgeLabelKey = edgeLabelKey;
    }

    /**
     * Read the GML from from the stream.
     *
     * If the file is malformed incomplete data can be loaded.
     *
     * @param inputStream
     * @throws IOException
     */
    public void inputGraph(InputStream inputStream) throws IOException {
        GMLReader.inputGraph(this.graph, inputStream, DEFAULT_BUFFER_SIZE, this.defaultEdgeLabel,
                this.vertexIdKey, this.edgeIdKey, this.edgeLabelKey, this.intentLabelKey, this.defaultIntentLabel);
    }

    /**
     * Read the GML from from the stream.
     *
     * If the file is malformed incomplete data can be loaded.
     *
     * @param filename
     * @throws IOException
     */
    public void inputGraph(String filename) throws IOException {
        GMLReader.inputGraph(this.graph, filename, DEFAULT_BUFFER_SIZE, this.defaultEdgeLabel,
                this.vertexIdKey, this.edgeIdKey, this.edgeLabelKey, this.intentLabelKey, this.defaultIntentLabel);
    }

    /**
     * Read the GML from from the stream.
     *
     * If the file is malformed incomplete data can be loaded.
     *
     * @param inputStream
     * @param bufferSize
     * @throws IOException
     */
    public void inputGraph(InputStream inputStream, int bufferSize) throws IOException {
        GMLReader.inputGraph(this.graph, inputStream, bufferSize, this.defaultEdgeLabel,
                this.vertexIdKey, this.edgeIdKey, this.edgeLabelKey, this.intentLabelKey, this.defaultIntentLabel);
    }

    /**
     * Read the GML from from the stream.
     *
     * If the file is malformed incomplete data can be loaded.
     *
     * @param filename
     * @param bufferSize
     * @throws IOException
     */
    public void inputGraph(String filename, int bufferSize) throws IOException {
        GMLReader.inputGraph(this.graph, filename, bufferSize, this.defaultEdgeLabel,
                this.vertexIdKey, this.edgeIdKey, this.edgeLabelKey, this.intentLabelKey, this.defaultIntentLabel);
    }

    /**
     * Load the GML file into the Graph.
     *
     * @param graph    to receive the data
     * @param filename GML file
     * @throws IOException thrown if the data is not valid
     */
    public static void inputGraph(Graph graph, String filename) throws IOException {
        inputGraph(graph, filename, DEFAULT_BUFFER_SIZE, DEFAULT_LABEL, GMLTokens.BLUEPRINTS_ID, GMLTokens.BLUEPRINTS_ID, null, null, DEFAULT_INTENT);
    }

    /**
     * Load the GML file into the Graph.
     *
     * @param graph       to receive the data
     * @param inputStream GML file
     * @throws IOException thrown if the data is not valid
     */
    public static void inputGraph(Graph graph, InputStream inputStream) throws IOException {
        inputGraph(graph, inputStream, DEFAULT_BUFFER_SIZE, DEFAULT_LABEL, GMLTokens.BLUEPRINTS_ID, GMLTokens.BLUEPRINTS_ID, null, DEFAULT_INTENT);
    }

    /**
     * Load the GML file into the Graph.
     *
     * @param inputGraph       to receive the data
     * @param filename         GML file
     * @param defaultEdgeLabel default edge label to be used if not defined in the data
     * @param vertexIdKey      if the id of a vertex is a &lt;data/&gt; property, fetch it from the data property.
     * @param edgeIdKey        if the id of an edge is a &lt;data/&gt; property, fetch it from the data property.
     * @param edgeLabelKey     if the label of an edge is a &lt;data/&gt; property, fetch it from the data property.
     * @throws IOException thrown if the data is not valid
     */
    public static void inputGraph(final Graph inputGraph, final String filename, final int bufferSize,
                                  final String defaultEdgeLabel, final String vertexIdKey, final String edgeIdKey,
                                  final String edgeLabelKey, final String intentLabelKey, final String defaultIntentLabel) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        GMLReader.inputGraph(inputGraph, fis, bufferSize, defaultEdgeLabel,
                vertexIdKey, edgeIdKey, edgeLabelKey, intentLabelKey, defaultIntentLabel);
        fis.close();
    }

    /**
     * Load the GML file into the Graph.
     *
     * @param inputGraph       to receive the data
     * @param inputStream      GML file
     * @param defaultEdgeLabel default edge label to be used if not defined in the data
     * @param vertexIdKey      if the id of a vertex is a &lt;data/&gt; property, fetch it from the data property.
     * @param edgeIdKey        if the id of an edge is a &lt;data/&gt; property, fetch it from the data property.
     * @param edgeLabelKey     if the label of an edge is a &lt;data/&gt; property, fetch it from the data property.
     * @throws IOException thrown if the data is not valid
     */
    public static void inputGraph(final Graph inputGraph, final InputStream inputStream, final int bufferSize,
                                  final String defaultEdgeLabel, final String vertexIdKey, final String edgeIdKey,
                                  final String edgeLabelKey, final String intentLabelKey, final String defaultIntentLabel) throws IOException {
        final BatchGraph graph = BatchGraph.wrap(inputGraph, bufferSize);
        String defaultEncoding = "UTF-8";
        
        BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
        ByteOrderMark bom = bOMInputStream.getBOM();
        String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
        InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);
        final Reader r = new BufferedReader(reader);
        //final Reader r = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        final StreamTokenizer st = new StreamTokenizer(r);

        try {
            st.commentChar(GMLTokens.COMMENT_CHAR);
            st.ordinaryChar('[');
            st.ordinaryChar(']');

            final String stringCharacters = "/\\(){}<>!£$%^&*-+=,.?:;@_`|~";
            for (int i = 0; i < stringCharacters.length(); i++) {
                st.wordChars(stringCharacters.charAt(i), stringCharacters.charAt(i));
            }

            new GMLParser(graph, defaultEdgeLabel, vertexIdKey, edgeIdKey, edgeLabelKey, intentLabelKey, defaultIntentLabel).parse(st);

            graph.commit();

        } catch (IOException e) {
            throw new IOException("GML malformed line number " + st.lineno() + ": ", e);
        } finally {
            r.close();
        }
    }
}
