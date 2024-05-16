package com.example.demo.entity.canvas.controller

import com.example.demo.entity.canvas.service.S3Service
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
class S3Controller(
    private val s3Service: S3Service,
) {

    @PostMapping("upload")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun upload(@RequestParam("file") file: MultipartFile, destinationFolder: String = "trash"): String {
        return s3Service.saveFile(file, destinationFolder)
    }

    @GetMapping("download/**")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun download(request: HttpServletRequest): ResponseEntity<ByteArray> {
        val filename = extractFilePath(request)
        val headers = HttpHeaders()
        headers.add("Content-type", MediaType.ALL_VALUE)
        headers.add("Content-Disposition", "attachment; filename=${filename.substringAfterLast("/")}")
        val bytes = s3Service.downloadFile(filename)
        return ResponseEntity.ok().headers(headers).body(bytes)
    }

    private fun extractFilePath(request: HttpServletRequest): String {
        return request.requestURI.substringAfter("/download/")
    }


    @DeleteMapping("{filename}")
    fun deleteFile(@PathVariable("filename") filename: String?): String {
        return s3Service.deleteFile(filename)
    }

    @GetMapping("list")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getAllFiles(): List<String> {
        return s3Service.listAllFiles()
    }

    @GetMapping("listUrlsInFolder")
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getAllUrlsImagesInFolder(@RequestParam(required = false) folder: String?): List<String> {
        return if (folder != null) {
            s3Service.listUrlImagesInFolder(folder)
        } else {
            s3Service.listAllFiles()
        }
    }
}