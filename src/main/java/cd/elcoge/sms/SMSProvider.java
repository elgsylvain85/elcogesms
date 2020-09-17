package cd.elcoge.sms;

public interface SMSProvider {

	public boolean sendSMS(String to, String content) throws Exception;

}
