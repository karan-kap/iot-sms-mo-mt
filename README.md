# iot-sms-mo-mt
Send SMS from Non-Aeris MO to Aeris MT (via Twilio)

Java Service is handler of the webhook added at Twilio. To send SMS from Non-Aeris to Aeris Device (MO to MT) you need to have a Twilio number. The SMS needs to be sent to the Twilio number, the body of the SMS should be in the format "<AERIS-MSISDN> smscontent". Twilio will forward the same sms body to this Java Service. This Java Servie will fetch the Aeris MSISDN, identify the mappeed IMSI with this MSISDN and then forward this message to AerFrame API.

PHP service is a wrapper over the API that Huawei modem uses to communicate with the SIM. This service is deployed on the Raspberry PI and is invoked via a shell script to get the total number of unread messages. There are more utility methods available to communicate with the modem.

Shell script to capture the total number of unread messages and push that to the Google IOT cloud

Contributors - Harish Taneja, Karan Kapoor, Kislay Kumar
