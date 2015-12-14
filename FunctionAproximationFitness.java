
/**
 * Write a description of class FunctionAproximationFitness here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FunctionAproximationFitness implements FitnessEvaluator
{
    //evaluate fucntion x^3 between -1 and 1
    public float getFitness(ANN ann){
        return rSquared(ann);
    }
    
    public float rSquared(ANN ann){
        float ymean = 0;
        float sstot = 0;
        float ssres = 0;
        for(float x = -1; x <= 1; x+= 0.05){
            sstot += (getRealValue(x)-ymean)*(getRealValue(x)-ymean);
            ssres += (getRealValue(x)-getApproximation(ann, x))*(getRealValue(x)-getApproximation(ann, x));
        }
        return 1-(float)ssres/sstot;
    }
    
    public float getApproximation(ANN ann, float x){
        float[] in = {x};
        return ann.step(in)[0];
    }
    
    public float getRealValue(float x){
        return x*x*x;
    }
}
