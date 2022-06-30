package com.pwc.grading.mail.session;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.grading.mail.service.MailServiceConstant;
import com.pwc.grading.mail.service.exception.MailSessionException;
import com.pwc.grading.util.PropertiesUtil;
import com.pwc.grading.util.ReadPropertiesFile;

/**
 * A class which initializes the authenticated mail session.
 *
 */
public class MailSessionInstance {

	private static final Logger logger = LoggerFactory.getLogger(MailSessionInstance.class);

	private static Session session = null;

//	private static Transport transport = null;

	private MailSessionInstance() {
		super();
		// SingleTon Instance
	}

	/**
	 * Used to get the mail session.
	 * @return the mail session.
	 * @throws MailSessionException if any exception occurs performing the operation.
	 */
	public static Session getMailSession() throws MailSessionException {
		try {
			logger.debug("inside getMailSession mtd of MailSessionInstance");
			if (session == null) {
				initializeMailSession();
			}
			return session;
		} catch (Exception e) {
			logger.error(" unable to get Mail Session Instance", e);
			throw new MailSessionException(" unable to get Mail Session Instance", e);
		}

	}

	/**
	 * Initialize a new mail session.
	 */
	private static synchronized void initializeMailSession() {
		if (session == null) {
			logger.info(" Initializing Mail Session Instance .");
			Properties emailProperties = PropertiesUtil
					.getPropertiesFromResoures(MailServiceConstant.MAIL_PROPERTIES_FILE);
			String userName = ReadPropertiesFile.readRequestProperty(MailServiceConstant.USER_NAME);
			String password = ReadPropertiesFile.readRequestProperty(MailServiceConstant.PWD);

			session = Session.getDefaultInstance(emailProperties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			logger.info(" Mail Session Initialized ..");
		}

	}

//	public static Transport getTransport() throws MailSessionException {
//		logger.debug(".in getTransport method");
//		try {
//			if (transport == null) {
//				initializeTransportInstance();
//			}
//			if (!transport.isConnected()) {
//				logger.debug("transport connection is not connected. So connecting Now...");
//				long start = System.currentTimeMillis();
//				transport.connect();
//				long end = System.currentTimeMillis();
//				logger.debug("it took " + (end - start) + " ms to connect");
//			}
//		} catch (Exception e) {
//			logger.error(" unable to get Mail Transport Instance", e);
//			throw new MailSessionException(" unable to get Mail Transport Instance ", e);
//		}
//
//		return transport;
//
//	}

	// Initializing Mail Transport Instance
//	private static synchronized void initializeTransportInstance() throws MailSessionException {
//		logger.debug(".in initializeTransportInstance method");
//		if (transport == null) {
//			if (session != null) {
//				try {
//					transport = session.getTransport();
//					logger.debug("got the transport..");
//
//				} catch (Exception e) {
//					throw new MailSessionException(e);
//				}
//			} else {
//				initializeMailSession();
//			}
//		}
//	}

	// reload session
//	public static void reloadSession() {
//	}

	/**
	 * Close the transport for email.
	 * @param transport
	 */
	public static void closeTransport(Transport transport) {
		if (transport != null) {
			try {
				logger.debug("closing the transport connection");
				transport.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
