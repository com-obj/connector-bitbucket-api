package sk.objectify.connector.endpoint;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.evolveum.polygon.common.GuardedStringAccessor;

import sk.objectify.connector.BitbucketConfiguration;

public class HttpClientFactory {
	private HttpClientFactory() {
	}

	public static HttpClient getHttpClient(BitbucketConfiguration configuration) {
		return HttpClient.newBuilder().authenticator(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				var accessorSecret = new GuardedStringAccessor();
				configuration.getApiPassword().access(accessorSecret);
				return new PasswordAuthentication(configuration.getUsername(), accessorSecret.getClearChars());
			}
		}).connectTimeout(Duration.ofSeconds(60)).sslContext(initSslContext()).version(Version.HTTP_1_1).build();
	}

	private static SSLContext initSslContext() {
		try {
			var sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			return sslContext;
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new ConnectorException("Failed to configure ssl.", e);
		}
	}

	private static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };
}
