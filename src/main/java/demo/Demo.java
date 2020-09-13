package demo;

import cd.elcoge.sms.orange.SMSByOrangeAPI;

public class Demo {

	public static void main(String[] args) throws Throwable {

		SMSByOrangeAPI smsbyorangeapi = new SMSByOrangeAPI();

		smsbyorangeapi.sendSMS("+243814524517", "Bonjour");
		System.out.println(smsbyorangeapi.getBalance());
	}
}
