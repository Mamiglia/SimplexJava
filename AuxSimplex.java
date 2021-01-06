import org.ejml.simple.SimpleMatrix; // must use v0.33 because recent versions don't have any documnetation

import java.util.ArrayList;
import java.util.List;

class AuxSimplex extends Simplex {
    private ArrayList<Integer> indexofAux;
    private ArrayList<Integer> indexofRedundant;

    public AuxSimplex(SimpleMatrix A, SimpleMatrix b) {
        // It doesn't check if there are already some identity columns
        super(
                cAux(A.numCols(), A.numRows()),
                A.concatColumns(SimpleMatrix.identity(A.numRows())),
                b);
        indexofRedundant = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getIndexBase() {
        System.out.println(indexofAux.toString());
        int columnToBeExited = findAuxCol(indexofB);
        while (columnToBeExited>=0) {
            System.out.println("Column to be exited: " + columnToBeExited);

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
                System.out.println("Redundant row found: "+ columnToBeEntered);
                indexofRedundant.add(columnToBeEntered);
                indexofB.remove(columnToBeEntered);
                B.reshape(m-1,m-1);
                N.reshape(m-1, N.numCols());
            }
            SimpleMatrix BinvertedN = B.invert().mult(N);

            columnToBeExited = findAuxCol(indexofB);
        }
        return (ArrayList<Integer>)  indexofB.clone();
    }

    @Override
    protected void initialize() {
        indexofB = new ArrayList<>();
        indexofN = new ArrayList<>();
        for (int i=0; i<m; i++) {
            indexofB.add(n-m+i);
        }
        indexofAux = (ArrayList<Integer>) indexofB.clone();
        for (int i=0; i<n-m; i++) {
            indexofN.add(i);
        }
        updateParams();

    }
    public ArrayList<Integer> getIndexofRedundant() {
        return indexofRedundant;
    }
    private int findAuxCol(List<Integer> indexof) {
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
