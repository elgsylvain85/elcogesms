# apropos
Cette API est un facilisateur d'envoi des SMS. Il implemente plusieurs platformes entre autre celle du provider Orange.

# demo

# provider Orange pour developpeur
Pour ce provider, tout d'abord creer et configurer le fichier 'smsbyorangeapi.properties' a la racine du projet comme suite :

	sender.number=tel\:+XXX
	sender.name=XXX
	access.token=[ce parametre est rempli automatiquement par l'API]
	application.id=XXX
	client.secret=XXX
	authorization.header=XXX
	client.id=XXX

Ensuite dans le code :

	SMSByOrangeAPI smsbyorangeapi = new SMSByOrangeAPI();
	smsbyorangeapi.sendSMS("+xxx", "Bonjour");
	/* si necessaire */
	System.out.println(smsbyorangeapi.getBalance());
