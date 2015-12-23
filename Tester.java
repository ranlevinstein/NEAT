
/**
 * Write a description of class Tester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tester
{
    public static void test(){
        FitnessEvaluator fitness = new FunctionAproximationFitness();
        ANN ann = new ANN(1, 1);
        for(int i = 0; i < 50; i++){
            ann.addRandomConnection(2, true);
            ann.addRandomNode();
        }
        System.out.println("t1: " + fitness.getFitness(ann));
        System.out.println("t2: " + fitness.getFitness(ann));
    }
}
