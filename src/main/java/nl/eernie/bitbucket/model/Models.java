package nl.eernie.bitbucket.model;

import com.atlassian.bitbucket.project.AbstractProjectVisitor;
import com.atlassian.bitbucket.project.PersonalProject;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import nl.eernie.bitbucket.events.BitbucketPushChange;
import nl.eernie.bitbucket.model.pullrequest.BitbucketServerPullRequest;
import nl.eernie.bitbucket.model.pullrequest.BitbucketServerPullRequestSource;
import nl.eernie.bitbucket.model.repository.BitbucketServerProject;
import nl.eernie.bitbucket.model.repository.BitbucketServerRepository;
import nl.eernie.bitbucket.model.repository.BitbucketServerRepositoryOwner;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Collections;

public final class Models
{
    private Models()
    {
    }

    public static BitbucketServerRepository createRepository(Repository repository,
                                                             ApplicationPropertiesService appPropSvc)
    {
        BitbucketServerRepository repoType = new BitbucketServerRepository();
        repoType.setProject(createProject(repository.getProject()));
        repoType.setPublic(repository.isPublic());
        repoType.setScmId(repository.getScmId());
        repoType.setSlug(repository.getSlug());
        URI baseUrl = appPropSvc.getBaseUrl();
        if (baseUrl != null)
        {
            String url = repository.getProject().accept(new AbstractProjectVisitor<String>()
            {
                @Override
                public String visit(@Nonnull PersonalProject project)
                {
                    return String.format("%s/users/%s/repos/%s/browse", baseUrl,
                            project.getOwner().getSlug(), repository.getSlug());
                }

                @Override
                public String visit(@Nonnull Project project)
                {
                    return String.format("%s/projects/%s/repos/%s/browse", baseUrl,
                            project.getKey(), repository.getSlug());
                }
            });

            repoType.setLinks(Collections.singletonMap("self",
                    Collections.singletonList(new BitbucketServerRepository.Link(url))));
        }
        return repoType;
    }

    public static BitbucketServerProject createProject(Project project)
    {
        BitbucketServerProject serverProject = new BitbucketServerProject();
        serverProject.setName(project.getName());
        serverProject.setKey(project.getKey());
        return serverProject;
    }

    public static BitbucketServerPullRequest createPullrequest(PullRequest pullRequest,
                                                               ApplicationPropertiesService appPropSvc)
    {
        BitbucketServerPullRequest pullRequestType = new BitbucketServerPullRequest();
        pullRequestType.setId(String.valueOf(pullRequest.getId()));
        pullRequestType.setFromRef(createSource(pullRequest.getFromRef(), appPropSvc));
        pullRequestType.setToRef(createSource(pullRequest.getToRef(), appPropSvc));
        pullRequestType.setTitle(pullRequest.getTitle());
        pullRequestType.setAuthorLogin(pullRequest.getAuthor().getUser().getDisplayName());
        return pullRequestType;
    }

    public static BitbucketServerPullRequestSource createSource(PullRequestRef pullRequestRef,
                                                                ApplicationPropertiesService appPropSvc)
    {
        BitbucketServerPullRequestSource source = new BitbucketServerPullRequestSource();
        source.setDisplayId(pullRequestRef.getDisplayId());
        source.setLatestCommit(pullRequestRef.getLatestCommit());
        source.setRepository(createRepository(pullRequestRef.getRepository(), appPropSvc));
        return source;
    }

    public static BitbucketServerRepositoryOwner createActor(ApplicationUser user)
    {
        return new BitbucketServerRepositoryOwner(user.getName(), user.getDisplayName());
    }

    public static BitbucketPushChange createChange(RefChange change)
    {
        BitbucketPushChange result = new BitbucketPushChange();
        BitbucketPushChange.State _new = null;
        BitbucketPushChange.State _old = null;
        switch (change.getType())
        {
            case ADD:
                _new = new BitbucketPushChange.State();
                _new.setType(change.getRef().getType());
                _new.setName(change.getRef().getDisplayId());
                _new.setTarget(new BitbucketPushChange.State.Target(change.getToHash()));
                result.setCreated(true);
                result.setClosed(false);
                break;
            case DELETE:
                _old = new BitbucketPushChange.State();
                _old.setType(change.getRef().getType());
                _old.setName(change.getRef().getDisplayId());
                _old.setTarget(new BitbucketPushChange.State.Target(change.getFromHash()));
                result.setCreated(false);
                result.setClosed(true);
                break;
            case UPDATE:
                _new = new BitbucketPushChange.State();
                _new.setType(change.getRef().getType());
                _new.setName(change.getRef().getDisplayId());
                _new.setTarget(new BitbucketPushChange.State.Target(change.getToHash()));
                _old = new BitbucketPushChange.State();
                _old.setType(change.getRef().getType());
                _old.setName(change.getRef().getDisplayId());
                _old.setTarget(new BitbucketPushChange.State.Target(change.getFromHash()));
                result.setCreated(false);
                result.setClosed(false);
                break;
        }
        result.setNew(_new);
        result.setOld(_old);
        return result;
    }
}
