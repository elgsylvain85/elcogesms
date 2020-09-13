package cd.elcoge.sms.orange;

public class OutboundSMSMessageRequest {

	private String address;
	private String senderAddress;
	private String senderName;
	private OutboundSMSTextMessage outboundSMSTextMessage;

	public OutboundSMSMessageRequest(String address, String senderAddress, String senderName,
			OutboundSMSTextMessage outboundSMSTextMessage) {
		super();
		this.address = address;
		this.senderAddress = senderAddress;
		this.senderName = senderName;
		this.outboundSMSTextMessage = outboundSMSTextMessage;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public OutboundSMSTextMessage getOutboundSMSTextMessage() {
		return outboundSMSTextMessage;
	}

	public void setOutboundSMSTextMessage(OutboundSMSTextMessage outboundSMSTextMessage) {
		this.outboundSMSTextMessage = outboundSMSTextMessage;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	

}
