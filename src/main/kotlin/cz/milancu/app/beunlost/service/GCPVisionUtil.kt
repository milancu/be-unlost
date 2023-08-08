package cz.milancu.app.beunlost.service

import com.google.cloud.vision.v1.*
import com.google.protobuf.ByteString
import cz.milancu.app.beunlost.domain.model.entity.CustomAnnotation
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
import mu.KotlinLogging
import org.apache.catalina.core.ApplicationPart
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.roundToInt


private val log = KotlinLogging.logger { }

@Component
class GCPVisionUtil(
    private val documentRepository: DocumentRepository,
) {

    /**
     * Optimizes the given image file by resizing it to a predefined width and maintaining the aspect ratio.
     * The image is resized to a width of 850 pixels, and the height is adjusted according to the aspect ratio.
     *
     * @param file The image file to optimize.
     * @return A ByteString representation of the optimized image, or null if the optimization fails.
     * @throws IOException If an I/O error occurs while reading or writing the image file.
     */
    @Throws(IOException::class)
    fun optimizeImage(file: ApplicationPart): ByteString? {
        val newWidth = 850
        val newHeight = (image.height.toDouble() * newWidth / image.width).roundToInt()
        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)

        val g = resizedImage.createGraphics()
        g.drawImage(image, 0, 0, newWidth, newHeight, null)
        g.dispose()

        return resizedImage
    }

    /**
     * Asynchronously extracts text from an image file, optimizes the image, and saves the extracted text and annotations
     * to a document identified by a document ID.
     *
     * @param file The image file to extract text from.
     * @param documentId The ID of the document to save the extracted text and annotations to.
     */
    @Async
    fun extractTextAndSave(file: ApplicationPart, documentId: UUID) {
        val imgBytes = optimizeImage(file)
        val img: Image = Image.newBuilder().setContent(imgBytes).build()
        val requests: MutableList<AnnotateImageRequest> = ArrayList()
        val feat: Feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
        val request: AnnotateImageRequest = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build()
        val entityAnnotation: MutableList<EntityAnnotation> = ArrayList()
        requests.add(request)

        val document = documentRepository.findById(documentId)

        ImageAnnotatorClient.create().use { client ->
            val response: BatchAnnotateImagesResponse = client.batchAnnotateImages(requests)
            val responses: List<AnnotateImageResponse> = response.responsesList
            for (res in responses) {
                if (res.hasError()) {
                    document?.documentStatus = DocumentStatus.ERROR
                    documentRepository.save(document!!)
                    log.error { "Error: ${res.error.message}" }
                    throw IllegalStateException(res.error.message)
                }
                for (annotation in res.textAnnotationsList) {
                    entityAnnotation.add(annotation)
                }
            }
        }

        document?.allTextDescription = entityAnnotation[0].description
        document?.customAnnotations = mapToCustomAnnotation(entityAnnotation)
        document?.documentStatus = DocumentStatus.READY
        documentRepository.save(document!!)
        log.info { "Extracted text for document with id: ${document.id}, Document status: ${document.documentStatus}" }
    }

    /**
     * Maps a list of EntityAnnotation objects to a list of CustomAnnotation objects.
     *
     * @param entityAnnotation the list of EntityAnnotation objects to map
     * @return the list of CustomAnnotation objects mapped from the input list of EntityAnnotation objects
     */
    fun mapToCustomAnnotation(entityAnnotation: List<EntityAnnotation>): List<CustomAnnotation> {
        val customAnnotations: MutableList<CustomAnnotation> = ArrayList()
        for (annotation in entityAnnotation.subList(1, entityAnnotation.size)) {
            customAnnotations.add(
                CustomAnnotation(
                    description = annotation.description,
                    x = annotation.boundingPoly.verticesList[0].x,
                    y = annotation.boundingPoly.verticesList[0].y,
                    width = annotation.boundingPoly.verticesList[1].x - annotation.boundingPoly.verticesList[0].x,
                    height = annotation.boundingPoly.verticesList[3].y - annotation.boundingPoly.verticesList[0].y
                )
            )
        }
        return customAnnotations
    }
}