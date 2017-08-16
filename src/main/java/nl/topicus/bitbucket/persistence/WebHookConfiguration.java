package nl.topicus.bitbucket.persistence;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.*;

@Table("WHConfig")
@Preload
public interface WebHookConfiguration extends Entity
{
	String COLUMN_BRANCH_CREATED = "BRANCH_CREATED";
	String COLUMN_BRANCH_DELETED = "BRANCH_DELETED";
	String COLUMN_ENABLED = "IS_ENABLED";
	String COLUMN_REPO_ID = "REPO_ID";
	String COLUMN_PR_CREATED = "PR_CREATED";
	String COLUMN_PR_DECLINED = "PR_DECLINED";
	String COLUMN_PR_MERGED = "PR_MERGED";
	String COLUMN_PR_RESCOPED = "PR_RESCOPED";
	String COLUMN_PR_REOPENED = "PR_REOPENED";
	String COLUMN_PR_UPDATED = "PR_UPDATED";
	String COLUMN_REPO_PUSH = "REPO_PUSH";
	String COLUMN_TAG_CREATED = "TAG_CREATED";
	String COLUMN_TITLE = "TITLE";
	String COLUMN_URL = "URL";

	@Accessor(COLUMN_TITLE)
	@NotNull
	String getTitle();

	@Mutator(COLUMN_TITLE)
	void setTitle(String title);

	@Accessor(COLUMN_URL)
	@NotNull
	@StringLength(StringLength.UNLIMITED)
	String getURL();

	@Mutator(COLUMN_URL)
	void setURL(String URL);

	@Accessor(COLUMN_REPO_ID)
	@Indexed
	@NotNull
	Integer getRepositoryId();

	@Accessor(COLUMN_ENABLED)
	@Default("true")
	@NotNull
	boolean isEnabled();

	@Mutator(COLUMN_ENABLED)
	void setEnabled(boolean isEnabled);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_CREATED)
	boolean isPrCreated();

	@Mutator(COLUMN_PR_CREATED)
	void setPrCreated(boolean isPrCreated);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_UPDATED)
	boolean isPrUpdated();

	@Mutator(COLUMN_PR_UPDATED)
	void setPrUpdated(boolean isPrUpdated);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_REOPENED)
	boolean isPrReopened();

	@Mutator(COLUMN_PR_REOPENED)
	void setPrReopened(boolean isPrReopened);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_MERGED)
	boolean isPrMerged();

	@Mutator(COLUMN_PR_MERGED)
	void setPrMerged(boolean isPrMerged);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_RESCOPED)
	boolean isPrRescoped();

	@Mutator(COLUMN_PR_RESCOPED)
	void setPrRescoped(boolean isPrRescoped);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_PR_DECLINED)
	boolean isPrDeclined();

	@Mutator(COLUMN_PR_DECLINED)
	void setPrDeclined(boolean isPrDeclined);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_REPO_PUSH)
	boolean isRepoPush();

	@Mutator(COLUMN_REPO_PUSH)
	void setRepoPush(boolean isRepoPush);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_BRANCH_CREATED)
	boolean isBranchCreated();

	@Mutator(COLUMN_BRANCH_CREATED)
	void setBranchCreated(boolean isBranchCreated);

	@NotNull
	@Default("true")
	@Accessor(COLUMN_BRANCH_DELETED)
	boolean isBranchDeleted();

	@Mutator(COLUMN_BRANCH_DELETED)
	void setBranchDeleted(boolean isBranchDeleted);

	@NotNull
	@Default("false")
	@Accessor(COLUMN_TAG_CREATED)
	boolean isTagCreated();

	@Mutator(COLUMN_TAG_CREATED)
	void setTagCreated(boolean isTagCreated);
}
