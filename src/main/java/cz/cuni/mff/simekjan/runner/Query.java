package cz.cuni.mff.simekjan.runner;

public interface Query<P> {

    void run(P params);
}
