package sms.callback.util;

/**
 * Constants used across the application
 * 
 * @author Karan.Kapoor@aeris.net
 *
 */
public interface IConstants {

	String CONFIG_PROPERTIES_FILE_NAME = "/env.properties";

	String CONTENT_TYPE_JSON = "application/json";
	String CONTENT_TYPE_XML = "application/xml";

	String KEY_AERFRAME_API_BASE_URL = "aerframe.api.base.url";
	String KEY_AERFRAME_API_ACCESS_KEY = "aerframe.api.access.key";
	String KEY_AERFRAME_API_ACCOUNT_ID = "aerframe.api.account.id";
	String KEY_AERFRAME_API_APP_SHORT_NAME = "aerframe.api.app.short.name";

	String KEY_MSISDN_IMSI_MAPPING = "msisdn.imsi.mapping";
	String KEY_FETCH_MAPPING_FROM_DB = "fetch.mapping.from.db";

	String KEY_DEFAULT_IMSI = "default.imsi";

	String HTTP_METHOD_POST = "POST";
	String FALSE = "false";
	String TRUE = "true";

	String REQ_PARAM_BODY = "Body";
}