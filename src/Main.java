import io.*;
import model.*;
import solve.*;

import java.util.List;

// Parser -> LinearProgram -> Standardizer -> StandardForm -> SimplexSolver -> Solution

public class Main {
    public static void main(String[] args) {
        LinearProgram lp = new LinearProgram(
                Sense.MIN,
                new double[]{2, -1, 3, -10, 1},
                List.of(
                        new Constraint(new double[]{0, 1, 0, 2, 0}, Relation.EQ, 4),
                        new Constraint(new double[]{1, 0, 2, -1, 0}, Relation.EQ, 4),
                        new Constraint(new double[]{0, 0, -1, -2, 1}, Relation.EQ, 6)
                )
        );
        System.out.println(lp);
    }
}
