package nl.eernie.bitbucket.events;

import java.util.List;
import java.util.Optional;

public interface Ignorable {

    Optional<String> getUsername();

    List<String> getBranches();
}
