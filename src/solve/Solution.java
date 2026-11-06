package solve;

import model.Status;

public final class Solution {
    private final Status status;
    private final double[] variables;
    private final double objectiveValue;

    private Solution(Status status, double[] variables, double objectiveValue) {
        this.status = status;
        this.variables = variables.clone();
        this.objectiveValue = objectiveValue;
    }

    public static Solution optimal(double[] x, double f) {
        return new Solution(Status.OPTIMAL, x, f);
    }

    public static Solution unbounded() {
        return new Solution(Status.UNBOUNDED, new double[0], Double.NaN);
    }

    public static Solution infeasible() {
        return new Solution(Status.INFEASIBLE, new double[0], Double.NaN);
    }

    public Status getStatus() {
        return status;
    }

    public double[] getVariables() {
        return variables.clone();
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    @Override
    public String toString() {
        if (status != Status.OPTIMAL) {
            return "Status: " + status;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Status: ").append(status);
        sb.append("\nx*= ");
        for (double v : variables) {
            sb.append(v).append("|");
        }
        sb.append("\nf= ").append(objectiveValue);

        return sb.toString();
    }
}
