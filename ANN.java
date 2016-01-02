
/**
 * Represents ANN used in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class ANN implements Comparable
{
    protected int lastNodeId;
    Node[] inputs;
    Node[] outputs;
    float fitness;
    float adjustedFitness;
    ANN(int numOfInputs, int numOfOutputs){
        lastNodeId = 0;
        inputs = new Node[numOfInputs+1];
        outputs = new Node[numOfOutputs];
        for(int i = 0; i < inputs.length-1; i++){
            lastNodeId++;
            inputs[i] = new Node(lastNodeId, NodeType.INPUT);
        }
        lastNodeId++;
        inputs[inputs.length-1] = new Node(lastNodeId, NodeType.BIAS);
        for(int i = 0; i < outputs.length; i++){
            lastNodeId++;
            outputs[i] = new Node(lastNodeId, NodeType.OUTPUT);
        }
        System.out.println("output length  " + outputs.length);
    }
    
    ANN(List<Node> nodes, List<Connection> connections){
        //attach inputs, bias and outputs to it's place in the new ANN.
        int largestId = 0;
        List<Node> inputsList = new ArrayList<Node>();
        List<Node> outputsList = new ArrayList<Node>();
        for(Node n: nodes){
            if(n.type == NodeType.INPUT || n.type == NodeType.BIAS){
                if(!containsNodeWithId(inputsList, n.id)){
                    inputsList.add(n);
                }
            }
            if(n.type == NodeType.OUTPUT){
                if(!containsNodeWithId(outputsList, n.id)){
                    outputsList.add(n);
                }
            }
            if(largestId < n.id){
                largestId = n.id;//maybe there is a need to regulate the id's.
            }
        }
        //System.out.println("number of outs  " + numOfOutputs);
        lastNodeId = largestId;
        //Collections.sort(inputsList);
        //Collections.sort(outputsList);
        inputs = new Node[inputsList.size()];
        outputs = new Node[outputsList.size()];
        if(inputsList.size() > 2){
            System.out.println("inputsss   " + inputsList.size());
        }
        for(int i = 0; i < inputs.length; i++){
            //id = i+1
            Node minId = inputsList.get(0);
            for(Node in: inputsList){
                if(in.id < minId.id){
                    minId = in;
                }
            }
            inputs[i] = minId;
            inputsList.remove(minId);
            if(minId.id != i+1){
                System.out.println("error: wanted id " + i+1 + "  minimal remaining id " + minId.id);
            }
        }
        if(inputs[inputs.length-1] == null){
                //inputs[inputs.length-1] = new Node(inputs.length, NodeType.BIAS);
        }
        for(int i = 0; i < outputs.length; i++){
            outputs[i] = outputsList.get(i);
            Node minId = outputsList.get(0);
            for(Node out: outputsList){
                if(out.id < minId.id){
                    minId = out;
                }
            }
            outputs[i] = minId;
            outputsList.remove(minId);
            if(minId.id != i+1+inputs.length){
                System.out.println("error: wanted id " + i+1+inputs.length + "  minimal remaining id " + minId.id);
            }
        }
        
        for(Connection c : connections){
            Node in = getNodeWithId(nodes, c.in.id);
            if(in == null){
                in = new Node(c.in.id, c.in.type);
                nodes.add(in);
            }
            Node out = getNodeWithId(nodes, c.out.id);
            if(out == null){
                out = new Node(c.out.id, c.out.type);
                nodes.add(out);
            }
            //if(in != null && out != null){
                Connection con = new Connection(in, out, c.weight, c.enabled, c.innovation);
                con.recurrent = c.recurrent;
                out.addInput(con);
            //}
        }
        //System.out.println("output length  " + outputs.length);
    }
    
    boolean addRandomNode(){
        List<Connection> connections = getConnections();
        if(connections.size() > 0){
            int index = (int)(Math.random()*connections.size());
            addNode(connections.get(index), new Node(-1, NodeType.HIDDEN));
            return true;
        }else{
            return false;
        }
   }
    
    boolean addRandomConnection(float weight, boolean enabled){
        List<Connection> addableConnections = new ArrayList<Connection>();
        List<Node> nodes = getNodes();
        for(Node n1: nodes){
            for(Node n2: nodes){
                Connection c = new Connection(n1, n2, weight, enabled);
                if(validToAdd(c)){
                    addableConnections.add(c);
                }
            }
        }
        if(addableConnections.isEmpty()){
            return false;//ANN is full. can't add any new connection without adding another node first.
        }else{
            int index = (int)(Math.random()*addableConnections.size());
            addConnection(addableConnections.get(index));
            return true;
        }
    }
    
    
    ANN copy(){//copy this ANN
        return new ANN(getNodes(), getConnections());
    }
    
    public List<Node> getNodes(){
        List<Node> nodes = new ArrayList<Node>();
        for(int i = 0; i < inputs.length; i++){
           nodes.add(inputs[i].copyWithoutConnections());
        }
        for(int i = 0; i < outputs.length; i++){
           addNodes(nodes, outputs[i]);
        }
        return nodes;
    }
    
    protected void addNodes(List<Node> nodes, Node n){
        if(!containsNodeWithId(nodes, n.id)){
            nodes.add(n.copyWithoutConnections());
            for(Connection c: n.getInputs()){
                addNodes(nodes, c.in);
            }
        }else{
            return;//added this node so all her input nodes are added. 
        }
    }
    
    public void addConnection(Connection c, int id){
        List<Node> visited = new ArrayList<Node>();
        for(int i = 0; i < outputs.length; i++){
            recursiveAddConnection(c, id, outputs[i], visited);
        }
    }
    
    void recursiveAddConnection(Connection c, int id, Node n, List<Node> visited){
        if(visited.contains(n)){
            return;
        }
        visited.add(n);
        if(n.id == id){
            n.addInput(c);
            return;
        }
        for(Connection con: n.getInputs()){
            //if(!con.recurrent){
                recursiveAddConnection(c, id, con.in, visited);
            //}
        }
    }
    
    
    public List<Connection> getConnections(){
        List<Connection> connections = new ArrayList<Connection>();
        for(int i = 0; i < outputs.length; i++){
            if(outputs[i] == null) System.out.println("outputs isss nullllllll!");
           addConnections(connections, outputs[i]);
        }
        return connections;
    }
    
    protected void addConnections(List<Connection> connections, Node n){
        List<Connection> inputs = n.getInputs();
        for(Connection c: inputs){
            if(!containsConnectionWithInnov(connections, c.innovation)){
                connections.add(c);
                addConnections(connections, c.in);
            }
        }
        
    }

    static boolean containsConnectionWithInnov(List<Connection> connections, int innov){
        for (Connection c : connections) {
            if(c.innovation == innov) return true;
        }
        return false;
    }
    
    static Connection getConnectionWithInnov(List<Connection> connections, int innov){
        for (Connection c : connections) {
            if(c.innovation == innov) return c;
        }
        return null;
    }
    
    static boolean containsNodeWithId(List<Node> nodes, int id){
        for (Node n : nodes) {
            if(n.id == id) return true;
        }
        return false;
    }
    
    static Node getNodeWithId(List<Node> nodes, int id){
        for (Node n : nodes) {
            if(n.id == id) return n;
        }
        return null;
    }
    
    //protected add 
    
    void update(){
        for(int i = 0; i < outputs.length; i++){
            this.outputs[i].updateAncestors();
        }
    }
    
    void reset(){
        for(int i = 0; i < outputs.length; i++){
            this.outputs[i].resetAncestors();
        }
    }
    
    float[] step(float[] inputs){
        float[] outputs = new float[this.outputs.length];
        for(int i = 0; i < this.inputs.length-1; i++){//this.inputs length is +1 then input because of bias.
            this.inputs[i].input = inputs[i];
        }
        for(int i = 0; i < outputs.length; i++){
            outputs[i] = this.outputs[i].output();
            outputs[i] = constrain(outputs[i], 0 , 1);
        }
        update();
        return outputs;
    }
    
    private static float constrain(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
    
    Node getNode(int id){
        for(int i = 0; i < inputs.length; i++){
            if(inputs[i].id == id)
                return inputs[i];
        }
        Node temp;
        for(int i = 0; i < outputs.length; i++){
            if(getNode(outputs[i], id) != null)
                return getNode(outputs[i], id);
        }
        return null;
    }
    
    protected Node getNode(Node n, int id){
        if(n.id == id) return n;
        for (Connection c : n.getInputs()) {
            if(!c.recurrent)
                if(getNode(c.in, id) != null)
                    return getNode(c.in, id);
        }
        return null;
    }
    
    boolean validToAdd(Connection c){
        if(c.in == null || c.out == null){
            //System.out.println("error: one or more of the nodes in this conncetions don't exist");
            return false;
        }
        //c.in and c.out should be references to nodes in the ANN and not just copies!
        if(c.out.id <= inputs.length){
            //System.out.println("error: trying to add input connection to input node");
            return false;
        }
        List<Connection> connections = getConnections();
        for(Connection con: connections){
            if(con.in.id == c.in.id && con.out.id == c.out.id){
                //System.out.println("error: trying to add existing connection");
                return false;
            }
        }
        return true;
    }
    
    void addConnection(Connection c){
        if(validToAdd(c)){
            Reproduction.setInnovation(this, c);
            addConnection(c, c.out.id);
            //c.out.addInput(c);
        }else{
            //System.out.println("c isn't valid to add to this ANN");
        }
    }
    
    void setAAsB(Node a, Node b){
        a = b;
    }
    
    void addNode(Connection disable, Node n){
        disable.enabled = false;
        Connection c1 = new Connection(disable.in, n, 1, true);
        Connection c2 = new Connection(n, disable.out, disable.weight, true);
        if(disable.recurrent){
            c1.recurrent = true;
            c2.recurrent = false;
        }
        addConnection(c1);
        addConnection(c2);
        lastNodeId++;
        n.id = lastNodeId;
        n.addInput(c1);
    }
    
    int[] getInnovationNumbers(){
        List<Connection> connections = getConnections();
        int [] innov = new int[connections.size()];
        int i = 0;
        for(Connection c: connections){
            innov[i] = c.innovation;
            i++;
        }
        Arrays.sort(innov);
        return innov;
    }
    
    public static boolean similar(ANN a, ANN b){
        int[] innov1 = a.getInnovationNumbers();
        int[] innov2 = b.getInnovationNumbers();
        if(innov1 != innov2)
            return false;
        for(int i = 0; i < innov1.length; i++){
            if(innov1[i] != innov2[i])
                return false;
        }
        return true;
    }
    
    public int compareTo(Object o){
        ANN ann = (ANN) o;
        float result = adjustedFitness-ann.adjustedFitness;
        if(result == 0) return 0;
        if(result < 0) return -1;
        return 1;
    }
    
}
