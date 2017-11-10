package nl.topicus.bitbucket.api;

import nl.topicus.bitbucket.events.BitbucketPushEvent;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class to capsule operations on PushEvents
 */
public class PushEventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestListener.class);


    private WebHookConfiguration configuration;

    public PushEventService(WebHookConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isValidEvent(BitbucketPushEvent event, WebHookConfiguration configuration) {
        return !isIgnoredEvent(event, configuration);
    }

    private boolean isIgnoredEvent(BitbucketPushEvent event, WebHookConfiguration configuration) {
        return ignoredByCommiters(event, configuration) || ignoredByBranch(event, configuration);
    }

    private boolean ignoredByCommiters(BitbucketPushEvent event, WebHookConfiguration configuration) {
        if (configuration.getCommittersToIgnore() != null && event.getActor() != null) {
            List<String> ignoredCommitersList = Arrays.asList(configuration.getCommittersToIgnore().split("\\s?,\\s?"));
            if (ignoredCommitersList.size() > 0 && ignoredCommitersList.contains(event.getActor().getUsername())) {
                LOGGER.debug(
                        "[repo: {}]| The push event by user {} is ignored because the username is listed as a commit to ignore: [{}({})-committersToIgnore:{}]",
                        configuration.getRepositoryId(),
                        event.getActor().getUsername(),
                        configuration.getTitle(),
                        configuration.getURL(),
                        configuration.getCommittersToIgnore());
                return true;
            }
        }
        return false;
    }

    private boolean ignoredByBranch(BitbucketPushEvent event, WebHookConfiguration configuration) {
        if (configuration.getBranchesToIgnore() != null && event.getPush() != null && event.getPush().getChanges() != null) {
            List<String> branchNames = event.getPush().getChanges()
                    .stream()
                    .map(bitbucketPushChange -> bitbucketPushChange.getNew().getName())
                    .collect(Collectors.toList());

            boolean allBranchesIgnored = branchNames.size() > 0 && branchNames
                    .stream()
                    .allMatch(name -> name.matches(configuration.getBranchesToIgnore()));

            if(allBranchesIgnored){
                LOGGER.debug(
                        "[repo: {}]| This push event contains changes on branches {} it will be ignored because all branches are listed as branches to ignore: [{}({})-branchesToIgnore:{}]",
                        configuration.getRepositoryId(),
                        branchNames,
                        configuration.getTitle(),
                        configuration.getURL(),
                        configuration.getBranchesToIgnore());
            }
            return allBranchesIgnored;
        }
        return false;
    }
}
