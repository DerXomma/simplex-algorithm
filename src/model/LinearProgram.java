package model;

import java.util.ArrayList;
import java.util.List;

public final class LinearProgram {
    private final Sense sense;
    private final double[] coefficients;
    private final List<Constraint> constraints;

    public LinearProgram(Sense sense, double[] coefficients, List<Constraint> constraints) {
        if (sense == null) {
            throw new IllegalArgumentException("Тип оптимизации не должен быть NULL");
        }
        if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Строка коэффициентов не может быть пустой");
        }
        for (double coef : coefficients) {
            if (!Double.isFinite(coef)) {
                throw new IllegalArgumentException("Коэффициенты должны быть конечными");
            }
        }
        if (constraints == null || constraints.isEmpty()) {
            throw new IllegalArgumentException("Набор ограничений не может быть пустым");
        }
        for (Constraint cons : constraints) {
            if (cons == null) {
                throw new IllegalArgumentException("Ограничение в наборе не может быть пустым");
            }
            int consCoefs = cons.getCoefficients().length;
            if (consCoefs != coefficients.length) {
                throw new IllegalArgumentException("Размерность ограничения (" + consCoefs + ") должна совпадать с размерностью целевой функции (" + coefficients.length + ")");
            }
        }

        this.sense = sense;
        this.coefficients = coefficients.clone();
        this.constraints = List.copyOf(constraints);
    }

    public Sense getSense() {
        return sense;
    }

    public double[] getCoefficients() {
        return coefficients.clone();
    }

    public List<Constraint> getConstraints() {
        return new ArrayList<>(constraints);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("f= ");
        for (double coef : coefficients) {
            sb.append(coef).append("|");
        }

        sb.append(" ->").append(sense);

        for (Constraint cons : constraints) {
            sb.append("\n").append(cons);
        }

        return sb.toString();
    }
}
