package com.company;

import java.lang.*;
import java.util.Vector;

import org.ejml.simple.*;

public class Simplex {
    public final static SimpleMatrix BOUNDLESS_SOLUTION = new SimpleMatrix(new double[][] {{Double.POSITIVE_INFINITY}});
    public final static SimpleMatrix INEXISTENT_SOLUTION = new SimpleMatrix();
    private final SimpleMatrix c;
    private final SimpleMatrix A;
    private final SimpleMatrix b;
    private final int m;
    private final int n;
    private Vector<Integer> indexofB;
    private Vector<Integer> indexofN;
    private SimpleMatrix cN;
    private SimpleMatrix cB;
    private SimpleMatrix B;
    private SimpleMatrix N;
    private SimpleMatrix gamma;
    private SimpleMatrix optimalSolution;

    public Simplex(double[] c, double[][] A, double[] b) {
        this.c = convertArrayToSimpleMatrix(c, false);
        this.A = new SimpleMatrix(A);
        this.b = convertArrayToSimpleMatrix(b, false);
        n = this.A.numCols();
        m = this.A.numRows();

        indexofB = new Vector<>();
        indexofB.add(3);
        indexofB.add(4);
        indexofB.add(5);

        indexofN = new Vector<>();
        indexofN.add(0);
        indexofN.add(1);
        indexofN.add(2);

        //temporary choice
        System.out.println("min ");
        System.out.println("Problem acquired, ready to Start");
    }

    private Simplex(SimpleMatrix A, SimpleMatrix b, boolean auxiliary) {
        m = A.numRows();
        n = A.numCols() + m;

        // It doesn't check if there are already some identity columns
        this.A = A.concatColumns(SimpleMatrix.identity(m));
        this.c = new SimpleMatrix(m, 1);
        this.c.set(1);
        this.b = b;

        indexofB = new Vector<>();
        indexofN = new Vector<>();
        for (int i=0; i<m; i++) {
            indexofB.add(n-m+i);
        }
        for (int i=0; i<n-m; i++) {
            indexofN.add(i);
        }




    }

    public void initialize() {
        Simplex aux = new Simplex(A, b, true);
        aux.solve();
        if (aux.getOptimalValue() != 0) {
            optimalSolution = INEXISTENT_SOLUTION;
        }
        
    }

    public SimpleMatrix solve() {
        int j=0;
        while (optimalSolution == null) {
            System.out.println("\n\nIteration " + j);
            updateParams();
            B.print();
            gamma.print();
            System.out.println(indexofB);
            System.out.println(indexofN);

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
        return optimalSolution;
    }
    public double getOptimalValue() {
        if (optimalSolution.equals(BOUNDLESS_SOLUTION)) return Double.NEGATIVE_INFINITY;
        if (optimalSolution.equals(INEXISTENT_SOLUTION)) return Double.POSITIVE_INFINITY;
        return c.transpose().mult(optimalSolution).get(0,0);
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
    private void updateParams() {
        generateBase();
//        B.print();
//        N.print();
//        cB.print();
//        cN.print();
        generateGamma();
    }
    private void generateGamma() {
        gamma = (cN.transpose().minus(cB.transpose().mult(B.invert().mult(N)))).transpose();
    }
    private void generateBase() {
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

    public static double[][] matrix2Array(SimpleMatrix matrix) {
        double[][] array = new double[matrix.numRows()][matrix.numCols()];
        for (int r = 0; r < matrix.numRows(); r++) {
            for (int c = 0; c < matrix.numCols(); c++) {
                array[r][c] = matrix.get(r, c);
            }
        }
        return array;
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
        for (int i=0; i<v.size(); i++) {
            min = Double.min(v.get(i), min);
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
