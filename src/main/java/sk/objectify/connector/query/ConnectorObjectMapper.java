package sk.objectify.connector.query;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

import sk.objectify.connector.endpoint.dto.User;

class ConnectorObjectMapper {
	private ConnectorObjectMapper() {
	}

	static ConnectorObject map(User user) {
		var builder = new ConnectorObjectBuilder();
		builder.setObjectClass(ObjectClass.ACCOUNT);
		builder.setUid(new Uid(user.getUuid()));
		builder.setName(user.getDisplayName());
		builder.addAttribute("uuid", user.getUuid());
		builder.addAttribute("display_name", user.getDisplayName());
		builder.addAttribute("nickname", user.getNickname());
		return builder.build();
	}
}
