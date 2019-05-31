package com.commons.common.utils;

import com.commons.common.utils.mail.Email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class MailUtils {

    /**
     * 发送邮件
     *
     * @param mail     邮件信息
     * @param username 用户名
     * @param password 密码
     * @param prop     邮箱参数
     * @return
     */
    public static boolean send(Email mail, final String username, final String password, Properties prop) {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        return send(mail, auth, prop);
    }

    /**
     * 发送邮件
     *
     * @param mail 邮件信息
     * @param prop 邮箱参数
     * @param auth 鉴权信息
     * @return
     */
    public static boolean send(Email mail, Authenticator auth, Properties prop) {
        try {
            Session session = Session.getInstance(prop, auth);
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(session);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mail.getSender());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 设置邮件消息的主题
            mailMessage.setSubject(mail.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());

            Multipart mainPart = new MimeMultipart();

            // 内容
            BodyPart html = new MimeBodyPart();
            html.setContent(mail.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);

            // 附件
            if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
                for (File file : mail.getAttachments()) {
                    BodyPart filePart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    filePart.setDataHandler(new DataHandler(source));
                    filePart.setFileName(MimeUtility.encodeWord(file.getName()));
                    mainPart.addBodyPart(filePart);
                }
            }

            // "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
            if (mail.getTo() != null && !mail.getTo().isEmpty()) {
                mailMessage.setRecipients(Message.RecipientType.TO, mail.getTo().toArray(new Address[]{}));
            }
            if (mail.getCc() != null && !mail.getCc().isEmpty()) {
                mailMessage.setRecipients(Message.RecipientType.CC, mail.getCc().toArray(new Address[]{}));
            }
            if (mail.getBcc() != null && !mail.getBcc().isEmpty()) {
                mailMessage.setRecipients(Message.RecipientType.BCC, mail.getBcc().toArray(new Address[]{}));
            }
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            Transport.send(mailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean send(Email mail, final String mailKey) {

        Properties prop = PropUtil.getPrefixProperties(mailKey + ".prop", mailKey + ".prop\\.", "");
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                Map<String, String> accountInfo = PropUtil.getPrefix(mailKey + ".auth", mailKey + "\\.auth\\.", "");
                return new PasswordAuthentication(accountInfo.get("username"), accountInfo.get("password"));
            }
        };
        return send(mail, auth, prop);
    }

    public static void main(String[] args) throws Exception {
//        send(null, "zyucmail");
//        Authenticator a = new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
////                return new PasswordAuthentication("1009980411@qq.com", "gongjian12121212");
//                return new PasswordAuthentication("gongjian@zhong-ying.com", "0928tz!b");
//            }
//        };
//        Properties p = new Properties();
////        p.put("mail.smtp.host","smtp.qq.com");
////        p.put("mail.transport.protocol", "smtp");
//        p.put("mail.smtp.host", "smtp.zhong-ying.com");
//        p.put("mail.smtp.port", 25);
//        p.put("mail.smtp.auth", "true");
//        p.put("mail.debug", "true");
//        Session session = Session.getInstance(p, a);
//        // 根据session创建一个邮件消息
//        Message mailMessage = new MimeMessage(session);
//        // 创建邮件发送者地址
//        Address from = new InternetAddress("gongjian@zhong-ying.com");
////        Address from = new InternetAddress("1009980411@qq.com");
//        // 设置邮件消息的发送者
//        mailMessage.setFrom(from);
//
//        List<Address> toMail = new ArrayList<>();//收件人
//        List<Address> ccMail = new ArrayList<>();//抄送人
//        List<Address> bccMail = new ArrayList<>();//密送人
//        // 创建邮件的接收者地址，并设置到邮件消息中
//        toMail.add(new InternetAddress("yaol@zhong-ying.com"));
//        ccMail.add(new InternetAddress("yangcy@zhong-ying.com"));
//        // "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
//        Address[] toMailArray = toMail.toArray(new Address[]{});
//        Address[] ccMailArray = ccMail.toArray(new Address[]{});
//        mailMessage.setRecipients(Message.RecipientType.TO, toMailArray);
//        mailMessage.setRecipients(Message.RecipientType.CC, ccMailArray);
//        // 设置邮件消息的主题
//        mailMessage.setSubject("你在干什么呀~2");
//        // 设置邮件消息发送的时间
//        mailMessage.setSentDate(new Date());
//
//        /**
//         * HTML内容发送
//         */
//        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
//        Multipart mainPart = new MimeMultipart();
//
//        // 创建一个包含HTML内容的MimeBodyPart
//        BodyPart html = new MimeBodyPart();
//        html.setContent("Html内容发送 <br><img src='http://c.hiphotos.baidu.com/image/pic/item/f7246b600c3387448982f948540fd9f9d72aa0bb.jpg'>", "text/html; charset=utf-8");
//        mainPart.addBodyPart(html);
//
//        File file=new File("G:\\文档\\云端卫士\\接口\\20160223.xlsx");
//        BodyPart filePart = new MimeBodyPart();
//        DataSource source = new FileDataSource(file);
//        filePart.setDataHandler(new DataHandler(source));
//        filePart.setFileName(MimeUtility.encodeWord("云端卫士"));
//        mainPart.addBodyPart(filePart);
//
//        // 将MiniMultipart对象设置为邮件内容
//        mailMessage.setContent(mainPart);
//
//        /**
//         * 纯文本内容发送
//         */
//        // 设置邮件消息的主要内容
////	      String mailContent = "Dota么亲！";
////	      mailMessage.setText(mailContent);
//        // 发送邮件
//        Transport.send(mailMessage);
//
////        // 获得Transport实例对象
////        Transport transport = session.getTransport();
////        // 打开连接
////        transport.connect("gongjian@zhong-ying.com", "0928tz!b");
////        // 将message对象传递给transport对象，将邮件发送出去
////        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
////        // 关闭连接
////        transport.close();
    }
}
