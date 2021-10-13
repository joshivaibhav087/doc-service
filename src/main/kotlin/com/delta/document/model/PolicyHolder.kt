package com.delta.document.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


@Document(collection = "PolicyHolder")
class PolicyHolder {
    @Id
    lateinit var id: String

    lateinit var partnerId:String

    @Pattern(regexp = "[A-Za-z ]+")
    @NotBlank(message = "Client Name is mandatory")
    lateinit var clientName : String

    @NotBlank(message = "Address  is mandatory")
    lateinit var clientAddress : String

    @Size(min=6, max=6)
    @NotBlank(message = "PinCode  is mandatory")
    lateinit var pinCode : String

    @NotBlank(message = "Email is mandatory")
    lateinit var emailId : String

    @Size(min=10, max=10)
    @NotBlank(message = "Mobile is mandatory")
    lateinit var mobile : String

    @Pattern(regexp="^\\d{4}\\s\\d{4}\\s\\d{4}$")
    @NotBlank(message = "Aadhar number is mandatory")
    lateinit var aadharNumber: String

    lateinit var aadharDoc: String

    @Size(min=10, max=10)
    @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]{1}")
    @NotBlank(message = "Pan details is mandatory")
    lateinit var panCardNumber: String

    lateinit var panCardDoc : String

    lateinit var insuranceType:String

    @NotBlank(message = "Policy number is mandatory")
    @Indexed(unique=true)
    lateinit var policyNumber : String

    lateinit var policyQuote: String

    lateinit var specificdocument :String

    lateinit var submissionDate: Date

    constructor()
    constructor(
        partnerId: String,
        clientName: String,
        clientAddress: String,
        pinCode: String,
        emailId: String,
        mobile: String,
        aadharNumber: String?,
        aadharDoc: String?,
        panCardNumber: String?,
        panCardDoc: String?,
        insuranceType: String,
        policyNumber: String,
        policyQuote: String?,
        specificdocument: String?,
        submissionDate: Date
    ) {
        this.partnerId = partnerId
        this.clientName = clientName
        this.clientAddress = clientAddress
        this.pinCode = pinCode
        this.emailId = emailId
        this.mobile = mobile
        this.aadharNumber = aadharNumber!!
        this.aadharDoc = aadharDoc!!
        this.panCardNumber = panCardNumber!!
        this.panCardDoc = panCardDoc!!
        this.insuranceType = insuranceType
        this.policyNumber = policyNumber
        this.policyQuote = policyQuote!!
        this.specificdocument = specificdocument!!
        this.submissionDate = submissionDate
    }




}