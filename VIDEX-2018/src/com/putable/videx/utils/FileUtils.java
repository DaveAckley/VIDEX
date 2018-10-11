package com.putable.videx.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Static methods for manipulating files and directories in various ways.
 * 
 * @author ackley
 *
 */
public class FileUtils {

    /**
     * Get the modification time of path, if possible.
     * 
     * @param path
     *            non-null path to check
     * @return null if the modification time cannot be accessed, else the last
     *         modification time as produced by
     *         {@link Files#getLastModifiedTime(Path, java.nio.file.LinkOption...)}
     * 
     */
    public static FileTime getModificationTime(Path path) {
        FileTime ret = null;
        try {
            ret = Files.getLastModifiedTime(path);
        } catch (IOException e) {
            System.err
                    .println("getModificationTime: Path " + path + " got " + e);
            // Fall through with ret=null
        }
        return ret;
    }

    /**
     * Find the lexicographically greatest directory name that is a subdirectory
     * of basedir. Note this is a one-level scan without recursion.
     * 
     * @param basedir
     *            non-null Path to directory to search
     * @return the Path to the subdirectory with the largest name, except
     *         instead it returns null if basedir is not a directory, or it is a
     *         directory but contains no subdirectories, or an IOException
     *         occurs.
     */
    public static Path findMaxSubdirNameUnder(final Path basedir) {
        TreeSet<File> subdirs = new TreeSet<File>();
        File[] files = basedir.toFile().listFiles();
        if (files == null)
            return null;
        for (final File fileEntry : files) {
            if (fileEntry.isDirectory()) {
                subdirs.add(fileEntry);
            }
        }
        if (subdirs.isEmpty())
            return null;
        return subdirs.last().toPath();
    }

    /**
     * Return the entire contents of toFile as a String.
     * 
     * @param toFile
     *            non-null Path to read
     * @return the contents of toFile as a String, or instead null if an
     *         IOException occurs
     * 
     * @throws NullPointerException
     *             if toFile is null, or possibly other exceptions as described
     *             at {@link Files#readAllBytes(Path)}
     */
    public static String readWholeFile(Path toFile) {
        byte[] bytes = readWholeFileAsByteArray(toFile);
        if (bytes == null) return null;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Return the entire contents of toFile as a byte[]
     * 
     * @param toFile
     *            non-null Path to read
     * @return the contents of toFile as a byte[], or instead null if an
     *         IOException occurs
     * 
     * @throws NullPointerException
     *             if toFile is null, or possibly other exceptions as described
     *             at {@link Files#readAllBytes(Path)}
     */
    public static byte[] readWholeFileAsByteArray(Path toFile) {
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(toFile);
        } catch (IOException e) {
            System.err.println("readWholeFileAsByteArray: Path " + toFile + " got " + e);
            // Fall through with ret=null
        }
        return encoded;
    }


    /**
     * Write text to toFile, replacing any previous content if toFile already
     * existed
     * 
     * @param toFile
     *            The file to write
     * @param text
     *            The content to write into toFile
     * @return null if all went well, or else an IOException describing the
     *         problem
     */
    public static IOException writeWholeFile(Path toFile, String text) {
        try {
            Writer w = new FileWriter(toFile.toFile());
            w.append(text);
            w.close();
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    /**
     * Create a new, empty subdirectory under basedir with a name based on the
     * current date and time, measured down to the second. Note this method may
     * sleep up to 2.5 seconds while attempting to find a date-time that hasn't
     * already been created under basedir.
     * 
     * @param basedir
     *            directory name to create the new subdirectory within
     * @return the Path to the newly created subdirectory
     * @throws IOException
     *             if a unique new timestamp directory was not successfully
     *             created after five attempts
     */
    public static Path createUniqueTimestampSubdir(String basedir)
            throws IOException {
        Path savedir = null;
        boolean worked = false;
        for (int tries = 0; tries < 5; ++tries) {
            String savetime = new SimpleDateFormat("YYYYMMddHHmmss")
                    .format(new Date());
            savedir = Paths.get(basedir, savetime);
            worked = new File(savedir.toString()).mkdirs();
            if (worked)
                return savedir;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new IOException(
                "Unable to create timestamp subdir under " + basedir);
    }

    public static Long fromLex(String lexOnly) {
        StringReader sr = new StringReader(lexOnly);
        Long ret = null;
        try {
            ret = fromLex(sr);
            if (sr.read() >= 0)
                return null; // Crap left over
        } catch (IOException e) {
        }
        return ret;
    }

    public static Long fromLex(Reader r) throws IOException {
        int ch = r.read();
        if (ch < 0)
            return null;
        long len;
        if (ch == '0')
            return null;
        if (ch == '9') {
            Long llen = fromLex(r);
            if (llen == null)
                return null;
            len = llen;
        } else if (ch >= '1' && ch <= '8') {
            len = ch - '0';
        } else
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            ch = r.read();
            if (ch < '0' || ch > '9')
                return null; // Includes EOF
            sb.append((char) ch);
        }
        return Long.valueOf(sb.toString());
    }

    public static String toLex(long num) {
        if (num < 0)
            throw new IllegalArgumentException();
        String snum = "" + num;
        int slen = snum.length();
        if (slen < 9)
            return slen + snum;
        return "9" + toLex(slen) + snum;
    }

    public static void getFilesRecursively(final File dir, List<File> list) {
        for (final File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory()) {
                getFilesRecursively(fileEntry, list);
            } else {
                list.add(fileEntry);
            }
        }
    }

    public static File[] getFilesRecursively(final File dir) {
        List<File> list = new LinkedList<File>();
        getFilesRecursively(dir, list);
        return list.toArray(new File[0]);
    }

}
