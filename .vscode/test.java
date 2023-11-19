import java.util.Random;
import java.util.Arrays;

class test {

	public static void main(String[] args) {
		
		//エージェントの数 961
		int agent = 961;

		//ステップ数 300
		int step = 300;

		//曲数 20
		int songs = 20;

		//流行に乗っているか否か
		boolean[][][] followTheTrend = new boolean[step+1][agent+1][songs+1];

		//視野レベル
		int[][] fieldOfViewLevel = new int[step+1][agent+1];

		//内的傾向値
		double[] interestToTrend = new double[agent+1];

		//agentの各値を決定
		for(int k=1; k<=agent; k++){
			interestToTrend[k] = 5;

			//初期の視野レベルを（1~10）ランダムに設定
			fieldOfViewLevel[0][k] = (int) (Math.random()*(3-1)) + 1;

			for(int l=1; l<=songs; l++){
				//最初は10%の人が流行に乗っている
				followTheTrend[0][k][l] = generateWithProbability(10);
			}
		}

		//視野拡大の頻度、拡大する確率が何パーセントか
		int expantionFrequency = 15;

		//視野拡大の時何段階拡大するか
		int expantionStage = 3;

		//視野縮小の速さ　同じ視野が何ステップ連続するか
		int reducationSpeed = 2;
		
		//同じ視野が連続しているステップ数　初期値は１
		int[] sameViewStep = new int[agent+1];
		for(int k=1; k<=agent; k++){
			sameViewStep[k] = 1;
		}

		for(int k=1; k<=step; k++){
			for(int l=1; l<=agent; l++){
				//視野の決定
				fieldOfViewLevel[k][l] = fieldOfView(expantionFrequency, expantionStage, reducationSpeed, sameViewStep[l], fieldOfViewLevel[k-1][l]);

				//視野の連続をカウント
				if(k==1){
					sameViewStep[l] = 1;
				}else if(fieldOfViewLevel[k][l] == fieldOfViewLevel[k-1][l]){
					sameViewStep[l] = sameViewStep[l]+1;
				}else{
					sameViewStep[l] = 1;
				}

				for(int m=1; m<=songs; m++){

					//1ステップ前の,m曲目のfollowTheTrendをコピーして渡す
					boolean[] previousFollowTheTrend = new boolean[agent+1];
					for(int i=1; i<=agent; i++){
						previousFollowTheTrend[i] = followTheTrend[k-1][i][m];
					}

					//視野レベルに応じた、流行に乗っている人のカウント
					int follower = countFollower(l, fieldOfViewLevel[k][l], previousFollowTheTrend);

					//視野内にいるエージェントの総数
					double agentInView = Math.pow(2*(double)fieldOfViewLevel[k][l]+1, 2);

					//流行に乗るか判断 kステップ目、lさん、m曲目
					if((interestToTrend[l]*follower) > (agentInView-follower)){
						followTheTrend[k][l][m] = true;
						//System.out.println(f[l]*follower+"は"+ (agentInView-follower)+"より大きいので流行に乗る！");
					}else{
						followTheTrend[k][l][m] = false;
						//System.out.println(f[l]*follower+"は"+ (agentInView-follower)+"より小さいので乗らない！");
					}
				}
			}

			int[] countSongFollower = new int[songs+1];
			for(int m=1; m<=songs; m++){
				countSongFollower[m]=0;
				for(int l=1; l<=agent; l++){
					if(followTheTrend[k][l][m]){
						countSongFollower[m]++;
					}
				}
			}
			int countAllFollower = Arrays.stream(countSongFollower).sum();
			System.out.println("全体："+countAllFollower);
            Arrays.sort(countSongFollower);
			for(int m=songs; m>19; m--){
				System.out.println(k+"ステップ目"+m+"位の曲は"+countSongFollower[m]);
				if(countAllFollower!=0) System.out.println(((double)countSongFollower[m]/countAllFollower)*100);
			}
		}
	}

	//視野決定のための関数
	static public int fieldOfView(int expantionFrequency, int expantionStage, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		Random random = new Random();
		boolean expantion = generateWithProbability(expantionFrequency);


		//視野レベル
		int level;

		if(expantion){
			level = previousLevel + expantionStage;
		}else if(sameViewStep >= reducationSpeed){
			level = previousLevel-1;
		}else{
			level = previousLevel;
		}

		if(level <= 0){
			return 1;
		}else if(level > 10){
			return 10;
		}else{
			return level;
		}
	}

    //視野内で流行に乗っているエージェントをカウントするための関数
	public static int countFollower(int agentNumber, int fieldOfViewLevel, boolean[] previousFollowTheTrend){
		
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
				int m = (k-1)*3+l;
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

		// System.out.println("視野レベル："+fieldOfViewLevel);
		// System.out.println("上："+top+"下："+bottom+"左："+left+"右："+right);

		//視野内、かつ存在する座標内において、流行に乗っているエージェントをカウント
		int follower = 0;
		for(int k=top; k<=bottom; k++){
			for(int l=left; l<=right; l++){
				if(gridAgent[k][l]){
					follower ++;
				}
			}
		}
		//System.out.println("合計数："+follower);

		return follower;
	}

	//一定の確率でtrueを返す、視野拡大と初期採用者の初期設定に使用
	public static boolean generateWithProbability(int percent) {
        Random random = new Random();
        int randomValue = random.nextInt(100); // 0から99までのランダムな整数を生成

        return randomValue < percent; // percentより小さい場合にtrueを返す
    }
}