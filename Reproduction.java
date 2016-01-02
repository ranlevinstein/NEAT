
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
    //All of this factosr are just smart guesses.
    //I might need to choose them more carefully in the future.
    
    static float minWeight = -2;
    static float maxWeight = 2;
    
    static float perturbationDistance = (float)(maxWeight/10.0);
    
    static float connectionMutateRate = (float)0.25;
    static float connectionPerturbed = (float)0.9;//The other 0.1 is the probability that new random weight is asigned.
    
    static float disableIfOneOfParentsDisabled = (float)0.75;
    
    static float addNewNode = (float)0.004;
    static float addNewConnection = (float)0.015;
    
    static float mutationWithoutCrossover = (float) 0.25;
    
    static float c1 = (float)2;
    static float c2 = (float)2;
    static float c3 = (float)0.2;
    static float compatibilityThreshold = (float)3;//3.5
    
    static int innovation = 1;
    static int generation = 1;
    static List<int[]> beforeMutationInnovation = new ArrayList<int[]>();
    static List<Connection> mutationInnovtion = new ArrayList<Connection>();
    
    static void newGeneration(){
        beforeMutationInnovation.clear();
        mutationInnovtion.clear();
        generation++;
    }
    
    static ANN mutate(ANN ann){
        List<Connection> connections = ann.getConnections();
        for(Connection c: connections){
            double alpha = Math.random();
            if(alpha < connectionPerturbed){
                double beta = Math.random();
                c.weight = (float)(c.weight + beta*2*connectionPerturbed - connectionPerturbed);
            }else{
                c.weight = randomWeight();
            }
        }
        int numOfOuts = 0;
        List<Node> offspringNodes = ann.getNodes();
        for(Node n: offspringNodes){
            if(n.type == NodeType.OUTPUT){
                numOfOuts++;
            }
        }
        //System.out.println("outs mutate   " + numOfOuts);
        ANN mutation = new ANN(ann.getNodes(), connections);
        double alpha = Math.random();
        if(alpha <= addNewNode){
            mutation.addRandomNode();
        }
        alpha = Math.random();
        if(alpha <= addNewConnection){
            mutation.addRandomConnection(randomWeight(), true);
            if(mutation.getConnections().size() == 0){
                System.out.println("errrorrrrrrr!!!!@!@!11111!!!!1");
            }
        }
        return mutation;
    }
    
    static ANN crossover(ANN a, ANN b){
        List<Connection> offspringConnections = new ArrayList<Connection>();
        List<Connection> aConnections = a.getConnections();
        List<Connection> bConnections = b.getConnections();
        for(Connection c: aConnections){
            Connection d = ANN.getConnectionWithInnov(bConnections, c.innovation);
            double beta = Math.random();
            if(d != null){
                double alpha = Math.random();
                if(alpha < 0.5){
                    if((!c.enabled || !d.enabled) && beta <= disableIfOneOfParentsDisabled){
                        c.enabled = false;
                    }
                    c.weight = (float)(alpha*1.1*c.weight + (1-alpha*1.1)*d.weight);//averaging
                    offspringConnections.add(c);
                }else{
                    bConnections.remove(d);
                    if((!c.enabled || !d.enabled) && beta <= disableIfOneOfParentsDisabled){
                        d.enabled = false;
                    }
                    d.weight = (float)(alpha*1.1*c.weight + (1-alpha*1.1)*d.weight);//averaging
                    offspringConnections.add(d);
                }
                
            }else{
                if(a.fitness >= b.fitness){
                    if(!c.enabled && beta <= disableIfOneOfParentsDisabled){
                        c.enabled = false;
                    }
                    offspringConnections.add(c);
                }
            }
        }
        if(b.fitness > a.fitness){
            for(Connection c: bConnections){
                double beta = Math.random();
                if(!c.enabled && beta <= disableIfOneOfParentsDisabled){
                        c.enabled = false;
                }
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
            if(!offspringNodes.contains(n)){
                offspringNodes.add(n);
            }
        }
        return new ANN(offspringNodes, offspringConnections);
    }
    
    static float compatibilityDistance(ANN a, ANN b){
        
        List<Connection> conA = a.getConnections();
        List<Connection> conB = b.getConnections();
        int sizeA = conA.size();
        int sizeB = conB.size();
        float sumDiff = 0;
        int counter = 0;
        for(Connection ca: conA){
            for(Connection cb: conB){
                if(ca.innovation == cb.innovation){
                    sumDiff += Math.abs(ca.weight-cb.weight);
                    counter++;
                    sizeA--;
                    sizeB--;
                    //conA.remove(ca);
                    //conB.remove(cb);
                }
            }
        } 
        float w = 0;
        if(counter != 0){//not defined otherwise. i am not sure yet what the value of w should be in that case;
            w = sumDiff/(float)counter;
        }
        int excess = sizeA;
        int disjoint = sizeB;
        float n = Math.min(sizeA, sizeB);//was max
        float distance = (c1*disjoint+c2*excess)/n + c3*w;
        if(n == 0){
            distance = 0;
        }
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
    
    
    static float randomWeight(){
        return (float)((maxWeight-minWeight)*Math.random()+minWeight);
    }
    
}
