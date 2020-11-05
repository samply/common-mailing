
package de.samply.common.mailing;

import de.samply.config.util.JaxbUtil;
import de.samply.string.util.StringUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Used to send emails and load the therefore needed email sending configuration.
 */
public class MailSender {

  public static final String CONFIG_FILE_NAME = "mailSending.xml";
  private final Session mailSession;
  private final MailSending mailSending;

  /**
   * This constructor uses an already loaded mail sending configuration. The configuration can be
   * loaded by {@link #loadMailSendingConfig(String, String...)} }.
   *
   * @param mailSending Configuration for mail sending
   */
  public MailSender(MailSending mailSending) {
    this.mailSending = mailSending;
    this.mailSession = getMailSessionFromConfig(mailSending);
  }

  /**
   * Creates mail session from settings in configuration file.
   *
   * @return created {@link Session}
   */
  private static Session getMailSessionFromConfig(MailSending mailSending) {
    Properties mailProperties = new Properties();

    String protocol = mailSending.getProtocol();

    mailProperties.setProperty("type", "transport");
    mailProperties.setProperty("mail.transport.protocol", protocol);
    mailProperties.setProperty("mail.host", mailSending.getHost());

    int port = mailSending.getPort();
    //Port used for tls in SMTP
    if (port == 587) {
      mailProperties.put("mail.smtp.starttls.enable", "true");
    } else if (port == 0) {
      port = 25;
    }
    mailProperties.setProperty("mail." + protocol + ".port", "" + port);

    return Session.getInstance(mailProperties);
  }

  /**
   * Loads the mail sending configuration from the XML file.
   *
   * @param projectName name of the folder to look for config
   * @return the created {@link MailSending} object
   * @see JaxbUtil#findUnmarshall(java.lang.String, javax.xml.bind.JAXBContext, java.lang.Class,
   * java.lang.String)
   */
  public static MailSending loadMailSendingConfig(String projectName, String... fallbacks) {
    try {
      return JaxbUtil.findUnmarshall(CONFIG_FILE_NAME,
          JAXBContext.newInstance(ObjectFactory.class),
          MailSending.class, projectName, fallbacks);

    } catch (FileNotFoundException | JAXBException | SAXException
        | ParserConfigurationException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Sends the email using the settings specified in the configuration file.
   *
   * @param email an email to be sent
   */
  public void send(OutgoingEmail email) {
    MimeMessage message = new MimeMessage(mailSession);
    try {
      for (String addressee : email.getAddressees()) {
        message.addRecipient(RecipientType.TO,
            new InternetAddress(addressee));
      }

      for (Address ccRecipient : email.getCcRecipient()) {
        message.addRecipient(RecipientType.CC, ccRecipient);
      }

      if (email.getReplyTo() != null && email.getReplyTo().size() > 0) {
        message.setReplyTo(email.getReplyToArray());
      }
      message.setFrom(getFromAddress());
      message.setSubject(email.getSubject());
      message.setContent(email.getText(), "text/plain; charset=utf-8");

      message = addAttachments(message, email.getAttachmentPaths());

      message.saveChanges();
      Transport tr = mailSession.getTransport();

      // Connect anonymously if necessary.
      if (StringUtil.isEmpty(mailSending.getUser())) {
        tr.connect();
      } else {
        tr.connect(mailSending.getUser(), mailSending.getPassword());
      }
      tr.sendMessage(message, message.getAllRecipients());
      tr.close();

    } catch (MessagingException ex) {
      LoggerFactory.getLogger(MailSender.class).error("Exception: ", ex);
    }
  }

  /**
   * Add attachments.
   *
   * @param mimeMessage     MimeMessage object to add attachments to
   * @param attachmentPaths paths to attachments
   */
  private MimeMessage addAttachments(MimeMessage mimeMessage, List<String> attachmentPaths)
      throws MessagingException {
    MimeMultipart mimeMultipart = new MimeMultipart();
    for (String filepath : attachmentPaths) {
      File file = new File(filepath);
      if (file.exists()) {
        MimeBodyPart attachment = new MimeBodyPart();
        attachment.setDataHandler(new DataHandler(new FileDataSource(file)));
        attachment.setFileName(file.getName());
        attachment.setDisposition(MimeBodyPart.ATTACHMENT);
        mimeMultipart.addBodyPart(attachment);
      }
    }
    if (mimeMultipart.getCount() != 0) {
      mimeMessage.setContent(mimeMultipart);
    }
    return mimeMessage;
  }

  /**
   * Create sender address from settings using mail address and optional display name.
   *
   * @return created address
   */
  private InternetAddress getFromAddress() {
    try {
      return new InternetAddress(
          mailSending.getFromAddress(),
          mailSending.getFromName(),
          "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(
          "Exception while setting from-address for mail sending", ex);
    }
  }

}
