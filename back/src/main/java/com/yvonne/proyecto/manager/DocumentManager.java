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
import org.dom4j.DocumentException;
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
import java.util.Map;

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
    public List<Document> generateNewDocument( DocumentDto document ) throws Exception
    {
        List<Document> result = new ArrayList<>();
        try
        {
            result = fileGenerator( document ); //ya vi el proceso, eso crea el documento 'fisicamente'
            for ( int i = 0; i < result.size(); i++ )
            {
                create( result.get( i ) ); //si no voy mal, esto es el createentity
            }
        }
        catch ( Exception e )
        {
            LOG.error( "ERROR: el archivo a guardar no existe " + e.getMessage(), e );
        }
        return result;
    }

    public byte[] downloadFile( Integer docCode ) throws IOException
    {

        Document file = documentRepository.findById( docCode ).orElse(null);
        String path = file.getPath();
         //   decryptDocument( path );

        byte[] pdfToByteArray = Files.readAllBytes( Paths.get( path ) );

           /* try
            {
                encryptDocument( path );
            }
            catch ( IOException | DocumentException e )
            {
                LOG.error( "ERROR: el archivo no existe " + e.getMessage(), e );
            }*/
        //por algun motivo, no me lo devuelve en base 64, asi que:
        return Base64.getEncoder().encode(pdfToByteArray);
    }
/*
    @Override
    public byte[] downloadAllFiles( String user ) throws CrudServiceException, IOException
    {
        EmployeeEntity employee = employeeService.obtainSessionUser( user );

        List<Document> allDocs = documentEmployeeService.getAllDocumentsByUser( employee.getEmpCode() );

        Map<String, DocumentDto> result = oneOfEachFile( allDocs );
        List<DocumentDto> documents = new ArrayList<>( result.values() );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (
                ZipOutputStream zipOutputStream = new ZipOutputStream( byteArrayOutputStream ) )
        {
            for ( DocumentDto document : documents )
            {

                String path = document.getDocPath();
                if ( document.getDocPath().contains( "pdf" ) )
                {
                    decryptDocument( path );
                }
                zipOutputStream.putNextEntry( new ZipEntry( new File( path ).getName() ) );

                byte[] bytes = Files.readAllBytes( Paths.get( path ) );

                zipOutputStream.write( bytes, 0, bytes.length );
                zipOutputStream.closeEntry();

                if ( document.getDocPath().contains( "pdf" ) )
                {
                    encryptDocument( path );
                }

            }
        }
        catch ( IOException | DocumentException e )
        {
            LOG.error( e );
            throw new CrudServiceException( e );
        }
        return byteArrayOutputStream.toByteArray();
    }
    */


  /*  private void encryptDocument( String path ) throws IOException, DocumentException
    {
        PasswordManager pwdManager = PasswordManager.readFile();

        String password = pwdManager.getPassword();

        String stampedPdf = path + "_stamp.pdf";
        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader( path );
        OutputStream out = new FileOutputStream( stampedPdf );
        PdfStamper stamper = new PdfStamper( reader, out );

        stamper.setEncryption( password.getBytes(), password.getBytes(), EncryptionConstants.ALLOW_COPY,
                EncryptionConstants.ENCRYPTION_AES_128 );

        stamper.close();
        reader.close();
        out.close();

        fileRename( path, stampedPdf );
    }

    private void decryptDocument( String path )
    {
        PasswordManager pwdManager = PasswordManager.readFile();

        String password;
        PdfReader reader;
        PdfDocument pdfDoc;
        try
        {
            password = pwdManager.getPassword();

            String decryptedPath = path + ".pdf"; // quiza sobra
            reader = new PdfReader( path, new ReaderProperties().setPassword( password.getBytes() ) );
            pdfDoc = new PdfDocument( reader, new PdfWriter( decryptedPath ) );
            pdfDoc.close();
            reader.close();
            fileRename( path, decryptedPath );

        }
        catch ( IOException e )
        {
            LOG.error( "ERROR: el archivo no existe " + e.getMessage(), e );
        }
    }
/*
    private void fileRename( String oldFile, String newFile )
    {
        File file = new File( oldFile );
        file.delete();

        File decryptedFile = new File( newFile );
        decryptedFile.renameTo( file );
    }*/
    @Override
    public List<Document> getAll() throws Exception {
        return (List<Document>) documentRepository.findAll();
    }

    public List<Document> getAllFromUser(User user) throws Exception {

        return documentRepository.findDocumentByUser(user);
    }

    public List<Document> getLasts() throws Exception {
        Pageable limit = PageRequest.of(0,5);
        List<Document> allDocs = (List<Document>) documentRepository.findAllByOrderByIdDesc(limit);

        return allDocs;
    }

    public List<Document> getUserLasts(User user) throws Exception {
        Pageable limit = PageRequest.of(0,5);
        List<Document> allDocs = (List<Document>) documentRepository.findDocumentByUserOrderByIdDesc(user,limit);

        return allDocs;
    }


    @Override
    public void delete(Document object) throws Exception {
        try {
            boolean borradofisico = deleteFileFromDir(object);
            if(borradofisico){
                documentRepository.delete(object);
            } else {
                throw new Exception("ERROR: el archivo  no existe "); //TODO
            }
        } catch (Exception e) {
            LOG.error( "ERROR: el archivo  no existe " + e.getMessage(), e );
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

    private boolean deleteFileFromDir(Document doc ) {

            boolean deleted = false;
                try
                {
                    File file = new File( doc.getPath() );
                    if ( file.delete() ) {
                        System.out.print( "deleted file from directory" );
                        deleted = true;
                    }
                    else
                    {
                        deleted = false;
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }

        return deleted;
    }

    private List<Document> fileGenerator( DocumentDto dto ) throws Exception    {
        List<Document> result = new ArrayList<>();
        //fileObject es el archivo en si
        List<FileObject> recivedFiles = dto.getFileArray(); //esto podria ser reemplazable con una lista en el parametro de entrada?
        String createdPath;
        for ( FileObject recivedFile : recivedFiles )
        {
            String[] fileByteData = recivedFile.getByteData().split( "," );

            String randomName = Util.generateRandomDate() + "_";

            //este bucle es innecesario proque solo van a un empleado
            Document entity = new Document();

            User employeeId = userManager.getById(dto.getUser().getId()); //mmmmmmm no estoy segura, el usuario tiene que venir por parametro
            entity.setUser(employeeId);
            String path = FILE_PATH +'\\'+ employeeId.getDni(); //solo el path? no queiro hacer subdirectorios

            if(recivedFile.getDocName().contains("_")){
                String wrongChars = recivedFile.getDocName().replaceAll("_", "-");
                createdPath = path + '-' + randomName + wrongChars + recivedFile.getExt(); //esta ya esta bien

            } else {
                createdPath = path + '-' + randomName + recivedFile.getDocName() + recivedFile.getExt(); //esta ya esta bien

            }

            File f = new File( createdPath ); //se crea el archivo el este path

            try (
                    FileOutputStream writtenFile = new FileOutputStream( f ) ) //el try 'escribe' el achivo
            {
                // como el ultimo siempre es el archivo, se hace asi
                String encodedTextFile = fileByteData[fileByteData.length - 1];

                byte[] decodedString = Base64.getDecoder().decode( encodedTextFile );
                writtenFile.write( decodedString );

                //encryptDocument( createdPath );  no se di deberia hacer esto o no
            }
            catch ( IOException e )
            {
                LOG.error( "ERROR: el archivo a guardar no existe " + e.getMessage(), e );
                throw new Exception();
            }
            //esto ya esta
            entity.setName( randomName + recivedFile.getDocName() + recivedFile.getExt() );
            entity.setPath( f.getPath() );
            result.add( entity );

        }
        return result; //devuelve una lista de documentos, con su nombre y path
    }

}
