package com.cognitionis.timeml_qa;

import com.cognitionis.nlp_files.XMLFile;
import com.cognitionis.timeml_basickit.TML_file_utils;
import com.cognitionis.timeml_basickit.TimeML;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hector
 */
public class TimeMLQATest {

    public TimeMLQATest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of TMQA part ActionHandler
     */
    @Test
    public void testTimeMLQA_questions() throws Exception {
        System.out.println("tmqa");
        String input_file = this.getClass().getResource("/example2/little-file.tml").toURI().toString();

        TimeGraphWrapper tg = null;
        XMLFile nlpfile = new XMLFile(input_file, null);
        if (!nlpfile.getClass().getSimpleName().equals("XMLFile")) {
            throw new Exception("Requires XMLFile files as input. Found: " + nlpfile.getClass().getSimpleName());
        }
        if (!nlpfile.getExtension().equalsIgnoreCase("tml")) {
            nlpfile.overrideExtension("tml");
        }
        if (!nlpfile.isWellFormatted()) {
            throw new Exception("File: " + input_file + " is not a valid TimeML (.tml) XML file.");
        }
        TimeML tml = TML_file_utils.ReadTml2Object(nlpfile.getFile().getCanonicalPath());
        tg = new TimeGraphWrapper(tml, "original_order");

        if (tg == null) {
            throw new Exception("Null TG wrapper.");
        }
        test_question( tg, "IS ei1 BEFORE ei2", "yes");
        test_question( tg, "IS ei2 BEFORE ei1", "no");
        test_question( tg, "IS ei1 BEFORE ei4", "yes");
        test_question( tg, "IS ei1 BEFORE 1998-02-20", "yes");
        // add more questions, complicate the file
    }
    
    public void test_question(TimeGraphWrapper tg, String question,  String expected_answer){
        String predicted_answer=TimeML_QA.answer_question(question,tg);
        if(!predicted_answer.split(" ")[0].equals(expected_answer)){
            System.err.println("ERROR: "+question+ " \n\t expected: "+expected_answer+" \n\t predicted:"+predicted_answer);
        }
        assertEquals(expected_answer, predicted_answer.split(" ")[0]);        
    }
    
}
