
public class Player implements Comparable<Object> {

	public boolean won;//1 indicates a win and 0 indicates a loose
	public String playerName;
	public int playerFinalLevel;
	public int playerLivesLeft;

	public Player(String name, boolean winOrLoose, int finalLevel, int endLives) {
		playerName = name;
		won = winOrLoose;
		playerFinalLevel = finalLevel;
		playerLivesLeft = endLives;
	}

	public int compareTo(Object o) {

		if(o instanceof Player){

			Player player = (Player)o;

			if(this.won && !player.won){
				return -1; 
			}
			else if(!this.won && player.won){
				return 1; 
			}
			else if(this.playerFinalLevel>player.playerFinalLevel){
				return -1; 
			}
			else if(this.playerFinalLevel<player.playerFinalLevel){
				return 1;
			}
			else if(this.playerLivesLeft<player.playerLivesLeft){
				return 1; 
			}
			else if(this.playerLivesLeft>player.playerLivesLeft){
				return -1;
			}
			else return ((this.playerName).compareTo(player.playerName));
		}
		else return 0;
	}

	public String toString(){
		if(won)		
		return playerName +"\t"+"Won"+"\t"+playerFinalLevel+"\t"+playerLivesLeft;
		else return playerName +"\t"+"Lost"+"\t"+playerFinalLevel+"\t"+playerLivesLeft;
	}

}
