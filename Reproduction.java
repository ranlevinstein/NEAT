
/**
 * Reproduction class is responsable for ANN reproduction and mutation and generally generating new offsprings..
 * 
 * @author Ran Levinstein 
 * @version 1.0
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Reproduction
{
    //All of this factor are just guesses.
    //I need to choose them more carefully in the future.
    static float c1 = (float)1.0;
    static float c2 = (float)1.0;
    static float c3 = (float)0.4;
    static float compatibilityThreshold = (float)3.5;
    
    static int innovation = 1;
    static int generation = 1;
    static List<int[]> beforeMutationInnovation = new ArrayList<int[]>();
    static List<Connection> mutationInnovtion = new ArrayList<Connection>();
    
    static void newGeneration(){
        beforeMutationInnovation = new ArrayList<int[]>();
        mutationInnovtion = new ArrayList<Connection>();
        generation++;
    }
    
    static ANN crossover(ANN a, ANN b){
        List<Connection> offspringConnections = new ArrayList<Connection>();
        List<Connection> aConnections = a.getConnections();
        List<Connection> bConnections = b.getConnections();
        for(Connection c: aConnections){
            Connection d = ANN.getConnectionWithInnov(bConnections, c.innovation);
            if(d != null){
                double alpha = Math.random();
                if(alpha < 0.5){
                    offspringConnections.add(c);
                }else{
                    offspringConnections.add(d);
                    bConnections.remove(d);
                }
            }else{
                if(a.fitness >= b.fitness){
                    offspringConnections.add(c);
                }
            }
        }
        if(b.fitness > a.fitness){
            for(Connection c: bConnections){
                offspringConnections.add(c);
            }
        }
        List<Node> offspringNodes = new ArrayList<Node>();
        List<Node> aNodes = a.getNodes();
        List<Node> bNodes = b.getNodes();
        for(Node n: aNodes){
            offspringNodes.add(n);
        }
        for(Node n: bNodes){
            if(!ANN.containsNodeWithId(offspringNodes, n.id)){
                offspringNodes.add(n);
            }
        }
        return new ANN(offspringNodes, offspringConnections);
    }
    
    static float compatibilityDistance(ANN a, ANN b){
        int excess = 1;
        int disjoint = 1;
        int[] innovA = a.getInnovationNumbers();
        int[] innovB = b.getInnovationNumbers();
        List<Connection> conA = a.getConnections();
        List<Connection> conB = b.getConnections();
        float sumDiff = 0;
        int counter = 0;
        for(Connection ca: conA){
            for(Connection cb: conB){
                if(ca.innovation == cb.innovation){
                    sumDiff += Math.abs(ca.weight-cb.weight);
                    counter++;
                }
            }
        } 
        float w = 0;
        if(counter != 0){//not defined otherwise. i am not sure yet what the value of w should be in that case;
            w = sumDiff/(float)counter;
        }
        float n = Math.max(conA.size(), conB.size());
        float distance = (c1*disjoint+c2*excess)/n + c3*w;
        return distance;
    }
    
    static void setInnovation(ANN ann, Connection c){
        //c should alredy be connected into the network.
        int[] innovation = ann.getInnovationNumbers();
        for(int i = 0; i < beforeMutationInnovation.size(); i++){
            boolean simmilar = false;
            if(beforeMutationInnovation.get(i).length == innovation.length){
                simmilar = true;
                for(int j = 0; j < innovation.length; j++){
                    simmilar = simmilar && innovation[j] == beforeMutationInnovation.get(i)[j];
                }
            }
            if(simmilar){
                //check if connection c is in the same place
                if(c.in.id == mutationInnovtion.get(i).in.id && c.out.id == mutationInnovtion.get(i).out.id){
                    c.innovation = mutationInnovtion.get(i).innovation;
                    return;
                }
            }
        }
        c.innovation = Reproduction.innovation;
        Reproduction.innovation++;
    }
    
}
