package com.googlecode.objectify;


/**
 * Exception thrown when a Ref.get() is called on a Ref that has not been loaded.  Before it can be dereferenced
 * a Ref needs to be loaded either implicitly via @Load or explicitly via ofy().loadRef(ref).
 */
public class RefNotLoadedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** */
    private Key<?> key;

    /** Thrown when Ref.get() is called on a Ref that wasn't previously loaded. */
    public <T> RefNotLoadedException(Key<T> key) {
        super("Ref was not loaded for key: " + key);
        this.key = key;
    }

    /** @return the key we are looking for, if known */
    public Key<?> getKey() {
        return this.key;
    }
}
