
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
    Population(int initialSize, ANN emptyANN){
        species = new ArrayList<Specie>();
        for(int i = 0; i < initialSize; i++){
            add(emptyANN);
        }
        size = initialSize;
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
