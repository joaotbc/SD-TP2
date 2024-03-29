package microgram.impl.rest.media;

import static utils.Log.Log;

import java.net.URI;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import discovery.Discovery;
import microgram.api.rest.RestMedia;
import microgram.impl.rest.utils.GenericExceptionMapper;
import microgram.impl.rest.utils.PrematchingRequestFilter;
import utils.IP;

public class MediaRestServer {

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}
	
	public static final int PORT = 12222;
	public static final String SERVICE = "Microgram-MediaStorage";
	public static String SERVER_BASE_URI = "https://%s:%s/rest";

	public static void main(String[] args) throws Exception {
		
		System.setProperty("java.net.preferIPv4Stack", "true");

		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

		String serviceURI = serverURI + RestMedia.PATH;

		Discovery.announce(SERVICE, serviceURI);
		ResourceConfig config = new ResourceConfig();

		config.register(new RestMediaResources(serviceURI));

		config.register(new GenericExceptionMapper());
		config.register(new PrematchingRequestFilter());

		JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, "0.0.0.0")), config, SSLContext.getDefault());

		Log.fine(String.format("MediaStorage Rest Server ready @ %s\n", serverURI));

	}
}
