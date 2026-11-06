package solve;

public final class Tableau {
    private final double[][] coefficients;
    private final double[] rhs;
    private final int[] basis;
    private final double[] reducedCost;
    private double objectiveValue;

    private static final double EPS = 1e-12;

    public Tableau(StandardForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Канонический вид не может быть null");
        }

        coefficients = form.getConstraintMatrix();
        rhs = form.getRhs();
        basis = form.getInitialBasis();

        int m = coefficients.length; // количество строк
        int n = coefficients[0].length; // длина строки

        double[] objectiveCoefficients = form.getObjectiveCoefficients(); // c - вектор коэффициентов целевой функции
        reducedCost = new double[n]; // Δ_j для всех j
        for (int j = 0; j < n; j++) {
            double zj = 0.0D; // промежуточное значение
            for (int i = 0; i < m; i++) {
                zj += objectiveCoefficients[basis[i]] * coefficients[i][j]; // z_j = Σ_i( c_{B_i} * a_{ij} ), где a_{ij} = (B⁻¹P_j)_i, то есть число из таблицы
            }
            reducedCost[j] = zj - objectiveCoefficients[j]; // Δ_j = z_j - c_j
        }

        objectiveValue = 0.0D; // f - значение целевой функции
        for (int i = 0; i < m; i++) {
            objectiveValue += objectiveCoefficients[basis[i]] * rhs[i]; // z_0 = Σ_i( c_{B_i} * rhs_i ), где rhs_i = (B⁻¹P_0)_i, то есть правая сторона
        }
    }

    /**
     * Метод pivot выполняет один шаг пересчёта симплекс-таблицы.
     * После вызова новая таблица описывает ту же систему уравнений относительно нового базиса.
     * @param pivotRow номер строки, базисная переменная которой выводится из базиса.
     * @param pivotCol номер столбца, переменная которого становится базисной.
     */
    public void pivot(int pivotRow, int pivotCol) {
        int m = coefficients.length; // количество строк
        int n = coefficients[0].length; // длина строки

        if (pivotRow < 0 || pivotRow >= m) {
            throw new IllegalArgumentException("Некорректная ведущая строка: " + pivotRow);
        }
        if (pivotCol < 0 || pivotCol >= n) {
            throw new IllegalArgumentException("Некорректный ведущий столбец: " + pivotCol);
        }

        double pivot = coefficients[pivotRow][pivotCol];
        if (!(pivot > EPS)) {
            throw new IllegalArgumentException("Некорректный ведущий элемент: " + pivot);
        }

        // Нормируем ведущую строку: делим коэффициенты и правую сторону на pivot
        for (int j = 0; j < n; j++) {
            coefficients[pivotRow][j] /= pivot;
        }
        rhs[pivotRow] /= pivot;
        if (Math.abs(rhs[pivotRow]) < EPS) {
            rhs[pivotRow] = 0.0;
        }

        // Меняем базис
        basis[pivotRow] = pivotCol;

        // Обнуляем ведущий столбец
        for (int i = 0; i < m; i++) {
            if (i == pivotRow) { // пропускаем базисную строку
                continue;
            }

            double factor = coefficients[i][pivotCol];
            if (Math.abs(factor) < EPS) { // пропускаем 0
                coefficients[i][pivotCol] = 0.0;
                continue;
            }

            for (int j = 0; j < n; j++) {
                coefficients[i][j] -= factor * coefficients[pivotRow][j];
            }
            rhs[i] -= factor * rhs[pivotRow];
            if (Math.abs(rhs[i]) < EPS) {
                rhs[i] = 0.0;
            }
        }

        // Пересчитываем строку Δ и значение f
        double pivotDelta = reducedCost[pivotCol];
        if (Math.abs(pivotDelta) > EPS) {
            for (int j = 0; j < n; j++) {
                reducedCost[j] -= pivotDelta * coefficients[pivotRow][j];
            }
            objectiveValue -= pivotDelta * rhs[pivotRow];
        } else {
            reducedCost[pivotCol] = 0.0;
        }
    }

    public int getRowCount() {
        return coefficients.length;
    }

    public int getColCount() {
        return coefficients[0].length;
    }

    public double getCoefficient(int row, int col) {
        return coefficients[row][col];
    }

    public double getRhs(int row) {
        return rhs[row];
    }

    public int getBasisVariable(int row) {
        return basis[row];
    }

    public double getReducedCost(int col) {
        return reducedCost[col];
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("B: ");
        for (int idx : basis) {
            sb.append("P").append(idx + 1).append("|");
        }
        sb.append("\n");

        for (int i = 0; i < coefficients.length; i++) {
            for (int j = 0; j < coefficients[i].length; j++) {
                sb.append(coefficients[i][j]).append("|");
            }
            sb.append(" =").append(rhs[i]).append("\n");
        }
        sb.append("\n");

        sb.append("Δ: ");
        for (double rc : reducedCost) {
            sb.append(rc).append("|");
        }
        sb.append("\n");

        sb.append("f= ").append(objectiveValue);

        return sb.toString();
    }
}
