package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.DocumentDto;
import com.yvonne.proyecto.model.dto.FileObject;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.DocumentRepository;
import com.yvonne.proyecto.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@PropertySource("classpath:variables.properties")
public class DocumentManager implements CrudManager<Document> {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserManager userManager;

    private static final String FILE_NAME_REGEX = "([0-9]{17}+_)";

    @Value("${spring.datasource.filepath}")
    private String FILE_PATH;

    @Value("${spring.datasource.documentPassword}")
    private String PASSWORD;

    private static final Logger LOG = LogManager.getLogger(Document.class);

    @Override
    public void create(Document doc) throws Exception {
        documentRepository.save(doc);
    }

    //subir el documento
    public void generateNewDocument(DocumentDto document) {
        List<Document> result;
        try {
            result = fileGenerator(document); //esto crea el documento 'fisicamente'
            for (Document value : result) {
                create(value); //crear en la base de datos
            }
        } catch (Exception e) {
            LOG.error("ERROR: el archivo a guardar no existe " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(Integer docCode) throws IOException {

        Document file = documentRepository.findById(docCode).orElse(null);
        if (file != null) {

            String path = file.getPath();
            decryptDocument(path, file);
            byte[] pdfToByteArray = Files.readAllBytes(Paths.get(path));

            encryptDocument(path);

            //por algun motivo, no me lo devuelve en base 64, asi que:
            return Base64.getEncoder().encode(pdfToByteArray);

        } else {
            return null;
        }
    }

    @Override
    public List<Document> getAll() throws Exception {
        return (List<Document>) documentRepository.findAll();
    }

    public List<Document> getAllFromUser(User user) {
        return documentRepository.findDocumentByUser(user);
    }

    public List<Document> getLasts() {
        Pageable limit = PageRequest.of(0, 5);
        List<Document> allDocs = documentRepository.findAllByOrderByIdDesc(limit);

        return allDocs;
    }

    public List<Document> getUserLasts(User user) throws Exception {
        Pageable limit = PageRequest.of(0, 5);
        List<Document> allDocs = documentRepository.findDocumentByUserOrderByIdDesc(user, limit);

        return allDocs;
    }

    @Override
    public void delete(Document object) throws Exception {
        try {
            boolean borradofisico = deleteFileFromDir(object);
            if (borradofisico) {
                documentRepository.delete(object);
            } else {
                throw new Exception("ERROR: el archivo  no existe "); //TODO
            }
        } catch (Exception e) {
            LOG.error("ERROR: el archivo  no existe " + e.getMessage(), e);
            throw new Exception("ERROR: el archivo  no existe ");
        }
    }

    @Override
    public Boolean update(Document object) {
        return null;
    }

    @Override
    public Document getById(Integer id) throws Exception {
        return documentRepository.findById(id).orElse(null);
    }

    public void deleteAllFromUser(User user){
        documentRepository.deleteAllByUser(user);
    }

    private boolean deleteFileFromDir(Document doc) throws Exception {
        //este metodo borra el archivo de la carpeta donde esta
        boolean deleted = false;
        try {
            File file = new File(doc.getPath());
            deleted = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("ERROR: la ruta no existe ");
        }
        return deleted;
    }

    private List<Document> fileGenerator(DocumentDto dto) throws Exception {

        List<Document> result = new ArrayList<>();
        //fileObject es el archivo en si
        List<FileObject> recivedFiles = dto.getFileArray();
        String createdPath;
        //por cada archivo subido en el front:
        for (FileObject recivedFile : recivedFiles) {
            String[] fileByteData = recivedFile.getByteData().split(",");

            //random nombre para evitar repetidos
            String randomName = Util.generateRandomDate() + "_";
            Document documentEntity = new Document();

            User employeeId = userManager.getById(dto.getUser().getId());
            documentEntity.setUser(employeeId);
            String path = FILE_PATH + '\\' + employeeId.getDni();

            //quitar las barras bajas para evitar problemas al recuperar los datos en el front
            if (recivedFile.getDocName().contains("_")) {
                String wrongChars = recivedFile.getDocName().replaceAll("_", "-");
                createdPath = path + '-' + randomName + wrongChars + recivedFile.getExt();
            } else {
                createdPath = path + '-' + randomName + recivedFile.getDocName() + recivedFile.getExt();
            }

            File f = new File(createdPath); //se crea el archivo el este path

            try (
                    FileOutputStream writtenFile = new FileOutputStream(f)) //el try 'escribe' el achivo
            {
                // como el ultimo siempre es el archivo, se hace asi
                String encodedTextFile = fileByteData[fileByteData.length - 1];

                byte[] decodedString = Base64.getDecoder().decode(encodedTextFile);
                writtenFile.write(decodedString);

            } catch (IOException e) {
                LOG.error("ERROR: el archivo a guardar no existe " + e.getMessage(), e);
                throw new Exception();
            }
            documentEntity.setPassword( encryptDocument(f.getPath()) );
            documentEntity.setName(randomName + recivedFile.getDocName() + recivedFile.getExt());
            documentEntity.setPath(f.getPath());
            result.add(documentEntity);
        }
        return result; //devuelve una lista de documentos, con su nombre y path
    }

    private String encryptDocument(String path) throws IOException {


        File file = new File(path);
        PDDocument pdd = PDDocument.load(file);

        AccessPermission ap = new AccessPermission();

        String pass = Util.randomString(25);
        String encriptedPass = Base64.getEncoder().encodeToString(pass.getBytes());

        StandardProtectionPolicy stpp = new StandardProtectionPolicy(pass , pass , ap);
        stpp.setEncryptionKeyLength(128);

        stpp.setPermissions(ap);
        pdd.protect(stpp);
        pdd.save(path);
        pdd.close();
        return encriptedPass;
    }

    private void decryptDocument(String path, Document document) throws IOException {

        File file = new File(path);
        byte[] decodedBytes = Base64.getDecoder().decode(document.getPassword());
        String decodedString = new String(decodedBytes);

        PDDocument pdd = PDDocument.load(file,decodedString);
        pdd.setAllSecurityToBeRemoved(true);
        pdd.save(path);
        pdd.close();
    }
}