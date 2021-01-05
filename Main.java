package com.company;

public class Main {

    public static void main(String[] args) {
        // UI main = new UI();
        double[] c = {2,3,1};
        double[] b = {2,1, 3};
        double[][] A ={
                new double[] {1,1,1},
                new double[] {-1,2,0},
                new double[] {0,3,1}
        };
        Simplex s = new Simplex(c, A, b);
        s.solve().print();
        System.out.println(s.getOptimalValue());
    }
}
