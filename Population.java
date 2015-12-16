
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
    Population(int initialSize, ANN emptyANN, FitnessEvaluator fitnessEvaluator){
        species = new ArrayList<Specie>();
        emptyANN.fitness = fitnessEvaluator.getFitness(emptyANN);
        for(int i = 0; i < initialSize; i++){
            add(emptyANN.copy());
        }
        size = initialSize;
        generation = 1;
        this.fitnessEvaluator = fitnessEvaluator;
    }
    
    void newGeneration(){
        List<ANN> offsprings = new ArrayList<ANN>();
        float fitnessSum = 0;
        for(Specie s: species){
            fitnessSum += s.expAdjustedFitnessSum();
        }
        for(Specie s: species){
            Collections.sort(s.anns);
            offsprings.add(s.anns.get(0));
        }
        for(Specie s: species){
            List<ANN> specieOffsprings = s.offsprings((int)(size * s.expAdjustedFitnessSum()/fitnessSum));
            for(ANN offspring: specieOffsprings){
                offsprings.add(offspring);
            }
        }
        species.clear();
        Reproduction.newGeneration();
        ANN best = offsprings.get(0);
        float maxFitness = 0;
        float maxConnections = 0;
        for(ANN offspring: offsprings){
            offspring.fitness = fitnessEvaluator.getFitness(offspring);
            //System.out.println(offspring.fitness + " > " + maxFitness +"    " + (offspring.fitness > maxFitness));
            if(offspring.fitness > maxFitness){
                maxFitness = offspring.fitness;
                best = offspring;
            }
            if(offspring.getConnections().size() > maxConnections){
                maxConnections = offspring.getConnections().size();
                
            }
            add(offspring);
            //System.out.println(maxFitness);
        }
        
        
        //System.out.println(species.size());
        System.out.println("generation " + generation + "  max fitness " +maxFitness+"   species  " + species.size() + "  max connections   " + maxConnections);
        System.out.println("winner: nodes  " + best.getNodes().size() +"   connections   " + best.getConnections().size());
        updateAdjustedFitness();
        generation++;
    }
    
    
    void add(ANN ann){
        ann.reset();
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
