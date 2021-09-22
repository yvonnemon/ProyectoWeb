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

    private static final Logger LOG = LogManager.getLogger(Document.class);

    @Override
    public void create(Document doc) throws Exception {
        documentRepository.save(doc);
    }

    //subir el documento
    public void generateNewDocument(DocumentDto document) throws Exception {
        List<Document> result;
        try {
            result = fileGenerator(document); //esto crea el documento 'fisicamente'
            for (int i = 0; i < result.size(); i++) {
                create(result.get(i)); //crear en la base de datos
            }
        } catch (Exception e) {
            LOG.error("ERROR: el archivo a guardar no existe " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(Integer docCode) throws IOException {

        Document file = documentRepository.findById(docCode).orElse(null);
        if (file != null) {
            String path = file.getPath();
            byte[] pdfToByteArray = Files.readAllBytes(Paths.get(path));
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

    public List<Document> getAllFromUser(User user) throws Exception {

        return documentRepository.findDocumentByUser(user);
    }

    public List<Document> getLasts() throws Exception {
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
    public Boolean update(Document object) throws Exception {
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
            if (file.delete()) {
                System.out.print("deleted file from directory");
                deleted = true;
            } else {
                deleted = false;
            }
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
            Document entity = new Document();

            User employeeId = userManager.getById(dto.getUser().getId());
            entity.setUser(employeeId);
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

            entity.setName(randomName + recivedFile.getDocName() + recivedFile.getExt());
            entity.setPath(f.getPath());
            result.add(entity);
        }
        return result; //devuelve una lista de documentos, con su nombre y path
    }
}