# GADGET 

## What is this?

Gadget is an XML inspector.

## What can I do with this? 

When you want to have a condensed representation of (normally, a lot!) of 
well-formed XML data. This is normally useful in situations like:

- data understanding and exploration
- data migration/transformation
- data cleanup
- data complexity evaluation
- schema adherence understanding
- schema emergence

## Requirements

Gadget is a command line application and requires:

* A Java 1.4 or later compatible virtual machine for your operating system.
* Maven 2.0 or later must be installed and the "mvn" command found in your shell path (get it from http://maven.apache.org if you don't have it already).

## Running Gadget is 4 steps

from your favorite unix shell or from the windows DOS prompt and 
being connected to the internet type:

1. `mvn package`
2. `./gadget index -o data/blah -r /path/to/your/pile/of/xml/`
3. `./gadget chart data/blah`
4. `mvn jetty:run`

Point your browser to `http://127.0.0.1:8080/` to browse gadget.

NOTE: on Windows, use `gadget` instead of `./gadget`.

## What do the above steps do?

1. builds gadget and fetches the required libraries from the web
2. generates the gadget indices used by the web application by processing your XML data. Since the -r option is turned on,  it will recursively scan the given directory for all the `*.xml` files, process them and save the indices in `data/blah`
3. generates the distribution charts and the sparklines. This stage is optional, meaning that the web application will perform the same operation at runtime if not performed before, but if your dataset is large it's better to pre-compute them to increase the gadget's startup performance when browsing.
4. runs the gadget web application and provides you a user interface way to access the generated indices via your web browser.

## Are there any more in-depth docs?

Point your browser to the ./docs/index.html file for this version's
documentation.

## Licensing and legal issues

Gadget is open source software and is licensed under the BSD license
located in the LICENSE.txt file located in the same directory as this very file
you are reading.

Note however that this software depends on libraries that are not
released under the same license. If you redistribute the software it's up to you
to make sure that your redistribution complies to the sum of all the requirements
not just to the ones of the Gadget license.

## Credits

This software was created by the [SIMILE project](http://simile.mit.edu/) and originally written by Stefano Mazzocchi <stefanom at mit.edu>

It was converted to Git by Conal Tuohy, from a copy taken from the [Google Code SVN Archive](https://code.google.com/archive/p/simile-gadget/), retaining some of the history that was still available in SVN.
