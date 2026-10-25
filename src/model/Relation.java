package model;

public enum Relation {
    EQ("="),
    LE("<="),
    GE(">=");

    private final String symbol;

    Relation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
