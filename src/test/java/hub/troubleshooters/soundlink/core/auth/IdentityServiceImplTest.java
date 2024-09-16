package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.core.auth.services.IdentityServiceImpl;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IdentityServiceImplTest {

    @Mock
    private CommunityFactory communityFactory;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private IdentityServiceImpl identityServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for setting and getting the user context
    @Test
    void testSetAndGetUserContext() {
        // Set the user context
        identityServiceImpl.setUserContext(userContext);
        // Verify that the correct context is returned
        assertEquals(userContext, identityServiceImpl.getUserContext());
    }

    // Test authorization when the user has SUPERADMIN scope
    @Test
    void testIsAuthorizedWithSuperAdminScope() {
        Community community = mock(Community.class);
        // Simulate SUPERADMIN scope in the user's context
        when(userContext.getCurrentScopes(community)).thenReturn(Set.of(Scope.SUPERADMIN));

        identityServiceImpl.setUserContext(userContext);

        // Verify that SUPERADMIN is authorized for any scope
        assertTrue(identityServiceImpl.isAuthorized(community, Scope.COMMUNITY_WRITE));
    }

    // Test authorization when the user has the correct required scopes
    @Test
    void testIsAuthorizedWithCorrectScopes() {
        Community community = mock(Community.class);
        // Simulate USER and COMMUNITY_WRITE scopes in the user's context
        when(userContext.getCurrentScopes(community)).thenReturn(Set.of(Scope.COMMUNITY_WRITE, Scope.EVENT_READ));

        identityServiceImpl.setUserContext(userContext);

        // Verify that the user has the required COMMUNITY_WRITE scope
        assertTrue(identityServiceImpl.isAuthorized(community, Scope.COMMUNITY_WRITE));
        // Verify that the user does not have the SUPERADMIN scope
        assertFalse(identityServiceImpl.isAuthorized(community, Scope.SUPERADMIN));
    }

    // Test authorization when the user does not have the required scopes
    @Test
    void testIsAuthorizedWithoutRequiredScopes() {
        Community community = mock(Community.class);
        // Simulate only EVENT_READ scope in the user's context
        when(userContext.getCurrentScopes(community)).thenReturn(Set.of(Scope.EVENT_READ));

        identityServiceImpl.setUserContext(userContext);

        // Verify that the user lacks the required COMMUNITY_WRITE scope
        assertFalse(identityServiceImpl.isAuthorized(community, Scope.COMMUNITY_WRITE));
    }

    // Test that getCommunities returns an empty list when the user context is null
    @Test
    void testGetCommunitiesWhenUserContextIsNull() {
        // Set the user context to null
        identityServiceImpl.setUserContext(null);

        // Verify that the result is an empty list
        List<Community> communities = identityServiceImpl.getCommunities();
        assertTrue(communities.isEmpty());
    }

    // Test getCommunities when the user has valid community memberships
    @Test
    void testGetCommunitiesWithValidUserContext() throws Exception {
        // Create mock community members
        CommunityMember member1 = mock(CommunityMember.class);
        CommunityMember member2 = mock(CommunityMember.class);

        // Simulate getCommunityId() for each member
        when(member1.getCommunityId()).thenReturn(1);
        when(member2.getCommunityId()).thenReturn(2);

        // Simulate userContext.getCommunityMembers() returning a list of members
        when(userContext.getCommunityMembers()).thenReturn(List.of(member1, member2));

        // Create mock communities
        Community community1 = mock(Community.class);
        Community community2 = mock(Community.class);

        // Simulate communityFactory.get() returning the communities
        when(communityFactory.get(List.of(1, 2))).thenReturn(List.of(community1, community2));

        // Set the user context
        identityServiceImpl.setUserContext(userContext);

        // Call getCommunities and verify the results
        List<Community> communities = identityServiceImpl.getCommunities();
        assertEquals(2, communities.size());
        assertTrue(communities.contains(community1));
        assertTrue(communities.contains(community2));
    }

    // Test getCommunities when an SQLException is thrown by the CommunityFactory
    @Test
    void testGetCommunitiesWhenSQLExceptionThrown() throws Exception {
        // Create mock community member
        CommunityMember member1 = mock(CommunityMember.class);
        when(member1.getCommunityId()).thenReturn(1);

        // Simulate userContext.getCommunityMembers() returning a list of one member
        when(userContext.getCommunityMembers()).thenReturn(List.of(member1));

        // Simulate communityFactory.get() throwing an SQLException
        when(communityFactory.get(List.of(1))).thenThrow(new SQLException());

        // Set the user context
        identityServiceImpl.setUserContext(userContext);

        // Call getCommunities and verify it returns an empty list due to the exception
        List<Community> communities = identityServiceImpl.getCommunities();
        assertTrue(communities.isEmpty());
    }
}




