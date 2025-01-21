
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
//import java.util.Set;
import java.lang.StringBuilder;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.*;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.io.BufferedReader;
import java.io.FileReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class KicadToXml
{
	static private int verbose = 0;
	XMLStreamWriter out;
	public String fname;

	public KicadToXml(String[] args)
	{
		fname = args[0];
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
		System.out.println("AT frag <"+frag+">");
		StringTokenizer st = new StringTokenizer(frag, " ");
		out.writeCharacters("\t\t");

		out.writeStartElement("at");
			out.writeStartElement("x");
				out.writeCharacters(st.nextToken());
			out.writeEndElement();
			out.writeStartElement("y");
				out.writeCharacters(st.nextToken());
			out.writeEndElement();
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
//		System.out.println("AT frag <"+frag+">");
		StringTokenizer st = new StringTokenizer(frag, " ");
//		out.writeCharacters("\t\t");

		out.writeStartElement(id);
			String temp  = st.nextToken();
			temp = stripChar(temp, "\"");
				out.writeCharacters(temp);
		out.writeEndElement();
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
//		System.out.println("AT frag <"+frag+">");
		StringTokenizer st = new StringTokenizer(frag, " ");
//		out.writeCharacters("\t\t");

		out.writeStartElement(id);
			out.writeStartElement(first);
				out.writeCharacters(st.nextToken());
			out.writeEndElement();
			out.writeStartElement(second);
				out.writeCharacters(st.nextToken());
			out.writeEndElement();
			while(st.hasMoreTokens())
			{
				out.writeEmptyElement(st.nextToken());
			}
		out.writeEndElement();
	}


	public void processFont(String frag) throws XMLStreamException
	{
		int index = frag.indexOf("(font");
		if( index<0 )
			return;
		index = frag.indexOf(" ", index+1);
		if(index<0)
			return;
//		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1).trim();
//		System.out.println("font frag <"+frag+">");
//		out.writeCharacters("");
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
//		int eindex = frag.indexOf(")", index+1);
		frag = frag.substring(index+1).trim();
//		System.out.println("effects frag <"+frag+">");
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
//		System.out.println("Layer frag <"+frag+">");
		frag = stripChar(frag, "\"");
//		System.out.println("strip frag <"+frag+">");
		out.writeCharacters("\t\t");
		out.writeStartElement("layer");
		out.writeCharacters(stripChar(frag, "\""));
		out.writeEndElement();
		out.writeCharacters("\r\n");
	}


	public void processFpText(String frag) throws XMLStreamException
	{
		int ename = frag.indexOf(" ");
		out.writeCharacters("\r\n\t\t");
		String elName = frag.substring(0, ename).trim();
	//	System.out.println("<"+elName+">");
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


	public void processFpPad(String frag) throws XMLStreamException
	{
		out.writeCharacters("\r\n\t\t");
			processDouble(frag, "at", "x", "y");
			processDouble(frag, "size", "w", "h");
			processDouble(frag, "end", "x", "y");
			processSingle(frag, "layer");
			processSingle(frag, "width");
			processSingle(frag, "fill");
		out.writeCharacters("\r\n");
	}


	public int singleValueElement(String tline, int startIndex) throws XMLStreamException
	{
		int nstart = tline.indexOf("(", startIndex);
		nstart++;
		int nend = tline.indexOf(")", nstart);
		String token = tline.substring(nstart, nend);
		int spnum = token.indexOf(" ");
		String name = token.substring(0, spnum);
		out.writeCharacters("\t");
		out.writeStartElement(name);
		String val = token.substring(spnum);
		val = val.trim();
		if(val.startsWith("\""))
		{
			val = val.substring(1);
			val = val.substring(0, val.indexOf("\""));
		}
		val = val.trim();
		out.writeCharacters(val);
		out.writeEndElement();
		out.writeCharacters("\r\n");
		return(nend);
	}


	public String elementName(String str)
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


	public int count(String str, int ch)
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
//		int depth = 0
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				return;
			line = line.trim();
//			if( line.startsWith(")" && (depth == 0))
//				return;
			if( line.startsWith("(layer") || line.startsWith("(tags") || line.startsWith("(attr") )
			{
				singleValueElement(line, 0);
			}
			else
			{
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
//				System.out.println("<"+line+">");
//				System.out.println("open: "+open+" close: "+close);
				String elName = elementName(line);
				if(elName == null)
					break;
				System.out.println("<"+elName+">");
				out.writeCharacters("\t");
				out.writeStartElement(elName);
				String eleFrag = line.substring(line.indexOf(' ')).trim();

				System.out.println("<"+elName+"> frag:"+eleFrag+":");

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
				else if(line.startsWith("(fp_pad"))
				{
					processFpPad(eleFrag);
				}
				out.writeCharacters("\t");
				out.writeEndElement();
				out.writeCharacters("\r\n");
			}
		}
	}


	public void parse() throws SAXException, ParserConfigurationException, IOException, XMLStreamException
	{
		System.out.println(this.getClass().getName()+" Reading config from "+fname);

		OutputStream outputStream = new FileOutputStream(new File("out.xml"));

		out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, "utf-8"));

		out.writeStartDocument();
		out.writeCharacters("\r\n");
		out.writeStartElement("kicad_mod");
		out.writeCharacters("\r\n");

		BufferedReader reader = new BufferedReader(new FileReader(fname));
		String line = reader.readLine();
		while( line != null)
		{
			System.out.println(line);
			String tline = line.trim();
			if(tline.startsWith("(footprint"))
			{
				out.writeStartElement("footprint");
				int nstart = tline.indexOf("\"");
				nstart++;
				int nend = tline.indexOf("\"", nstart);
				String name = tline.substring(nstart, nend);
				out.writeCharacters("\r\n\t");
				out.writeStartElement("name");
				out.writeCharacters(name.trim());
				out.writeEndElement();
				out.writeCharacters("\r\n");

				nend = singleValueElement(tline, nend);
				nend++;
				tline = tline.substring(nend);
				singleValueElement(tline, 0);
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
		out.close();
	}

	public static void main (String[] args)
	{
		try
		{
			System.out.println("Hello Mike");
			if(args.length<1)
			{
				System.out.println("Usage: java KicadToXml pathname");
				System.exit(-1);
			}
			System.out.println("Parsing: "+args[0]);
			KicadToXml a1 = new KicadToXml(args);
			System.out.println("parsing");
			a1.parse();
		}
		catch (Exception cfe)
		{
//			System.out.println(Thread.currentThread().getStackTrace());
			int size = cfe.getStackTrace().length - 1;
			System.out.println("   Penultimate cause: method=" + cfe.getStackTrace()[size-1].getMethodName() + " class=" + cfe.getStackTrace()[size-1].getClassName() +
		    " line=" + cfe.getStackTrace()[0].getLineNumber());
			System.err.println(cfe.getMessage());
			System.exit(1);
		}
	}
}

