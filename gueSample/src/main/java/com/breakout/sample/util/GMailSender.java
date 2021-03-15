package com.breakout.sample.util;


import android.os.StrictMode;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * gmail sender
 * <p>
 * build.gradle >>> implementation fileTree(dir: 'libs/email', include: ['*.jar'])
 *
 * @author sung-gue
 * @version 1.0 (2016-02-15)
 */
public class GMailSender extends javax.mail.Authenticator {
    private final String user;
    private final String password;
    private Session session;

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;
        initSession();
    }

    /**
     * https://support.google.com/mail/answer/7126229
     */
    private void initSession() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build())
        ;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/net/Authenticator.html
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
        //return super.getPasswordAuthentication();
    }

    /**
     * https://docs.oracle.com/javaee/5/api/javax/mail/internet/MimeMessage.html
     */
    public synchronized void sendMail(String subject, String body, String recipients) throws Exception {
        sendMail(subject, body, recipients, null, null);
    }

    public synchronized void sendMail_t(String subject, String body, String recipients) throws Exception {
        MimeMessage msg = new MimeMessage(session);
        msg.setSender(new InternetAddress(user));
        msg.setSubject(subject);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        msg.setDataHandler(handler);
        if (recipients.indexOf(',') > 0)
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        Transport.send(msg);
    }

    /**
     * send mail
     *
     * @param subject    title
     * @param body       body
     * @param recipients recieve email "email1,email2,..."
     * @param filePath   attachment file path
     * @param fileName   attachment file nmae
     */
    public synchronized void sendMail(String subject, String body, String recipients, String filePath, String fileName) throws Exception {
        MimeMessage msg = new MimeMessage(session);
        //msg.setHeader("Content-Type", "text/plain; charset=UTF-8");
        msg.setSender(new InternetAddress(user));
        msg.setSubject(subject);
        if (TextUtils.isEmpty(filePath)) {
            msg.setText(body);
        } else {
            MimeMultipart multipart = new MimeMultipart();

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);
            multipart.addBodyPart(bodyPart);

            MimeBodyPart attachment = new MimeBodyPart();
            DataSource source = new FileDataSource(filePath);
            attachment.setDataHandler(new DataHandler(source));
            attachment.setFileName(fileName);
            multipart.addBodyPart(attachment);

            msg.setContent(multipart, "text/plain; charset=UTF-8");
        }
        if (recipients.indexOf(',') > 0) {
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        } else {
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        }
        Transport.send(msg);
    }

    private String makeAuthCode(int count) {
        String[] arr = {
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
        };
        StringBuilder newCode = new StringBuilder();
        for (int i = 0; i < count; i++) {
            newCode.append(arr[(int) (Math.random() * arr.length)]);
        }
        return newCode.toString();
    }

    private static class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
