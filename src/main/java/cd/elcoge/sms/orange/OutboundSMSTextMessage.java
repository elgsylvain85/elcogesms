package cd.elcoge.sms.orange;

public class OutboundSMSTextMessage {

	private String message;

	public OutboundSMSTextMessage(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
