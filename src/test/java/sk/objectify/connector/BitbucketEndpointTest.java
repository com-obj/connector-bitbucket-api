package sk.objectify.connector;

import static org.mockito.Mockito.times;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.objectify.connector.endpoint.BitbucketEndpoint;
import sk.objectify.connector.endpoint.dto.User;

@Test
class BitbucketEndpointTest {
	static final String HOST = "https://api.bitbucket.org/2.0";
	Gson gson = new GsonBuilder().create();

	@Test
	void testGetUser() throws IOException, InterruptedException, URISyntaxException {
		HttpClient httpClient = Mockito.mock(HttpClient.class);
		HttpResponse<String> response = Mockito.mock(HttpResponse.class);
		Mockito.when(response.body()).thenReturn("{\"display_name\": \"Test Test\"}");
		Mockito.when(httpClient.send(ArgumentMatchers.any(), ArgumentMatchers.eq(BodyHandlers.ofString())))
				.thenReturn(response);

		String uid = "userId";
		var user = new BitbucketEndpoint(HOST, httpClient, gson).getUser(uid);

		var captor = ArgumentCaptor.forClass(HttpRequest.class);
		Mockito.verify(httpClient).send(captor.capture(), ArgumentMatchers.eq(BodyHandlers.ofString()));
		Mockito.verify(response).body();

		verifyRequest(captor.getValue(), "https://api.bitbucket.org/2.0/users/" + uid);

		Assertions.assertEquals("Test Test", user.getDisplayName());
	}

	@Test
	void testGetUsersFromWorkspace() throws IOException, InterruptedException, URISyntaxException {
		HttpClient httpClient = Mockito.mock(HttpClient.class);
		HttpResponse<String> response = Mockito.mock(HttpResponse.class);
		Mockito.when(response.body()).thenReturn(
				"{\"pagelen\": 50,\"values\":[{\"user\": {\"display_name\": \"Test Test\"}}],\"page\": 1,\"size\": 1}");
		Mockito.when(httpClient.send(ArgumentMatchers.any(), ArgumentMatchers.eq(BodyHandlers.ofString())))
				.thenReturn(response);

		String workspace = "workspace";
		var users = new BitbucketEndpoint(HOST, httpClient, gson).getUsersFromWorkspace(workspace);

		var captor = ArgumentCaptor.forClass(HttpRequest.class);
		Mockito.verify(httpClient).send(captor.capture(), ArgumentMatchers.eq(BodyHandlers.ofString()));
		Mockito.verify(response).body();

		verifyRequest(captor.getValue(), "https://api.bitbucket.org/2.0/workspaces/" + workspace
				+ "/members?fields=-values.links,-values.workspace,-values.user.links&page=1");

		Assertions.assertEquals(1, users.size());
		User user = users.get(0);
		Assertions.assertEquals("Test Test", user.getDisplayName());
	}

	@Test
	void testPagingGetUsersFromWorkspace() throws IOException, InterruptedException, URISyntaxException {
		HttpClient httpClient = Mockito.mock(HttpClient.class);
		HttpResponse<String> response = Mockito.mock(HttpResponse.class);
		Mockito.when(response.body()).thenReturn(
				"{\"pagelen\": 1,\"values\":[{\"user\": {\"display_name\": \"Test Test\"}}],\"page\": 1,\"size\": 1}");

		HttpResponse<String> secondResponse = Mockito.mock(HttpResponse.class);
		Mockito.when(secondResponse.body()).thenReturn(
				"{\"pagelen\": 2,\"values\":[{\"user\": {\"display_name\": \"Tset Tset\"}}],\"page\": 2,\"size\": 1}");

		Mockito.when(httpClient.send(ArgumentMatchers.any(), ArgumentMatchers.eq(BodyHandlers.ofString())))
				.thenReturn(response).thenReturn(secondResponse);

		String workspace = "workspace";
		var users = new BitbucketEndpoint(HOST, httpClient, gson).getUsersFromWorkspace(workspace);

		var captor = ArgumentCaptor.forClass(HttpRequest.class);
		Mockito.verify(httpClient, times(2)).send(captor.capture(), ArgumentMatchers.eq(BodyHandlers.ofString()));
		Mockito.verify(response).body();
		Mockito.verify(secondResponse).body();

		var requests = captor.getAllValues();

		verifyRequest(requests.get(0), "https://api.bitbucket.org/2.0/workspaces/" + workspace
				+ "/members?fields=-values.links,-values.workspace,-values.user.links&page=1");
		verifyRequest(requests.get(1), "https://api.bitbucket.org/2.0/workspaces/" + workspace
				+ "/members?fields=-values.links,-values.workspace,-values.user.links&page=2");

		Assertions.assertEquals(2, users.size());
		User user = users.get(0);
		Assertions.assertEquals("Test Test", user.getDisplayName());

		user = users.get(1);
		Assertions.assertEquals("Tset Tset", user.getDisplayName());
	}

	@Test
	void testTestCall() throws IOException, InterruptedException, URISyntaxException {
		HttpClient httpClient = Mockito.mock(HttpClient.class);
		HttpResponse<String> response = Mockito.mock(HttpResponse.class);
		Mockito.when(httpClient.send(ArgumentMatchers.any(), ArgumentMatchers.eq(BodyHandlers.discarding())))
				.thenReturn(Mockito.mock(HttpResponse.class));

		new BitbucketEndpoint(HOST, httpClient, gson).testConnection();

		var captor = ArgumentCaptor.forClass(HttpRequest.class);
		Mockito.verify(httpClient).send(captor.capture(), ArgumentMatchers.eq(BodyHandlers.discarding()));

		verifyRequest(captor.getValue(), "https://api.bitbucket.org/2.0/user");
	}

	private void verifyRequest(HttpRequest request, String url) throws URISyntaxException {
		Assertions.assertEquals("GET", request.method());
		Assertions.assertEquals(new URI(url), request.uri());
	}
}
