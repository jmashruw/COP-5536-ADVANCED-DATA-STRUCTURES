import java.io.*;

public class Vertex{

	Vertex child, parent, back, front;
	int freq;
	boolean lostChild = false;
	int degree = 0;
    String hashTag;

	public Vertex(String hashTag, int freq){
		this.freq = freq;
        this.hashTag = hashTag;
        this.degree = 0;
        this.lostChild = false;
        this.back = this;
		this.front = this;
		this.parent = null;
		this.child = null;
	}

}
