package com.cognitionis.timeml_qa;

import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.commons.cli.*;
import com.cognitionis.utils_basickit.*;

/**
 * @author Hector Llorens
 * @since 2012
 *
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String action = "interactive"; //default action
            String action_parameters = null;
            String input_files[];
            String input_text = null;
            long startTime = System.currentTimeMillis();
            //System.out.println("Current Date Time : " + dateFormat.format(ExecTime));


            Options opt = new Options();
            //addOption(String opt, boolean hasArg, String description)
            opt.addOption("h", "help", false, "Print this help");
            opt.addOption("a", "action", true, "Action/s to be done (tqa, interactive...)");
            opt.addOption("ap", "action_parameters", true, "Optionally actions can have parameters (-a annotate -ap model=TIPSemB)");
            opt.addOption("t", "text", true, "To use text instead of a file (for short texts)");
            opt.addOption("d", "debug", false, "Debug mode: Output errors stack trace (default: disabled)");

            PosixParser parser = new PosixParser();
            CommandLine cl_options = parser.parse(opt, args);
            input_files = cl_options.getArgs();
            HelpFormatter hf = new HelpFormatter();
            if (cl_options.hasOption('h')) {
                hf.printHelp("FreeTime", opt);
                System.exit(0);
            } else {
                if (cl_options.hasOption('d')) {
                    System.setProperty("DEBUG", "true");
                }
                if (cl_options.hasOption('a')) {
                    action = cl_options.getOptionValue("a");
                    try {
                        ActionHandler.Action.valueOf(action.toUpperCase());
                    } catch (Exception e) {
                        String errortext = "\nValid acctions are:\n";
                        for (ActionHandler.Action c : ActionHandler.Action.values()) {
                            errortext += "\t" + c.name() + "\n";
                        }
                        throw new RuntimeException("\tIlegal action: " + action.toUpperCase() + "\n" + errortext);
                    }
                } /*else {
                action = "annotate";
                }*/
                if (cl_options.hasOption("ap")) {
                    action_parameters = cl_options.getOptionValue("ap");
                }

                if (cl_options.hasOption("t")) {
                    input_text = cl_options.getOptionValue("t");
                }

            }
            // Convert input text to a file if necessary
            if (input_text != null && input_text.length() > 0) {
                System.err.println("FreeTime text: " + input_text);
                // Save text to a default file
                //String tmpfile = FileUtils.getApplicationPath() + "program-data/tmp/tmp" + dateFormat.format(ExecTime);
                final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
                String tmpfile = "tmp" + dateFormat.format(new Date());
                BufferedWriter outfile = new BufferedWriter(new FileWriter(tmpfile));
                try {
                    outfile.write(input_text + "\n");
                } finally {

                    if (outfile != null) {
                        outfile.close();
                    }
                    input_files = new String[1];
                    input_files[0] = tmpfile;
                }
            }

            ActionHandler.doAction(action, input_files, action_parameters);

            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                long endTime = System.currentTimeMillis();
                long sec = (endTime - startTime) / 1000;
                if (sec < 60) {
                    System.err.println("Done in " + StringUtils.twoDecPosS(sec) + " sec!\n");
                } else {
                    System.err.println("Done in " + StringUtils.twoDecPosS(sec / 60) + " min!\n");
                }
            }

            if (input_text != null) {
                System.err.println("Result:\n");
                BufferedReader reader = new BufferedReader(new FileReader(input_files[0] + ".tml"));
                String text = null;
                while ((text = reader.readLine()) != null) {
                    System.out.println(text + "\n");
                }
            }
        } catch (Exception e) {
            System.err.println("Errors found:\n\t" + e.getMessage() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
            }
            System.exit(1);
        }
    }
}
