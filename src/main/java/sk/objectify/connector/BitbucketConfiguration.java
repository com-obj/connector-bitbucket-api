package sk.objectify.connector;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.framework.spi.StatefulConfiguration;

public class BitbucketConfiguration extends AbstractConfiguration implements StatefulConfiguration {
	private String workspace;
	private String username;
	private GuardedString apiPassword;
	private String bitbucketHost = "https://api.bitbucket.org/2.0";

	@ConfigurationProperty(order = 10, displayMessageKey = "Workspace", helpMessageKey = "Name of workspace, which is account member of", required = true)
	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	@ConfigurationProperty(order = 20, displayMessageKey = "Username", helpMessageKey = "Username of account (used as login)", required = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@ConfigurationProperty(order = 30, displayMessageKey = "ApiPassword", helpMessageKey = "Generated API password of account (not regular password, it has to be generated).", required = true, confidential = true)
	public GuardedString getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(GuardedString apiPassword) {
		this.apiPassword = apiPassword;
	}

	@ConfigurationProperty(order = 40, displayMessageKey = "BitbucketHost", helpMessageKey = "Configurable bitbucket host in case bitbucket instance it is different from cloud bitbucket.", required = false)
	public String getBitbucketHost() {
		return bitbucketHost;
	}

	public void setBitbucketHost(String bitbucketHost) {
		this.bitbucketHost = bitbucketHost;
	}

	@Override
	public void validate() {
		if (StringUtil.isBlank(workspace)) {
            throw new ConfigurationException("Workspace cannot be empty.");
        }        

        if (StringUtil.isBlank(username)) {
            throw new ConfigurationException("Username cannot be empty.");
        }
        
        if ("".equals(apiPassword)) {
            throw new ConfigurationException("ApiPassword Secret cannot be empty.");
        }
	}

	@Override
	public void release() {
		this.workspace = null;
		this.username = null;
        this.apiPassword.dispose();
        this.bitbucketHost = null;
	}

}
