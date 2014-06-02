package com.cognitionis.timeml_qa;

import com.cognitionis.jtimegraph.gregoriangraph.GregorianGraph;
import com.cognitionis.timeml_basickit.*;
import com.cognitionis.utils_basickit.*;
import java.io.*;
import com.cognitionis.nlp_files.*;
import java.util.HashMap;

/**
 * @author Hector Llorens
 */
public class ActionHandler {

    public static enum Action {

        READ_TML, TQA, WIKITQA;
    }

    public static String getParameter(String params, String param) {
        String paramValue = null;
        if (params != null && params.contains(param)) {
            if (params.matches(".*" + param + "=[^,]*,.*")) {
                paramValue = params.substring(params.lastIndexOf(param + "=") + param.length() + 1, params.indexOf(',', params.lastIndexOf(param + "=")));
            } else {
                if (params.matches(".*" + param + "=[^,]*")) {
                    paramValue = params.substring(params.lastIndexOf(param + "=") + param.length() + 1);
                }
            }
        }
        return paramValue;
    }

    public static void doAction(String action, String[] input_files, String action_parameters) {

        try {
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                System.err.println("\n\nDoing action: " + action.toUpperCase() + "\n------------");
            }
            switch (Action.valueOf(action.toUpperCase())) {

                /*
                 * Input: a file with the following format:
                 * questionID|TimeML-file|Question in timeml-qa syntax|Question in NL
                 * Output: a file with answers attached
                 * 
                 * 
                 */
                case TQA:
                    int total_questions = 0;
                    int total_correct = 0;
                    HashMap<String, Integer> file_totals = new HashMap<>();
                    HashMap<String, Integer> file_corrects = new HashMap<>();
                    for (int i = 0; i < input_files.length; i++) {
                        File f = new File(input_files[i]);
                        String path = FileUtils.getFolder(f.getCanonicalPath());
                        BufferedReader pipesreader = new BufferedReader(new FileReader(input_files[i]));
                        
                        int linen = 0;
                        try {
                            String pipesline;
                            String[] pipesarr = null;
                            String curr_fileid = "";
                            TimeGraphWrapper tg = null;
                            while ((pipesline = pipesreader.readLine()) != null) {
                                String predicted_answer = "unknown";
                                linen++;
                                total_questions++;
                                pipesarr = pipesline.split("\\|");
                                if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                    System.err.println("Processing: " + pipesline);
                                }
                                if (pipesarr.length != 5) {
                                    throw new Exception("ERROR: BAD FORMAT required 5 fields separated by |");
                                }
                                if (!curr_fileid.equals(pipesarr[1])) {
                                    curr_fileid = pipesarr[1];
                                    XMLFile nlpfile = new XMLFile(path + pipesarr[1], null);
                                    if (!nlpfile.getClass().getSimpleName().equals("XMLFile")) {
                                        throw new Exception("Requires XMLFile files as input. Found: " + nlpfile.getClass().getSimpleName());
                                    }
                                    if (!nlpfile.getExtension().equalsIgnoreCase("tml")) {
                                        nlpfile.overrideExtension("tml");
                                    }
                                    if (!nlpfile.isWellFormatted()) {
                                        throw new Exception("File: " + input_files[i] + " is not a valid TimeML (.tml) XML file.");
                                    }
                                    TimeML tml = TML_file_utils.ReadTml2Object(nlpfile.getFile().getCanonicalPath());
                                    tg = new TimeGraphWrapper(tml, "original_order");
                                }

                                if (tg == null) {
                                    throw new Exception("Null TG wrapper.");
                                }

                                String[] command = pipesarr[2].trim().split("\\s+");
                                System.out.print(pipesline + "|predicted=");

                                if (command[0].equals("IS")) {
                                    predicted_answer = (tg.getTimeGraph().checkRelation(command[1], command[3], command[2]));
                                }

                                if (command[0].equals("LIST")) {
                                    if (command[1].equals("BETWEEN")) {
                                        predicted_answer = (tg.getTimeGraph().getEntitiesBetween(command[2], command[3]));
                                    } else if (command[1].equals("BEFORE")) {
                                        predicted_answer = (tg.getTimeGraph().getEntitiesBeforeEntity(command[2]));
                                    } else {
                                        if (command[1].equals("AFTER")) {
                                            predicted_answer = (tg.getTimeGraph().getEntitiesAfterEntity(command[2]));
                                        } else {
                                            if (command[1].equals("SINCE")) {
                                                predicted_answer = (tg.getTimeGraph().getEntitiesSinceEntity(command[2]));
                                            } else {
                                                if (command[1].equals("WITHIN")) {
                                                    predicted_answer = (tg.getTimeGraph().getEntitiesWithinEntity(command[2]));
                                                } else {
                                                    predicted_answer = ("\t Need to implement this");
                                                }
                                            }
                                        }
                                    }
                                }

                                if (command[0].equals("WHEN")) {
                                    predicted_answer = (tg.getTimeGraph().getEntitiesIncludeEntity(command[1]));
                                }

                                System.out.println(predicted_answer);
                                if(predicted_answer.split(" ")[0].equals(pipesarr[4])){
                                    total_correct++;
                                }
                            }
                            
                        } catch (Exception e) {
                            System.err.println("\nErrors found (timeml-qa):\n\t" + e.toString() +" - line "+ linen+ "\n");
                            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                e.printStackTrace(System.err);
                                System.exit(1);
                            }
                        } finally {
                            if (pipesreader != null) {
                                pipesreader.close();
                            }
                        }
                    }
                    
                    System.out.println("questions="+total_questions+" correct="+total_correct+" accuracy="+((double)((double) total_correct/(double) total_questions)));
                    
                    break;

                /*
                 * Reads a TimeML file and answers the questions below (to be added manually for testing)
                 */
                case READ_TML:
                    for (int i = 0; i < input_files.length; i++) {
                        XMLFile nlpfile = new XMLFile(input_files[i], null);
                        if (!nlpfile.getClass().getSimpleName().equals("XMLFile")) {
                            throw new Exception("Requires XMLFile files as input. Found: " + nlpfile.getClass().getSimpleName());
                        }
                        if (!nlpfile.getExtension().equalsIgnoreCase("tml")) {
                            nlpfile.overrideExtension("tml");
                        }
                        if (!nlpfile.isWellFormatted()) {
                            throw new Exception("File: " + input_files[i] + " is not a valid TimeML (.tml) XML file.");
                        }
                        TimeML tml = TML_file_utils.ReadTml2Object(nlpfile.getFile().getCanonicalPath());
                        TimeGraphWrapper tg = new TimeGraphWrapper(tml, "original_order");
                        System.out.println("\nFinal TimeGraph:\n----------\n" + tg.getTimeGraph());
                        System.out.println("\nIs ei112 before ei105? " + tg.getTimeGraph().checkRelation("ei112", "ei105", "BEFORE"));
                        System.out.println("\nIs ei105 before ei112? " + tg.getTimeGraph().checkRelation("ei105", "ei112", "BEFORE"));
                        System.out.println("\nIs ei112 before ei107? " + tg.getTimeGraph().checkRelation("ei112", "ei107", "BEFORE"));
                        System.out.println("\nIs ei107 before ei112? " + tg.getTimeGraph().checkRelation("ei107", "ei112", "BEFORE"));
                        System.out.println("\nRelation between ei112 ei105? " + tg.getTimeGraph().getRelation("ei112", "ei105"));
                        System.out.println("\nRelation between ei105 ei112? " + tg.getTimeGraph().getRelation("ei105", "ei112"));
                        System.out.println("\nRelation between ei112 ei107? " + tg.getTimeGraph().getRelation("ei112", "ei107"));
                        System.out.println("\nRelation between ei107 ei112? " + tg.getTimeGraph().getRelation("ei107", "ei112"));
                        System.out.println("\nNeed some more complicated relations... ");
                        System.out.println("\nPrint all before 110: " + tg.getTimeGraph().getEntitiesBeforeEntity("ei110"));
                        System.out.println("\nPrint all before 107: " + tg.getTimeGraph().getEntitiesBeforeEntity("ei107"));
                        System.out.println("\nPrint all after 110: " + tg.getTimeGraph().getEntitiesAfterEntity("ei110"));
                        System.out.println("\nPrint all after 107: " + tg.getTimeGraph().getEntitiesAfterEntity("ei107"));
                        System.out.println("\nPrint all possible rels between : ");
                        System.out.println("\nPossible consistent rels between 110 107: " + tg.getTimeGraph().getPossibleConsistentRelations("ei110", "ei107"));
                        System.out.println("\nPossible consistent rels between 105 107: " + tg.getTimeGraph().getPossibleConsistentRelations("ei105", "ei107"));
                        System.out.println("\nPrint all entities between: ");
                        System.out.println("\nEntities between 110 107: " + tg.getTimeGraph().getEntitiesBetween("ei110", "ei107"));
                        System.out.println("\nEntities between 105 107: " + tg.getTimeGraph().getEntitiesBetween("ei105", "ei107"));
                        System.out.println("\nPlaying with TIMEX: ");
                        System.out.println("\nPrint all before 1999: ");
                        System.out.println("\nPrint all after 1999: ");
                        System.out.println("\nPrint all between 1999 and 2000: ");
                        System.out.println("\nPrint all in 1999 (calculate upper-lower): ");
                        System.out.println("\nDo not implement anything esle.");
                    }
                    break;


                case WIKITQA:
                    GregorianGraph gg = new GregorianGraph("/home/hector/Desktop/wikitime.txt");
                    for (int i = 0; i < input_files.length; i++) {
                        BufferedReader pipesreader = new BufferedReader(new FileReader(input_files[i]));

                        try {
                            int linen = 0;
                            String pipesline;
                            String[] pipesarr = null;
                            while ((pipesline = pipesreader.readLine()) != null) {
                                linen++;
                                pipesarr = pipesline.split("\\|");
                                if (pipesarr.length < 3) {
                                    continue;
                                }
                                if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                    System.err.println("Processing: " + pipesline);
                                }
                                String[] command = pipesarr[2].trim().split("\\s+");
                                System.out.println(pipesarr[0] + ". " + pipesarr[1] + ": " + pipesarr[2] + "?");

                                if (command[0].equals("IS")) {
                                    System.out.println("\t" + gg.checkRelation(command[1], command[3], command[2]));
                                }

                                if (command[0].equals("LIST")) {
                                    if (command[1].equals("BETWEEN")) {
                                        System.out.println("\t" + gg.getEntitiesBetween(command[2], command[3]));
                                    } else if (command[1].equals("BEFORE")) {
                                        System.out.println("\t" + gg.getEntitiesBeforeEntity(command[2]));
                                    } else {
                                        if (command[1].equals("AFTER")) {
                                            System.out.println("\t" + gg.getEntitiesAfterEntity(command[2]));
                                        } else {
                                            if (command[1].equals("SINCE")) {
                                                System.out.println("\t" + gg.getEntitiesSinceEntity(command[2]));
                                            } else {
                                                if (command[1].equals("WITHIN")) {
                                                    System.out.println("\t" + gg.getEntitiesWithinEntity(command[2]));
                                                } else {
                                                    System.out.println("\t Need to implement this");
                                                }
                                            }
                                        }
                                    }
                                }

                                if (command[0].equals("WHEN")) {
                                    System.out.println("\t" + gg.getEntitiesIncludeEntity(command[1]));
                                }

                            }
                        } finally {
                            if (pipesreader != null) {
                                pipesreader.close();
                            }
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            System.err.println("\nErrors found (ActionHandler):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }

    }
}
