
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
            add(emptyANN);
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
            List<ANN> specieOffsprings = s.offsprings((int)(size * s.expAdjustedFitnessSum()/fitnessSum));
            for(ANN offspring: specieOffsprings){
                offsprings.add(offspring);
            }
        }
        species = new ArrayList<Specie>();
        float maxFitness = 0;
        for(ANN offspring: offsprings){
            offspring.fitness = fitnessEvaluator.getFitness(offspring);
            add(offspring);
            if(offspring.fitness > maxFitness){
                maxFitness = offspring.fitness;
            }
        }
        System.out.println("generation " + generation + "  max fitness " +maxFitness);
        updateAdjustedFitness();
        generation++;
    }
    
    
    void add(ANN ann){
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
