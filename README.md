Java Tesseract OCR Automatic Translator
===============================

**Target Java Build: 1.8**

**Dependency1: Google Cloud Translate API (Please sign up!)**

**Dependency2: Java OpenCV 400 (It's free)**

**This application supports both Windows and OSX**


Please visit www.mbaprogrammer.com to download the dependency files.

#Prerequisite 0 - Tesseract 4
1. Install Tesseract 4
2. Make sure that you add the Tesseract binary file (e.g., tesseract.exe) in your path, so that you can access the Tesseract binary file in any folder.

https://github.com/tesseract-ocr/tesseract

For those who don't know Tesseract yet... It's a machine-learning based open source OCR project. I also uploaded the latest jpn_vert.traineddata file on my GitHub to provide you with the higest accuracy in reading Japanese comic books (aka manga), which are mostly vertically aligned.


#Prerequisite 1 - Google Cloud Translate API java library (Do not get confused with Google Translate API!)
1. Please sign up the Google Cloud Translate API service.
2. Download the API key file
3. Register the environmental variable accordingly.
Otherwise, this application won't work for you. Alternatively, you can turn off the auto-translation function.

You can download the Google Translate API jar file through the below link also.
http://www.mbaprogrammer.com/bulletin/view.jsp?categoryseq=17

#Prerequisite 2 - OpenCV 4 java library
Please download all jar files and dll file (OSX case, dylib file) from the below link.
Please put them in the same folder.

#How to execute
1. Compile all files
2. Create a bat file.

java -Djava.library.path=".\*" -Dsun.java2d.opengl=true -Xms512m -Dfile.encoding="UTF-8" -classpath ".\*" edu.nd.ImageViewer

Or you can download the binary files at www.mbaprogrammer.com

![Example image1](http://www.mbaprogrammer.com/upload/ttt1.png)
