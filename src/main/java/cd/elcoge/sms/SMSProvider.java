package cd.elcoge.sms;

public interface SMSProvider extends AutoCloseable{

	public boolean sendSMS(String to, String content) throws Exception;

}
