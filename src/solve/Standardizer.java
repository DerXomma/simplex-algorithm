package solve;

import model.*;

public final class Standardizer {

    private static final double EPS = 1e-12;

    public StandardForm standardize(LinearProgram lp) {
        if (lp == null) {
            throw new IllegalArgumentException("Линейная программа не может быть null");
        }

        double[] cOrig = lp.getCoefficients();
        int n = cOrig.length; // число исходных переменных
        int m = lp.getConstraints().length; // число ограничений

        double[][] coeffs = new double[m][];
        double[] rhs = new double[m];
        Relation[] relations = new Relation[m];
        normalizeConstraints(lp.getConstraints(), n, m, coeffs, rhs, relations);

        // Ищем столбцы в ограничениях, которые уже являются единичными
        int[] unitCol = findUnitColumns(coeffs, relations, n, m);
        int extra = countExtraVariables(relations, unitCol); // сколько новых переменных добавляется

        int nTotal = n + extra;
        double[][] matrix = new double[m][nTotal];
        double[] c = new double[nTotal];
        int[] initialBasis = new int[m];
        boolean[] artificial = new boolean[nTotal];

        // Коэффициенты исходной целевой функции остаются в первых n столбцах, дополнительные 0, искусственные M
        System.arraycopy(cOrig, 0, c, 0, n);
        buildMatrix(coeffs, relations, unitCol, n, m, matrix, initialBasis, artificial);

        return new StandardForm(lp.getSense(), n, c, matrix, rhs, initialBasis, artificial);
    }

    private static void normalizeConstraints(Constraint[] constraints, int n, int m,
                                             double[][] coeffs, double[] rhs,
                                             Relation[] relations) {
        for (int i = 0; i < m; i++) {
            Constraint cons = constraints[i];
            Relation rel = cons.getRelation();
            double[] a = cons.getCoefficients();
            double b = cons.getRhs();

            if (b < -EPS) { // если правая сторона отрицательная, умножаем на -1 и меняем знак сравнения
                for (int j = 0; j < n; j++) {
                    a[j] = -a[j];
                }
                b = -b;
                rel = flip(rel);
            } else if (b < 0) { // обнуляем совсем маленькие значения от -EPS до 0
                b = 0.0;
            }

            coeffs[i] = a;
            rhs[i] = b;
            relations[i] = rel;
        }
    }

    private static int[] findUnitColumns(double[][] coeffs, Relation[] relations, int n, int m) {
        int[] unitCol = new int[m];
        for (int i = 0; i < m; i++) {
            unitCol[i] = -1;
        }

        for (int i = 0; i < m; i++) {
            if (relations[i] == Relation.LE) {
                continue; // для меньше-или-равно столбец дополнительной переменной всегда дает единичный
            }
            for (int j = 0; j < n; j++) {
                if (isUnitColumn(coeffs, i, j, m)) {
                    unitCol[i] = j;
                    break; // ищем первый подходящий столбец
                }
            }
        }
        return unitCol;
    }

    // Проверка, что столбец col является единичным для строки row
    private static boolean isUnitColumn(double[][] coeffs, int row, int col, int m) {
        for (int k = 0; k < m; k++) {
            if (k != row && Math.abs(coeffs[k][col]) > EPS) {
                return false;
            }
        }
        return true;
    }

    private static int countExtraVariables(Relation[] relations, int[] unitCol) {
        int extra = 0;
        for (int i = 0; i < relations.length; i++) {
            extra += switch (relations[i]) {
                case LE -> 1;
                case GE -> unitCol[i] == -1 ? 2 : 1;
                case EQ -> unitCol[i] == -1 ? 1 : 0;
            };
        }
        return extra;
    }

    // Заполняет матрицу ограничений, начальный базис и флаги искусственных переменных, начиная с n
    private static void buildMatrix(double[][] coeffs, Relation[] relations, int[] unitCol,
                                    int n, int m, double[][] matrix,
                                    int[] initialBasis, boolean[] artificial) {
        int nextCol = n;
        for (int i = 0; i < m; i++) {
            System.arraycopy(coeffs[i], 0, matrix[i], 0, n);

            switch (relations[i]) {
                case LE -> {
                    matrix[i][nextCol] = 1.0; // всегда дополнительная есть
                    initialBasis[i] = nextCol++; // двигаемся к следующему столбцу
                }
                case GE -> {
                    matrix[i][nextCol++] = -1.0;
                    if (unitCol[i] != -1) { // если для строки i есть единичный столбец
                        initialBasis[i] = unitCol[i];
                    } else { // иначе делаем искусственную
                        matrix[i][nextCol] = 1.0;
                        artificial[nextCol] = true;
                        initialBasis[i] = nextCol++;
                    }
                }
                case EQ -> {
                    if (unitCol[i] != -1) {
                        initialBasis[i] = unitCol[i];
                    } else {
                        matrix[i][nextCol] = 1.0;
                        artificial[nextCol] = true;
                        initialBasis[i] = nextCol++;
                    }
                }
            }
        }
    }

    private static Relation flip(Relation rel) {
        return switch (rel) {
            case LE -> Relation.GE;
            case GE -> Relation.LE;
            case EQ -> Relation.EQ;
        };
    }
}
