package ysq.fall.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

public class FileParam extends Param {

    private String filename;
    private String contentType;
    private int fileSize;
    private ByteArrayOutputStream bOut = null;
    private FileOutputStream fOut = null;
    private File tempFile = null;


    public FileParam(String name) {
        super(name);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     *
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @return
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     *
     */
    public void createTempFile() {
        try {
            tempFile = File.createTempFile(new UID().toString().replace(":", "-"), ".upload");
            tempFile.deleteOnExit();
            fOut = new FileOutputStream(tempFile);
        } catch (IOException ex) {
            throw new FileUploadException("Could not create temporary file");
        }
    }

    /**
     *
     * @param data
     * @param start
     * @param length
     * @throws IOException
     */
    @Override
    public void appendData(byte[] data, int start, int length) throws IOException {
        if (fOut != null) {
            fOut.write(data, start, length);
            fOut.flush();
        } else {
            if (bOut == null) {
                bOut = new ByteArrayOutputStream();
            }
            bOut.write(data, start, length);
        }
        fileSize += length;
    }

    /**
     *
     * @return
     */
    public byte[] getData() {
        if (fOut != null) {
            try {
                fOut.close();
            } catch (IOException ex) {
            }
            fOut = null;
        }
        if (bOut != null) {
            return bOut.toByteArray();
        } else if (tempFile != null) {
            if (tempFile.exists()) {
                try {
                    FileInputStream fIn = new FileInputStream(tempFile);
                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                    byte[] buf = new byte[512];
                    int read = fIn.read(buf);
                    while (read != -1) {
                        bOut.write(buf, 0, read);
                        read = fIn.read(buf);
                    }
                    bOut.flush();
                    fIn.close();
                    tempFile.delete();
                    return bOut.toByteArray();
                } catch (IOException ex) {
                    /* too bad? */
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public InputStream getInputStream() {
        if (fOut != null) {
            try {
                fOut.close();
            } catch (IOException ex) {
            }
            fOut = null;
        }
        if (bOut != null) {
            return new ByteArrayInputStream(bOut.toByteArray());
        } else if (tempFile != null) {
            try {
                return new FileInputStream(tempFile) {

                    @Override
                    public void close() throws IOException {
                        super.close();
                        tempFile.delete();
                    }
                };
            } catch (FileNotFoundException ex) {
            }
        }
        return null;
    }

    /**
     *
     * @param p
     */
    public void add(FileParam p) {
        if (value == null) {
            value = new ArrayList<>();
        }
        ((List) value).add(p);
    }
}
