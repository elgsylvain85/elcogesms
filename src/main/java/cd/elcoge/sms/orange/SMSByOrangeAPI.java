package cd.elcoge.sms.orange;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cd.elcoge.sms.SMSProvider;

public class SMSByOrangeAPI implements SMSProvider {

	final public static String SEND_MESSAGE_URL = "https://api.orange.com/smsmessaging/v1/outbound/{{dev_phone_number}}/requests";
	final public static String GET_SMS_BALANCE_URL = "https://api.orange.com/sms/admin/v1/contracts";
	final public static String GET_TOKEN_URL = "https://api.orange.com/oauth/v2/token";
	final private static String TOKEN_FILE = SMSByOrangeAPI.class.getName() + "TokenFile";

	final public static int SMS_MAX_LENGTH = 400;

	private String sendernumber;
	private String sendername;
	private String authorizationheader;
	private String accesstoken;

	private Gson gson;

	public SMSByOrangeAPI() throws Exception {
		this.accesstoken = "";

		gson = new Gson();

		Properties pp = new Properties();
		InputStream in;
		try {
			in = getClass().getClassLoader().getResourceAsStream("smsbyorangeapi.properties");
		} catch (Exception e) {
			throw new Exception("smsbyorangeapi.properties not found");
		}

		pp.load(in);

		sendernumber = pp.getProperty("sender.number");
		sendername = pp.getProperty("sender.name");
		authorizationheader = pp.getProperty("authorization.header");

		in.close();

		FileReader fr = null;

		try {
			fr = new FileReader(new File(System.getProperty("java.io.tmpdir"), TOKEN_FILE));
			int ca = 0;
			while ((ca = fr.read()) >= 0) {
				accesstoken += (char) ca;
			}

			fr.close();
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new Exception("error reading token");
			System.err.println("token file not found " + TOKEN_FILE);
		}

	}

	@Override
	public boolean sendSMS(String to, String content) throws Exception {

		boolean result = false;

		if (content != null) {

			if (content.length() > SMS_MAX_LENGTH) {
				// content = content.substring(0, SMS_MAX_LENGTH);
				// System.err.println("SMS part of the content has been cut");
				throw new Exception(String.format("SMS Content is great than %d -> %s", SMS_MAX_LENGTH, content));
			}
		}

		OrangeBuild ob = new OrangeBuild(new OutboundSMSMessageRequest("tel:" + to, sendernumber, sendername,
				new OutboundSMSTextMessage(content)));

		// HttpUrl httpurl = HttpUrl
		// .parse(SEND_MESSAGE_URL.replace("{{dev_phone_number}}",
		// this.sendernumber).replace(":+", "%3A%2B"))
		// .newBuilder().build();

		// RequestBody requestbody = RequestBody.create(gson.toJson(ob,
		// OrangeBuild.class),
		// MediaType.parse("application/json; charset=utf-8"));

		// Request request = new Request.Builder().addHeader("Content-Type",
		// "application/json")
		// .addHeader("Authorization",
		// accesstoken).url(httpurl).post(requestbody).build();

		// OkHttpClient client = new OkHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						SEND_MESSAGE_URL.replace("{{dev_phone_number}}", this.sendernumber).replace(":+", "%3A%2B")))
				.header("Content-Type", "application/json; charset=utf-8").header("Authorization", accesstoken)
				.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ob, OrangeBuild.class))).build();

		HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(999)).build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 200 & response.statusCode() <= 299) {
				result = true;

			} else if (response.statusCode() == 401) {
				getToken();
				System.err.println(
						"an error 401 has been detected and refresh Token has been called, please invoke again the same method");
				throw new Exception(response.body());
			} else {
				throw new Exception(response.body());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		return result;

	}

	public void getToken() throws Exception {

		// HttpUrl httpurl = HttpUrl.parse(GET_TOKEN_URL).newBuilder().build();

		// RequestBody requestbody = RequestBody.create("grant_type=client_credentials",
		// MediaType.parse("application/x-www-form-urlencoded"));

		// Request request = new Request.Builder().addHeader("Authorization",
		// this.authorizationheader).url(httpurl)
		// .post(requestbody).build();

		// OkHttpClient client = new OkHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GET_TOKEN_URL))
				.header("Authorization", this.authorizationheader)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials")).build();

		HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(999)).build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 200 & response.statusCode() <= 299) {
				String jsonstring = response.body();

				JsonElement element = gson.fromJson(jsonstring, JsonElement.class);
				JsonObject jsonObj = element.getAsJsonObject();

				accesstoken = jsonObj.get("token_type").getAsString() + " " + jsonObj.get("access_token").getAsString();

				FileWriter fw;
				try {
					fw = new FileWriter(new File(System.getProperty("java.io.tmpdir"), TOKEN_FILE));
					fw.write(accesstoken);
					fw.flush();

				} catch (Exception e) {
					throw new Exception("error saving new token");
				}

				fw.close();

			} else {
				throw new Exception(response.body());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getBalance() throws Throwable {

		// HttpUrl httpurl = HttpUrl.parse(GET_SMS_BALANCE_URL).newBuilder().build();

		// Request request = new Request.Builder().addHeader("Authorization",
		// accesstoken).url(httpurl).get().build();

		// OkHttpClient client = new OkHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GET_SMS_BALANCE_URL))
				.header("Authorization", this.accesstoken).GET().build();

		HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(999)).build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 200 & response.statusCode() <= 299) {
				String jsonstring = response.body();

				return jsonstring;

			} else if (response.statusCode() == 401) {
				getToken();
				System.err.println(
						"an error 401 has been detected and refresh Token has been called, please invoke again the same method");
				throw new Exception(response.body());
			} else {
				throw new Exception(response.body());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@Override
	public void close() throws Exception {

	}
}
