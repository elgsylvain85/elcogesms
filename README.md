# apropos
Cette API est un facilisateur d'envoi des SMS. Il implemente plusieurs platformes entre autre celle du provider Orange.

# demo

# provider Orange pour developpeur
Pour ce provider, tout d'abord creer et configurer le fichier 'smsbyorangeapi.properties' qui sera charg? via 'getClassLoader().getResourceAsStream("smsbyorangeapi.properties")' :

	sender.number=tel\:+XXX
	sender.name=XXX
	application.id=XXX
	client.secret=XXX
	authorization.header=XXX
	client.id=XXX

Ensuite dans le code :

	SMSProvider smsprovider  = new SMSByOrangeAPI();

	smsprovider.sendSMS("+XXX", "Bonjour");
	System.out.println(((SMSByOrangeAPI)smsprovider).getBalance());
