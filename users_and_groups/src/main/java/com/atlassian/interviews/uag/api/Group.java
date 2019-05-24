package com.atlassian.interviews.uag.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

/**
 * A group that may or may not have any users in it.
 */
@ParametersAreNonnullByDefault
public class Group implements Comparable<Group> {
    private final String name;

    /**
     * Creates a new instance of a group.
     * This does not implicitly register the group with the {@link GroupService}.
     *
     * @param name the unique name that identifies this group; must not be {@code null}.
     */
    public Group(String name) {
        this.name = requireNonNull(name, "name");
    }

    /**
     * Returns the name that identifies this group.
     *
     * @return the name that identifies this group.
     */
    public String getName() {
        return name;
    }

    public int compareTo(@Nonnull Group other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
