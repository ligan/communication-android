package com.glit.filetask;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Gan on 7/4/2017.
 */

public class FolderZipper {

    private String mFolderPath;
    private String mZipFilePath;

    public boolean zipFiles(String srcFolder, String destZipFile) {
        boolean result = false;

        try {
            Log.d("Inspection", "Program starts zipping the given files");
            zipFolder(srcFolder, destZipFile);
            result = true;
            Log.d("Inspection", "Given files are successfully zipperd");
        }catch(Exception e) {
            Log.d("Inspection", "Some errors happened during the zip process");
        } finally {
            return result;
        }
    }

    private void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        /*
         * Create the output stream to zip file result.
         */
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        /*
         * add the folder to the zip
         */
        addFolderToZip("", srcFolder, zip);
        /*
         * close the zip objects
         */
        zip.flush();
        zip.close();
    }

    private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws Exception {
       /*
        * create the file object for inputs.
        */
        File folder = new File(srcFile);

        /*
         *  if the folder is empty add empty folder to the zip file
         */
        if(flag == true) {
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
        } else {
            /*
             * if  the current name is directory, recursively traverse it to get the files
             */
            if(folder.isDirectory()) {
                addFolderToZip(path, srcFile, zip);
            } else {
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        if(folder.list().length == 0) {
            Log.d("Inspection", folder.getName());
            addFileToZip(path, srcFolder, zip, true);
        } else {
            for(String fileName : folder.list()) {
                if(path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                } else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }
}

