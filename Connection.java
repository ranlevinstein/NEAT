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
    Connection(Node in, Node out, float weight, boolean enabled, int innovation){
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.enabled = enabled;
        this.innovation = innovation;
        recurrent = in.hasAncestor(out);
        
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


