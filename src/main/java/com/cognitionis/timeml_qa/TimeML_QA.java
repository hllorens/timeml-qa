/*
 * Copyright 2014 hector.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognitionis.timeml_qa;

/**
 *
 * @author hector
 */
public class TimeML_QA {

    public static String answer_question(String question, TimeGraphWrapper tg) {
        String predicted_answer = "unknown";
        String[] command = question.trim().split("\\s+");
        if (command[0].equals("IS")) {
            predicted_answer = (tg.getTimeGraph().checkRelation(command[1], command[3], command[2]));
        } else {

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
            } else {

                if (command[0].equals("WHEN")) {
                    predicted_answer = (tg.getTimeGraph().getEntitiesIncludeEntity(command[1]));
                }
            }
        }
        return predicted_answer;

    }
}
