package com.putable.videx.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.putable.videx.utils.StringConsumer;

public class ExternalProgramRunner {

    private static class StreamGobbler implements Callable<String> {
        private InputStream inputStream;
        private Consumer<String> consumer;
     
        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }
     
        @Override
        public String call() throws Exception {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
            .forEach(consumer);
            return null;
        }
    }
    ExternalProgramRunner(String[] progAndArgs, Consumer<String> stdout, Consumer<String> stderr) {

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(progAndArgs);
        //Map<String,String> env = builder.environment();
        //env.put("PATH","/home/ackley/bin");
        builder.directory(new File(System.getProperty("user.home")));
        Process process;
        if (stdout == null) stdout = new StringConsumer();
        if (stderr == null) stderr = new StringConsumer();
        try {
            process = builder.start();
            List<StreamGobbler> gobblers = Arrays.asList(
                    new StreamGobbler(process.getInputStream(), stdout),
                    new StreamGobbler(process.getErrorStream(), stderr));
            Executors.newSingleThreadExecutor().invokeAll(gobblers);
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String[] cmd = {"/home/ackley/bin/VLC","-h"};
        StringConsumer stdout = new StringConsumer();        
        StringConsumer stderr= new StringConsumer();        
        new ExternalProgramRunner(cmd,stdout,stderr);
        System.out.println("----STDOUT\n"+stdout.toString()+"----");
        System.out.println("----STDERR\n"+stderr.toString()+"----");

        System.err.println("Clams");
        System.exit(0);
    }
}
