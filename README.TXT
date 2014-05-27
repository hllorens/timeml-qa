
TimeML-QA (timeml-qa)

A java application capable of performing temporal QA over a TimeML annotated text (via timegraph reasoning).

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

mkdir -p build/classes; javac -cp lib/commons-cli-1.2.jar:lib/joda-time-1.6.jar  -d build/classes src/main/java/com/cognigionis/timeml_qa/Main.java
mkdir dist; jar cfm dist/timeml-qA.jar manifest.mf -C build/classes/  .
cp -R lib/ dist/;

INSTALLATION

Just move/rename compiled jar dist folder with the lib subfolder to any destination on your system.

USAGE

java -jar [path-to-the-jar/timeml-qa.jar] file/s

EXAMPLE (if timeml-qa is included in the CLASSPATH)

java -jar timeml-qa.jar xxxxxxxxxxxxx

OPTIONS

-d for debugging information



