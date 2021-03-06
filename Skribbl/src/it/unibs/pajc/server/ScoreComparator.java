package it.unibs.pajc.server;

import java.util.Comparator;
import java.util.Map;

public class ScoreComparator implements Comparator<Player>{


	@Override
	public int compare(Player p1, Player p2) {
		if(p1.getScore() > p2.getScore())
			return 1;
		else 
			if(p1.getScore() == p2.getScore() && p1.getName().equals(p2.getName()))
				return 0;
			else
				return -1;
	}


}
