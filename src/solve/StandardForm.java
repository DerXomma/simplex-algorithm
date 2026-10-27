package solve;

import model.Sense;

public final class StandardForm {
    private final Sense originalSense;
    private final int originalVariableCount;
    private final double[] objectiveCoefficients;
    private final double[][] constraintMatrix;
    private final double[] rhs;
    private final int[] initialBasis;
    private final boolean[] artificialVariables;

    public StandardForm(Sense originalSense,
                        int originalVariableCount,
                        double[] objectiveCoefficients,
                        double[][] constraintMatrix,
                        double[] rhs,
                        int[] initialBasis,
                        boolean[] artificialVariables) {

        if (originalSense == null) {
            throw new IllegalArgumentException("Тип оптимизации не должен быть null");
        }

        if (objectiveCoefficients == null || objectiveCoefficients.length == 0) {
            throw new IllegalArgumentException("Размерность целевой функции не может быть равно 0");
        }
        for (double c : objectiveCoefficients) {
            if (!Double.isFinite(c)) {
                throw new IllegalArgumentException("Коэффициенты целевой функции должны быть конечными");
            }
        }
        int n = objectiveCoefficients.length;

        if (originalVariableCount <= 0) {
            throw new IllegalArgumentException("Первоначальное число переменных должно быть больше 0");
        }
        if (originalVariableCount > n) {
            throw new IllegalArgumentException("Число исходных переменных (" + originalVariableCount + ") не может быть больше общего числа переменных (" + n + ")");
        }

        if (constraintMatrix == null || constraintMatrix.length == 0) {
            throw new IllegalArgumentException("Размерность ограничений не может быть равна 0");
        }
        int m = constraintMatrix.length;
        for (double[] constraint : constraintMatrix) {
            if (constraint == null) {
                throw new IllegalArgumentException("Строка коэффициентов не может быть пустой");
            }
            if (constraint.length != n) {
                throw new IllegalArgumentException("Длина строки коэффициентов (" + constraint.length + ") не равна ожидаемой (" + n + ")");
            }
        }

        if (rhs == null) {
            throw new IllegalArgumentException("Правая часть уравнения не может быть null");
        }
        if (rhs.length != m) {
            throw new IllegalArgumentException("Количество правых частей (" + rhs.length + ") не равна ожидаемой (" + m + ")");
        }
        for (double coef : rhs) {
            if (coef < 0) {
                throw new IllegalArgumentException("Значения в правой части должны быть неотрицательными");
            }
        }

        if (initialBasis == null) {
            throw new IllegalArgumentException("Начальные базисы не могут быть null");
        }
        if (initialBasis.length != m) {
            throw new IllegalArgumentException("Размер начального базиса (" + initialBasis.length + ") не равен ожидаемой (" + m + ")");
        }
        boolean[] seen = new boolean[n];
        for (int idx : initialBasis) {
            if (idx < 0 || idx >= n) {
                throw new IllegalArgumentException("Индекс базисной переменной " + idx + " вне диапазона от [0 до " + n + ")");
            }
            if (seen[idx]) {
                throw new IllegalArgumentException("Индекс " + idx + " повторяется в начальном базисе");
            }
            seen[idx] = true;
        }

        if (artificialVariables == null) {
            throw new IllegalArgumentException("Искусственные переменные не могут быть null");
        }
        if (artificialVariables.length != n) {
            throw new IllegalArgumentException("Размер массива искусственных переменных (" + artificialVariables.length + ") не равен (" + n + ")");
        }

        this.originalSense = originalSense;
        this.originalVariableCount = originalVariableCount;
        this.objectiveCoefficients = objectiveCoefficients.clone();
        this.constraintMatrix = deepCopy(constraintMatrix);
        this.rhs = rhs.clone();
        this.initialBasis = initialBasis.clone();
        this.artificialVariables = artificialVariables.clone();
    }

    private static double[][] deepCopy(double[][] source) {
        double[][] copy = new double[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    public Sense getOriginalSense() {
        return originalSense;
    }

    public int getOriginalVariableCount() {
        return originalVariableCount;
    }

    public double[] getObjectiveCoefficients() {
        return objectiveCoefficients.clone();
    }

    public double[][] getConstraintMatrix() {
        return deepCopy(constraintMatrix);
    }

    public double[] getRhs() {
        return rhs.clone();
    }

    public int[] getInitialBasis() {
        return initialBasis.clone();
    }

    public boolean[] getArtificialVariables() {
        return artificialVariables.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("f= ");
        for (double coef : objectiveCoefficients) {
            sb.append(coef).append("|");
        }
        sb.append(" ->").append(originalSense).append("\n");

        for (int i = 0; i < constraintMatrix.length; i++) {

            for (int j = 0; j < objectiveCoefficients.length; j++) {
                sb.append(constraintMatrix[i][j]).append("|");
            }
            sb.append(" =").append(rhs[i]).append("\n");
        }
        sb.append("\n");

        sb.append("initialBasis: ");
        for (int idx : initialBasis) {
            sb.append(idx).append("|");
        }
        sb.append("\n");

        sb.append("artificialVariables: ");
        for (boolean idx : artificialVariables) {
            sb.append(idx).append(" ");
        }
        sb.append("\n");

        sb.append("originalVariableCount: ").append(originalVariableCount);
        return sb.toString();
    }
}
