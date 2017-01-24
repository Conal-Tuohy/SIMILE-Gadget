


                                       G A D G E T 




  What is this?
  -------------

  Gadget is a command line XML inspector.
  
     [sound of inspector gadget theme playing in the back]



  What can I do with this? 
  ------------------------

  You can understand how the internal structure of tons of XML "looks like".
  This is normally useful in situations like: 
  
    - data migration/transformation
    - data cleanup
    - data complexity evaluation
    - schema adherence understanding



  Why was it built?
  -----------------
  
  Well, because I was given the task of transforming a few gigabytes of XML into RDF
  and I found out (the hard way!) that with that amount of data things start to break 
  down and you need radically different approaches and I was reaching the limits of
  what my 12 stages grep+sed+sort+uniq could do. 



  Requirements
  ------------

  Gadget is a command line application and requires:
  
   o  A Java 1.4 or later compatible virtual machine for your operating system.

   o  the JAVA_HOME environment variable must be set to the installation directory
     of the java virtual machine in your system.



  Oh man, why Java?
  -----------------
  
  All right, listen: I know some of you could rewrite this in, say, OCML, Ruby or
  Scheme in about 4 lines of code, but I can't. Just pay attention to the results
  and don't bother. Ah, if anybody other there wants to port the approach to C
  I'd very much appreciate, but I can't, I grew allergic to pointers.


  All right, all right, are there any docs?
  -----------------------------------------------
  
  Point your browser to the ./docs/index.html file or, even better, go to
  
       http://simile.mit.edu/gadget/


  
  Licensing and legal issues
  --------------------------

  Gadget is open source software and is licensed under the BSD license
  located in the LICENSE.txt file located in the same directory as this very file
  you are reading.

  Note however, that this software ships with libraries that are not released under
  the same license, that we are redistributing them untouched and each of them
  are licensed according to the terms of the license files located in the /legal
  subdirectory.




  Credits
  -------

  This software was created by the SIMILE project and originally written by:

   - Stefano Mazzocchi <stefanom at mit.edu>

  This product includes software developed by the Apache Software Foundation 
  (http://www.apache.org/).




                                        --- o ---


  Thanks for your interest.




                                                        The SIMILE Project
                                                      http://simile.mit.edu/

