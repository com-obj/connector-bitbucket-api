package sk.objectify.connector;

import java.io.IOException;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.objectify.connector.endpoint.BitbucketEndpoint;
import sk.objectify.connector.endpoint.HttpClientFactory;
import sk.objectify.connector.query.QueryManager;

@ConnectorClass(displayNameKey = "bitbucketconnector.connector.display", configurationClass = BitbucketConfiguration.class)
public class BitbucketConnector implements Connector, SearchOp<Filter>, TestOp, SchemaOp {
	private BitbucketConfiguration configuration;
	private BitbucketEndpoint endpoint;
	private QueryManager queryManager;

	@Override
	public void init(Configuration configuration) {
		this.configuration = (BitbucketConfiguration) configuration;
		this.configuration.validate();

		Gson gson = new GsonBuilder().create();
		endpoint = new BitbucketEndpoint(this.configuration.getBitbucketHost(), HttpClientFactory.getHttpClient(this.configuration), gson);
		queryManager = new QueryManager(endpoint, this.configuration.getWorkspace());

	}

	@Override
	public void dispose() {
		configuration = null;
		endpoint = null;
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions arg1) {
		return CollectionUtil::newList;
	}

	@Override
	public void executeQuery(ObjectClass objectClass, Filter query, ResultsHandler handler, OperationOptions options) {
		if (objectClass == null) {
			throw new InvalidAttributeValueException("Attribute of type ObjectClass is not provided.");
		}

		if (handler == null) {
			throw new InvalidAttributeValueException("Attribute of type ResultsHandler is not provided.");
		}

		if (options == null) {
			throw new InvalidAttributeValueException("Attribute of type OperationOptions is not provided.");
		}

		try {
			queryManager.createAndProcessQuery(objectClass, query, handler);
		} catch (IOException | InterruptedException e) {
			throw new ConnectorIOException(e);
		}
	}

	@Override
	public void test() {
		try {
			endpoint.testConnection();
		} catch (IOException | InterruptedException e) {
			throw new ConnectorIOException(e);
		}
	}

	@Override
	public Schema schema() {
		return SchemaProvider.provide();
	}

}
