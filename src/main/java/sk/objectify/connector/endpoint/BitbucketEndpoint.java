package sk.objectify.connector.endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import sk.objectify.connector.endpoint.dto.GetWorkspaceMembersResponse;
import sk.objectify.connector.endpoint.dto.UserWrapper;
import sk.objectify.connector.endpoint.dto.User;

public class BitbucketEndpoint {
	private final String host;
	private final HttpClient httpClient;
	private final Gson gson;

	public BitbucketEndpoint(final String host, final HttpClient httpClient, final Gson gson) {
		this.host = host;
		this.httpClient = httpClient;
		this.gson = gson;
	}

	public List<User> getUsersFromWorkspace(String workspace) throws IOException, InterruptedException {
		var url = String.format("/workspaces/%s/members?fields=-values.links,-values.workspace,-values.user.links",
				workspace);
		var maxPageLength = 0;
		var actualLenngth = 0;
		var actualPage = 0;
		List<UserWrapper> member = new ArrayList<>();

		do {
			var response = makeApiCallGetUsersInWorkspace(host + url + "&page=" + (actualPage + 1));
			member.addAll(response.getValues());

			maxPageLength = response.getPageLength();
			actualLenngth = response.getValues().size();
			actualPage = response.getPage();
		} while (maxPageLength == actualLenngth);

		return member.stream().map(UserWrapper::getUser).collect(Collectors.toList());
	}

	private GetWorkspaceMembersResponse makeApiCallGetUsersInWorkspace(String url)
			throws IOException, InterruptedException {
		var httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		var response = httpClient.send(httpRequest, BodyHandlers.ofString());
		return gson.fromJson(response.body(), GetWorkspaceMembersResponse.class);
	}

	public User getUser(String uid) throws IOException, InterruptedException {
		var url = String.format("/users/%s", URLEncoder.encode(uid, StandardCharsets.UTF_8));
		var httpRequest = HttpRequest.newBuilder().uri(URI.create(host + url)).GET().build();

		var response = httpClient.send(httpRequest, BodyHandlers.ofString());
		return gson.fromJson(response.body(), User.class);
	}

	public void testConnection() throws IOException, InterruptedException {
		var url = host + "/user";
		var httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		httpClient.send(httpRequest, BodyHandlers.discarding());
	}

}
