
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

public class ANN
{
    int lastNodeId;
    Node[] inputs;
    Node[] outputs;
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
    }
    
    
    ANN copy(){//copy this ANN
        ANN copy = new ANN(inputs.length-1, outputs.length);
        List<Node> nodes = getNodes();
        List<Connection> connections = getConnections();
        //attach inputs, bias and outputs to it's place in the new ANN.
        for(int i = 0; i < inputs.length-1; i++){
            //id = i+1
            copy.inputs[i] = getNodeWithId(nodes, i+1);
        }
        copy.inputs[inputs.length-1] = getNodeWithId(nodes, inputs.length);
        for(int i = 0; i < outputs.length; i++){
            copy.outputs[i] = getNodeWithId(nodes, i+1+inputs.length);
        }
        for(Connection c : connections){
            Node in = getNodeWithId(nodes, c.in.id);
            Node out = getNodeWithId(nodes, c.out.id);
            Connection con = new Connection(in, out, c.weight, c.enabled, c.innovation);
            con.recurrent = c.recurrent;
            out.addInput(con);
        }
        return copy;
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
    
    
    public List<Connection> getConnections(){
        List<Connection> connections = new ArrayList<Connection>();
        for(int i = 0; i < outputs.length; i++){
           addConnections(connections, outputs[i]);
        }
        return connections;
    }
    
    protected void addConnections(List<Connection> connections, Node n){
        for(Connection c: n.getInputs()){
            if(!containsConnectionWithInnov(connections, c.innovation)){
                connections.add(c);
                addConnections(connections, c.in);
            }
        }
        
    }

    protected static boolean containsConnectionWithInnov(List<Connection> connections, int innov){
        for (Connection c : connections) {
            if(c.innovation == innov) return true;
        }
        return false;
    }
    
    protected static boolean containsNodeWithId(List<Node> nodes, int id){
        for (Node n : nodes) {
            if(n.id == id) return true;
        }
        return false;
    }
    
    protected static Node getNodeWithId(List<Node> nodes, int id){
        for (Node n : nodes) {
            if(n.id == id) return n;
        }
        return null;
    }
    
    //protected add 
    
    
    
    
    float[] step(float[] inputs){
        float[] outputs = new float[this.outputs.length];
        for(int i = 0; i < this.inputs.length; i++){
            this.inputs[i].input = inputs[i];
        }
        for(int i = 0; i < outputs.length; i++){
            outputs[i] = this.outputs[i].output();
        }
        for(int i = 0; i < outputs.length; i++){
            this.outputs[i].updateAncestors();
        }
        return outputs;
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
    
    boolean ValidToAdd(Connection c){
        if(c.in == null || c.out == null){
            //System.out.println("error: one or more of the nodes in this conncetions don't exist");
            return false;
        }
        //c.in and c.out should be references to nodes in the ANN and not just copies!
        if(c.out.id <= inputs.length){
            //System.out.println("error: trying to add input connection to input node");
            return false;
        }
        return true;
    }
    
    void addConnection(Connection c){
        if(ValidToAdd(c)){
            c.out.addInput(c);
        }else{
            //System.out.println("c isn't valid to add to this ANN");
        }
    }
    
    void addNode(Connection disable, Node n, int innovation1, int innovation2){
        disable.enabled = false;
        Connection c1 = new Connection(disable.in, n, 1, true, innovation1);
        Connection c2 = new Connection(n, disable.out, disable.weight, true, innovation2);
        addConnection(c1);
        addConnection(c2);
        lastNodeId++;
        n.id = lastNodeId;
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
}
