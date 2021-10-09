package com.example.se7etak_tpa.Utils

import android.util.Log
import java.util.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeBodyPart

import javax.activation.DataHandler
import javax.activation.DataSource

import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.BodyPart
import javax.mail.internet.MimeMultipart


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
    var attachment1Name: String? = null
    var attachment2Name: String? = null

    private lateinit var absolutePath: String

    companion object{
        fun init(
            fromEmail: String?, fromPassword: String?,
            toEmail: String?, emailSubject: String?, emailBody: String?, attachment1Name: String?, attachment2Name: String?,absolutePath: String
        ) = GmailSender().apply {
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
            this.attachment1Name = attachment1Name
            this.attachment2Name = attachment2Name
            this.absolutePath = absolutePath
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


        val multipart = MimeMultipart()
        val bodyPart = MimeBodyPart()
        bodyPart.setText(emailBody)
        multipart.addBodyPart(bodyPart)
        attachment1Name?.let { multipart.addBodyPart(prepareFile(attachment1Name!!)) }
        attachment2Name?.let {  multipart.addBodyPart(prepareFile(attachment2Name!!))}
        emailMessage?.setContent(multipart)
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.")
        return emailMessage
    }

    fun sendEmail() {
        val transport: Transport? = mailSession?.getTransport("smtp")
        Thread {
            transport?.connect(emailHost, fromEmail, fromPassword)
            Log.i("GMail", "allRecipients: " + emailMessage?.allRecipients)
            transport?.sendMessage(emailMessage, emailMessage?.allRecipients)
            transport?.close()
            Log.i("GMail", "Email sent successfully.")
        }.start()
    }


    private fun prepareFile(fileName: String): BodyPart {
        val filePath = "$absolutePath/'$fileName'"
        val source: DataSource = FileDataSource(filePath)
        val messageBodyPart: BodyPart = MimeBodyPart()
        messageBodyPart.dataHandler = DataHandler(source)
        messageBodyPart.fileName = fileName
        return messageBodyPart
    }
}