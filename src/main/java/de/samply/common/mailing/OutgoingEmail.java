
package de.samply.common.mailing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class OutgoingEmail {

  public static final String LOCALE_LEY = "locale";
  private final HashMap<String, String> parameters;
  private final ArrayList<String> addressees;
  private String subject;
  private List<Address> replyTo;
  private List<Address> ccRecipients;
  private EmailBuilder emailBuilder;
  private List<String> attachmentPaths;

  /**
   * Todo.
   */
  public OutgoingEmail() {
    addressees = new ArrayList<>();
    replyTo = new ArrayList<>();
    ccRecipients = new ArrayList<>();
    parameters = new HashMap<>();
    parameters.put(LOCALE_LEY, "en");
    attachmentPaths = new ArrayList<>();
  }

  /**
   * Sets the locale of the email, passed to the emailBuilder.
   *
   * @param locale a locale
   */
  public void setLocale(String locale) {
    parameters.put(LOCALE_LEY, locale);
  }

  public String getSubject() {
    return subject;
  }

  /**
   * Set the subject of the mail.
   * @param subject the subject of the mail.
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Add the email of the receiving person.
   * @param address the email address.
   */
  public void addReplyTo(String address) {
    try {
      replyTo.add(new InternetAddress(address));
    } catch (AddressException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public List<Address> getReplyTo() {
    return replyTo;
  }

  /**
   * Get all emails of the persons as array which should receive the email.
   * @return the emails as array.
   */
  public Address[] getReplyToArray() {
    Address[] replyToArray = new Address[replyTo.size()];
    replyToArray = replyTo.toArray(replyToArray);
    return replyToArray;
  }

  /**
   * The cc recipient which should get the mail.
   * @param address the email address of the cc recipient.
   */
  public void addCcRecipient(String address) {
    try {
      ccRecipients.add(new InternetAddress(address));
    } catch (AddressException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public List<Address> getCcRecipient() {
    return ccRecipients;
  }

  /**
   * Gets the text of the email.
   *
   * @return the text of the email
   */
  public String getText() {
    if (emailBuilder == null) {
      return "";
    }
    return emailBuilder.getText(parameters);
  }

  public EmailBuilder getBuilder() {
    return emailBuilder;
  }

  /**
   * Sets the email builder for generating the mail's text.
   *
   * @param emailBuilder the new email builder instance to set
   */
  public void setBuilder(EmailBuilder emailBuilder) {
    this.emailBuilder = emailBuilder;
  }

  /**
   * Adds an email address that this mail is sent to.
   * @param emailAddress the email address of the new addressee
   */
  public void addAddressee(String emailAddress) {
    addressees.add(emailAddress);
  }

  /**
   * Return the list of email addresses that this email should be sent to.
   * @return list of email addresses that this email should be sent to.
   */
  public List<String> getAddressees() {
    return addressees;
  }

  /**
   * Sets a parameter that is used in the Email.
   *
   * @param key   the parameter key
   * @param value the parameter value
   */
  public void putParameter(String key, String value) {
    parameters.put(key, value);
  }

  public List<String> getAttachmentPaths() {
    return attachmentPaths;
  }

  public void setAttachmentPaths(List<String> attachmentPaths) {
    this.attachmentPaths = attachmentPaths;
  }
}
