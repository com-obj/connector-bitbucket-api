package sk.objectify.connector.query;

import java.io.IOException;

import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import sk.objectify.connector.endpoint.BitbucketEndpoint;

public class QueryManager {
	private String workspace;
	private EqualsFilterHandler equalsFilterHandler;
	private GetAllHandler getAllHandler;

	public QueryManager(BitbucketEndpoint endpoint, String workspace) {
		this.workspace = workspace;
		this.equalsFilterHandler = new EqualsFilterHandler(endpoint);
		this.getAllHandler = new GetAllHandler(endpoint);
	}

	public void createAndProcessQuery(ObjectClass objectClass, Filter query, ResultsHandler handler)
			throws IOException, InterruptedException {
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			processObjectQuery(query, handler);
		} else {
			throw new UnsupportedOperationException("Attribute of type ObjectClass is not supported.");
		}
	}

	private void processObjectQuery(Filter query, ResultsHandler handler) throws IOException, InterruptedException {
		if (query instanceof EqualsFilter) {
			equalsFilterHandler.handle((EqualsFilter) query, handler);
		} else if (query instanceof ContainsFilter) {
			throw new IllegalStateException("ContainsFilter not implemented.");
		} else {
			getAllHandler.handle(workspace, handler);
		}
	}
}
