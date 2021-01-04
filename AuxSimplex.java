package com.company;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.Vector;

class AuxSimplex extends Simplex {

    private AuxSimplex(SimpleMatrix A, SimpleMatrix b) {
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
        for (int i=0; i<n-m; i++) {
            indexofN.add(i);
        }




    }

    static private SimpleMatrix cAux(int m) {
        // creates the auxiliary object function (1,1,1,1,1,...,1)
        SimpleMatrix cAux = new SimpleMatrix(m, 1);
        cAux.set(1);
        return cAux;

    }


}
