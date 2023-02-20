package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * A functional interface, instances of it will be attached to
 * an Observable, which will call the update method.
 *
 * @author Abdul
 * @version 1.2
 */
@FunctionalInterface
public interface Observer<S extends Observable<S>> {
    /**
     * Normally checks what data has changed in the object and runs code
     * based on it.
     *
     * @param subject The Observable object that called this method.
     */
    public void update(S subject);
}
