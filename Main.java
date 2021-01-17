public class Main {

    public static void main(String[] args) {
        double[] c = {-5,-7,-12,1,0,0};
        double[] b = {38,55};
        double[][] A = {
                new double[] {2,3,2,1,1,0},
                new double[] {3,2,4,-1,0,1}
        };
        Integer[] inB = {4,5};
        Integer[] inN = {0,1,2,3};
        Simplex s = new Simplex(c, A, b, inB, inN);
        s.solve().print();
        System.out.println("Optimal Value: " + s.getOptimalValue());
    }
}
