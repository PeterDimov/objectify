/*
 */

package com.googlecode.objectify.test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.test.util.TestBase;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Tests of @Load annotation in transactions
 *
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
class LoadTransactionalTests extends TestBase {
	/** */
	@Entity
	@Data
	private static class One {
		static class Foo {}
		static class Bar {}

		@Id long id;
		@Load Ref<Two> always;
		@Load(unless=Foo.class) Ref<Two> withUnless;
		@Load(Foo.class) Ref<Two> withGroup;
	}

	/** */
	@Entity
	@Data
	private static class Two {
		public @Id long id;
	}

	/** */
	@Test
	void properLoadBehaviorInTransactions() throws Exception {
		factory().register(One.class);
		factory().register(Two.class);

		final Two twoAlways = new Two();
		twoAlways.id = 123;
		final Key<Two> twoAlwaysKey = ofy().save().entity(twoAlways).now();
		final Ref<Two> twoAlwaysRef = Ref.create(twoAlwaysKey);

		final Two twoWithUnless = new Two();
		twoWithUnless.id = 456;
		final Key<Two> twoWithUnlessKey = ofy().save().entity(twoWithUnless).now();
		final Ref<Two> twoWithUnlessRef = Ref.create(twoWithUnlessKey);

		final Two twoWithGroup = new Two();
		twoWithGroup.id = 789;
		final Key<Two> twoWithGroupKey = ofy().save().entity(twoWithGroup).now();
		final Ref<Two> twoWithGroupRef = Ref.create(twoWithGroupKey);

		final One one = new One();
		one.id = 123;
		one.always = twoAlwaysRef;
		one.withUnless = twoWithUnlessRef;
		one.withGroup = twoWithGroupRef;
		ofy().save().entity(one).now();

		ofy().transact(() -> {
			final One fetched = ofy().load().entity(one).now();
			assertThat(fetched.always.isLoaded()).isFalse();
			assertThat(fetched.withUnless.isLoaded()).isFalse();
			assertThat(fetched.withGroup.isLoaded()).isFalse();
		});

		ofy().transact(() -> {
			final One fetched = ofy().load().group(One.Foo.class).entity(one).now();
			assertThat(fetched.always.isLoaded()).isFalse();
			assertThat(fetched.withUnless.isLoaded()).isFalse();
			assertThat(fetched.withGroup.isLoaded()).isTrue();
		});

		ofy().transact(() -> {
			final One fetched = ofy().load().group(One.Bar.class).entity(one).now();
			assertThat(fetched.always.isLoaded()).isFalse();
			assertThat(fetched.withUnless.isLoaded()).isFalse();
			assertThat(fetched.withGroup.isLoaded()).isFalse();
		});
	}
}