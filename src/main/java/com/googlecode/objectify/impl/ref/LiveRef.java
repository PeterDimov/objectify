package com.googlecode.objectify.impl.ref;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.RefNotLoadedException;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.impl.ObjectifyOptions;

import java.io.ObjectStreamException;


/**
 * <p>Implementation of Refs which are "live" and connected to the datastore so they can fetch
 * entity values even if they have not already been loaded. This is the standard Ref implementation.</p>
 *
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class LiveRef<T> extends Ref<T>
{
	private static final long serialVersionUID = 1L;

	/** So that Refs can be associated with a session */
	protected transient Result<T> result;

	/** For GWT serialization */
	protected LiveRef() {}

	/**
	 * Create a Ref based on the key
	 */
	public LiveRef(Key<T> key) {
		this(key, null);
	}

	/**
	 * Create a Ref based on the key, with the specified session
	 */
	public LiveRef(Key<T> key, Result<T> result) {
		super(key);
		this.result = result;
	}

	/**
	 * Update the result loaded for key.
	 */
	public void setResult(Result<T> result, ObjectifyOptions options) {
		if (options.getRefLoadListener() != null) {
			options.getRefLoadListener().onRefLoad(key());
		}
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.objectify.Ref#get()
	 */
	@Override
	public T get() {
		if (result == null) {
			throw new RefNotLoadedException(key);
		}
		else return result.now();
	}

	/* (non-Javadoc)
	 * @see com.googlecode.objectify.Ref#isLoaded()
	 */
	@Override
	public boolean isLoaded() {
		return result != null;
	}

	/**
	 * When this serializes, write out the DeadRef version. Use the getValue() for value so that
	 * if the value is not loaded, it serializes as null.
	 */
	protected Object writeReplace() throws ObjectStreamException {
		return new DeadRef<>(key(), getValue());
	}
}