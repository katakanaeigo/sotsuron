import java.util.HashMap;
import java.util.Map;

public class count_follower{
    //視野内で流行に乗っているエージェントをカウントするための関数
	public static Map<String, Integer> countFollower(int agentNumber, int fieldOfViewLevel, boolean[] previousFollowTheTrend){
		
		//何×何の格子にするか
		int grid = 31;

		//agentを31×31格子状に再配置
		boolean[][] gridAgent = new boolean[grid+1][grid+1];

		//対象のエージェントの座標をマーキング
		int thisAgentHeght = 0;
		int thisAgentwidth = 0;

		for(int k=1; k<=grid; k++){
			for(int l=1; l<=grid; l++){
				//エージェント番号を把握
				int m = (k-1)*grid+l;
				gridAgent[k][l] = previousFollowTheTrend[m];
				//System.out.println("縦座標："+k+"、横座標："+l+"に"+gridAgent[k][l]+"が存在（"+m+"番目のエージェント）");
				if(m == agentNumber){
					thisAgentHeght = k;
					thisAgentwidth = l;
					//System.out.println("自分は縦座標："+k+"、横座標："+l+"です。");
				}
			}
		}

		//存在しない座標について補正
		int top;
		int bottom;
		int left;
		int right;

		//一番上が1より小さい座標になってしまうと、存在しないので1に補正
		if((thisAgentHeght-fieldOfViewLevel)<1){
			top = 1;
		}else{
			top = thisAgentHeght-fieldOfViewLevel;
		}
		//一番下が31より大きい座標になってしまうと、存在しないので31に補正
		if((thisAgentHeght+fieldOfViewLevel)>grid){
			bottom = grid;
		}else{
			bottom = thisAgentHeght+fieldOfViewLevel;
		}
		//一番上が1より小さい座標になってしまうと、存在しないので1に補正
		if((thisAgentwidth-fieldOfViewLevel)<1){
			left = 1;
		}else{
			left = thisAgentwidth-fieldOfViewLevel;
		}
		//一番下が31より大きい座標になってしまうと、存在しないので31に補正
		if((thisAgentwidth+fieldOfViewLevel)>grid){
			right = grid;
		}else{
			right = thisAgentwidth+fieldOfViewLevel;
		}

		//視野内、かつ存在する座標内において、流行に乗っているエージェントをカウント
		int followerCount = 0;
		int notFollowerCount = 0;
		for(int k=top; k<=bottom; k++){
			for(int l=left; l<=right; l++){
				if(gridAgent[k][l]){
					followerCount ++;
				}else{
					notFollowerCount ++;
				}
			}
		}
		//System.out.println("合計数："+followerCount);

		Map<String, Integer> agentCount = new HashMap<>();

        // Mapに値を追加
        agentCount.put("follower", followerCount);
        agentCount.put("notFollower", notFollowerCount);

		return agentCount;
	}
}