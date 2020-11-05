# Samply Common Mailing

This library offers utilities for building and sending HTML emails.

It provides a master template file that is used for all emails.
The content and footer for emails can be set by the projects that use this
library. The template engine is [Google Closure Templates][closuretemplates].

The configuration for email sending (connection to mail server etc.) 
can be specified in an XML file, which is parsed
with the Samply Common Config library.

[closuretemplates]: https://developers.google.com/closure/templates/

## Usage

The configuration file for mail sending is named `mailSending.xml`.

You can either create a `MailSending` configuration object by reading the XML file
or set its values in Java Code.

    MailSending mailSending = MailSender.loadMailSendingConfig("myproject");

In the documentation for the Samply Common Config library you can see where
the configuration file can be placed. The configuration is needed to create a
`MailSender` instance:

    MailSender mailSender = new MailSender(mailSending);

An email builder combines different template files and generates the text:

    String templateFolder = mailSending.getTemplateFolder();
    EmailBuilder builder = new EmailBuilder(templateFolder);
    builder.addTemplateFile("Footer.soy", "Footer");
    builder.addTemplateFile("TestMailContent.soy", "TestMailContent");

You can write and add template files that provide implementations 
for different parts of the email.
This is done with [Delegate Templates][deltemplates].
There are three delegate templates, namely `maincontent`, 
`footer` and `greeting`. Setting a `maincontent` is mandatory, the other 
templates are optional. 
The greeting part has a default implementation, which should usually suffice.

*For a complete working example please take a look at the maven test folder
(`src/test/`).*

The template files must all be located in one folder whose 
path must be specified in the configuration.

[deltemplates]: https://developers.google.com/closure/templates/docs/commands#delegates-with-variant

An `OutgoingEmail` object gets all information needed for writing the
email:

    OutgoingEmail email = new OutgoingEmail();
    email.addAddressee("werner.mueller@example.com");
    email.setSubject("Subject: Test");
    email.putParameter("name", "Müller");
    email.putParameter("title", "Prof. Dr.");
    email.setLocale("de");
    email.putParameter("testPar", "http://example.org");
    email.setBuilder(builder);

The parameters `"name"` and `"title"` are used in the main template to 
generate a standard personalized greeting, depending on the locale.
If no name is provided, you get an anonymous greeting.
All other  parameters can be set to be used by the templates.

Now, after having set the parameters and specified the templates, the mail can
be sent:

    mailSender.send(email);


## Build

Use maven to build the jar:

    mvn clean package

Use it as a dependency:

    <dependency>
        <groupId>de.samply</groupId>
        <artifactId>common-mailing</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

