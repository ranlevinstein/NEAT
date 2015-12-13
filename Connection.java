/**
 * Represents a connection in ANN in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */
public class Connection
{
    public Node in;
    public Node out;
    public float weight;
    public boolean enabled;
    public int innovation;
    public boolean recurrent;
    Connection(Node in, Node out, float weight, boolean enabled){
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.enabled = enabled;
        this.innovation = -1;
        recurrent = in.hasAncestor(out) || (in.type == NodeType.OUTPUT && out.type == NodeType.OUTPUT);
    }
    
    Connection(Node in, Node out, float weight, boolean enabled, int innovation){
        this(in, out, weight, enabled);
        this.innovation = innovation;
    }
    
    float output(){
        if(enabled){
            if(recurrent){
                return in.lastOutput()*weight;
            }else{
                return in.output()*weight;
            }
        }else{
            return 0;
        }
    }
}


