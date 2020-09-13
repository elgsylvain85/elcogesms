package cd.elcoge.sms.orange;

public class OrangeBuild {

	private OutboundSMSMessageRequest outboundSMSMessageRequest;

	public OrangeBuild(OutboundSMSMessageRequest outboundSMSMessageRequest) {
		super();
		this.outboundSMSMessageRequest = outboundSMSMessageRequest;
	}

	public OutboundSMSMessageRequest getOutboundSMSMessageRequest() {
		return outboundSMSMessageRequest;
	}

	public void setOutboundSMSMessageRequest(OutboundSMSMessageRequest outboundSMSMessageRequest) {
		this.outboundSMSMessageRequest = outboundSMSMessageRequest;
	}

}
