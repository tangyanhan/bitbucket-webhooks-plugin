package it.nl.topicus.bitbucket;

import com.atlassian.bitbucket.project.NoSuchProjectException;
import com.atlassian.bitbucket.repository.NoSuchRepositoryException;
import com.atlassian.bitbucket.test.BaseRetryingFuncTest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import net.sf.json.JSONObject;
import org.junit.Test;

import static com.atlassian.bitbucket.test.DefaultFuncTestData.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class WebhookResourceTest extends BaseRetryingFuncTest {

    @Test
    public void testCrudWebhook() { // For simplicity, this tests creating, reading, updating and deleting a webhook
        JSONObject body = new JSONObject();
        body.put("title", "Test Hook");
        body.put("url", "https://example.com/webhook");
        body.put("enabled", true);

        // Create
        int webhookId = RestAssured.given()
                .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                .body(body)
                .contentType(ContentType.JSON)
                .expect().statusCode(200)
                .log().ifValidationFails()
                .body("title", equalTo(body.get("title")))
                .body("url", equalTo(body.get("url")))
                .body("enabled", equalTo(true))
                .when().put(getUrl(getProject1(), getProject1Repository1()))
                .jsonPath()
                .getInt("id");

        try {
            // Read
            RestAssured.given()
                    .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                    .expect().statusCode(200)
                    .log().ifValidationFails()
                    .body("size()", equalTo(1))
                    .body("[0].id", equalTo(webhookId))
                    .body("[0].title", equalTo(body.get("title")))
                    .body("[0].url", equalTo(body.get("url")))
                    .body("[0].enabled", equalTo(true))
                    .when().get(getUrl(getProject1(), getProject1Repository1()));

            JSONObject update = new JSONObject();
            update.put("id", webhookId);
            update.put("title", "Updated Test");
            update.put("url", "http://example.com/webhook");
            update.put("enabled", false);

            // Update
            RestAssured.given()
                    .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                    .body(update)
                    .contentType(ContentType.JSON)
                    .expect().statusCode(200)
                    .log().ifValidationFails()
                    .body("id", equalTo(webhookId))
                    .body("title", equalTo(update.get("title")))
                    .body("url", equalTo(update.get("url")))
                    .body("enabled", equalTo(false))
                    .when().post(getUrl(getProject1(), getProject1Repository1()) + "/" + webhookId);
        } finally {
            RestAssured.given()
                    .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                    .expect().statusCode(204)
                    .log().ifValidationFails()
                    .when().delete(getUrl(getProject1(), getProject1Repository1()) + "/" + webhookId);
        }
    }

    @Test
    public void testGetWebhooksForNonexistentProject() {
        RestAssured.given()
                .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                .expect().statusCode(404)
                .log().ifValidationFails()
                .body("errors[0].exceptionName", equalTo(NoSuchProjectException.class.getCanonicalName()))
                .body("errors[0].message", equalTo("Project NONE does not exist."))
                .when().get(getUrl("NONE", "nonexistent"));
    }

    @Test
    public void testGetWebhooksForNonexistentRepository() {
        RestAssured.given()
                .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                .expect().statusCode(404)
                .log().ifValidationFails()
                .body("errors[0].exceptionName", equalTo(NoSuchRepositoryException.class.getCanonicalName()))
                .body("errors[0].message", equalTo("Repository " + getProject1() + "/nonexistent does not exist."))
                .when().get(getUrl(getProject1(), "nonexistent"));
    }

    @Test
    public void testGetWebhooksWithNoneConfigured() {
        RestAssured.given()
                .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                .expect().statusCode(200)
                .log().ifValidationFails()
                .body("isEmpty()", equalTo(true))
                .when().get(getUrl(getProject1(), getProject1Repository1()));
    }

    @Test
    public void testRemoveWebhookWithNonexistentId() {
        RestAssured.given()
                .auth().preemptive().basic(getAdminUser(), getAdminPassword())
                .expect().statusCode(404)
                .log().ifValidationFails()
                .body(equalTo("Webhook not found"))
                .when().delete(getUrl(getProject1(), getProject1Repository1()) + "/9999");
    }

    private String getUrl(String projectKey, String repositorySlug) {
        return getRepositoryRestURL("webhook", "latest", projectKey, repositorySlug) + "/configurations";
    }
}
