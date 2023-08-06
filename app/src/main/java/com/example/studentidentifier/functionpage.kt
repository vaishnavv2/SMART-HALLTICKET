@file:Suppress("DEPRECATION")
package com.example.studentidentifier


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min


class functionpage : AppCompatActivity() {
var currentDocumentIndex=-1
    private var documentIterator: MutableIterator<DocumentSnapshot>? = null
    private lateinit var faceNetInterpreter: Interpreter
    private val CAMERA_REQUEST_CODE = 22
    private var faceNetImageProcessor: ImageProcessor? = null
    private val CAMERA_PERMISSION_CODE = 1001
    private lateinit var imageBitmap: Bitmap
    private lateinit var faceDetector: FaceDetector
    private var faceOutputArray = Array(1) { FloatArray(192) }
    private var dtbfaceOutputArray = Array(1) { FloatArray(192) }

    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_functionpage)
        faceNetImageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(112,112, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        initializeFaceNetInterpreter()
        initializeFaceDetector()
        val cam = findViewById<Button>(R.id.camera)
        cam.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
                return@setOnClickListener
            }
            openCamera()
        }
    }

    private fun initializeFaceNetInterpreter() {
        faceNetInterpreter = Interpreter(
            FileUtil.loadMappedFile(this, "mobile_face_net.tflite"),
            Interpreter.Options()
        )
    }

    private fun initializeFaceDetector() {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .enableTracking()
            .build()
        faceDetector = FaceDetection.getClient(highAccuracyOpts)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            var image = data?.extras?.get("data") as Bitmap?
            if (image != null) {
                imageBitmap = image.copy(Bitmap.Config.ARGB_8888, true)
                processCapturedImage(imageBitmap)
            } else {
                Toast.makeText(baseContext, "Failed to capture image", Toast.LENGTH_SHORT).show()
                comeback()
            }
        }
    }


    private fun processCapturedImage(bitmap: Bitmap) {
        var finalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        var image = InputImage.fromBitmap(finalBitmap, 0)
        faceDetector.process(image)
            .addOnFailureListener { error ->
                error.printStackTrace()
            }
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    Toast.makeText(
                        baseContext,
                        "NO FACE DETECTED",
                        Toast.LENGTH_SHORT
                    ).show()
                    comeback()
                } else {
                    val face=faces[0]
                    Toast.makeText(
                        baseContext,
                        "${faces.size} faces detected",
                        Toast.LENGTH_SHORT

                    ).show()
                    var imageBitmap2=cropFaceFromImage(imageBitmap,face)
                    var imageBitmap3= imageBitmap2.copy(Bitmap.Config.ARGB_8888, true)

                    var tensorImage = TensorImage.fromBitmap(imageBitmap3)
                    var faceNetByteBuffer: ByteBuffer? = faceNetImageProcessor?.process(tensorImage)?.buffer
                    faceOutputArray = Array(1) {
                        FloatArray(192)

                    }
                    faceNetInterpreter.run(faceNetByteBuffer, faceOutputArray)
                    compareWithDatabase()
                }
            }
    }

    private fun compareWithDatabase() {
        val collectionReference = FirebaseFirestore.getInstance().collection("Student")
        collectionReference.get().addOnSuccessListener { querySnapshot ->
            documentIterator = querySnapshot.documents.iterator()
processNextDocument()

            }
        }




    private  fun download(url: String) {

        Picasso.get().load(url).into(target)

    }

    private val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {

            if (bitmap != null) {
                // Do something with the bitmap
                var dtb = bitmap
                Toast.makeText(baseContext, "FACE LOADED FROM DBMS", Toast.LENGTH_SHORT).show()
                var finalBitmap2 = dtb.copy(Bitmap.Config.ARGB_8888, true)
                var dtbimage = InputImage.fromBitmap(finalBitmap2, 0)
                faceDetector.process(dtbimage)
                    .addOnFailureListener { error ->
                        error.printStackTrace()
                        Toast.makeText(
                            baseContext,
                            "FAILURE FACE DETECTED IN DBMS",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnSuccessListener { faces ->
                        if (faces.isEmpty()) {
                            Toast.makeText(
                                baseContext,
                                "NO FACE DETECTED IN DBMS",
                                Toast.LENGTH_SHORT
                            ).show()
                            comeback()
                        } else {
                            Toast.makeText(
                                baseContext,
                                "${faces.size} faces detected in dbms",
                                Toast.LENGTH_SHORT
                            ).show()


                            val face2=faces[0]
                            var dtb2=cropFaceFromImage(dtb, face2)
                            var dtb3=dtb2.copy(Bitmap.Config.ARGB_8888, true)
                            var tensorImage2 = TensorImage.fromBitmap(dtb3)
                            var dtbfaceNetByteBuffer: ByteBuffer? =
                                faceNetImageProcessor?.process(tensorImage2)?.buffer
                            dtbfaceOutputArray = Array(1) {
                                FloatArray(192)
                            }
                            faceNetInterpreter.run(dtbfaceNetByteBuffer, dtbfaceOutputArray)
                            var sumSquaredDiff = 0f
                            for (i in faceOutputArray.indices) {
                                for (j in faceOutputArray[i].indices) {
                                    var diff = faceOutputArray[i][j] - dtbfaceOutputArray[i][j]
                                    sumSquaredDiff += diff * diff
                                }
                            }

                            var euclideanDistance = Math.sqrt(sumSquaredDiff.toDouble()).toFloat()
                            Toast.makeText(
                                baseContext,
                                "$euclideanDistance",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (euclideanDistance < 1.0f) {

                                navigateToHallTicket()
                            }
                            else{
                                processNextDocument()
                            }
                        }

                    }

                if (dtb == null) {
                    comeback()
                }
            }

        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            Toast.makeText(
                baseContext,
                "PICTURE LOADING FAILED",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            Toast.makeText(
                baseContext,
                "PICTURE LOADING",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToHallTicket() {

        val intent5 = Intent(this@functionpage, HallTicket::class.java)

        intent5.putExtra("key",id)
        startActivity(intent5)
    }
    private fun cropFaceFromImage(imageBitmap: Bitmap, face: Face): Bitmap {
        var faceRect = face.boundingBox

        // Ensure that we are not cropping outside of image bounds
        var adjustedLeft = max(0, faceRect.left)
        var adjustedTop = max(0, faceRect.top)
        var adjustedRight = min(imageBitmap.width, faceRect.right)
        var adjustedBottom = min(imageBitmap.height, faceRect.bottom)

        // Calculate new width and height after adjustments
        val croppedWidth = adjustedRight - adjustedLeft

        val croppedHeight = adjustedBottom - adjustedTop

        return Bitmap.createBitmap(
            imageBitmap,
            adjustedLeft,
            adjustedTop,
            croppedWidth,
            croppedHeight
        )
    }

    private fun comeback(){
        val intenta = Intent(this@functionpage, functionpage::class.java)
        startActivity(intenta)
    }
    /*private fun downloadNextDocument() {
        // Increment the current document index
        currentDocumentIndex++

        // Get the reference to the Firestore collection
        val collectionReference = FirebaseFirestore.getInstance().collection("Student")

        // Retrieve all documents in the collection
        collectionReference.get().addOnSuccessListener { querySnapshot ->
            // Check if there are more documents to process
            if (currentDocumentIndex < querySnapshot.documents.size) {
                var documentSnapshot = querySnapshot.documents[currentDocumentIndex]

                // Get the photo URL from the document
                var photoUrl = documentSnapshot.getString("Photo")
                if (photoUrl != null) {
                    dtbid = documentSnapshot.id
                    Toast.makeText(this,"$dtbid",Toast.LENGTH_SHORT).show()

                    download(photoUrl)
                } else {
                    // The current document does not have a photo URL, skip to the next document
                    downloadNextDocument()
                }
            } else {
                // All documents have been processed and no match is found
                Toast.makeText(baseContext, "No match found", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
    private fun processNextDocument() {
        if (documentIterator?.hasNext() == true) {
            var documentSnapshot = documentIterator?.next()
            var photoUrl = documentSnapshot?.getString("Photo")
            if (photoUrl != null) {
                var dtbid=documentSnapshot?.id
                id= dtbid.toString()
                download(photoUrl)
            }
        } else {
            // No more documents to process
            Toast.makeText(this,"NO MORE DOCS",Toast.LENGTH_SHORT).show()

        }
    }


}

