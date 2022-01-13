package sk.objectify.connector;

import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;

import sk.objectify.connector.bitbucket.BitbucketUserConst;

class SchemaProvider {
	private SchemaProvider() {
	}

	static Schema provide() {
		var userObjClassBuilder = new ObjectClassInfoBuilder();
		userObjClassBuilder.setType(ObjectClass.ACCOUNT_NAME);

		var attr = new AttributeInfoBuilder(BitbucketUserConst.UUID).setRequired(false).setType(String.class)
				.setCreateable(false).setUpdateable(false).setReadable(true).build();
		userObjClassBuilder.addAttributeInfo(attr);

		attr = new AttributeInfoBuilder(BitbucketUserConst.DISPLAY_NAME).setRequired(true).setType(String.class)
				.setCreateable(true).setUpdateable(true).setReadable(true).build();
		userObjClassBuilder.addAttributeInfo(attr);

		attr = new AttributeInfoBuilder(BitbucketUserConst.NICKNAME).setRequired(false).setType(String.class)
				.setCreateable(true).setUpdateable(true).setReadable(true).build();
		userObjClassBuilder.addAttributeInfo(attr);

		var schemaBuilder = new SchemaBuilder(BitbucketConnector.class);
		schemaBuilder.defineObjectClass(userObjClassBuilder.build());
		return schemaBuilder.build();
	}
}
