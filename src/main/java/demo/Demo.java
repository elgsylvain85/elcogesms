package demo;

import cd.elcoge.sms.SMSProvider;
import cd.elcoge.sms.orange.SMSByOrangeAPI;

public class Demo {

	public static void main(String[] args) throws Throwable {

		SMSProvider smsprovider  = new SMSByOrangeAPI();

		smsprovider.sendSMS("+243814524517", "Bonjour");
		System.out.println(((SMSByOrangeAPI)smsprovider).getBalance());
	}
}
