package solve;

public final class Tableau {
    private double[][] coefficients;
    private double[] rhs;
    private int[] basis;
    private double[] reducedCost;
    private double objectiveValue;

    public Tableau(StandardForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Канонический вид не может быть null");
        }

        coefficients = form.getConstraintMatrix();
        rhs = form.getRhs();
        basis = form.getInitialBasis();

        double[] objectiveCoefficients = form.getObjectiveCoefficients(); // c - вектор коэффициентов целевой функции
        reducedCost = new double[objectiveCoefficients.length]; // Δ_j для всех j
        for (int j = 0; j < reducedCost.length; j++) {
            double zj = 0.0D; // промежуточное значение
            for (int i = 0; i < coefficients.length; i++) {
                zj += objectiveCoefficients[basis[i]] * coefficients[i][j]; // z_j = Σ_i( c_{B_i} * a_{ij} ), где a_{ij} = (B⁻¹P_j)_i, то есть число из таблицы
            }
            reducedCost[j] = zj - objectiveCoefficients[j]; // Δ_j = z_j - c_j
        }

        objectiveValue = 0.0D; // f - значение целевой функции
        for (int i = 0; i < rhs.length; i++) {
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

    }

    public int getRowCount() {
        return coefficients[0].length;
    }

    public int getColCount() {
        return rhs.length;
    }

    public double getCoefficients(int row, int col) {
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
}
