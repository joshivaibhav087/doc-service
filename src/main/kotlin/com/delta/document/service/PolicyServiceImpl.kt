package com.delta.document.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils
import com.delta.document.component.Aes
import com.delta.document.model.PolicyHolder
import com.delta.document.repository.PolicyHolderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey


@Service
class PolicyServiceImpl(
    @Autowired
    val s3Client :AmazonS3,
    @Autowired
    val policyHolderRepository : PolicyHolderRepository,
    @Autowired
    val aes : Aes,
    @Autowired
    val  encoder: PasswordEncoder


    ) : PolicyService {

    private val logger: Logger = LoggerFactory.getLogger(PolicyServiceImpl::class.java)

    var bucketName: String? = "deltainsurance"

    val secKey: SecretKey? = aes.getSecretEncryptionKey()


    @Throws(Exception::class)
    override fun downloadProofFile(folder: String?, fileName: String?): ByteArray? {

        val s3Object =
            s3Client.getObject("deltainsurance",folder.toString() + "/" + fileName.toString())

        val inputStream = s3Object.objectContent
        return IOUtils.toByteArray(inputStream)
    }

    @Transactional
    override fun submitDocuments(
        partnerId:String,
        clientName : String,
        clientAddress : String,
        pinCode : String,
        emailId : String,
        mobile : String,
        aadharNumber: String,
        aadharDoc: MultipartFile,
        panCardNumber: String,
        panCardDoc : MultipartFile,
        insuranceType:String,
        policyNumber : String,
        policyQuote: MultipartFile,
        specificdocument :MultipartFile,

    ): String? {

            if(policyHolderRepository.existsByPolicyNumber(policyNumber)){
                return throw Exception("policy Number is already taken")
            }
        else {


                val aadharFile: File? = aadharDoc?.let { convertMultipartFileToFile(it) }
                val panFile: File? = convertMultipartFileToFile(panCardDoc)
                val quoteFile: File? = convertMultipartFileToFile(policyQuote)
                val relatedFile: File? = convertMultipartFileToFile(specificdocument)
                val date = Date()

                logger.info("Getting original file names")
                val addharFileName: String? = aadharDoc?.originalFilename.toString()
                val panFileName: String? = panCardDoc?.originalFilename
                val quoteFileName: String? = policyQuote?.originalFilename
                val relatedFileName: String? = specificdocument?.originalFilename

                try {


                    logger.info("storing the data in database")
                    val data = PolicyHolder(
                        partnerId,
                        clientName,
                        clientAddress,
                        pinCode,
                        emailId,
                        mobile,
                        aadharNumber,
                        addharFileName,
                        panCardNumber,
                        panFileName,
                        insuranceType,
                        policyNumber,
                        quoteFileName,
                        relatedFileName,
                        date
                    )

                    policyHolderRepository.insert(data)


                    try {
                        logger.info("storing aadhar file in s3")
                        s3Client.putObject(
                            PutObjectRequest(
                                "$bucketName/${policyNumber}",
                                "$addharFileName",
                                aadharFile
                            )
                        )
                        logger.info("aadhar file is stored in s3")
                        logger.info("storing pan card file in s3")
                        s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", panFileName, panFile))
                        logger.info("pan file is stored in s3")
                        logger.info("storing quote file in s3")
                        s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", quoteFileName, quoteFile))
                        logger.info("quote file is stored in s3")
                        logger.info("storing related file in s3")
                        s3Client.putObject(
                            PutObjectRequest(
                                "$bucketName/${policyNumber}",
                                relatedFileName,
                                relatedFile
                            )
                        )
                        logger.info("related documnet file is stored in s3")

                        logger.info(" All the files Stored in s3 and data is stored in database")
                        return "All the files Stored in s3 and data is stored in database"

                    } catch (e: Exception) {
                        logger.error("error while storing the  in files in s3", e)
                        logger.info("deleteing the data which is stored in database")

                        return e.printStackTrace().toString()

                    }
                } catch (e: Exception) {
                    logger.error("error while storing the data in database", e)
                    e.printStackTrace()
                    return e.printStackTrace().toString()
                }
            }


    }

    override fun getAllDetails(): MutableList<PolicyHolder?> {
        logger.info("get all policy details")
       return policyHolderRepository.findAll()
    }

    override fun getDetailsById(_id: String?): Optional<PolicyHolder?> {
        logger.info("get policy details by id")
        return policyHolderRepository.findById(_id!!)
    }

    override fun isPolicyPresentByPolicyNumber(policyNumber: String): Boolean {
        return policyHolderRepository.existsByPolicyNumber(policyNumber)
    }


    open fun convertMultipartFileToFile(file: MultipartFile): File {
        val convertedFile = File(file.originalFilename)
        try {
            logger.info("converting multipart file to file")
            FileOutputStream(convertedFile).use { fos -> fos.write(file.bytes) }
            logger.info("Multipart file converted to file")
        } catch (e: IOException) {

            logger.error("Error converting multipart file to file", e);
            e.printStackTrace()
        }
        return convertedFile

    }
}




