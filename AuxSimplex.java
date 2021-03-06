import org.ejml.simple.SimpleMatrix; // must use v0.33 since recent versions don't have any documentation

import java.util.ArrayList;

class AuxSimplex extends Simplex {
    /**
     * Creates the auxiliar problem, to find out:
     * 1- whether the problem is non-void
     * 2- find a valid base
     * the auxiliar problem has this form:
     * min (1,1,...,1,1) a
     *     Ax + Ia = b
     *     x>=0
     *     a>=0
     * a are named "Auxiliar Variables"
     */
    private ArrayList<Integer> indexofAux; // indexes of Auxiliar Variables
    private ArrayList<Integer> indexofRedundant; // indexes of constraints which are linear dependent from other

    public AuxSimplex(SimpleMatrix A, SimpleMatrix b) {
        // It doesn't check if there are already some identity columns TODO
        super(
                cAux(A.numCols(), A.numRows()),
                A.concatColumns(SimpleMatrix.identity(A.numRows())),
                b);
        indexofRedundant = new ArrayList<>();
    }

    public ArrayList<Integer> getIndexBase() {
        System.out.println(indexofAux.toString());
        int columnToBeExited = findAuxCol(indexInB);
        while (columnToBeExited>=0) {
            System.out.println("Column to be exited: " + columnToBeExited);

            int i = 0;
            int columnToBeEntered = indexInN.get(i);
            while (indexofAux.contains(columnToBeEntered)) {
                columnToBeEntered = indexInN.get(i++);

            }
            System.out.println("column to enter (K) : " + columnToBeEntered);

            indexInB.set(indexInB.indexOf(columnToBeExited), columnToBeEntered);
            indexInN.set(indexInN.indexOf(columnToBeEntered), columnToBeExited);
            generateBase();
            if (base.determinant() == 0) {
                System.out.println("Redundant row found: "+ columnToBeEntered);
                indexofRedundant.add(columnToBeEntered);
                indexInB.remove(columnToBeEntered);
                base.reshape(numOfConstraints()-1,numOfConstraints()-1);
                nonBase.reshape(numOfConstraints()-1, nonBase.numCols());
            }

            columnToBeExited = findAuxCol(indexInB);
        }
        return (ArrayList<Integer>)  indexInB.clone();
    }

    @Override
    protected void initialize() {
        indexInB = new ArrayList<>();
        indexInN = new ArrayList<>();
        for (int i=0; i<numOfConstraints(); i++) {
            indexInB.add(numOfVars()-numOfConstraints()+i);
        }
        indexofAux = (ArrayList<Integer>) indexInB.clone();
        for (int i=0; i<numOfVars()-numOfConstraints(); i++) {
            indexInN.add(i);
        }
        updateParams();

    }

    public ArrayList<Integer> getIndexofRedundant() {
        return indexofRedundant;
    }

    private int findAuxCol(ArrayList<Integer> indexof) {
        for (Integer i : indexofAux) {
            if (indexof.contains(i)) return i;
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
