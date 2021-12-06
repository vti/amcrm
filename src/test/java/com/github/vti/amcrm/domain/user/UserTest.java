package com.github.vti.amcrm.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.user.event.*;

class UserTest {

    @Test
    void buildsNewUser() {
        User user = TestFactory.newUserBuilder().name("John").build();

        assertEquals("John", user.getName());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getCreatedBy(), user.getEvents().get(0).getUserId());
        assertEquals(UserCreated.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void togglesAdminStatus() {
        UserId userId = UserId.of(TestData.getRandomId());
        User user = TestFactory.newUserBuilder().name("John").build();

        user.clearEvents();

        assertFalse(user.isAdmin());

        user.toggleAdminStatus(userId);

        assertTrue(user.isAdmin());

        user.clearEvents();

        user.toggleAdminStatus(userId);

        assertFalse(user.isAdmin());

        assertEquals(userId, user.getUpdatedBy());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getUpdatedBy(), user.getEvents().get(0).getUserId());
        assertEquals(
                UserAdminStatusToggled.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void delete() {
        UserId userId = UserId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        user.clearEvents();

        user.delete(userId);

        assertEquals(true, user.isDeleted());
        assertEquals(userId, user.getDeletedBy());

        assertEquals(1, user.getEvents().size());
        assertEquals(user.getId(), user.getEvents().get(0).getOriginId());
        assertEquals(user.getDeletedBy(), user.getEvents().get(0).getUserId());
        assertEquals(UserDeleted.class.getSimpleName(), user.getEvents().get(0).getName());
    }

    @Test
    void throwsWhenDeletingAlreadyDeleted() {
        UserId userId = UserId.of(TestData.getRandomId());
        User user = TestFactory.newUser();

        user.delete(userId);

        assertThrows(IllegalStateException.class, () -> user.delete(userId));
    }

    @Test
    void userWithSameIdAreEqual() {
        User user1 = TestFactory.newUserBuilder().id(UserId.of("1")).build();
        User user2 = TestFactory.newUserBuilder().id(UserId.of("1")).build();

        assertTrue(user1.equals(user2));
    }
}
