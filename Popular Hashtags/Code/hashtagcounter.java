import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class hashtagcounter{
	
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();	
		String sCurrentLine;
        String fileName = args[0];
		
		Map<String, Vertex> hm = new HashMap<String, Vertex>();
		
		FibonacciHeap fib = new FibonacciHeap();
		
		String myHashTagPattern = "((#)([a-z]+)(\\s)(\\d+))"; // pattern for strings of type "#saturday 4"
		String mykeyPattern = "(\\d+)";                       // pattern for strings of type "4"
			
		Pattern p1 = Pattern.compile(myHashTagPattern);
		Pattern p2 = Pattern.compile(mykeyPattern); 

		PrintWriter writer = null;
		
		try{
			File f = new File(fileName);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			writer = new PrintWriter("output_file.txt", "UTF-8");

			while (((sCurrentLine = br.readLine()).compareToIgnoreCase("stop"))!=0)
			{
				Matcher m1 = p1.matcher(sCurrentLine);
				Matcher m2 = p2.matcher(sCurrentLine);
				
				if((m1.find())) {                   /* if matcher finds a hit for pattern for strings of type "#saturday 4"*/
					String value = m1.group(3);
					int key = Integer.parseInt(m1.group(5));

					if(!(hm.containsKey(value)))        /* determine if it's not already added to tree and if doesnt exist then add it to heap */
                    {
						Vertex n1 = new Vertex(value,key);
						fib.insert(n1); 
						hm.put(value, n1);
					}
					else{                            /* determine if it's already added to tree then increase its key */
						Vertex n = hm.get(value);
						fib.increaseKey(n,key);
					}
				}
				else if(!m1.find() && m2.find()){    /* if matcher finds a hit for pattern for strings of type "4"*/
					int count = Integer.parseInt(m2.group(1));

					Queue<Vertex> q= new LinkedList<Vertex>();

					for(int i = 0; i < count; i++)      //for the specified count call removeMax
					{
						Vertex maximum = fib.removeMax();
						hm.remove(maximum.hashTag);
						Vertex n1 = new Vertex(maximum.hashTag, maximum.freq);
						q.add(n1);                      //store this vertex in queue, so as to insert back in tree in that order
						if(i==count-1) 
						{
                            writer.print(n1.hashTag);
						}
						else {
                            writer.print(n1.hashTag+", ");
						}
					}
					
					writer.println();

					while(q.peek()!=null){          //insert back in tree by extracting vertices stored in queue
						Vertex n = q.poll();
						fib.insert(n);
						hm.put(n.hashTag,n);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(writer!=null){
				try{
					writer.close();
				}
				catch(Exception i){
					i.printStackTrace();
				}
			}
		}

	  long stopTime = System.currentTimeMillis();
      //System.out.println(stopTime - startTime);
	}
}

