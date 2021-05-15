package com.yvonne.proyecto.manager;

import com.yvonne.proyecto.model.Document;
import com.yvonne.proyecto.model.User;
import com.yvonne.proyecto.model.dto.FileObject;
import com.yvonne.proyecto.repository.CrudManager;
import com.yvonne.proyecto.repository.DocumentRepository;
import com.yvonne.proyecto.util.Util;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DocumentManager implements CrudManager<Document> {

    @Autowired
    private DocumentRepository documentRepository;

    private static final String FILE_NAME_REGEX = "([0-9]{17}+_)";

    @Override
    public void create(Document doc) throws Exception {
        documentRepository.save(doc);
    }

    //subir el documento
  /*  public List<Document> generateNewDocument( Document document ) throws CrudServiceException
    {
        List<Document> result = new ArrayList<>();
        try
        {
            result = fileGenerator( document ); //ya vi el proceso, eso crea el documento 'fisicamente'
            for ( int i = 0; i < result.size(); i++ )
            {
                if ( i % document.getEmployees().size() == 0 )//?????
                {
                    Document newDocument = create( result.get( i ) ); //si no voy mal, esto es el createentity
                    for ( int j = 0; j < document.getEmployees().size(); j++ )
                    {
                        // por cada archivo y empleado, se crea un registro en bbdd|| ESTO NO HACE FALTA TODO
                        EmployeeEntity employeeId = employeeService
                                .getEmployeeByNif( document.getEmployees().get( j ) );
                        DocumentEmployeeEntity entity = new DocumentEmployeeEntity(); //esta es la del muchos a muchos, sobra
                        entity.setDocument( newDocument );
                        entity.setEmployee( employeeId );

                        documentEmployeeService.create( entity );
                    }
                }
            }
        }
        catch ( CrudServiceException e )
        {
            LOG.error( "ERROR: el archivo a guardar no existe " + e.getMessage(), e );
        }

        return result;
    }

    @Override
    public List<DocumentDto> getAllFromUser( String user ) throws CrudServiceException
    {
        EmployeeEntity employee = employeeService.obtainSessionUser( user );
        List<Document> docsByCurrentUser = documentEmployeeService.getAllDocumentsByUser( employee.getEmpCode() );

        Map<String, DocumentDto> result = oneOfEachFile( docsByCurrentUser );

        result.forEach( ( k, v ) -> v.setDocPath( null ) );

        return new ArrayList<>( result.values() );
    }

    @Override
    public List<DocumentDto> getAllFiles() throws CrudServiceException
    {
        List<Document> allDocs = documentEmployeeService.getAllDocuments();

        // Map<Object, List<DocumentDto>> documentsDto = allDocs.stream().map( d -> simplifiedFor( d ) ).distinct()
        // .collect( Collectors.groupingBy( d -> String.valueOf( d.getDocName() ) ) );

        Map<String, DocumentDto> result = oneOfEachFile( allDocs );

        result.forEach( ( k, v ) -> v.setDocPath( null ) );

        return new ArrayList<>( result.values() );
    }

    @Override
    public byte[] downloadFile( Integer docCode ) throws CrudServiceException, IOException
    {
        Document file = obtainByPrimaryKey( docCode );
        String path = file.getDocPath();
        // comprobar si es un pdf, porque estara encriptado si lo es
        if ( file.getDocPath().contains( "pdf" ) )
        {
            decryptDocument( path );
        }
        byte[] pdfToByteArray = Files.readAllBytes( Paths.get( path ) );

        if ( file.getDocPath().contains( "pdf" ) )
        {
            try
            {
                encryptDocument( path );
            }
            catch ( IOException | DocumentException e )
            {
                LOG.error( "ERROR: el archivo no existe " + e.getMessage(), e );
            }
        }
        return pdfToByteArray;
    }

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

    public void deleteDocById( Integer id )
    {
        try
        {
            Document doc = obtainByPrimaryKey( id );

            List<Integer> dirs = documentEmployeeService.getAllUsersWithDocument( id );
            String path = EJBConstants.FILE_PATH;
            boolean deleted = deleteFileFromDir( dirs, path, "\\" + doc.getDocName() );
            if ( deleted )
            {
                documentEmployeeService.deleteById( id );
                deleteById( id );
            }
        }
        catch ( CrudServiceException e )
        {
            LOG.error( "ERROR: el archivo a borrar no existe " + e.getMessage(), e );

        }
    }*/

   /* private Map<String, DocumentDto> oneOfEachFile( List<DocumentEntity> documents )
    {
        Map<String, DocumentDto> result = new LinkedHashMap<>();
        for ( DocumentEntity documentEntity : documents )
        {
            // mas facil de ver con este metodo
            DocumentDto onlyOneDoc = fromEntityToDto( documentEntity );

            List<Integer> empCodes = documentEmployeeService.getAllUsersWithDocument( documentEntity.getDocCode() );
            String[] fixedName = documentEntity.getDocName().split( FILE_NAME_REGEX );

            onlyOneDoc.setEmployees( employeeList( empCodes ) );
            onlyOneDoc.setDocName( fixedName[1] );
            result.put( fixedName[1], onlyOneDoc );

        }
        return result;
    }
*/
   /* private List<Document> fileGenerator( DocumentDto dto ) throws CrudServiceException
    {
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

                User employeeId = documento.getUser(); //mmmmmmm no estoy segura, el usuario tiene que venir por parametro
                String path = EJBConstants.FILE_PATH + employeeId.getEmpNif(); //solo el path? no queiro hacer subdirectorios

                createdPath = path + "/" + randomName + recivedFile.getDocName() + recivedFile.getExt(); //esta ya esta bien

                File f = new File( createdPath ); //se crea el archivo el este path

                try (
                        FileOutputStream writtenFile = new FileOutputStream( f ) ) //el try 'escribe' el achivo
                {
                    // como el ultimo siempre es el archivo, se hace asi
                    String encodedTextFile = fileByteData[fileByteData.length - 1];

                    byte[] decodedString = Base64.getDecoder().decode( encodedTextFile );
                    writtenFile.write( decodedString );
                }
                catch ( IOException e )
                {
                    LOG.error( "ERROR: el archivo a guardar no existe " + e.getMessage(), e );
                    throw new CrudServiceException();
                }
                if ( recivedFile.getExt().equals( ".pdf" ) ) //esto se deberia hacer igualmente? solo pensaba aceptar pdf
                {
                    try
                    {
                        encryptDocument( createdPath );
                    }
                    catch ( IOException | DocumentException e )
                    {
                        LOG.error( "ERROR: el archivo no existe " + e.getMessage(), e );
                    }
                }
                //esto ya esta
                entity.setName( randomName + recivedFile.getDocName() + recivedFile.getExt() );
                entity.setPath( f.getPath() );
                result.add( entity );

        }
        return result; //devuelve una lista de documentos, con su nombre y path
    }

    private void encryptDocument( String path ) throws IOException, DocumentException
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

    private void fileRename( String oldFile, String newFile )
    {
        File file = new File( oldFile );
        file.delete();

        File decryptedFile = new File( newFile );
        decryptedFile.renameTo( file );
    }

    private boolean deleteFileFromDir( List<Integer> empId, String path, String fileName )
    {
        boolean deleted = false;

        for ( Integer userid : empId )
        {
            try
            {
                EmployeeEntity emp = employeeService.obtainByPrimaryKey( userid );
                String fixedPath = path + emp.getEmpNif() + fileName;
                File file = new File( fixedPath );
                if ( file.delete() )
                {
                    System.out.print( "deleted file from directory" );
                    deleted = true;
                }
                else
                {
                    deleted = false;
                }
            }
            catch ( CrudServiceException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return deleted;

    }

    private DocumentDto fromEntityToDto( Document document )
    {
        DocumentDto d = new DocumentDto();
        d.setDocCode( document.getDocCode() );
        d.setDocCreDat( document.getDocCreDat() );
        d.setDocCreUse( document.getDocCreUse() );
        d.setDocModDat( document.getDocModDat() );
        d.setDocModUse( document.getDocModUse() );

        d.setDocPath( document.getDocPath() );

        d.setDocName( document.getDocName().split( FILE_NAME_REGEX )[1] );

        return d;
    }*/

    @Override
    public List<Document> getAll() throws Exception {
        return null;
    }

    @Override
    public void delete(Document object) throws Exception {

    }

    @Override
    public Boolean update(Document object) throws Exception {
        return null;
    }

    @Override
    public Document getById(Integer id) throws Exception {
        return null;
    }
}
