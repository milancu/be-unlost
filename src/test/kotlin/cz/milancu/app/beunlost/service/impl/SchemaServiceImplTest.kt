package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.repository.*
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.SchemaService
import cz.milancu.app.beunlost.utils.Utils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class SchemaServiceImplTest(
    @Autowired val folderRepository: FolderRepository,
    @Autowired val schemaService: SchemaService,
    @Autowired val schemaRepository: SchemaRepository
) {

    private val folderId = Utils.folderId

    @AfterEach
    fun cleanDB() {
        schemaRepository.deleteAll()
        folderRepository.deleteAll()
    }

    @BeforeEach
    fun initTenantContext() {
        folderRepository.save(Utils.createFolder())
    }


    @Test
    fun createSchema() {
        val labels = ArrayList<String>()
        labels.addAll(listOf("label1", "label2", "label3", "label4"))

        schemaService.createSchema(folderId = folderId, labels = labels)
        assertEquals(1, schemaRepository.count())
    }

    @Test
    fun updateSchema() {
        val labels = ArrayList<String>()
        labels.addAll(listOf("label1", "label2", "label3", "label4"))

        val newLabels = ArrayList<String>()
        newLabels.addAll(listOf("label1", "label2", "label3", "label4"))

        schemaService.createSchema(folderId = folderId, labels = labels)

        val schema = schemaService.updateSchema(newLabels, folderId)

        assertEquals(newLabels, schema.labels)
    }
}