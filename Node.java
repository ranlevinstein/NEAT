
/**
 * Represents a node in ANN used in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node
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
    
    public List getInputs(){
        return inputs;
    }

    public NodeType getType(){
        return this.type;
    }
    
    float output(){
        if(type == NodeType.INPUT) return input;
        if(type == NodeType.BIAS) return 1;
        if(calculatedInOut) return output;
        return activation(input);
    }
    
    float lastOutput(){
        return lastOutput;
    }
    
    protected void calculateInputOutput(){
        input = 0;
        for (Connection c : inputs) {
            input += c.output();
        }
        output = activation(input);
        calculatedInOut = true;
    }
    
    void update(){
        lastOutput = output();
        input = 0;
        calculatedInOut = false;
    }
    
    protected float activation(float input){
        return sigmoid(input);
    }
    
    protected float sigmoid(float x){
        return (float)(1/(1+(1/(Math.pow(Math.E, (double)-x)))));
    }
    
    public boolean equals(Node n){
        return (id == n.id) && (type == n.getType()) && (inputs.equals(n.getInputs()));
    }
    
    
    public boolean hasAncestor(Node n){
        if (n.type == NodeType.INPUT || n.type == NodeType.BIAS) return false;
        if(this.equals(n)) return true;
        for (Connection c : inputs) {
            if(c.in.hasAncestor(n)) return true;
        }
        return false;
    }
}

enum NodeType{
    INPUT,
    HIDDEN,
    OUTPUT,
    BIAS
}