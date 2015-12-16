
/**
 * Represents a node in ANN used in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node implements Comparable
{
    public int id;
    protected NodeType type; 
    float lastOutput;
    float input;
    protected boolean calculatedInOut;
    float output;
    protected List<Connection> inputs;
    public Node(int id, NodeType type)
    {
        this.id = id;
        this.type = type;
        input = 0;
        lastOutput = 0;
        output = 0;
        calculatedInOut = false;
        inputs = new ArrayList<Connection>();
    }
    
    /*public Node copy(){//what about recurrent connections?
        Node copy = new Node(id, type);
        for (Connection c : inputs){
                Node in = c.in.copy();
                Connection connectionCopy = new Connection(in, copy, c.weight, c.enabled, c.innovation);
                connectionCopy.recurrent = c.recurrent;
                copy.addInput(connectionCopy);
        }
        return copy;
    }*/
    
    
    public Node copyWithoutConnections(){
        return new Node(this.id, this.type);
    }
    
    public void addInput(Connection c){
        c.out = this;
        if(c.out.equals(this) && !inputs.contains(c))
            inputs.add(c);
        else{
            if(!c.out.equals(this)){
                System.out.println("error: trying to add invalid connection to node with id " + id);
            }else{
                System.out.println("error: trying to add existing connection to node with id " + id);
            }
            
        }
            
    }
    
    public void removeInput(Connection c){
        inputs.remove(c);
    }
    
    public List<Connection> getInputs(){
        return inputs;
    }

    public NodeType getType(){
        return this.type;
    }
    
    float output(){
        if(type == NodeType.INPUT) return input;
        if(type == NodeType.BIAS) return 1;
        if(!calculatedInOut) calculateInputOutput();
        return output;
    }
    
    float lastOutput(){
        return lastOutput;
    }
    
    protected void calculateInputOutput(){
        if(type != NodeType.INPUT && type != NodeType.BIAS){
            calculatedInOut = true;
            input = 0;
            for (Connection c : inputs) {
                input += c.output();
            }
            output = activation(input);
        }
    }
    
    void update(){
        lastOutput = output;
        input = 0;
        calculatedInOut = false;
    }
    
    void updateAncestors(){
        updateAncestors(new ArrayList<Node>());
    }
    
    void updateAncestors(List<Node> updated){
        if(updated.contains(this)) return;
        update();
        updated.add(this);
        for (Connection c : inputs){
            if(!c.recurrent)
                c.in.updateAncestors(updated);
        }
    }
    
    protected float activation(float input){
        return modifiedSigmoid(input);
    }
    
    protected float modifiedSigmoid(float x){
        return sigmoid((float)((4.9)*x));
    }
    
    protected float sigmoid(float x){
        return (float)(1/(1+(1/(Math.pow(Math.E, (double)-x)))));
    }
    
    public boolean equals(Node n){
        return (id == n.id) && (type == n.getType());//removed identical connections because of a problem
    }
    
    public boolean hasAncestor(Node n){
        return hasAncestor(n, new ArrayList<Node>());
    }
    
    public boolean hasAncestor(Node n, List<Node> visited){
        if(visited.contains(this)) return false;
        visited.add(this);
        if(this.type == NodeType.INPUT || this.type == NodeType.BIAS) return false;
        if(visited.contains(n)) return true;
        for (Connection c : inputs) {
            if(c.in.hasAncestor(n, visited)) return true;
        }
        return false;
    }
    
    public int compareTo(Object o)
    {
        Node n = (Node) o;
        return(id - n.id);
    }
}

enum NodeType{
    INPUT,
    HIDDEN,
    OUTPUT,
    BIAS
}