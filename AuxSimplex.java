package com.company;

import org.ejml.simple.SimpleMatrix;
import java.util.Vector;

class AuxSimplex extends Simplex {
    private Vector<Integer> indexofAux;



    private Vector<Integer> indexofRedundant;

    public AuxSimplex(SimpleMatrix A, SimpleMatrix b) {
        // It doesn't check if there are already some identity columns
        super(
                cAux(A.numCols(), A.numRows()),
                A.concatColumns(SimpleMatrix.identity(A.numRows())),
                b);
        indexofRedundant = new Vector<>();
    }

    public Vector<Integer> getIndexBase() {
        System.out.println(indexofAux.toString());
        int columnToBeExited = findAuxCol(indexofB);
        while (columnToBeExited>=0) {
            System.out.println("Column to be exited: " + columnToBeExited);
            System.out.println(indexofB.toString());
            System.out.println(indexofN.toString());

            int i=0;
            int columnToBeEntered = indexofN.get(i);
            while (indexofAux.contains(columnToBeEntered)) {
                columnToBeEntered = indexofN.get(i);
                i++;
            }
            System.out.println("column to enter (K) : " + columnToBeEntered);

            indexofB.set(indexofB.indexOf(columnToBeExited), columnToBeEntered);
            indexofN.set(indexofN.indexOf(columnToBeEntered), columnToBeExited);
            generateBase();
            if (B.determinant() == 0) {
                B.print();
                N.print();
                indexofRedundant.add(columnToBeEntered);
                indexofB.remove(columnToBeEntered);
                B.reshape(m-1,m-1);
                N.reshape(m-1, N.numCols());
                B.print();
                N.print();

            }
            SimpleMatrix BinvertedN = B.invert().mult(N);
            BinvertedN.print();


            columnToBeExited = findAuxCol(indexofB);
        }
        return (Vector<Integer>) indexofB.clone();
    }

    @Override
    protected void initialize() {
        indexofB = new Vector<>();
        indexofN = new Vector<>();
        for (int i=0; i<m; i++) {
            indexofB.add(n-m+i);
        }
        indexofAux = (Vector<Integer>) indexofB.clone();
        for (int i=0; i<n-m; i++) {
            indexofN.add(i);
        }
        updateParams();

    }
    public Vector<Integer> getIndexofRedundant() {
        return indexofRedundant;
    }
    private int findAuxCol(Vector indexof) {
        for (int i=0; i<indexofAux.size(); i++) {
            if (indexof.contains(indexofAux.get(i))) return indexofAux.get(i);
        }
        return -1;
    }
    static private SimpleMatrix cAux(int n, int m) {
        // creates the auxiliary object function (1,1,1,1,1,...,1)
        SimpleMatrix cAux = new SimpleMatrix(m, 1);
        cAux.set(1);
        return (new SimpleMatrix(n, 1)).concatRows(cAux);

    }
}
