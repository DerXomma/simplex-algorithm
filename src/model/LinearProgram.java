package model;

import java.util.ArrayList;
import java.util.List;

public final class LinearProgram {
    private final Sense sense;
    private final double[] objectiveCoefficients;
    private final List<Constraint> constraints;

    public LinearProgram(Sense sense, double[] objectiveCoefficients, List<Constraint> constraints) {
        if (sense == null) {
            throw new IllegalArgumentException("Тип оптимизации не должен быть NULL");
        }
        if (objectiveCoefficients == null || objectiveCoefficients.length == 0) {
            throw new IllegalArgumentException("Строка коэффициентов не может быть пустой");
        }
        for (double coef : objectiveCoefficients) {
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
            if (consCoefs != objectiveCoefficients.length) {
                throw new IllegalArgumentException("Размерность ограничения (" + consCoefs + ") должна совпадать с размерностью целевой функции (" + objectiveCoefficients.length + ")");
            }
        }

        this.sense = sense;
        this.objectiveCoefficients = objectiveCoefficients.clone();
        this.constraints = List.copyOf(constraints);
    }

    public Sense getSense() {
        return sense;
    }

    public double[] getObjectiveCoefficients() {
        return objectiveCoefficients.clone();
    }

    public List<Constraint> getConstraints() {
        return new ArrayList<>(constraints);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (double coef : objectiveCoefficients) {
            sb.append(coef).append("|");
        }

        sb.append("->").append(sense);

        for (Constraint cons : constraints) {
            sb.append("\n").append(cons);
        }

        return sb.toString();
    }
}
