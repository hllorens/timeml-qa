
TimeML-QA (timeml-qa)

A java application capable of performing temporal QA over TimeML annotated texts (via timegraph reasoning).

Input:
	A questionset following the format:
		questionID|TimeML file*|question in timeml-qa syntax|question in NL|Expected answer
		
		*use / for the path even in Windows environments

Output:
	A text file reporting the answers for the questions in the input questionset

LICENSE
  Copyright 2011 Hector Llorens

   Licensed under the Apache License, Version 2.0;
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
   
COMPILATION AND JARING

mvn clean package

INSTALLATION

Just move/rename compiled jar dist folder with the lib subfolder to any destination on your system.

USAGE

java -jar path-to-the-jar/timeml-qa.jar -a ACTION questionset-file/s

EXAMPLE (if timeml-qa is included in the CLASSPATH)

java -jar timeml-qa.jar -a TQA examples/example2/questionset.txt

OPTIONS

-d for debugging information



