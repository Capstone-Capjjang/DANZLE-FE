package com.example.danzle.aws

//import android.content.Context
//import android.content.Intent
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.s3.AmazonS3Client
//import com.example.danzle.practice.HighlightPractice
//import com.example.danzle.practice.HighlightPracticeFinish
//import java.io.File
//
//fun uploadWithTransferUtility(fileName: String, file: File, context: Context) {
//    val awsCredentials = BasicAWSCredentials("AccessKey", "SecretKey")
//    val s3Client = AmazonS3Client(awsCredentials)
//    s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_NORTHEAST_2))
//
//    val transferUtility = TransferUtility.builder()
//        .s3Client(s3Client)
//        .context(context.applicationContext)
//        .build()
//
//    TransferNetworkLossHandler.getInstance(context.applicationContext)
//
//    val uploadObserver = transferUtility.upload(
//        "talent-house-app/photo",
//        fileName,
//        file
//    )
//
//    uploadObserver.setTransferListener(object : TransferListener {
//        override fun onStateChanged(id: Int, state: TransferState?) {
//            if (state == TransferState.COMPLETED) {
//                Log.d("Upload", "Upload completed")
//                Handler(Looper.getMainLooper()).post {
//                    Toast.makeText(context, "업로드 완료!", Toast.LENGTH_SHORT).show()
//
//                    if (context is HighlightPractice) {
//                        val intent = Intent(context, HighlightPracticeFinish::class.java)
//                        intent.putExtra("selected song", context.selectedSong)
//                        context.startActivity(intent)
//                    }
//                }
//            }
//        }
//
//        override fun onProgressChanged(id: Int, current: Long, total: Long) {
//            val percentDone = (current.toDouble() / total * 100).toInt()
//            Log.d("Upload", "Progress: $percentDone%")
//        }
//
//        override fun onError(id: Int, ex: Exception?) {
//            Log.e("Upload", "Error: ${ex?.message}")
//            Handler(Looper.getMainLooper()).post {
//                Toast.makeText(context, "업로드 중 오류 발생", Toast.LENGTH_SHORT).show()
//            }
//        }
//    })
//}