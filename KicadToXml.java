
import java.lang.StringBuilder;
import java.util.StringTokenizer;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.stream.*;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class KicadToXml
{
	XMLStreamWriter out;
	public String fname;
	public String outFname;

	public KicadToXml(String[] args)
	{
		fname = args[0];
		if(args.length > 1)
			outFname = args[1];
		else
			outFname = "out.xml";
	}


	private String stripChar(String str, String ch)
	{
		if( str.indexOf(ch) <0)
			return(str);
		StringBuilder sb = new StringBuilder(str);
		int index = sb.indexOf(ch);
		while(index>=0)
		{
			sb.deleteCharAt(index);
			index = sb.indexOf(ch);
		}
		return(sb.toString());
	}


	public void processAt(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(at");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1, eindex).trim();
		StringTokenizer st = new StringTokenizer(frag, " ");
		out.writeCharacters("\t\t");

		out.writeStartElement("at");
			out.writeAttribute("x", st.nextToken());
			out.writeAttribute("y", st.nextToken());
			while(st.hasMoreTokens())
			{
				out.writeEmptyElement(st.nextToken());
			}
		out.writeEndElement();
		out.writeCharacters("\r\n");
	}


	public void processSingle(String frag, String id) throws XMLStreamException
	{
		int index = frag.indexOf("("+id);
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1, eindex).trim();
		StringTokenizer st = new StringTokenizer(frag, " ");

		out.writeEmptyElement(id);
			String temp  = stripChar(st.nextToken(), "\"");
			out.writeAttribute("val", temp);
	}


	public void processDouble(String frag, String id, String first, String second) throws XMLStreamException
	{
		int index = frag.indexOf("("+id);
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1, eindex).trim();

		StringTokenizer st = new StringTokenizer(frag, " ");
		int count = st.countTokens();
		if(count >2)
		{
			out.writeStartElement(id);
				out.writeAttribute(first, st.nextToken());
				out.writeAttribute(second, st.nextToken());
				while(st.hasMoreTokens())
				{
					out.writeEmptyElement(st.nextToken());
				}
			out.writeEndElement();
		}
		else
		{
			out.writeEmptyElement(id);
				out.writeAttribute(first, st.nextToken());
				out.writeAttribute(second, st.nextToken());
		}
	}


	public void processFont(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(font");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		frag = frag.substring(index+1).trim();

		out.writeStartElement("font");
		processDouble(frag, "size", "w", "h");
		processSingle(frag, "thickness");
		out.writeEndElement();
		out.writeCharacters("\r\n");
	}


	public void processEffects(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(effects");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		frag = frag.substring(index+1).trim();
		out.writeCharacters("\t\t");
		out.writeStartElement("effects");
			processFont(frag);
		out.writeCharacters("\t\t");
		out.writeEndElement();
		out.writeCharacters("\r\n");
	}


	public void processLayer(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(layer");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1, eindex).trim();
		frag = stripChar(frag, "\"");

		out.writeCharacters("\t\t");
		out.writeStartElement("layer");
		out.writeCharacters(stripChar(frag, "\""));
		out.writeEndElement();
		out.writeCharacters("\r\n");
	}


	public void processLayers(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(layers");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1, eindex).trim();
		frag = stripChar(frag, "\"");
		out.writeCharacters("\r\n\t\t");
		out.writeStartElement("layers");
		StringTokenizer st = new StringTokenizer(frag, " ");
		while(st.hasMoreElements())
		{
			String temp  = stripChar(st.nextToken(), "\"");
			out.writeEmptyElement("layer");
			out.writeAttribute("val", temp);
		}
		out.writeEndElement();
	}


	public void processFpText(String frag) throws XMLStreamException
	{
		int ename = frag.indexOf(" ");
		out.writeCharacters("\r\n\t\t");
		String elName = frag.substring(0, ename).trim();
		out.writeStartElement(elName);

		frag = frag.substring(ename).trim();
		ename = frag.indexOf(" ", 1);
		String val = stripChar(frag.substring(1,ename), "\"");
		out.writeCharacters(val);
		out.writeEndElement();
		out.writeCharacters("\r\n");

		frag = frag.substring(ename).trim();
		processAt(frag);
		processLayer(frag);
		processEffects(frag);
	}


	public void processFpLine(String frag) throws XMLStreamException
	{
		out.writeCharacters("\r\n\t\t");
			processDouble(frag, "start", "x", "y");
			processDouble(frag, "end", "x", "y");
			processSingle(frag, "layer");
			processSingle(frag, "width");
		out.writeCharacters("\r\n");
	}


	public void processFpCircle(String frag) throws XMLStreamException
	{
		out.writeCharacters("\r\n\t\t");
			processDouble(frag, "center", "x", "y");
			processDouble(frag, "end", "x", "y");
			processSingle(frag, "layer");
			processSingle(frag, "width");
			processSingle(frag, "fill");
		out.writeCharacters("\r\n");
	}


	public void processFpArc(String frag) throws XMLStreamException
	{
		out.writeCharacters("\r\n\t\t");
			processDouble(frag, "start", "x", "y");
			processDouble(frag, "mid", "x", "y");
			processDouble(frag, "end", "x", "y");
			processSingle(frag, "layer");
			processSingle(frag, "width");
			processSingle(frag, "fill");
		out.writeCharacters("\r\n");
	}


	public void processPad(String frag) throws XMLStreamException
	{
		StringTokenizer st = new StringTokenizer(frag, " ");
		String temp  = stripChar(st.nextToken(), "\"");
		out.writeAttribute("number",temp);
		temp  = stripChar(st.nextToken(), "\"");
		out.writeAttribute("type",temp);
		temp  = stripChar(st.nextToken(), "\"");
		out.writeAttribute("shape",temp);

		out.writeCharacters("\r\n\t\t");
			processDouble(frag, "at", "x", "y");
			processDouble(frag, "size", "w", "h");
			processDouble(frag, "end", "x", "y");
			processSingle(frag, "drill");
			if(frag.contains("(layers"))
				processLayers(frag);
			else
				processSingle(frag, "layer");
			processSingle(frag, "width");
			processSingle(frag, "fill");
		out.writeCharacters("\r\n");
	}


	public int singleValueAttribute(String tline, int startIndex) throws XMLStreamException
	{
		int nstart = tline.indexOf("(", startIndex);
		nstart++;
		int nend = tline.indexOf(")", nstart);
		String token = tline.substring(nstart, nend);
		int spnum = token.indexOf(" ");
		String name = token.substring(0, spnum);
		String val = token.substring(spnum);
		val = val.trim();
		if(val.startsWith("\""))
		{
			val = val.substring(1);
			val = val.substring(0, val.indexOf("\""));
		}
		val = val.trim();
		out.writeAttribute(name, val);
		return(nend);
	}


	private String elementName(String str)
	{
		str = str.trim();
		int start = str.indexOf('(');
		if(start < 0 )
		{
			return(null);
		}
		start++;
		return(str.substring(start, str.indexOf(' ', start)).trim());
	}


	private int count(String str, int ch)
	{
		int retval = 0;
		int loc = str.indexOf(ch);
		while(loc>=0)
		{
			retval++;
			loc = str.indexOf(ch, loc+1);
		}
		return(retval);
	}


	public void parseSexpression(BufferedReader reader) throws IOException, XMLStreamException
	{
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				return;
			line = line.trim();
			if( line.startsWith("(layer") || line.startsWith("(tags") || line.startsWith("(attr") )
			{
				singleValueAttribute(line, 0);
			}
			else
			{
				out.writeCharacters("\r\n");
				// concatinate lines to get a an element with matching '('')'
				int open = count(line, '(');
				int close = count(line, ')');
				while(open != close)
				{
					String next = reader.readLine();
					if(next == null)
					{
						break;
					}
					line = line + next.trim();
					open = count(line, '(');
					close = count(line, ')');
				}
				String elName = elementName(line);
				if(elName == null)
					break;
				out.writeCharacters("\t");
				out.writeStartElement(elName);
				String eleFrag = line.substring(line.indexOf(' ')).trim();

				if(line.startsWith("(fp_text"))
				{
					processFpText(eleFrag);

				}
				else if(line.startsWith("(fp_line"))
				{
					processFpLine(eleFrag);
				}
				else if(line.startsWith("(fp_circle"))
				{
					processFpCircle(eleFrag);
				}
				else if(line.startsWith("(fp_arc"))
				{
					processFpArc(eleFrag);
				}
				else if(line.startsWith("(pad"))
				{
					processPad(eleFrag);
				}
				out.writeCharacters("\t");
				out.writeEndElement();
			}
		}
	}


	public void parse() throws IOException, XMLStreamException
	{
		System.out.println(this.getClass().getName()+" Parsing: "+fname+" to: "+outFname);

		try
		{
		OutputStream outputStream = new FileOutputStream(new File(outFname));

		out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, "utf-8"));

		out.writeStartDocument();
		out.writeCharacters("\r\n");
		out.writeStartElement("kicad_mod");
		out.writeCharacters("\r\n");

		BufferedReader reader = new BufferedReader(new FileReader(fname));
		String line = reader.readLine();
		while( line != null)
		{
			String tline = line.trim();
			if(tline.startsWith("(footprint"))
			{
				out.writeStartElement("footprint");
				int nstart = tline.indexOf("\"");
				nstart++;
				int nend = tline.indexOf("\"", nstart);
				String name = tline.substring(nstart, nend);
				out.writeAttribute("name", name.trim());
				nend = singleValueAttribute(tline, nend);
				nend++;
				tline = tline.substring(nend);
				singleValueAttribute(tline, 0);
				parseSexpression(reader);
				out.writeEndElement();
				out.writeCharacters("\r\n");
			}
			line = reader.readLine();
		}

		out.writeEndElement();
		out.writeCharacters("\r\n");
		out.writeEndDocument();
		out.writeCharacters("\r\n");
		}
		finally
		{
			out.close();
		}
	}


	public static void main (String[] args)
	{
		try
		{
			if(args.length<1)
			{
				System.out.println("This utility converts a Kicad footprint to an xml document that can be parsed with xslt tools");
				System.out.println("Usage: java KicadToXml pathname/source [path/dest]");
				System.exit(-1);
			}
			KicadToXml a1 = new KicadToXml(args);
			a1.parse();
		}
		catch (Exception cfe)
		{
			cfe.printStackTrace();
			System.err.println(cfe.getMessage());
			System.exit(1);
		}
	}
}

