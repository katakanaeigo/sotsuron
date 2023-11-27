import java.util.Random;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

class test {

	public static void main(String[] args) {
		
		//エージェントの数 961
		int agent = 961;

		//ステップ数 30
		int step = 30;

		//曲数 20
		int songs = 20;

		//流行に乗っているか否か
		boolean[][][] followTheTrend = new boolean[step+1][agent+1][songs+1];

		//視野レベル
		int[][] fieldOfViewLevel = new int[step+1][agent+1];

		//内的傾向値
		//平均
		double interestToTrendAve = 2;
		//標準偏差
		double interestToTrendSd = 0.5;
		double[][] interestToTrend = new double[agent+1][songs+1];

		//最初から流行に乗っている人数
		int innovatorTo = 50;
		int innovatorFrom = 5;

		//agentの各値を決定
		for(int k=1; k<=agent; k++){
			//一曲目はoverdoseとして値を特別に代入
			interestToTrend[k][1] = generateRandomGaussian(interestToTrendAve, interestToTrendSd);
			//最初は25%の人が乗っている　とする
			followTheTrend[0][k][1] = generateWithProbability(28);

            for(int l=2; l<=songs; l++){
                //流行への興味を曲それぞれに設定
			    interestToTrend[k][l] = generateRandomGaussian(interestToTrendAve, interestToTrendSd);

				//最初から流行に乗っているエージェント
				int percent = generateRandomNumber(innovatorFrom, innovatorTo);
				followTheTrend[0][k][l] = generateWithProbability(percent);
            }

			//初期の視野レベルを（1~10）ランダムに設定
			fieldOfViewLevel[0][k] = generateRandomNumber(1, 10);
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

		//kステップ目、m曲目の流行に乗っている人数
		int[][] countSongFollower = new int[step+1][songs+1];

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
					Map<String, Integer> agentCount = countFollower(l, fieldOfViewLevel[k][l], previousFollowTheTrend);
					// 値の取得
					int follower = agentCount.get("follower");
					int notFollower = agentCount.get("notFollower");

					//流行に乗るか判断 kステップ目、lさん、m曲目
					if((interestToTrend[l][m]*follower) > notFollower){
						followTheTrend[k][l][m] = true;
					}else{
						followTheTrend[k][l][m] = false;
					}
				}
			}
		}

		int[] countAllFollower=new int[step+1];

		//合計
		for(int k=0; k<=step; k++){
			for(int m=1; m<=songs; m++){
				for(int l=1; l<=agent; l++){
					if(followTheTrend[k][l][m]){
						countSongFollower[k][m]++;
						countAllFollower[k]++;
					}
				}
			}
		}

		// 出力ファイルのパス
		 LocalDateTime currentDateTime = LocalDateTime.now();

        // 年月日時分秒を含むファイル名
        String filePath = "output/output_" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "シェア率.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            //設定した値の記述
			writer.println("reduction_speed,"+reducationSpeed);
			writer.println("expantion_frequency,"+expantionFrequency);
			writer.println("expantion_stage,"+expantionStage);
			writer.println("interest_to_trend 平均値,"+interestToTrendAve);
			writer.println("interest_to_trend 標準偏差,"+interestToTrendSd);
			writer.println("最初から流行に乗る人数,"+innovatorFrom+",~,"+innovatorTo);
			writer.println();
			
            // ヘッダー行の書き込み
            writer.print("step, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15, m16, m17, m18, m19, m20");
			writer.println(); //改行

            for(int k=0; k<=step; k++){
                writer.print(k+",");
				for(int m=1; m<=songs; m++){
					writer.print((double)countSongFollower[k][m]/countAllFollower[k]+",");
				}
				writer.println();
			}

            System.out.println("CSVファイルが正常に出力されました。");
        } catch (IOException e) {
            System.err.println("CSVファイルの出力中にエラーが発生しました: " + e.getMessage());
        }

		// 年月日時分秒を含むファイル名
        String filePath2 = "output/output_" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "聴いてる人数.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath2))) {
            //設定した値の記述
			writer.println("reduction_speed,"+reducationSpeed);
			writer.println("expantion_frequency,"+expantionFrequency);
			writer.println("expantion_stage,"+expantionStage);
			writer.println("interest_to_trend 平均値,"+interestToTrendAve);
			writer.println("interest_to_trend 標準偏差,"+interestToTrendSd);
			writer.println("最初から流行に乗る人数,"+innovatorFrom+",~,"+innovatorTo);
			writer.println();
			
			// ヘッダー行の書き込み
            writer.print("step, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15, m16, m17, m18, m19, m20");
			writer.println(); //改行

            for(int k=0; k<=step; k++){
                writer.print(k+",");
				for(int m=1; m<=songs; m++){
					writer.print(countSongFollower[k][m]+",");
				}
				writer.println();
			}

            System.out.println("CSVファイルが正常に出力されました。");
        } catch (IOException e) {
            System.err.println("CSVファイルの出力中にエラーが発生しました: " + e.getMessage());
        }
	}

	//視野決定のための関数
	static public int fieldOfView(int expantionFrequency, int expantionStage, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
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

		Map<String, Integer> agentCount = new HashMap<>();

        // Mapに値を追加
        agentCount.put("follower", followerCount);
        agentCount.put("notFollower", notFollowerCount);

		return agentCount;
	}


    //ランダム系のメソッドまとめ
	//確率percentでtrueを返す
	public static boolean generateWithProbability(int percent) {
        Random random = new Random();
        int randomValue = random.nextInt(100); // 0から99までのランダムな整数を生成

        return randomValue < percent; // percentより小さい場合にtrueを返す
    }

	//平均a, 標準偏差b の正規分布に従うランダムな値を生成
	public static double generateRandomGaussian(double a, double b) {
		Random random = new Random();
        double randomNumber = random.nextGaussian() * b + a;
        return randomNumber;
    }

	//a以上b以下でランダムな値を生成する
	public static int generateRandomNumber(int a, int b){
		Random random = new Random();
        int randomNumber = random.nextInt(b-a+1) + a;
		return randomNumber;
	}
}