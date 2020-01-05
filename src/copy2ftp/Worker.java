/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copy2ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author vazhenin
 */
public class Worker {

    FTPClient f = new FTPClient();
    String server = "";
    String username;
    String password;
    String log4jPath;
    static Logger log = Logger.getLogger(Worker.class);
    String paramFile;
    String dir;
    String ftpWorkDir;

    public Worker(String paramFile) {
        this.paramFile = paramFile;
        initialize();
    }

    void run() {
        try {
            f.connect(server);
            f.login(username, password);
            f.cwd(this.ftpWorkDir);
            f.setBufferSize(102400); // 100 Mb
            log.info("Connected to " + this.server + " as " + this.username);
            File[] list = getDirFiles(this.dir); // get list of files we want to upload
            FTPFile[] dirs = f.listDirectories();

            for (int i = 0; i < list.length; i++) {
                File localFile = list[i];
                uploadFile2FTP(localFile);
                if (localFile.delete()) {
                    log.info("File " + localFile.getName() + " has been deleted");
                }
            }

        } catch (Exception e) {
            log.fatal(e);
        }
    }

    void initialize() {
        ParseXMLUtilities xml = new ParseXMLUtilities(this.paramFile);
        xml.initiate();
        this.log4jPath = xml.getNodeValue(xml.getChildNodes("parameters"), "log4jPath");
        this.server = xml.getNodeValue(xml.getChildNodes("parameters"), "ftpHost");
        this.username = xml.getNodeValue(xml.getChildNodes("parameters"), "ftpUser");
        this.password = xml.getNodeValue(xml.getChildNodes("parameters"), "ftpPwd");
        this.dir = xml.getNodeValue(xml.getChildNodes("parameters"), "dir");
        this.ftpWorkDir = xml.getNodeValue(xml.getChildNodes("parameters"), "ftpWorkDir");
        PropertyConfigurator.configure(this.log4jPath);
    }

    File[] getDirFiles(String directoryPath) {
        File dir = new File(directoryPath);
        File[] list = null;
        if (!dir.isDirectory()) {
            log.fatal("Specified is not directory");
        } else {
            list = dir.listFiles();
        }
        if (list == null) {
            log.fatal("Null list of files returned");
        }
        return list;
    }

    void uploadFile2FTP(File f) {
        try {
            InputStream is = new FileInputStream(f);
            boolean done = this.f.storeFile(f.getName(), is);
            is.close();
            if (done) {
                log.info("File " + f.getName() + " has been uploaded");
            } else {
                log.info("Error uploading " + f.getName());
            }

        } catch (Exception e) {
            log.fatal(e);
        }
    }

}
