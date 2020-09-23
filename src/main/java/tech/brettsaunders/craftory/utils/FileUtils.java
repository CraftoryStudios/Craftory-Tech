/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.lang.StringUtils;

public class FileUtils {

  public static boolean copyFile(final File toCopy, final File destFile) {
    try {
      return FileUtils.copyStream(new FileInputStream(toCopy),
          new FileOutputStream(destFile));
    } catch (final FileNotFoundException e) {
      Log.warn("File not found when copying resources");
    }
    return false;
  }

  private static boolean copyFilesRecusively(final File toCopy, final File destDir) {
    assert destDir.isDirectory();

    if (!toCopy.isDirectory()) {
      return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
    } else {
      final File newDestDir = new File(destDir, toCopy.getName());
      if (!newDestDir.exists() && !newDestDir.mkdir()) {
        return false;
      }
      File[] files =  toCopy.listFiles();
      if (files == null) return false;
      for (final File child : files) {
        if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
          return false;
        }
      }
    }
    return true;
  }

  public static boolean copyJarResourcesRecursively(final File destDir,
      final JarURLConnection jarConnection) throws IOException {

    final JarFile jarFile = jarConnection.getJarFile();

    for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
      final JarEntry entry = e.nextElement();
      if (entry.getName().startsWith(jarConnection.getEntryName())) {
        final String filename = StringUtils.removeStart(entry.getName(), //
            jarConnection.getEntryName());

        final File f = new File(destDir, filename);
        if (!entry.isDirectory()) {
          final InputStream entryInputStream = jarFile.getInputStream(entry);
          if (!FileUtils.copyStream(entryInputStream, f)) {
            return false;
          }
          entryInputStream.close();
        } else {
          if (!FileUtils.ensureDirectoryExists(f)) {
            throw new IOException("Could not create directory: "
                + f.getAbsolutePath());
          }
        }
      }
    }
    return true;
  }

  public static void copyResourcesRecursively(
      final URL originUrl, final File destination) {
    try {
      final URLConnection urlConnection = originUrl.openConnection();
      if (urlConnection instanceof JarURLConnection) {
        FileUtils.copyJarResourcesRecursively(destination,
            (JarURLConnection) urlConnection);
      } else {
        FileUtils.copyFilesRecusively(new File(originUrl.getPath()),
            destination);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static boolean copyStream(final InputStream is, final File f) {
    try {
      return FileUtils.copyStream(is, new FileOutputStream(f));
    } catch (final FileNotFoundException e) {
      Log.warn("File not found when copying resource stream");
    }
    return false;
  }

  private static boolean copyStream(final InputStream is, final OutputStream os) {
    try {
      final byte[] buf = new byte[1024];

      int len = 0;
      while ((len = is.read(buf)) > 0) {
        os.write(buf, 0, len);
      }
      is.close();
      os.close();
      return true;
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  private static boolean ensureDirectoryExists(final File f) {
    return f.exists() || f.mkdir();
  }

  public static String removeStart(String str, String remove) {
    if (isEmpty(str) || isEmpty(remove)) {
      return str;
    }
    if (str.startsWith(remove)) {
      return str.substring(remove.length());
    }
    return str;
  }

  public static boolean isEmpty(CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
}