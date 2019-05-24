package com.atlassian.interviews.uag.memory;

import com.atlassian.interviews.uag.api.Group;
import com.atlassian.interviews.uag.api.GroupService;
import com.atlassian.interviews.uag.api.MembershipService;
import com.atlassian.interviews.uag.api.User;
import com.atlassian.interviews.uag.core.ServiceFactory;
import com.atlassian.interviews.uag.core.Services;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MemoryMembershipServiceTest {
    private static final User FRED = new User("fred");
    private static final User GEORGE = new User("george");
    private static final User NOBODY = new User("nobody");
    private static final Group HACKERS = new Group("hackers");
    private static final Group ADMINS = new Group("admins");
    private static final Group NOGROUP = new Group("nogroup");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MembershipService membershipService;
    
    private GroupService groupService;

    @Before
    public void setUp() {
        final Services services = ServiceFactory.createServices();
        services.getUserService().create(FRED);
        services.getUserService().create(GEORGE);
        services.getGroupService().create(ADMINS);
        services.getGroupService().create(HACKERS);
        membershipService = services.getMembershipService();
        groupService = services.getGroupService();
    }

    @Test
    public void addUserToGroup_duplicate() {
        membershipService.addUserToGroup(FRED, HACKERS);
        membershipService.addUserToGroup(GEORGE, HACKERS);

        final Set<User> expected = new HashSet<>();
        expected.add(FRED);
        expected.add(GEORGE);
        assertEquals(asList(FRED, GEORGE), sorted(membershipService.getUsersInGroup(HACKERS)));

        membershipService.addUserToGroup(FRED, HACKERS);
        assertEquals(asList(FRED, GEORGE), sorted(membershipService.getUsersInGroup(HACKERS)));
    }

    @Test
    public void addUserToGroup_noSuchGroup() {
        thrown.expect(IllegalArgumentException.class);
        membershipService.addUserToGroup(FRED, NOGROUP);
    }

    @Test
    public void addUserToGroup_noSuchUser() {
        thrown.expect(IllegalArgumentException.class);
        membershipService.addUserToGroup(NOBODY, HACKERS);
    }

    @Test
    public void addUserToGroup_npeGroup() {
        thrown.expect(NullPointerException.class);
        membershipService.addUserToGroup(FRED, null);
    }

    @Test
    public void addUserToGroup_npeUser() {
        thrown.expect(NullPointerException.class);
        membershipService.addUserToGroup(null, HACKERS);
    }

    @Test
    public void testRemoveUserFromGroup() {
        membershipService.addUserToGroup(FRED, ADMINS);
        membershipService.addUserToGroup(GEORGE, HACKERS);
        assertTrue("fred is an admin", membershipService.isUserInGroup(FRED, ADMINS));

        membershipService.removeUserFromGroup(FRED, ADMINS);
        assertFalse("fred is not an admin anymore", membershipService.isUserInGroup(FRED, ADMINS));
    }
    
    @Test
    public void testRemoveUserFromEmptyGroup() {
    	membershipService.removeUserFromGroup(FRED, ADMINS);
    	assertFalse("fred is not present in the group", membershipService.isUserInGroup(FRED, ADMINS));
    }
    
    @Test
    public void testIsUserInGroup_no() {
        membershipService.addUserToGroup(FRED, ADMINS);
        membershipService.addUserToGroup(GEORGE, HACKERS);

        assertFalse("fred is not a hacker", membershipService.isUserInGroup(FRED, HACKERS));
        assertFalse("george is not an admin", membershipService.isUserInGroup(GEORGE, ADMINS));
    }
    
    @Test
    public void testRecreateGroupWithEmptyUser() {
    	Group foo = new Group("foo");
    	groupService.create(foo);
    	membershipService.addUserToGroup(FRED, foo);
    	assertTrue("fred is present in group", membershipService.isUserInGroup(FRED, foo));
    	groupService.delete(foo);
    	assertNull(groupService.findByName(foo.getName()));
    	groupService.create(foo);
    	assertFalse("fred is not present in group", membershipService.isUserInGroup(FRED, foo));
    }

    @Test
    public void testIsUserInGroup_yes() {
        membershipService.addUserToGroup(FRED, ADMINS);
        membershipService.addUserToGroup(GEORGE, HACKERS);

        assertTrue("fred is an admin", membershipService.isUserInGroup(FRED, ADMINS));
        assertTrue("george is a hacker", membershipService.isUserInGroup(GEORGE, HACKERS));
    }

    private static <T extends Comparable<T>> List<T> sorted(Collection<T> items) {
        final List<T> list = new ArrayList<>(items);
        Collections.sort(list);
        return list;
    }
}