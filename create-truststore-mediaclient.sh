cp base-truststore.ks client-truststore.ks
echo "Use password: changeit"
keytool -importcert -file mediaserver.cert -alias mediaserver -keystore client-truststore.ks
