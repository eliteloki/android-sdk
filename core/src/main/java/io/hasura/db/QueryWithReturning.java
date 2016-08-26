package io.hasura.db;

import java.util.HashSet;

public abstract class QueryWithReturning<Q, R> {
    protected HashSet<String> retSet;

    public QueryWithReturning() {
        this.retSet = new HashSet<>();
    }

    public abstract Q fromRetSet(HashSet<String> retSet);

    public <T1> Q returning(PGField<R, T1> f1) {
        this.retSet.add(f1.getColumnName());
        return fromRetSet(this.retSet);
    }

    public <T1, T2> Q returning(PGField<R, T1> f1, PGField<R, T2> f2) {
        this.retSet.add(f1.getColumnName());
        this.retSet.add(f2.getColumnName());
        return fromRetSet(this.retSet);
    }

    public <T1, T2, T3> Q returning(PGField<R, T1> f1, PGField<R, T2> f2, PGField<R, T3> f3) {
        this.retSet.add(f1.getColumnName());
        this.retSet.add(f2.getColumnName());
        this.retSet.add(f3.getColumnName());
        return fromRetSet(this.retSet);
    }

}
