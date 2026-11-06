package model;

public final class Constraint {
    private final double[] coefficients;
    private final Relation relation;
    private final double rhs;

    public Constraint(double[] coefficients, Relation relation, double rhs) {
        if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Строка коэффициентов не может быть пустой");
        }
        for (double coef : coefficients) {
            if (!Double.isFinite(coef)) {
                throw new IllegalArgumentException("Коэффициенты должны быть конечными");
            }
        }
        if (relation == null) {
            throw new IllegalArgumentException("Знак сравнения не может быть null");
        }
        if (!Double.isFinite(rhs)) {
            throw new IllegalArgumentException("Правая часть уравнения должна быть конечной");
        }

        this.coefficients = coefficients.clone();
        this.relation = relation;
        this.rhs = rhs;
    }

    public Constraint(Constraint other) {
        if (other == null) {
            throw new IllegalArgumentException("Объект для копии не может null");
        }

        this.coefficients = other.coefficients.clone();
        this.relation = other.relation;
        this.rhs = other.rhs;
    }

    public int getCoefficientCount() {
        return coefficients.length;
    }

    public double[] getCoefficients() {
        return coefficients.clone();
    }

    public Relation getRelation() {
        return relation;
    }

    public double getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (double coef : coefficients) {
            sb.append(coef).append("|");
        }
        sb.append(" ").append(relation.getSymbol());
        sb.append(rhs);

        return sb.toString();
    }
}
