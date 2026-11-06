package solve;

import model.Sense;

public final class SimplexSolver {
    private static final int MAX_ITER = 10000;
    private static final double EPS = 1e-12;

    public Solution solve(StandardForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Канонический вид не может быть null");
        }

        // Приводим задачу к внутренней форме, всегда min и искусственные переменные штрафуются на big-M
        Tableau tableau = new Tableau(toSolverForm(form));
        int rowCount = tableau.getRowCount();
        boolean[] artificial = form.getArtificialVariables();

        boolean optimal = false;
        for (int iter = 0; iter < MAX_ITER; iter++) {
            // Ищем столбец, по которому можно улучшить целевую функцию
            int entering = chooseEntering(tableau, artificial);
            if (entering == -1) { // если такого нет, то текущий базис оптимален
                optimal = true;
                break;
            }

            // По правилу минимального отношения определяем, какая базисная переменная выйдет из базиса
            int leaving = chooseLeaving(tableau, entering);
            if (leaving == -1) { // если такого нет, то значит задача неограничена
                return Solution.unbounded();
            }

            tableau.pivot(leaving, entering); // шаг симплекс-метода
        }
        if (!optimal) {
            throw new IllegalStateException("Симплекс-метод не сошелся за " + MAX_ITER + " итераций");
        }

        // Проверка: если какая-то искусственная переменная осталась больше 0, то значит, исходные ограничения несовместны
        for (int i = 0; i < rowCount; i++) {
            if (artificial[tableau.getBasisVariable(i)] && tableau.getRhs(i) > EPS) {
                return Solution.infeasible();
            }
        }

        // Далее собирается решение
        int n = form.getOriginalVariableCount();
        double[] x = new double[n];
        for(int i = 0; i < rowCount; i++) {
            int b = tableau.getBasisVariable(i);
            if (b < n) {
                x[b] = tableau.getRhs(i);
            }
        }

        double[] cOrig = form.getObjectiveCoefficients();
        double f = 0.0; // пересчет f
        for (int j = 0; j < n; j++) {
            f += cOrig[j] * x[j];
        }
        return Solution.optimal(x, f);
    }

    private int chooseEntering(Tableau tableau, boolean[] artificial) {
        int bestCol = -1; // вернет -1, если положительных Δ_j нет
        double bestDelta = EPS;
        for (int j = 0; j < tableau.getColCount(); j++) {
            if (artificial[j]) {
                continue;
            }
            double d = tableau.getReducedCost(j);
            if (d > bestDelta) {
                bestDelta = d;
                bestCol = j;
            }
        }
        return bestCol;
    }

    private int chooseLeaving(Tableau tableau, int enteringCol) {
        int bestRow = -1; // вернет -1, если если все коэффициенты в столбце меньше или равны 0
        double bestRatio = Double.POSITIVE_INFINITY;
        for (int i = 0; i < tableau.getRowCount(); i++) {
            double a = tableau.getCoefficient(i, enteringCol);
            if (a > EPS) {
                double ratio = tableau.getRhs(i) / a;
                if (ratio < bestRatio - EPS) {
                    bestRatio = ratio;
                    bestRow = i;
                } else if (Math.abs(ratio - bestRatio) <= EPS && bestRow != -1
                        && tableau.getBasisVariable(i) < tableau.getBasisVariable(bestRow)) {
                    bestRow = i;
                }
            }
        }
        return bestRow;
    }

    private StandardForm toSolverForm(StandardForm form) {
        boolean maximize = form.getOriginalSense() == Sense.MAX;
        double[] c = form.getObjectiveCoefficients();
        boolean[] artificial = form.getArtificialVariables();

        double scale = 1.0; // выбирается наибольшим значением из коэффициентов и правых частей
        for (int j = 0; j < c.length; j++) {
            if (maximize) { // перевод задачи max в min
                c[j] = -c[j];
            }
            if (!artificial[j]) {
                scale = Math.max(scale, Math.abs(c[j]));
            }
        }
        double[] rhs = form.getRhs();
        for (double b : rhs) {
            scale = Math.max(scale, Math.abs(b));
        }

        // Штрафуем искусственные переменные на масштабированную M
        double M = 1e9 * scale;
        if (!Double.isFinite(M)) {
            throw new IllegalStateException("M переполнено");
        }

        for (int j = 0; j < c.length; j++) {
            if (artificial[j]) {
                c[j] = M;
            }
        }

        return new StandardForm(
                Sense.MIN,
                form.getOriginalVariableCount(),
                c,
                form.getConstraintMatrix(),
                rhs,
                form.getInitialBasis(),
                artificial
        );
    }
}
