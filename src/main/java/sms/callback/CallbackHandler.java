package sms.callback;

import static sms.callback.util.PropertyReaderUtil.getInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sms.callback.util.IConstants;
import sms.callback.util.SMSUtil;

/**
 * Handles the webhook configured at Twilio to forward the SMS to AerFrame
 *
 * @author Karan.Kapoor@aeris.net
 */
public class CallbackHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String twilioSMSBody = req.getParameter(IConstants.REQ_PARAM_BODY);
		System.out.println("Twilio sent SMS body as " + twilioSMSBody);
		processCall(twilioSMSBody);
		sendEmptyTwimlResponse(resp);
	}

	@Override
	public void destroy() {
		// any cleanup to perform
	}

	/**
	 * Processes the call as received from Twilio.
	 * 
	 * @param smsBody
	 */
	protected static void processCall(final String smsBody) {
		if (smsBody == null || smsBody.trim().isEmpty()) {
			System.out.println(
					"SMS Body received empty, not correctly formatted, the format should be <{PhoneNumber}> {SMS Content}");
			throw new RuntimeException(
					"SMS Body not correctly formatted, the format should be <{PhoneNumber}> {SMS Content}");
		}

		if (smsBody.indexOf("<") == -1 || smsBody.indexOf(">") == -1) {
			System.out.println("SMS Body received " + smsBody
					+ " not correctly formatted, the format should be <{PhoneNumber}> {SMS Content}");
			throw new RuntimeException(
					"SMS Body not correctly formatted, the format should be <{PhoneNumber}> {SMS Content}");
		}

		final String toNumber = SMSUtil.getToNumber(smsBody);
		final String smsContent = SMSUtil.getSMSContent(smsBody);

		sendMsgToAerFrame(getInstance().getProperty(IConstants.KEY_AERFRAME_API_ACCOUNT_ID),
				getInstance().getProperty(IConstants.KEY_AERFRAME_API_APP_SHORT_NAME), toNumber, smsContent);
	}

	/**
	 * Sends the sms message to the AerFrame API to deliver this to the Aeris
	 * SIM
	 * 
	 * @param accountNumber
	 * @param appName
	 * @param toNumber
	 * @param smsContent
	 */
	private static void sendMsgToAerFrame(final String accountNumber, final String appName, final String toNumber,
			final String smsContent) {
		HttpURLConnection conn = null;
		try {
			// aerframe accepts IMSI and not MSISDN, so fetch the IMSI mapped to
			// MSISDN
			final String imsi = SMSUtil.getIMSIFromNumber(toNumber);
			if (imsi == null) {
				throw new RuntimeException("IMSI not found for the given MSISDN " + toNumber);
			}

			final URL url = new URL(getInstance().getProperty(IConstants.KEY_AERFRAME_API_BASE_URL) + accountNumber
					+ "/outbound/" + appName + "/requests?apiKey="
					+ getInstance().getProperty(IConstants.KEY_AERFRAME_API_ACCESS_KEY));

			conn = (HttpURLConnection) url.openConnection();
			SMSUtil.setHeaders(conn);

			final String input = SMSUtil.getRequestPayload(smsContent, imsi);
			System.out.println("Aerframe API request payload " + input);

			final OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			System.out.println("Response Code " + conn.getResponseCode());

			final BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception occurred while calling AerFrame API to send message", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Returns an empty response to Twilio once the message is sent to AerFrame
	 * 
	 * @param resp
	 * @throws IOException
	 */
	private static void sendEmptyTwimlResponse(HttpServletResponse resp) throws IOException {
		resp.setContentType(IConstants.CONTENT_TYPE_XML);
		PrintWriter out = resp.getWriter();
		out.println(SMSUtil.getEmptyTwimlResponse());
		out.flush();
	}
}