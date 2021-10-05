package com.example.se7etak_tpa.Utils

import android.util.Log
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class GmailSender {
    val emailPort = "587" // gmail's smtp port
    val smtpAuth = "true"
    val starttls = "true"
    val emailHost = "smtp.gmail.com"
    var fromEmail: String? = null
    var fromPassword: String? = null
    var toEmail: String? = null
    var emailSubject: String? = null
    var emailBody: String? = null
    var emailProperties: Properties? = null
    var mailSession: Session? = null
    var emailMessage: MimeMessage? = null

    companion object{
        fun init(fromEmail: String?, fromPassword: String?,
                 toEmail: String?, emailSubject: String?, emailBody: String?) = GmailSender().apply {
            this.fromEmail = fromEmail
            this.fromPassword = fromPassword
            this.toEmail = toEmail
            this.emailSubject = emailSubject
            this.emailBody = emailBody
            emailProperties = System.getProperties()
            emailProperties?.put("mail.transport.protocol", "smtp");
            emailProperties?.setProperty("mail.smtp.port",emailPort)
            emailProperties?.setProperty("mail.smtp.auth",smtpAuth)
            emailProperties?.setProperty("mail.smtp.starttls.enable",starttls)
        }
    }

    fun createEmailMessage(): MimeMessage? {
        mailSession = Session.getDefaultInstance(emailProperties, null)
        emailMessage = MimeMessage(mailSession)
        emailMessage?.setFrom(InternetAddress(fromEmail, fromEmail))
        emailMessage?.addRecipient(
            Message.RecipientType.TO,
            InternetAddress(toEmail)
        )

        emailMessage?.subject = emailSubject
        emailMessage?.setContent(emailBody, "text/html") // for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.")
        return emailMessage
    }

    fun sendEmail() {
        val transport: Transport? = mailSession?.getTransport("smtp")
        if(transport == null){Log.i("sdf","laaaaaaa")}
        else {Log.i("sdf","sdfsfdfsd")}
        Thread {
            transport?.connect(emailHost, fromEmail, fromPassword)
            Log.i("GMail", "allRecipients: " + emailMessage?.allRecipients)
            transport?.sendMessage(emailMessage, emailMessage?.allRecipients)
            transport?.close()
            print("sdf")
            Log.i("GMail", "Email sent successfully.")
        }.start()
    }

}