import java.lang.*;
import java.util.ArrayList;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix; // must use v0.33 since recent versions don't have any documentation (or I couldn't find it)


public class Simplex {
    public final static SimpleMatrix BOUNDLESS_SOLUTION = new SimpleMatrix(new double[][] {{Double.NEGATIVE_INFINITY}});
    public final static SimpleMatrix NONEXISTENT_SOLUTION = new SimpleMatrix(0,0);
    protected final SimpleMatrix c;
    protected SimpleMatrix A;
    protected SimpleMatrix b;
    protected int m;
    protected final int n;
    protected ArrayList<Integer> indexofB;
    protected ArrayList<Integer> indexofN;
    private SimpleMatrix cN;
    private SimpleMatrix cB;
    protected SimpleMatrix B;
    protected SimpleMatrix N;
    private SimpleMatrix gamma;
    private SimpleMatrix optimalSolution;

    public Simplex(SimpleMatrix c, SimpleMatrix A, SimpleMatrix b) {
        this.c = c;
        this.A = A;
        this.b = b;
        n = this.A.numCols();
        m = this.A.numRows();
    }
    public Simplex(double[] c, double[][] A, double[] b) {
        this(
                convertArrayToSimpleMatrix(c, false),
                new SimpleMatrix(A),
                convertArrayToSimpleMatrix(b, false)
        );
    }


    protected void initialize() {
        System.out.println("Beginning Auxiliar problem");
        AuxSimplex aux = new AuxSimplex(A, b);
        aux.solve();
        if (aux.getOptimalValue() != 0) {
            System.out.println("Auxiliar optimal value is not 0\nThe Problem has no valid solutions");
            optimalSolution = NONEXISTENT_SOLUTION;
            return;
        }
        indexofB = aux.getIndexBase();
        indexofN = new ArrayList<>();
        for (int i=0; i<n; i++) {
            if(!indexofB.contains(i)) indexofN.add(i);
        }

        removeRedundant(aux.getIndexofRedundant());

        System.out.println(
                "Auxiliar problem solved, the Optimal solution exist\n"
                + "Base Found: " + indexofB.toString()
        );
    }

    private void removeRedundant(ArrayList<Integer> indexofRedundant) {
        SimpleMatrix AReducted = null, bReducted=null;
        for (int i=0; i<A.numRows(); i++) {
            if (!indexofRedundant.contains(i)) {
                if (AReducted == null || b==null) {
                    AReducted = A.extractVector(true, i);
                    bReducted = b.extractVector(true, i);
                } else {
                    AReducted = AReducted.concatRows(A.extractVector(true, i));
                    bReducted = bReducted.concatRows(b.extractVector(true, i));
                }
            }
        }
        m = m - indexofRedundant.size();
        A = AReducted;
        b = bReducted;
    }

    public SimpleMatrix solve() {
        if (!checkAmmissibility()) return null;
        initialize();
        int j=0;
        while (optimalSolution == null) {
            System.out.println("\n\nIteration " + j);
            updateParams();
            printStatus();


            if (optimalityCriterion()) {
                System.out.println("Optimality Criterion is verified");
                optimalSolution = generateSBA();
                return optimalSolution;
            } else if (unlimitednessCriterion()) {
                System.out.println("Unlimitidness Criterion is verified");
                optimalSolution = BOUNDLESS_SOLUTION;
                return optimalSolution;
            } else {
                System.out.println("No Criterion is verified");

                int h= indexOfMinimum(gamma); //index of N to be entered in B'

                int k; //index of B to be exited from B'
                SimpleMatrix BinvertedNcolumnh = B.invert().mult(N).extractVector(false, h);
                SimpleMatrix Binvertedb = B.invert().mult(b);
                Vector<Double> roArray = new Vector<>();

                //populate roArray
                for (int i=0; i<BinvertedNcolumnh.numRows(); i++) {
                    double pihi = BinvertedNcolumnh.get(i,0);
                    if
                        (pihi<=0) roArray.add(Double.POSITIVE_INFINITY);
                    else
                        roArray.add(Binvertedb.get(i,0)/pihi);
                }
                double ro = findMinimum(roArray);
                k = roArray.indexOf(ro);
                // Note that BLAND anticycle rule is applied, because indexOf always choose the first occurence

                System.out.println("h=" + h + " k=" + k);

                int temp = indexofB.get(k);
                indexofB.set(k, indexofN.get(h));
                indexofN.set(h, temp);

                j++;
            }
        }
        return optimalSolution;
    }

    public SimpleMatrix getOptimalSolution() {
        return optimalSolution.copy();
    }
    public double getOptimalValue() {
        if (optimalSolution.equals(BOUNDLESS_SOLUTION)) return Double.NEGATIVE_INFINITY;
        else if (optimalSolution.equals(NONEXISTENT_SOLUTION)) return Double.POSITIVE_INFINITY;
        else return c.transpose().mult(optimalSolution).get(0,0);
    }

    private SimpleMatrix generateSBA() {
        SimpleMatrix xB = B.invert().mult(b);
        SimpleMatrix xN = new SimpleMatrix(indexofN.size(), 1);
        SimpleMatrix xUnordered = xB.concatRows(xN);
        SimpleMatrix xStar = new SimpleMatrix(xUnordered.numRows(), 1);

        for (int i=0; i<indexofB.size(); i++) {
            xStar.set(indexofB.get(i),0,xUnordered.get(i,0));
        }
        for (int i=0; i<indexofN.size(); i++) {
            xStar.set(indexofN.get(i),0,xUnordered.get(i+indexofB.size(),0));
        }
        return xStar;
    }
    protected void updateParams() {
        generateBase();
        generateGamma();
    }
    private void generateGamma() {
        gamma = (cN.transpose().minus(cB.transpose().mult(B.invert().mult(N)))).transpose();
    }
    protected void generateBase() {
        B = null;
        N = null;
        cB = new SimpleMatrix(m, 1);
        cN = new SimpleMatrix(n-m,1);
        for (int i=0; i<indexofB.size();i++) {
            if (B == null) B = A.extractVector(false,indexofB.get(i));
            else B = B.concatColumns(A.extractVector(false,indexofB.get(i)));
            cB.set(i,0,c.get(indexofB.get(i),0));
        }
        for (int j=0; j<indexofN.size();j++) {
            if (N == null) N = A.extractVector(false,indexofN.get(j));
            else N = N.concatColumns(A.extractVector(false,indexofN.get(j)));
            cN.set(j,0,c.get(indexofN.get(j),0));
        }
    }
    protected void printStatus() {
        System.out.println(
                "Current Base:\n"
                + B.toString()
                + "Current Gamma:\n"
                + gamma.toString()
                + "Indexes in Base: "
                + indexofB.toString()
                + "\nIndexes not in Base: "
                + indexofN.toString()
        );
    }
    private boolean optimalityCriterion() {
        for (int i=0; i<gamma.numRows(); i++) {
            if (gamma.get(i,0) < 0) return false;
        }
        return true;
    }
    private boolean unlimitednessCriterion() {
        SimpleMatrix BinvertedN = B.invert().mult(N);
        for (int i=0; i<gamma.numRows(); i++) {
            if (
                    gamma.get(i,0) < 0
                    && isMatrixNonPositive(BinvertedN.extractVector(false,i))
            ) {
                return true;
            }
        }
        return false;
    }
    private boolean checkAmmissibility() {
        if (
                A.numRows() == m &&
                A.numCols() == n &&
                b.numRows() == m &&
                c.numRows() == n
        ) {
            return true;
        } else {
            A.print();
            b.print();
            c.print();
            System.out.println("Some sizes don't match, Dimension error");
            return false;
        }

    }

    public static int indexOfMinimum(SimpleMatrix v) {
        Vector<Double> c = new Vector<>();
        for (int i=0; i<v.numRows(); i++) {
            c.add(v.get(i,0));
        }

        return c.indexOf(findMinimum(c));
    }
    public static double findMinimum(Vector<Double> v) {
        double min = Double.POSITIVE_INFINITY;
        for (Double aDouble : v) {
            min = Double.min(aDouble, min);
        }
        return min;
    }
    public static boolean isMatrixNonPositive(SimpleMatrix m) {
        for (int ii=0; ii<m.numRows()*m.numCols(); ii++) {
            if (m.get(ii)>0) return false;
        }
        return true;
    }
    public static SimpleMatrix convertArrayToSimpleMatrix(double[] a, boolean horizontal) {
        int n = (horizontal?1:a.length);
        int m = (horizontal?a.length:1);
        SimpleMatrix matrix = new SimpleMatrix(n,m);
        if (horizontal) {
            matrix.setRow(0,0,a);
        } else {
            matrix.setColumn(0,0,a);
        }
        return matrix;
    }
}
