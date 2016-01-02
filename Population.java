
/**
 * Class Population represents a population of anns in NEAT algorithm.
 * 
 * @author Ran Levinstein
 * @version 1.0
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class Population
{
    List<Specie> species;
    int size;
    int generation;
    FitnessEvaluator fitnessEvaluator;
    float maxFitness;
    Population(int initialSize, ANN emptyANN, FitnessEvaluator fitnessEvaluator){
        this.fitnessEvaluator = fitnessEvaluator;
        maxFitness = 0;//cant be negative!!!!!!
        species = new ArrayList<Specie>();
        emptyANN.fitness = fitnessEvaluator.getFitness(emptyANN);
        for(int i = 0; i < initialSize; i++){
            add(emptyANN.copy());
        }
        size = initialSize;
        generation = 1;
    }
    
    void newGeneration(){
        maxFitness = 0;
        List<ANN> offsprings = new ArrayList<ANN>();
        long fitnessSum = 0;
        float maxF = 0;
        for(Specie s: species){
            fitnessSum += s.expAdjustedFitnessSum();
        }
        for(Specie s: species){
            ANN best = s.anns.get(0);
            for(ANN ann: s.anns){
                if(best.fitness < ann.fitness){
                    best = ann;
                }
            }
            if(best.fitness > maxF){
                offsprings.add(best);
                maxF = best.fitness;
            }
            
        }
        for(Specie s: species){
            List<ANN> specieOffsprings = s.offsprings((int)(size * s.expAdjustedFitnessSum()/fitnessSum));
            for(ANN offspring: specieOffsprings){
                offsprings.add(offspring);
            }
        }
        species.clear();
        Reproduction.newGeneration();
        float maxConnections = 0;
        for(ANN offspring: offsprings){
            if(offspring.getConnections().size() > maxConnections){
                maxConnections = offspring.getConnections().size();
                
            }
            add(offspring);
            //System.out.println(maxFitness);
        }
        
        //System.out.println(species.size());
        System.out.println("generation " + generation + "  max fitness " +maxFitness+"   species  " + species.size() + "  max connections   " + maxConnections);
        updateAdjustedFitness();
        generation++;
        if(maxFitness == 0){
            System.out.println();
        }
    }
    
    
    void add(ANN ann){
        ann.reset();
        ann.fitness = fitnessEvaluator.getFitness(ann);
        if(maxFitness < ann.fitness){
            maxFitness = ann.fitness;
        }
        Specie match = null;
        float minDistance = Float.MAX_VALUE;
        for(Specie s: species){
            
            float d = s.compatibility(ann);
            if(d < minDistance){
                match = s;
                minDistance = d;
            }
        }
        
        if(match != null && minDistance <= Reproduction.compatibilityThreshold){
            match.add(ann);
        }else{
            Specie s = new Specie(ann);
            species.add(s);
        }
    }
    
    void updateAdjustedFitness(){
        for(Specie i: species){
            for(ANN a: i.anns){
                float lowerSum = 0;
                for(Specie j: species){
                    for(ANN b: j.anns){
                        lowerSum += Reproduction.compatibilityDistance(a, b) <= Reproduction.compatibilityThreshold? 1 : 0;
                    }
                }
                a.adjustedFitness = a.fitness/lowerSum;
            }
        }
    }
    
    
    
    
}
