
/**
 * Write a description of class Tester here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tester
{
    public static void test(){
        ANN ann = new ANN(1, 1);
        ann.addRandomConnection(5, true);
        System.out.println("size  " + ann.getConnections().size());
    }
}
