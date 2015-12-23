
/**
 * Class Specie represents a group of simmilar ANN's in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Specie
{
    ANN model;
    List<ANN> anns;
    Specie(ANN model){
        
        this.model = model.copy();
        anns = new ArrayList<ANN>();
        anns.add(model);
        //System.out.println("specie model outs  " + anns.get(0).outputs.length);
    }
    
    float compatibility(ANN ann){
        return Reproduction.compatibilityDistance(ann, model);
    }
    
    boolean addable(ANN ann){
        return (compatibility(ann) <= Reproduction.compatibilityThreshold);
    }
    
    void add(ANN ann){
        if(addable(ann)){
            anns.add(ann);
        }
    }
    
    float expAdjustedFitnessSum(){
        float sum = 0;//we must keep fitness small because large fitness can cause the sum to go to infinity!
        for(ANN ann: anns){
            sum += Math.exp(ann.adjustedFitness);
        }
        return sum;
    }
    
    List<ANN> offsprings(int numOfOffsprings){
        //we are doing species reproduction a bit diffrent here.
        //we will choose 2 anns randomly acording to their adjuested fitness and create a new population.(not necesirly different!)
        //all the new offsprings might be mutated.
        List<ANN> offsprings = new ArrayList<ANN>();
        for(int i = 0; i < numOfOffsprings; i++){
            double alpha = Math.random();
            if(alpha <= Reproduction.mutationWithoutCrossover){
                offsprings.add(Reproduction.mutate(rouletteFitnessANN()));
            }else{
                offsprings.add(Reproduction.mutate(rouletteFitnessOffspring()));
            }
        }
        return offsprings;
    }
    
    ANN rouletteFitnessANN(){
        float alpha = (float)Math.random()*expAdjustedFitnessSum();
        float sum = 0;
        ANN p = null;
        for(ANN ann: anns){
            sum += Math.exp(ann.adjustedFitness);
            if(sum >= alpha){
                p = ann;
            }
        }
        if(p == null){
            p = anns.get(anns.size()-1);
        }
        return p;
    }
    
    ANN rouletteFitnessOffspring(){
        ANN p1 = rouletteFitnessANN();
        ANN p2 = rouletteFitnessANN();
        return Reproduction.crossover(p1, p2);
    }
    
}
