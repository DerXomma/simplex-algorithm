import io.*;
import model.*;
import solve.*;

import java.util.List;

// Parser -> LinearProgram -> Standardizer -> StandardForm -> SimplexSolver -> Solution

public class Main {
    public static void main(String[] args) {
        double[] c = { 2, -1, 3, -10, 1 };
        double[][] A = {
                { 0,  1,  0,  2, 0 },
                { 1,  0,  2, -1, 0 },
                { 0,  0, -1, -2, 1 },
        };
        double[] b = { 4, 4, 6 };
        int[] initialBasis = { 1, 0, 4 };          // x2, x1, x5
        boolean[] artificial = { false, false, false, false, false };
        StandardForm form = new StandardForm( Sense.MIN,5, c, A, b, initialBasis, artificial);
        Tableau tableau = new Tableau(form);
        System.out.println(tableau);
    }
}
