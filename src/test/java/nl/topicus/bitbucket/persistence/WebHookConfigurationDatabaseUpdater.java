package nl.topicus.bitbucket.persistence;

import com.google.common.collect.ImmutableMap;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

import static nl.topicus.bitbucket.persistence.WebHookConfiguration.*;

public class WebHookConfigurationDatabaseUpdater implements DatabaseUpdater {

    @Override
    @SuppressWarnings("unchecked")
    public void update(EntityManager entityManager) throws Exception {
        entityManager.migrate(WebHookConfiguration.class);

        entityManager.create(WebHookConfiguration.class, ImmutableMap.<String, Object>builder()
                .put(COLUMN_ENABLED, true)
                .put(COLUMN_REPO_ID, 1)
                .put(COLUMN_TITLE, "Jenkins")
                .put(COLUMN_URL, "https://example.com/jenkins/webhook")
                .build());

        entityManager.create(WebHookConfiguration.class, ImmutableMap.<String, Object>builder()
                .put(COLUMN_ENABLED, true)
                .put(COLUMN_REPO_ID, 2)
                .put(COLUMN_TITLE, "HipChat")
                .put(COLUMN_URL, "https://example.com/hipchat/webhook")
                .build());
        entityManager.create(WebHookConfiguration.class, ImmutableMap.<String, Object>builder()
                .put(COLUMN_ENABLED, false)
                .put(COLUMN_REPO_ID, 2)
                .put(COLUMN_TITLE, "Slack")
                .put(COLUMN_URL, "https://example.com/slack/webhook")
                .build());
    }
}
