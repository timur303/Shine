package kg.kadyrbekov.services;

import kg.kadyrbekov.dto.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    public void sendEmail(Mail mail, String url) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.host", "localhost");
        properties.setProperty("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("pro.fri.suppor5@gmail.com", "azwyzkslybwnfvdz");
            }
        });
        try {
            Message message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper((MimeMessage) message);
            message.setFrom(new InternetAddress("pro.fri.suppor5@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo()));
            message.setSubject(message.getSubject());
            String subject = "Here's the link to reset your password";
            String text = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>"
                    + "<p><a href=\"" + url + "\">Change my password</a></p>"
                    + "<br>"
                    + "<p>Ignore this email if you do remember your password, "
                    + "or you have not made the request.</p>";

//            message.setText(text);
//            message.setSubject(subject);
            helper.setText(text,true);
            helper.setSubject(subject);
            Transport.send(message);

        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
