package com.company;

public class Main {

    public static void main(String[] args) {
        // UI main = new UI();
        double[] c = {-1,-1, 0, 0};
        double[] b = {1,1};
        double[][] A = {
                new double[] {1,-2,-1,0},
                new double[] {-1,1,0,-1}
        };
        Simplex s = new Simplex(c, A, b);
        s.solve().print();
        System.out.println(s.getOptimalValue());
    }
}
