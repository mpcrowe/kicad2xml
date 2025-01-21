# kicad2xml
A utility to convert kicad .mod file into an xml document
See [kicad docs](https://dev-docs.kicad.org/en/file-formats/sexpr-intro/index.html)
for more information.
comple with<br/>
`javac KicadToXml.java`<br/>
run with <br/>
`java KicadToXml test/MOD18_MAX-M10M_UBL.kicad_mod`<br/>
it will generate an out.xml file. optionally the destination filename can be
provided<br/>
The resulting file can be used by an xslt markup to
generate a gEDA PCB footprint or whatever you would like.<br/>
TODO add xslt to generate an SVG and xlt to generate geda PCB<br/>
 
