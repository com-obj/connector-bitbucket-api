package sk.objectify.connector.endpoint.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetWorkspaceMembersResponse {
	private int page;

	@SerializedName("pagelen")
	private int pageLength;
	private List<UserWrapper> values;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageLength() {
		return pageLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}

	public List<UserWrapper> getValues() {
		return values;
	}

	public void setValues(List<UserWrapper> values) {
		this.values = values;
	}
}
