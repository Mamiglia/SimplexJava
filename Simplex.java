import java.lang.Double;
import java.lang.Integer;
import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix; // must use v0.33 since recent versions don't have any documentation (or I couldn't find it)


public class Simplex {
    /**
     * Solves minimization problems in the standard form:
     * min c^T x
     *     Ax=b
     *     x>=0
     */
    public final static SimpleMatrix BOUNDLESS_SOLUTION = new SimpleMatrix(new double[][] {{Double.NEGATIVE_INFINITY}});
    public final static SimpleMatrix NONEXISTENT_SOLUTION = new SimpleMatrix(0,0);
    protected final SimpleMatrix objectiveFunction; // c
    protected SimpleMatrix constraintsMatrix; // A
    protected SimpleMatrix constraintsVector; // b
    protected ArrayList<Integer> indexofB; // indexes of variables in the current base.
    protected ArrayList<Integer> indexofN; // indexes of variables left out from base.
    private SimpleMatrix cB; // vector of objective function with only the indexes of B.
    private SimpleMatrix cN; // vector of objective function with only the indexes not in B.
    protected SimpleMatrix base; // matrix with columns indexofB.
    protected SimpleMatrix nonBase; // matrix with columns indexofN.
    private SimpleMatrix gamma; // vector of reduced costs, it's calculated in the generateGamma() method
    private SimpleMatrix optimalSolution;

    /**
     * Creates a new Simplex object which can be used to solve a linear problem
     * @param objectiveFunction the vector representing the coefficients of the objectiveFunction (c), dimensions must be nx1
     * @param constraintsMatrix the matrix representing the constraints (A), dimensions must be mxn
     * @param constraintsVector the vector representing the constraints (b), dimensions must be mx1
     */
    public Simplex(SimpleMatrix objectiveFunction, SimpleMatrix constraintsMatrix, SimpleMatrix constraintsVector) {
        this.objectiveFunction = objectiveFunction;
        this.constraintsMatrix = constraintsMatrix;
        this.constraintsVector = constraintsVector;
    }

    public Simplex(double[] objectiveFunction, double[][] constraintsMatrix, double[] constraintsVector) {
        this(
                convertArrayToSimpleMatrix(objectiveFunction, false),
                new SimpleMatrix(constraintsMatrix),
                convertArrayToSimpleMatrix(constraintsVector, false)
        );
    }


    protected void initialize() {
        System.out.println("Beginning Auxiliar problem");
        AuxSimplex aux = new AuxSimplex(constraintsMatrix, constraintsVector);
        aux.solve();
        if (aux.getOptimalValue() != 0) {
            System.out.println("Auxiliar optimal value is not 0\nThe Problem has no valid solutions");
            optimalSolution = NONEXISTENT_SOLUTION;
            return;
        }
        indexofB = aux.getIndexBase();
        indexofN = new ArrayList<>();
        for (int i = 0; i<numOfVars(); i++) {
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
        for (int i = 0; i<constraintsMatrix.numRows(); i++) {
            if (!indexofRedundant.contains(i)) {
                if (AReducted == null || constraintsVector == null) {
                    AReducted = constraintsMatrix.extractVector(true, i);
                    bReducted = constraintsVector.extractVector(true, i);
                } else {
                    AReducted = AReducted.concatRows(constraintsMatrix.extractVector(true, i));
                    bReducted = bReducted.concatRows(constraintsVector.extractVector(true, i));
                }
            }
        }
        constraintsMatrix = AReducted;
        constraintsVector = bReducted;
    }

    public SimpleMatrix solve() {
        if (!checkAmmissibility()) return null;
        initialize();
        int j = 0;
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

                int h = indexOfMinimum(gamma); //index of N to be entered in B'

                int k; //index of B to be exited from B'
                SimpleMatrix BinvertedNcolumnh = base.invert().mult(nonBase).extractVector(false, h);
                SimpleMatrix Binvertedb = base.invert().mult(constraintsVector);
                ArrayList<Double> roArray = new ArrayList<>();

                //populate roArray
                for (int i = 0; i<BinvertedNcolumnh.numRows(); i++) {
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
        if (optimalSolution.equals(BOUNDLESS_SOLUTION)) {
            return Double.NEGATIVE_INFINITY;
        } else if (optimalSolution.equals(NONEXISTENT_SOLUTION)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return objectiveFunction.transpose().mult(optimalSolution).get(0,0);
        }
    }

    public int numOfConstraints() {
        return constraintsMatrix.numRows();
    }

    public int numOfVars() {
        return constraintsMatrix.numCols();
    }

    private SimpleMatrix generateSBA() {
        SimpleMatrix xB = base.invert().mult(constraintsVector);
        SimpleMatrix xN = new SimpleMatrix(indexofN.size(), 1);
        SimpleMatrix xUnordered = xB.concatRows(xN);
        SimpleMatrix xStar = new SimpleMatrix(xUnordered.numRows(), 1);

        for (int i = 0; i<indexofB.size(); i++) {
            xStar.set(indexofB.get(i),0,xUnordered.get(i,0));
        }
        for (int i = 0; i<indexofN.size(); i++) {
            xStar.set(indexofN.get(i),0,xUnordered.get(i+indexofB.size(),0));
        }
        return xStar;
    }

    protected void updateParams() {
        generateBase();
        generateGamma();
    }

    private void generateGamma() {
        gamma = (cN.transpose().minus(cB.transpose().mult(base.invert().mult(nonBase)))).transpose();
    }

    protected void generateBase() {
        base = null;
        nonBase = null;
        cB = new SimpleMatrix(numOfConstraints(), 1);
        cN = new SimpleMatrix(numOfVars()-numOfConstraints(),1);
        for (int i = 0; i<indexofB.size();i++) {
            if (base == null) base = constraintsMatrix.extractVector(false,indexofB.get(i));
            else base = base.concatColumns(constraintsMatrix.extractVector(false,indexofB.get(i)));
            cB.set(i,0,objectiveFunction.get(indexofB.get(i),0));
        }
        for (int j = 0; j<indexofN.size();j++) {
            if (nonBase == null) nonBase = constraintsMatrix.extractVector(false,indexofN.get(j));
            else nonBase = nonBase.concatColumns(constraintsMatrix.extractVector(false,indexofN.get(j)));
            cN.set(j,0,objectiveFunction.get(indexofN.get(j),0));
        }
    }
    protected void printStatus() {
        System.out.println(
                "Current Base:\n"
                + base.toString()
                + "Current Gamma:\n"
                + gamma.toString()
                + "Indexes in Base: "
                + indexofB.toString()
                + "\nIndexes not in Base: "
                + indexofN.toString()
        );
    }
    private boolean optimalityCriterion() {
        for (int i = 0; i<gamma.numRows(); i++) {
            if (gamma.get(i,0) < 0) return false;
        }
        return true;
    }

    private boolean unlimitednessCriterion() {
        SimpleMatrix BinvertedN = base.invert().mult(nonBase);
        for (int i = 0; i<gamma.numRows(); i++) {
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
                constraintsVector.numRows() == numOfConstraints() &&
                objectiveFunction.numRows() == numOfVars()
        ) {
            return true;
        } else {
            constraintsMatrix.print();
            constraintsVector.print();
            objectiveFunction.print();
            System.out.println("Some sizes don't match, Dimension error");
            return false;
        }

    }

    public static int indexOfMinimum(SimpleMatrix v) {
        ArrayList<Double> c = new ArrayList<>();
        for (int i=0; i<v.numRows(); i++) {
            c.add(v.get(i,0));
        }

        return c.indexOf(findMinimum(c));
    }

    public static double findMinimum(ArrayList<Double> v) {
        double min = Double.POSITIVE_INFINITY;
        for (Double aDouble : v) {
            min = Double.min(aDouble, min);
        }
        return min;
    }

    public static boolean isMatrixNonPositive(SimpleMatrix m) {
        for (int i = 0; i<m.numRows()*m.numCols(); i++) {
            if (m.get(i)>0) return false;
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
