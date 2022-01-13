package sk.objectify.connector.endpoint.dto;

import com.google.gson.annotations.SerializedName;

public class User {
	@SerializedName("display_name")
	private String displayName;
	private String uuid;
	private String nickname;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "User [displayName=" + displayName + ", uuid=" + uuid + ", nickname=" + nickname + "]";
	}

}
