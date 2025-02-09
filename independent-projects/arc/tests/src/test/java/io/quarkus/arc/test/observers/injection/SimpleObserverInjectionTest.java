package io.quarkus.arc.test.observers.injection;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.arc.Arc;
import io.quarkus.arc.test.ArcTestContainer;

public class SimpleObserverInjectionTest {

    @RegisterExtension
    public ArcTestContainer container = new ArcTestContainer(Fool.class, StringObserver.class);

    @Test
    public void testObserverInjection() {
        AtomicReference<String> msg = new AtomicReference<String>();
        Fool.DESTROYED.set(false);
        Arc.container().beanManager().getEvent().select(new TypeLiteral<AtomicReference<String>>() {
        }).fire(msg);
        String id1 = msg.get();
        assertNotNull(id1);
        assertTrue(Fool.DESTROYED.get());
        Fool.DESTROYED.set(false);
        Arc.container().beanManager().getEvent().select(new TypeLiteral<AtomicReference<String>>() {
        }).fire(msg);
        assertNotEquals(id1, msg.get());
        assertTrue(Fool.DESTROYED.get());
    }

    @Singleton
    static class StringObserver {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        void observeString(@Observes AtomicReference value, Fool fool, Instance<Fool> fools) {
            value.set(fool.id);
            fools.forEach(f -> {
                // Fool is @Dependent!
                assertNotEquals(fool.id, f.id);
            });
        }

    }

    @Dependent
    static class Fool {

        static final AtomicBoolean DESTROYED = new AtomicBoolean();

        private String id;

        @PostConstruct
        void init() {
            id = UUID.randomUUID().toString();
        }

        @PreDestroy
        void destroy() {
            DESTROYED.set(true);
        }
    }

}
