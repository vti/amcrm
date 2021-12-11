package com.github.vti.amcrm.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.user.event.*;

public class UserTest {

    @Test
    void buildsNewUser() {
        User user = TestFactory.newUserBuilder().name("John").build();

        assertEquals("John", user.getName());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId().value(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getCreatedBy(), user.getEvents().get(0).getActorId());
        assertEquals(UserCreated.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void togglesAdminStatus() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        User user = TestFactory.newUserBuilder().name("John").build();

        user.clearEvents();

        assertFalse(user.isAdmin());

        user.toggleAdminStatus(actorId);

        assertTrue(user.isAdmin());

        user.clearEvents();

        user.toggleAdminStatus(actorId);

        assertFalse(user.isAdmin());

        assertEquals(actorId, user.getUpdatedBy());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId().value(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getUpdatedBy(), user.getEvents().get(0).getActorId());
        assertEquals(
                UserAdminStatusToggled.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void delete() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        user.clearEvents();

        user.delete(actorId);

        assertEquals(true, user.isDeleted());
        assertEquals(actorId, user.getDeletedBy());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId().value(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getDeletedBy(), user.getEvents().get(0).getActorId());
        assertEquals(UserDeleted.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void throwsWhenDeletingAlreadyDeleted() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        user.delete(actorId);

        assertThrows(IllegalStateException.class, () -> user.delete(actorId));
    }

    @Test
    void userWithSameIdAreEqual() {
        User user1 = TestFactory.newUserBuilder().id(UserId.of("1")).build();
        User user2 = TestFactory.newUserBuilder().id(UserId.of("1")).build();

        assertTrue(user1.equals(user2));
    }
}
