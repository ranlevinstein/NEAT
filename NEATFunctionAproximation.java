
/**
 * Write a description of class NEATFunctionAproximation here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NEATFunctionAproximation
{
   public static void main(){
       ANN function = new ANN(1, 1);
       FitnessEvaluator fitness = new FunctionAproximationFitness();
       Population pop = new Population(100, function, fitness);
       for(int i = 0; i < 1000; i++){
           pop.newGeneration();
       }
    }
}
