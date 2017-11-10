package nl.topicus.bitbucket.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.bitbucket.ao.AbstractAoDaoTest;
import com.atlassian.bitbucket.repository.Repository;
import net.java.ao.test.jdbc.Data;
import nl.topicus.bitbucket.events.EventType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Data(WebHookConfigurationDatabaseUpdater.class)
public class WebHookConfigurationDaoTest extends AbstractAoDaoTest {

    private ActiveObjects activeObjects;
    private WebHookConfigurationDao dao;

    public WebHookConfigurationDaoTest() {
        super(WebHookConfiguration.class);
    }

    @Before
    public void setup() {
        activeObjects = new TestActiveObjects(entityManager);
        dao = new WebHookConfigurationDao(activeObjects);
    }

    @Test
    public void testCreateOrUpdateWebHookConfigurationForCreate() {
        WebHookConfiguration created = dao.createOrUpdateWebHookConfiguration(mockRepository(3),
                null, "Bamboo", "https://example.com/bamboo/webhook", "bob", "master", true);
        assertNotNull(created);
        assertEquals(Integer.valueOf(3), created.getRepositoryId());
        assertEquals("Bamboo", created.getTitle());
        assertEquals("https://example.com/bamboo/webhook", created.getURL());
        assertEquals("bob", created.getCommittersToIgnore());
        assertEquals("master", created.getBranchesToIgnore());
        assertTrue(created.isEnabled());
    }

    @Test
    public void testCreateOrUpdateWebHookConfigurationForUpdate() {
        WebHookConfiguration[] configurations = activeObjects.find(WebHookConfiguration.class);
        assumeTrue(configurations != null && configurations.length > 0);

        WebHookConfiguration existing = configurations[0];

        WebHookConfiguration updated = dao.createOrUpdateWebHookConfiguration(
                mockRepository(existing.getRepositoryId()), String.valueOf(existing.getID()),
                "Updated", "https://example.com/updated/webhook", "bob", "develop", !existing.isEnabled());
        assertNotNull(updated);
        assertEquals(existing.getRepositoryId(), updated.getRepositoryId());
        assertEquals("Updated", updated.getTitle());
        assertEquals("https://example.com/updated/webhook", updated.getURL());
        assertEquals("bob", updated.getCommittersToIgnore());
        assertEquals("develop", updated.getBranchesToIgnore());
        assertEquals(!existing.isEnabled(), updated.isEnabled());
    }

    @Test
    public void testDeleteWebhookConfiguration() {
        WebHookConfiguration[] configurations = activeObjects.find(WebHookConfiguration.class);
        assumeTrue(configurations != null && configurations.length > 0);

        WebHookConfiguration configuration = configurations[0];
        dao.deleteWebhookConfiguration(configuration);

        assertNull(activeObjects.get(WebHookConfiguration.class, configuration.getID()));
    }

    @Test
    public void testDeleteWebhookConfigurations() {
        Repository repo = mockRepository(2);
        assertEquals(2, dao.deleteWebhookConfigurations(repo));

        WebHookConfiguration[] configurations = dao.getWebHookConfigurations(repo);
        assertTrue(configurations == null || configurations.length == 0);
    }
    
    @Test
    public void testDeleteWebhookConfigurationsForUnconfiguredRepository() {
        assertEquals(0, dao.deleteWebhookConfigurations(mockRepository(99)));
    }

    @Test
    public void testGetEnabledWebHookConfigurations() {
        WebHookConfiguration[] configurations = dao.getEnabledWebHookConfigurations(mockRepository(2), EventType.REPO_PUSH);
        assertNotNull(configurations);
        assertEquals(1, configurations.length);

        WebHookConfiguration configuration = configurations[0];
        assertEquals(Integer.valueOf(2), configuration.getRepositoryId());
        assertEquals("HipChat", configuration.getTitle());
        assertEquals("https://example.com/hipchat/webhook", configuration.getURL());
        assertTrue(configuration.isEnabled());
    }

    @Test
    public void testGetWebHookConfiguration() {
        WebHookConfiguration[] configurations = activeObjects.find(WebHookConfiguration.class);
        assumeTrue(configurations != null && configurations.length > 0);

        WebHookConfiguration expected = configurations[0];

        WebHookConfiguration actual = dao.getWebHookConfiguration(String.valueOf(expected.getID()));
        assertEquals(expected.getRepositoryId(), actual.getRepositoryId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getURL(), actual.getURL());
        assertEquals(expected.isEnabled(), actual.isEnabled());
    }

    @Test
    public void testGetWebHookConfigurations() {
        WebHookConfiguration[] configurations = dao.getWebHookConfigurations(mockRepository(2));
        assertNotNull(configurations);
        assertEquals(2, configurations.length);

        WebHookConfiguration hipchat = configurations[0];
        assertEquals(Integer.valueOf(2), hipchat.getRepositoryId());
        assertEquals("HipChat", hipchat.getTitle());
        assertEquals("https://example.com/hipchat/webhook", hipchat.getURL());
        assertTrue(hipchat.isEnabled());

        WebHookConfiguration slack = configurations[1];
        assertEquals(Integer.valueOf(2), slack.getRepositoryId());
        assertEquals("Slack", slack.getTitle());
        assertEquals("https://example.com/slack/webhook", slack.getURL());
        assertFalse(slack.isEnabled());
    }

    private static Repository mockRepository(int id) {
        Repository repository = mock(Repository.class);
        when(repository.getId()).thenReturn(id);

        return repository;
    }
}