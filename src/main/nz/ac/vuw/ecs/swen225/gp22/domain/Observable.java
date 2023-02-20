package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A class can extend this so an instance of it
 * can be observed by Observer objects.
 *
 * @author Abdul
 * @version 1.4
 */
public abstract class Observable<S extends Observable<S>> {
    /**
     * Contains a list of the Observer objects that are monitoring this object.
     */
    private List<Observer<S>> observers = new ArrayList<>();

    /**
     * Adds an Observer to the list of observers.
     *
     * @param observer Observer to be added.
     */
    public void addObserver(Observer<S> observer) {
        if (observer == null) throw new IllegalArgumentException("Given observer is null");
        if (observers.contains(observer))
            throw new IllegalArgumentException("Observer is already attached to this object.");
        observers.add(observer);
        assert observers.contains(observer) : "Observer not added.";
    }

    /**
     * Removes an Observer from the list of observers.
     *
     * @param observer Observer to be removed.
     */
    public void removeObserver(Observer<S> observer) {
        if (observer == null) throw new IllegalArgumentException("Given observer is null");
        if (!observers.contains(observer))
            throw new IllegalArgumentException("Observer is not attached to this object.");
        observers.remove(observer);
        assert !observers.contains(observer) : "Observer not removed.";
    }

    /**
     * Calls the update() method for each observer
     * attached to this object. This method suppresses the unchecked cast because type S
     * is always a subtype of Observable with the generic type also being S,
     * and the value of 'this' will also be a subtype of Observable with
     * the generic type also being S, so the cast is safe.
     */
    @SuppressWarnings("unchecked")
    public void updateObservers() {
        for (int o = observers.size() - 1; o >= 0; o--) {
            observers.get(o).update((S) this);
        }
    }
}
