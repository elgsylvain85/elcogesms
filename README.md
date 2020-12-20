# About
elcogesms is a API which encapsulates processes from different providers and facilitates sending sms.

# Configuration

## ORANGE HTTP API
It's mandatory to complete "smsbyorangeapi.properties" file located as resource:
- sender.number=tel\:+
- sender.name=
- application.id=
- client.secret=
- authorization.header=
- client.id=

## Orange Integration Code demo
package cd.elcoge.sms.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cd.elcoge.sms.SMSProvider;
import cd.elcoge.sms.orange.SMSByOrangeAPI;


public class ElcogesmsTest {

	SMSProvider smsprovider;

	@Before
	public void init() throws Exception {
		smsprovider = new SMSByOrangeAPI();
	}

	@After
	public void after() throws Exception {
		smsprovider.close();
	}

	@Test
	public void getOrangeBalance() throws Throwable {

		String balance = ((SMSByOrangeAPI) smsprovider).getBalance();
		System.out.println(balance);

		assertTrue(balance.getClass()==String.class);
	}

	@Test
	public void sendSMS() throws Exception {

		assertTrue(smsprovider.sendSMS("+xxxxxxxxxx", "Bonjour"));
	}
}

