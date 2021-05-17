package com.yvonne.proyecto.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Component
@PropertySource("classpath:variables.properties")
public class EmailSender {

    @Value("${spring.datasource.email}")
    private String mail;

    @Value("${spring.datasource.pass}")
    private String pass;


    private static String MAIL;
    private static String PASS;

    @Value("${spring.datasource.email}")
    public void setMail(String mail){
        EmailSender.MAIL = mail;
    }
    @Value("${spring.datasource.pass}")
    public void setPass(String pass){
        EmailSender.PASS = pass;
    }

    private EmailSender()
    {
    }

    public static void sendEmail( String subject, String HTMLTemplateUrl, String dataAsBody, String userEmail ) throws Exception {

            Properties props = System.getProperties();
            props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
            props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
            props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
            props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google
            props.put( "mail.smtp.ssl.trust", "*" );

            Session session = Session.getInstance( props, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication( MAIL, PASS );
                }
            } );
           /// Session session = Session.getDefaultInstance(props);
            Message message = new MimeMessage( session );

            message.setFrom( new InternetAddress( "practicayvonne@gmail.com" ) );
            message.setRecipients( Message.RecipientType.TO,
                    InternetAddress.parse( userEmail ) );

            message.setSubject( subject );

            MimeMultipart multipart = new MimeMultipart( "related" );

            MimeBodyPart mimeBodyPart = new MimeBodyPart();

            // mimeBodyPart.setContent( mailMessage, "text/html" );

            // coger contenido del email.html
            String content = getContentOfHTML( HTMLTemplateUrl );
            // cuerpo

            content = content.replaceAll( "\\{\\{body\\}\\}", dataAsBody );

            mimeBodyPart.setContent( content, "text/html; charset=UTF-8" );
            mimeBodyPart.addHeader( "Content-Type", "text/html; charset=UTF-8" );

            multipart.addBodyPart( mimeBodyPart );

            message.setContent( multipart );

            Transport.send( message );


    }

    private static String getContentOfHTML( String filename ) throws IOException
    {
        try (
                InputStream is = EmailSender.class.getClassLoader().getResourceAsStream( filename ) )
        {
            if ( is == null )
            {
                return "";
            }
            try (
                    InputStreamReader isr = new InputStreamReader( is );
                    BufferedReader reader = new BufferedReader( isr ) )
            {
                return reader.lines().collect( Collectors.joining( System.lineSeparator() ) );
            }
        }

    }
}