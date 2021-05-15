package com.yvonne.proyecto.model.dto;

public class FileObject {
    private String docName;
    private String byteData;
    private String ext;

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getByteData() {
        return byteData;
    }

    public void setByteData(String byteData) {
        this.byteData = byteData;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
