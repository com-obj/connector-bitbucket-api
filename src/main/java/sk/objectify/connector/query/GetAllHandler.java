package sk.objectify.connector.query;

import java.io.IOException;

import org.identityconnectors.framework.common.objects.ResultsHandler;

import sk.objectify.connector.endpoint.BitbucketEndpoint;

class GetAllHandler {
	private BitbucketEndpoint endpoint;

	GetAllHandler(BitbucketEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	void handle(String workspace, ResultsHandler handler) throws IOException, InterruptedException {
		endpoint.getUsersFromWorkspace(workspace).forEach(user -> handler.handle(ConnectorObjectMapper.map(user)));
	}
}
