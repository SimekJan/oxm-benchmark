package cz.cuni.mff.simekjan.example;

import cz.cuni.mff.simekjan.runner.api.NoParams;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/** Placeholder query to be replaced by a submitted implementation. */
public class EmployeeQuery implements HibernateQuery<NoParams> {
    /** {@inheritDoc} */
    @Override
    public String id() {
        return "employee-example";
    }

    /** {@inheritDoc} */
    @Override
    public Class<NoParams> parameterType() {
        return NoParams.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOrdered() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Object run(Session context, NoParams params) {
        throw new UnsupportedOperationException("Example query is not implemented");
    }
}
