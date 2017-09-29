package com.googlecode.objectify.impl;

import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.googlecode.objectify.Key;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * <p>Encapsulates the various options that can be twiddled in an objectify session. Immutable/functional.</p>
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@Data
@RequiredArgsConstructor
public class ObjectifyOptions {

	public interface RefLoadListener {
		<T> void onRefLoad(Key<T> key);
	}

	private final boolean cache;
	private final Consistency consistency;
	private final Double deadline;
	private final boolean mandatoryTransactions;
	private final RefLoadListener refLoadListener;

	ObjectifyOptions() {
		this(true, Consistency.STRONG, null, false, null);
	}

	public ObjectifyOptions consistency(final Consistency value) {
		if (value == null)
			throw new IllegalArgumentException("Consistency cannot be null");

		return new ObjectifyOptions(cache, value, deadline, mandatoryTransactions, refLoadListener);
	}

	public ObjectifyOptions deadline(final Double value) {
		return new ObjectifyOptions(cache, consistency, value, mandatoryTransactions, refLoadListener);
	}

	public ObjectifyOptions cache(final boolean value) {
		return new ObjectifyOptions(value, consistency, deadline, mandatoryTransactions, refLoadListener);
	}

	public ObjectifyOptions mandatoryTransactions(final boolean value) {
		return new ObjectifyOptions(cache, consistency, deadline, value, refLoadListener);
	}

	public ObjectifyOptions refLoadListener(final RefLoadListener refLoadListener) {
		return new ObjectifyOptions(cache, consistency, deadline, mandatoryTransactions, refLoadListener);
	}
}