package cd.elcoge.sms.orange;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cd.elcoge.sms.SMSProvider;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SMSByOrangeAPI implements SMSProvider {

	final public static String SEND_MESSAGE_URL = "https://api.orange.com/smsmessaging/v1/outbound/{{dev_phone_number}}/requests";
	final public static String GET_SMS_BALANCE_URL = "https://api.orange.com/sms/admin/v1/contracts";
	final public static String GET_TOKEN_URL = "https://api.orange.com/oauth/v2/token";

	final public static int SMS_MAX_LENGTH = 400;

	private String sendernumber;
	private String sendername;
	private String authorizationheader;
	private String accesstoken;

	private Gson gson;

	public SMSByOrangeAPI() throws Exception {

		gson = new Gson();

		Properties pp = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("smsbyorangeapi.properties");
		} catch (FileNotFoundException e) {
			throw new Exception("smsbyorangeapi.properties not found");
		}

		pp.load(in);

		sendernumber = pp.getProperty("sender.number");
		sendername = pp.getProperty("sender.name");
		authorizationheader = pp.getProperty("authorization.header");
		accesstoken = pp.getProperty("access.token");

		in.close();
	}

	@Override
	public boolean sendSMS(String to, String content) throws Throwable {

		boolean result = false;

		if (content != null) {

			if (content.length() > SMS_MAX_LENGTH) {
				content = content.substring(0, SMS_MAX_LENGTH);
				System.err.println("SMS part of the content has been cut");
			}
		}

		OrangeBuild ob = new OrangeBuild(new OutboundSMSMessageRequest("tel:" + to, sendernumber, sendername,
				new OutboundSMSTextMessage(content)));

		HttpUrl httpurl = HttpUrl
				.parse(SEND_MESSAGE_URL.replace("{{dev_phone_number}}", this.sendernumber).replace(":+", "%3A%2B"))
				.newBuilder().build();

		RequestBody requestbody = RequestBody.create(gson.toJson(ob, OrangeBuild.class),
				MediaType.parse("application/json; charset=utf-8"));

		Request request = new Request.Builder().addHeader("Content-Type", "application/json")
				.addHeader("Authorization", accesstoken).url(httpurl).post(requestbody).build();

		OkHttpClient client = new OkHttpClient();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				result = true;

			} else if (response.code() == 401) {
				getToken();
				System.err.println(
						"an error 401 has been detected and refresh Token has been called, please invoke again the same method");
				throw new Exception(response.body().string());
			} else {
				throw new Exception(response.body().string());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		return result;

	}

	public void getToken() throws Throwable {

		HttpUrl httpurl = HttpUrl.parse(GET_TOKEN_URL).newBuilder().build();

		RequestBody requestbody = RequestBody.create("grant_type=client_credentials",
				MediaType.parse("application/x-www-form-urlencoded"));

		Request request = new Request.Builder().addHeader("Authorization", this.authorizationheader).url(httpurl)
				.post(requestbody).build();

		OkHttpClient client = new OkHttpClient();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				String jsonstring = response.body().string();

				JsonElement element = gson.fromJson(jsonstring, JsonElement.class);
				JsonObject jsonObj = element.getAsJsonObject();

				accesstoken = jsonObj.get("token_type").getAsString() + " " + jsonObj.get("access_token").getAsString();

				Properties pp = new Properties();
				FileInputStream in;
				FileOutputStream out;
				try {
					in = new FileInputStream("smsbyorangeapi.properties");
					
				} catch (FileNotFoundException e) {
					throw new Exception("smsbyorangeapi.properties not found");
				}

				pp.load(in);
				in.close();
				
				pp.put("access.token", accesstoken);

				out = new FileOutputStream("smsbyorangeapi.properties");
				pp.store(out, null);
				out.close();

			} else {
				throw new Exception(response.body().string());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getBalance() throws Throwable {

		HttpUrl httpurl = HttpUrl.parse(GET_SMS_BALANCE_URL).newBuilder().build();

		Request request = new Request.Builder().addHeader("Authorization", accesstoken).url(httpurl).get().build();

		OkHttpClient client = new OkHttpClient();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				String jsonstring = response.body().string();

				return jsonstring;

			} else if (response.code() == 401) {
				getToken();
				System.err.println(
						"an error 401 has been detected and refresh Token has been called, please invoke again the same method");
				throw new Exception(response.body().string());
			} else {
				throw new Exception(response.body().string());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
}
