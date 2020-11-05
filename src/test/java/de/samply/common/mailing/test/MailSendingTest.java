
package de.samply.common.mailing.test;

import de.samply.common.mailing.EmailBuilder;
import de.samply.common.mailing.MailSender;
import de.samply.common.mailing.MailSending;
import de.samply.common.mailing.OutgoingEmail;
import java.io.File;

public class MailSendingTest {

  public static void main(String[] args) {

    OutgoingEmail email = new OutgoingEmail();
    email.addAddressee("stefan.lenz@uni-mainz.de");
    email.setSubject("Subject: Test");
    email.putParameter("name", "Müller");
    email.putParameter("title", "Prof. Dr.");
    email.setLocale("de");
    email.putParameter("testPar", "http://example.org");

    // set confdir to find the mail configuration file
    System.setProperty("samply.confdir",
        new File("src/test/resources/").getAbsolutePath());
    MailSending mailSending = MailSender.loadMailSendingConfig("samply");

    String templateFolder = mailSending.getTemplateFolder();

    EmailBuilder builder = new EmailBuilder(templateFolder);
    builder.addTemplateFile("Footer.soy", "Footer");
    builder.addTemplateFile("TestMailContent.soy", "TestMailContent");
    email.setBuilder(builder);

    MailSender mailSender = new MailSender(mailSending);
    mailSender.send(email);

    System.out.println("Sent mail with text: ");
    System.out.println(email.getText());

  }

}
