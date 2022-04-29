package mc.bape.vapu.irc;

public class IRCUser {
	private final boolean anonymous;
	private final String username, ingamename, rank;
	
	public IRCUser(String ingamename) {
		this.anonymous = true;
		this.username = "Guest";
		this.ingamename = ingamename;
		this.rank = null;
	}
	public IRCUser(String ingamename, String username, String rank) {
		this.anonymous = false;
		this.ingamename = ingamename;
		this.username = username;
		this.rank = rank;
	}

	public String getUsername() {
		return username;
	}

	public String getIngamename() {
		return ingamename;
	}
	public String getRank() {
		return rank;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (anonymous ? 1231 : 1237);
		result = prime * result + ((ingamename == null) ? 0 : ingamename.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IRCUser))
			return false;
		IRCUser other = (IRCUser) obj;
		if (anonymous != other.anonymous)
			return false;
		if (ingamename == null) {
			if (other.ingamename != null)
				return false;
		} else if (!ingamename.equals(other.ingamename))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
