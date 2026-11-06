import io.*;
import model.*;
import solve.*;

// Parser -> LinearProgram -> Standardizer -> StandardForm -> SimplexSolver -> Solution

public class Main {
    public static void main(String[] args) {
        LinearProgram lp = new LinearProgram(
                Sense.MIN,
                new double[]{2, -1, 3, -10, 1},
                new Constraint[]{
                        new Constraint(new double[]{0, 1, 0, 2, 0}, Relation.EQ, 4),
                        new Constraint(new double[]{1, 0, 2, -1, 0}, Relation.EQ, 4),
                        new Constraint(new double[]{0, 0, -1, -2, 1}, Relation.EQ, 6)
                }
        );
        System.out.println("\n" + lp);

        Standardizer standardizer = new Standardizer();
        StandardForm form = standardizer.standardize(lp);
        System.out.println("\n" + form);

        SimplexSolver solver = new SimplexSolver();
        Solution solution = solver.solve(form);
        System.out.println("\n" + solution);
    }
}
