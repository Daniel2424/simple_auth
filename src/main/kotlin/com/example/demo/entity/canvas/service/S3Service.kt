package com.example.demo.entity.canvas.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.util.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.stream.Collectors


@Service
class S3Service(
    private val s3: AmazonS3,
) {
    @Value("\${bucketName}")
    private val bucketName: String? = null

    fun saveFile(file: MultipartFile): String {
        val originalFilename = file.originalFilename
        var count = 0
        val maxTries = 3
        while (true) {
            try {
                val file1: File = convertMultiPartToFile(file)
                val putObjectResult: PutObjectResult = s3.putObject(bucketName, originalFilename, file1)
                return putObjectResult.contentMd5
            } catch (e: IOException) {
                if (++count == maxTries) throw RuntimeException(e)
            }
        }
    }

    fun downloadFile(filename: String?): ByteArray {
        val obj = s3.getObject(bucketName, filename)
        val objectContent = obj.objectContent
        return try {
            IOUtils.toByteArray(objectContent)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun deleteFile(filename: String?): String {
        s3.deleteObject(bucketName, filename)
        return "File deleted"
    }

    fun listAllFiles(): List<String> {
        val listObjectsV2Result = s3.listObjectsV2(bucketName)
        return listObjectsV2Result.objectSummaries.map { obj: S3ObjectSummary -> obj.key }
    }

    private fun convertMultiPartToFile(file: MultipartFile): File {
        val convFile = File(file.originalFilename!!)
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }

}