package sk.objectify.connector.query;

import java.io.IOException;

import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;

import sk.objectify.connector.endpoint.BitbucketEndpoint;

class EqualsFilterHandler {
	private BitbucketEndpoint endpoint;

	EqualsFilterHandler(BitbucketEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	void handle(EqualsFilter equalsFilter, ResultsHandler handler) throws IOException, InterruptedException {
		if (equalsFilter.getAttribute() instanceof Uid) {

			var uid = (Uid) equalsFilter.getAttribute();
			if (uid.getUidValue() == null) {
				throw new InvalidAttributeValueException(
						"Value of uid attribute not provided for query: " + equalsFilter);
			}

			handler.handle(ConnectorObjectMapper.map(endpoint.getUser(uid.getUidValue())));
		} else {
			throw new IllegalStateException("EqualsFilter for " + equalsFilter.getAttribute() + " is not implemented.");
		}
	}
}
