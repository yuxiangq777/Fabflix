

public class Actor {

	private String name;
	private int Year;
	public Actor(){
		name="";
		Year=0;
	}
	
	public Actor(String name, int Year) {
		this.name = name;
		this.Year= Year;
	}


	public int getYear() {
		return Year;
	}

	public void setYear(int Year) {
		this.Year = Year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Actor Details - ");
		sb.append("Name:" + getName());
		sb.append(", birthYear:"+getYear());
		return sb.toString();
	}
}
