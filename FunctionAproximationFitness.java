
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
        ann.reset();
        return rSquared(ann);
    }
    
    public static float rSquared(ANN ann){
        float ymean = 0;
        float numOfSamples = 0;
        float sstot = 0;
        float ssres = 0;
        for(float x = -1; x <= 1; x+= 0.05){
            numOfSamples++;
            ymean += getRealValue(x);
        }
        ymean /= numOfSamples;
        for(float x = -1; x <= 1; x+= 0.05){
            sstot += (getRealValue(x)-ymean)*(getRealValue(x)-ymean);
            ssres += (getRealValue(x)-getApproximation(ann, x))*(getRealValue(x)-getApproximation(ann, x));
        }
        //System.out.println((float)1/ssres);
        return (float)1/ssres;
    }
    
    public static float getApproximation(ANN ann, float x){
        float[] in = {x};
        //System.out.println(ann.step(in).length);
        float out = ann.step(in)[0];
        return out;
    }
    
    public static float getRealValue(float x){
        return (float) (Math.sin(1*x));
    }
}
