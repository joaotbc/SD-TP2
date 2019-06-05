keytool -genkey -alias mediaserver -keyalg RSA -validity 365 -keystore mediaserver.ks -storetype pkcs12
keytool -exportcert -alias mediaserver -keystore mediaserver.ks -file mediaserver.cert
