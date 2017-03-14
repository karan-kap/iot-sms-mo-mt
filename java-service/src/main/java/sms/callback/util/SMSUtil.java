package sms.callback.util;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic utility class with helper / util methods
 * 
 * @author Karan.Kapoor@aeris.net
 *
 */
public final class SMSUtil {
	private static Map<String, String> imsiMapping = null;

	static {
		loadMappingCache();
	}

	private SMSUtil() {
		//
	}

	/**
	 * Loads the mapping of IMSI and MSISDN into an in-memory cache. This will
	 * be used where the DB access is not available
	 */
	private static void loadMappingCache() {
		final String fetchMappingFromDB = PropertyReaderUtil.getInstance()
				.getProperty(IConstants.KEY_FETCH_MAPPING_FROM_DB);

		if (fetchMappingFromDB != null && fetchMappingFromDB.trim().equalsIgnoreCase(IConstants.FALSE)) {
			final String msisdnImsiString = PropertyReaderUtil.getInstance()
					.getProperty(IConstants.KEY_MSISDN_IMSI_MAPPING);
			if (msisdnImsiString != null && !msisdnImsiString.trim().isEmpty()) {
				imsiMapping = new HashMap<String, String>();
				final String[] pairList = msisdnImsiString.split(";");
				for (final String pair : pairList) {
					final String[] identifiers = pair.split(":");
					imsiMapping.put(identifiers[0], identifiers[1]);
				}
			}
		}
	}

	/**
	 * Creates the request payload sent to AerFrame API
	 * 
	 * @param smsContent
	 * @param imsi
	 * @return
	 */
	public static String getRequestPayload(final String smsContent, final String imsi) {
		final String appName = PropertyReaderUtil.getInstance().getProperty(IConstants.KEY_AERFRAME_API_APP_SHORT_NAME);
		String input = "{\"address\":[\"" + imsi + "\"],\"senderAddress\":\"" + appName
				+ "\",\"outboundSMSTextMessage\":{\"message\":\"" + smsContent
				+ "\",\"clientCorrelator\":\"1234\",\"senderName\":\"PiTestClient\"}}";
		return input;
	}

	/**
	 * Returns an emtpy XML Twilio response to be returned back to Twilio after
	 * the message is successfuly sent to AerFrame API
	 * 
	 * @return
	 */
	public static final String getEmptyTwimlResponse() {
		final StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<Response>");
		sb.append("</Response>");
		return sb.toString();
	}

	/**
	 * Returns the Aeris "To" Number from the SMS Body
	 * 
	 * @param smsBody
	 * @return
	 */
	public static String getToNumber(final String smsBody) {
		return smsBody.substring(smsBody.indexOf("<") + 1, smsBody.indexOf(">"));
	}

	/**
	 * Returns the actual content to be sent in the SMS
	 * 
	 * @param smsBody
	 * @return
	 */
	public static String getSMSContent(final String smsBody) {
		return smsBody.substring(smsBody.indexOf(">") + 2, smsBody.length());
	}

	/**
	 * Sets the connection headers
	 * 
	 * @param conn
	 * @throws ProtocolException
	 */
	public static void setHeaders(final HttpURLConnection conn) throws ProtocolException {
		conn.setDoOutput(true);
		conn.setRequestMethod(IConstants.HTTP_METHOD_POST);
		conn.setRequestProperty("Content-Type", IConstants.CONTENT_TYPE_JSON);
		conn.setRequestProperty("Accept", IConstants.CONTENT_TYPE_JSON);
	}

	/**
	 * Returns the IMSI of the MSISDN sent in the message body.
	 * 
	 * @param toNumber
	 * @return
	 */
	public static final String getIMSIFromNumber(final String toNumber) {
		final String fetchMappingFromDB = PropertyReaderUtil.getInstance()
				.getProperty(IConstants.KEY_FETCH_MAPPING_FROM_DB);
		System.out.println("Fetch Mapping from DB is [" + fetchMappingFromDB + "]");

		if (fetchMappingFromDB != null && fetchMappingFromDB.trim().equalsIgnoreCase(IConstants.FALSE)) {
			final String imsi = imsiMapping.get(toNumber);
			if (imsi == null) {
				final String defaultImsi = PropertyReaderUtil.getInstance().getProperty(IConstants.KEY_DEFAULT_IMSI);
				System.out.println("IMSI Mapping not found for number [" + toNumber + "], returning default imsi ["
						+ defaultImsi + "]");
				return defaultImsi;
			} else {
				System.out.println("IMSI [" + imsi + "] found for number [" + toNumber + "] in cache");
				return imsi;
			}
		} else {
			throw new RuntimeException("This is currently not supported");
		}
	}
}