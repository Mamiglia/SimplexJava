package com.company;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.Vector;

class AuxSimplex extends Simplex {
    private Vector<Integer> indexofAux;

    public AuxSimplex(SimpleMatrix A, SimpleMatrix b) {
        // It doesn't check if there are already some identity columns
        super(
                cAux(A.numRows()),
                A.concatColumns(SimpleMatrix.identity(A.numRows())),
                b);

        indexofB = new Vector<>();
        indexofN = new Vector<>();
        for (int i=0; i<m; i++) {
            indexofB.add(n-m+i);
        }
        indexofAux = (Vector<Integer>) indexofB.clone();
        for (int i=0; i<n-m; i++) {
            indexofN.add(i);
        }




    }

    private void getRidOfAuxColumns() {
        int columnToBeExited = areThereAuxColumnsInB();
        while (columnToBeExited>=0) {
            int k = indexofAux.get(columnToBeExited);
            SimpleMatrix BinvertedN = B.invert().mult(N);
            if ()

            columnToBeExited = areThereAuxColumnsInB();
        }

    }

    private int areThereAuxColumnsInB() {
        for (int i=0; i<indexofAux.size(); i++) {
            if (indexofB.contains(indexofAux.get(i))) return i;
        }
        return -1;
    }
    static private SimpleMatrix cAux(int m) {
        // creates the auxiliary object function (1,1,1,1,1,...,1)
        SimpleMatrix cAux = new SimpleMatrix(m, 1);
        cAux.set(1);
        return cAux;

    }


}
