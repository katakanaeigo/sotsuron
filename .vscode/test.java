import java.util.Random;

class test {
	public static void main(String[] args) {
		
		//エージェントの数
		int agent = 961;

		//ステップ数
		int step = 100;

		//流行に乗っているか否か
		boolean[][] followTheTrend = new boolean[step+1][agent+1];

		//視野レベル
		int[][] fieldOfViewLevel = new int[step+1][agent+1];

		//内的傾向値
		double[] f = new double[agent+1];

		//agentの各値を決定
		for(int k=1; k<=agent; k++){
			f[k] = 2;
			fieldOfViewLevel[0][k] = (int) (Math.random()*(10-1)) + 1;

			//最初は10%の人が流行に乗っている
			followTheTrend[0][k] = generateWithProbability(10);
		}

		//視野拡大の頻度、拡大する確率が何パーセントか
		int expantionFrequency = 15;

		//視野縮小の速さ　同じ視野が何ステップ連続するか
		int reducationSpeed = 3;
		
		//同じ視野が連続しているステップ数　初期値は１
		int[] sameViewStep = new int[agent+1];
		for(int k=1; k<=agent; k++){
			sameViewStep[k] = 1;
		}

		for(int k=1; k<=step; k++){
			for(int l=agent; l<=1; l++){

				//視野の決定
				fieldOfViewLevel[k][l] = fieldOfView(expantionFrequency, reducationSpeed, sameViewStep[l], fieldOfViewLevel[k-1][l]);
				if(fieldOfViewLevel[k][l] < 1){
					fieldOfViewLevel[k][l] = 0;
				}

				//視野の連続をカウント
				if(k==1){
					sameViewStep[l] = 1;
				}else if(fieldOfViewLevel[k][l] == fieldOfViewLevel[k-1][l]){
					sameViewStep[l] = sameViewStep[l]+1;
				}else{
					sameViewStep[l] = 1;
				}
				System.out.println(k+"ステップ目の"+l+"人目のエージェントの視野は"+fieldOfViewLevel[k][l]);

				//1ステップ前のfollowTheTrendをコピーして渡す
				boolean[] previousFollowTheTrend = new boolean[agent+1];
				for(int i=1; i<=agent; i++){
					previousFollowTheTrend[i] = followTheTrend[k-1][i];
				}

				//視野レベルに応じた、流行に乗っている人のカウント
				int follower = countFollower(l, fieldOfViewLevel[k][l], previousFollowTheTrend);

				//視野内にいるエージェントの総数
				double agentInView = Math.pow(2*(double)fieldOfViewLevel[k][l]-1, 2);
				//流行に乗るか判断
				if((f[l]*follower) > (agentInView-follower)){
					followTheTrend[k][l] = true;
					System.out.println(f[l]*follower+"は"+ (agentInView-follower)+"より大きいので流行に乗る！");
				}else{
					followTheTrend[k][l] = false;
					System.out.println(f[l]*follower+"は"+ (agentInView-follower)+"より小さいので乗らない！");
				}

			}
		}
	}

	//視野決定のための関数
	static public int fieldOfView(int expantionFrequency, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		Random random = new Random();
		boolean expantion = generateWithProbability(expantionFrequency);

		if(expantion){
			System.out.println("かくだい！！！");
			return 10;
		}else if(sameViewStep > reducationSpeed){
			System.out.println("縮小！！");
			return previousLevel-1;
		}else{
			System.out.println("変化なし！");
			return previousLevel;
		}
	}

    //視野内で流行に乗っているエージェントをカウントするための関数
	public static int countFollower(int agentNumber, int fieldOfViewLevel, boolean[] previousFollowTheTrend){
		
		//agentを31×31格子状に再配置
		boolean[][] gridAgent = new boolean[32][32];

		//対象のエージェントの座標をマーキング
		int thisAgentHeght = 0;
		int thisAgentwidth = 0;

		for(int k=1; k<=31; k++){
			for(int l=1; l<=31; l++){
				for(int m=1; m<=961; m++){
					gridAgent[k][l] = previousFollowTheTrend[m];
					if(m == agentNumber){
						thisAgentHeght = k;
						thisAgentwidth = l;
					}
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
		if((thisAgentHeght+fieldOfViewLevel)>31){
			bottom = 31;
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
		if((thisAgentwidth+fieldOfViewLevel)>31){
			right = 31;
		}else{
			right = thisAgentwidth+fieldOfViewLevel;
		}

		//視野内、かつ存在する座標内において、流行に乗っているエージェントをカウント
		int follower = 0;
		for(int k=top; k<=bottom; k++){
			for(int l=left; l<=rigt; l++){
				if(gridAgent[k][l]){
					follower ++;
				}
			}
		}

		return follower;
	}

	//一定の確率でtrueを返す、視野拡大と初期採用者の初期設定に使用
	public static boolean generateWithProbability(int percent) {
        Random random = new Random();
        int randomValue = random.nextInt(100); // 0から99までのランダムな整数を生成

        return randomValue < percent; // percentより小さい場合にtrueを返す
    }
}