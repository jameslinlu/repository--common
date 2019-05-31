package com.commons.common.utils.mail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C)
 * Email
 * Author: jameslinlu
 */
public class Email implements Serializable {
    private String sender;//发件人
    List<Address> to = new ArrayList<>();//收件人
    List<Address> cc = new ArrayList<>();//抄送人
    List<Address> bcc = new ArrayList<>();//密送人
    private String subject;//标题
    private String content;//内容
    List<File> attachments = new ArrayList<>();//附件

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Address> getTo() {
        return to;
    }

    public void setTo(List<Address> to) {
        this.to = to;
    }

    public void setTo(String to) {
        try {
            if (this.to == null) {
                this.to = new ArrayList<>();
            }
            for (String t : to.split(",")) {
                this.to.add(new InternetAddress(t));
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    public List<Address> getCc() {
        return cc;
    }

    public void setCc(List<Address> cc) {
        this.cc = cc;
    }

    public List<Address> getBcc() {
        return bcc;
    }

    public void setBcc(List<Address> bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }
}
