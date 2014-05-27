/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitionis.timeml_qa;

import com.cognitionis.jtimegraph.timegraph.TimeGraph;
import com.cognitionis.timeml_basickit.*;
import java.util.*;

/**
 *
 * @author hector
 */
public class TimeGraphWrapper {

    public static enum Methods {

        ORIGINAL_ORDER, NEIGHBOURS_STARTING_FROM_DCT;
    }
    // All links: linkid->Link
    private HashMap<String, Link> links_hash;
    // hash entity -> links related to that entity
    private HashMap<String, ArrayList<String>> entity_links;
    // Queue of unadded_links
    private Queue<String> links_queue; // to add them in natural order
    // Queue of priority entities to consider next (if any preference)
    private Queue<String> entity_queue; // to alter the natural order
    // Hash link -> added ?? Seems duplicated from links queue
    private HashMap<String, String> added_links; // important if the natural order is altered
    // Hash entity -> considered ...
    private HashMap<String, String> added_entities; // important if the natural order is altered
    // TimeGraph
    private TimeGraph tg;

    // consider removing the adding methods from here and give a sorted links list instead... (some entities must have explicit dates or durations)
    public TimeGraphWrapper(TimeML tml) {
        this(tml, "neighbours-starting-from-dct");
    }

    /**
     * Build a TimeGraph
     *
     * Methods are: add-in-order, neighours-in-order, neighbours-from-dct, create-reference-relations
     *
     * reference relations include dct and means all ref timexes related to each other generated automatically from timexes
     *
     * @param tml
     * @param method
     */
    public TimeGraphWrapper(TimeML tml, String method) {
        tg = new TimeGraph();
        links_queue = new LinkedList<String>();
        entity_queue = new LinkedList<String>();
        added_links = new HashMap<String, String>();
        added_entities = new HashMap<String, String>();
        links_hash = new HashMap<String, Link>();
        entity_links = new HashMap<String, ArrayList<String>>();

        try {
            ArrayList<Link> links_array = tml.getLinks();
            Timex dct = tml.getDCT();
            switch (Methods.valueOf(method.toUpperCase())) {
                case ORIGINAL_ORDER: {
                    for (int i = 0; i < links_array.size(); i++) {
                        Link l = links_array.get(i);
                        String greginterval1_s = null;
                        String greginterval1_e = null;
                        String greginterval2_s = null;
                        String greginterval2_e = null;
                        if (tml.getTimexes().containsKey(l.get_id1()) && tml.getTimexes().get(l.get_id1()).isReference()) {
                            greginterval1_s = tml.getTimexes().get(l.get_id1()).get_lower_value();
                            greginterval1_e = tml.getTimexes().get(l.get_id1()).get_upper_value();
                        }
                        if (tml.getTimexes().containsKey(l.get_id2()) && tml.getTimexes().get(l.get_id2()).isReference()) {
                            greginterval2_s = tml.getTimexes().get(l.get_id2()).get_lower_value();
                            greginterval2_e = tml.getTimexes().get(l.get_id2()).get_upper_value();
                        }
                        if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                            System.out.println("\nAdding " + l.get_id()+ "  "+l.get_id1()+"  "+l.get_category()+"   "+l.get_id2());
                        }
                        tg.addRelation(l.get_id(), l.get_id1(), greginterval1_s, greginterval1_e, l.get_id2(), greginterval2_s, greginterval2_e, l.get_category());
                        if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                            System.out.println("\nTimeGraph after adding " + l.get_id() + ":\n" + tg);
                        }
                    }
                    tg.checkInconsistentConnections();
                }
                break;

                case NEIGHBOURS_STARTING_FROM_DCT: {
                    if (method.equals("neighbours-starting-from-dct")) {

                        // fill links_queue and entity_links
                        for (int i = 0; i < links_array.size(); i++) {
                            Link l = links_array.get(i);

                            links_hash.put(l.get_id(), l);
                            links_queue.add(l.get_id());

                            // add first entity
                            if (!entity_links.containsKey(l.get_id1())) {
                                entity_links.put(l.get_id1(), new ArrayList());
                            }
                            entity_links.get(l.get_id1()).add(l.get_id());

                            // add second entity
                            if (!entity_links.containsKey(l.get_id2())) {
                                entity_links.put(l.get_id2(), new ArrayList());
                            }
                            entity_links.get(l.get_id2()).add(l.get_id());
                        }

                        // queue dct first if exists
                        if (dct != null && entity_links.containsKey(dct.get_id())) {
                            entity_queue.add(dct.get_id());
                        }

                        while (!links_queue.isEmpty()) {
                            if (entity_queue.size() == 0) {
                                String current_link = links_queue.poll();
                                while (added_links.containsKey(current_link)) {
                                    // already added
                                    if (!links_queue.isEmpty()) {
                                        current_link = links_queue.poll();
                                    } else {
                                        current_link = null;
                                        break;
                                    }
                                }
                                if (current_link != null) {
                                    Link l = links_hash.get(current_link);
                                    String greginterval1_s = null;
                                    String greginterval1_e = null;
                                    String greginterval2_s = null;
                                    String greginterval2_e = null;
                                    if (tml.getTimexes().containsKey(l.get_id1()) && tml.getTimexes().get(l.get_id1()).isReference()) {
                                        greginterval1_s = tml.getTimexes().get(l.get_id1()).get_lower_value();
                                        greginterval1_e = tml.getTimexes().get(l.get_id1()).get_upper_value();
                                    }
                                    if (tml.getTimexes().containsKey(l.get_id2()) && tml.getTimexes().get(l.get_id2()).isReference()) {
                                        greginterval2_s = tml.getTimexes().get(l.get_id2()).get_lower_value();
                                        greginterval2_e = tml.getTimexes().get(l.get_id2()).get_upper_value();
                                    }
                                    tg.addRelation(l.get_id(), l.get_id1(), greginterval1_s, greginterval1_e, l.get_id2(), greginterval2_s, greginterval2_e, l.get_category());
                                    added_links.put(current_link, "");
                                }
                            } else {
                                String current_entity = entity_queue.poll();
                                added_entities.put(current_entity, "");
                                // add neighbours
                                ArrayList<String> links2neighbours = entity_links.get(current_entity);
                                for (int i = 0; i < links2neighbours.size(); i++) {
                                    String current_link = links2neighbours.get(i);
                                    if (!added_links.containsKey(current_link)) {
                                        Link l = links_hash.get(current_link);
                                        String greginterval1_s = null;
                                        String greginterval1_e = null;
                                        String greginterval2_s = null;
                                        String greginterval2_e = null;
                                        if (tml.getTimexes().containsKey(l.get_id1()) && tml.getTimexes().get(l.get_id1()).isReference()) {
                                            greginterval1_s = tml.getTimexes().get(l.get_id1()).get_lower_value();
                                            greginterval1_e = tml.getTimexes().get(l.get_id1()).get_upper_value();
                                        }
                                        if (tml.getTimexes().containsKey(l.get_id2()) && tml.getTimexes().get(l.get_id2()).isReference()) {
                                            greginterval2_s = tml.getTimexes().get(l.get_id2()).get_lower_value();
                                            greginterval2_e = tml.getTimexes().get(l.get_id2()).get_upper_value();
                                        }
                                        tg.addRelation(l.get_id(), l.get_id1(), greginterval1_s, greginterval1_e, l.get_id2(), greginterval2_s, greginterval2_e, l.get_category());
                                        added_links.put(current_link, "");
                                        // add related entity (one is self so it will be already added)
                                        if (!added_entities.containsKey(l.get_id1())) {
                                            entity_queue.add(l.get_id1());
                                        }
                                        if (!added_entities.containsKey(l.get_id2())) {
                                            entity_queue.add(l.get_id2());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw new Exception("Invalid method: " + method);
                    }
                }
            }

            tg.removeEmptyChain();

        } catch (Exception e) {
            System.err.println("Errors found (TimeGraph):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);

            }
        }
    }

    public TimeGraph getTimeGraph() {
        return tg;
    }
}
