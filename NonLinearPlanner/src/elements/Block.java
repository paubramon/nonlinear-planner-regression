package elements;

public class Block {
	public String name;
	public int weight;
	
	public Block(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}
	
	public void printBlock() {
		System.out.println(String.format("Block %s\nweight=%d",name,weight));
	}
}
