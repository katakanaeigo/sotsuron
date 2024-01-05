import java.util.Random;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class main_listener_ver1 {

	public static void main(String[] args) {

		// 出力ファイルのパス
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		//エージェントの数 961
		int agent = 961;

		//ステップ数 30
		int step = 30;

		int sikou = 30;

		//流行に乗っているか否か
		boolean[][] followTheTrend = new boolean[step+1][agent+1];

        Scanner scan = new Scanner(System.in);

		//kステップ目、m曲目の流行に乗っている人数
		int[][] countSongFollower = new int[sikou+1][step+1];

		//内的傾向値
		//平均
		double interestToTrendAve = 2.1;

		//標準偏差
		double interestToTrendSd = 0.35;
		double[] interestToTrend = new double[agent+1];

		//最初から曲を聴いている（＝innovator）人数
		int thisSongInnovator = 47;

		//最初から曲を聴いている（=innnovator）にフラグを付ける
		boolean[] innovatorFlag = new boolean[agent+1];
		
        //視野レベル
		int[][] fieldOfViewLevel = new int[step+1][agent+1];

		//視野拡大の頻度、拡大する確率が何パーセントか
        System.out.println("expantion_frequency 視野の拡大が起こる頻度　確率(%)  int型");
		int expantionFrequency = scan.nextInt();

		//視野拡大の時何段階拡大するか
		int expantionType = 1;

        int expantionStage = 3;

		//視野縮小の速さ　同じ視野が何ステップ連続するか
        System.out.println("reduction_speed 視野の縮小が起こる速さ  int型");
		int reducationSpeed = scan.nextInt();

		for(int x=1; x<=sikou; x++){
			System.out.println(x+"試行目");

			//agentの各値を決定
			for(int k=1; k<=agent; k++){
				//流行への興味　正規分布
				interestToTrend[k] = random_tool.generateRandomGaussian(interestToTrendAve, interestToTrendSd);

				//初期の視野レベルを（1~10）ランダムに設定
				fieldOfViewLevel[0][k] = 1;
			}

			//最初から流行に乗っている初期値設定
			boolean[] innovator = new boolean[agent];
			for(int k=0; k<thisSongInnovator; k++){
				innovator[k] = true;
			}
			innovator = random_tool.shuffleArray(innovator);

			for(int k=1; k<=agent; k++){
				followTheTrend[0][k] = innovator[k-1];
				if(followTheTrend[0][k]) innovatorFlag[k] = true;
				else innovatorFlag[k] = false;
			}

			//同じ視野が連続しているステップ数　初期値は１
			int[] sameViewStep = new int[agent+1];
			for(int k=1; k<=agent; k++){
				sameViewStep[k] = 1;
			}

			for(int k=1; k<=step; k++){
				for(int l=1; l<=agent; l++){

					//視野の決定
					fieldOfViewLevel[k][l] = field_of_view.fieldOfView(expantionFrequency, expantionType, expantionStage, reducationSpeed, sameViewStep[l], fieldOfViewLevel[k-1][l]);
					
					//視野の連続をカウント
					if(k==1){
						sameViewStep[l] = 1;
					}else if(fieldOfViewLevel[k][l] == fieldOfViewLevel[k-1][l]){
						sameViewStep[l] = sameViewStep[l]+1;
					}else{
						sameViewStep[l] = 1;
					}

					//1ステップ前の,m曲目のfollowTheTrendをコピーして渡す
					boolean[] previousFollowTheTrend = new boolean[agent+1];
					for(int i=1; i<=agent; i++){
						previousFollowTheTrend[i] = followTheTrend[k-1][i];
					}

					//視野レベルに応じた、流行に乗っている人のカウント
					Map<String, Integer> agentCount = count_follower.countFollower(l, fieldOfViewLevel[k][l], previousFollowTheTrend);
					// 値の取得
					int follower = agentCount.get("follower");
					int notFollower = agentCount.get("notFollower");

					//流行に乗るか判断 kステップ目、lさん、m曲目
					if((interestToTrend[l]*follower) > notFollower){
						followTheTrend[k][l] = true;
					}else{
						followTheTrend[k][l] = false;
					}

					//innovatorは曲を聴かない判断をしない
					if(innovatorFlag[l]) followTheTrend[k][l] = true;
				}
			}

			//合計
			for(int k=0; k<=step; k++){
				for(int l=1; l<=agent; l++){
					if(followTheTrend[k][l]){
						countSongFollower[x][k]++;
					}
				}
			}

			// 年月日時分秒を含むファイル名
        	String filePath2 = "output/4/output_" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + x + "試行目のcell出力.csv";
			try (PrintWriter writer = new PrintWriter(new FileWriter(filePath2))) {
				for(int k=0; k<=step; k++){
					writer.println(k+"step");
					for(int l=1; l<=agent; l++){
						if(followTheTrend[k][l]) writer.print("●,");
						else writer.print("〇,");
						if(l%31==0) writer.println();
					}
					writer.println();
				}
			}catch (IOException e) {
            System.err.println("CSVファイルの出力中にエラーが発生しました: " + e.getMessage());
        	}
		}

		// 年月日時分秒を含むファイル名
        String filePath = "output/3/output_" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "聴いてる人数.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            //設定した値の記述
			writer.println("interest_to_trend 平均値,"+interestToTrendAve);
			writer.println("interest_to_trend 標準偏差,"+interestToTrendSd);
            writer.println("最初から流行に乗る人数,"+thisSongInnovator+",人");
			writer.println("reduction_speed,"+reducationSpeed+",step");
			writer.println("expantion_frequency,"+expantionFrequency+",%");
			if(expantionType==1) writer.println("expantion_type,固定段階拡大する,");
			else if(expantionType==2) writer.println("expantion_type,ランダムな段階拡大する,");
			else if(expantionType==3) writer.println("expantion_type,常に同じ視野まで拡大する,");
			writer.println("expantion_stage,"+expantionStage+",段階");
			writer.println();
			
			// ヘッダー行の書き込み
            writer.print("step, ");
			for(int x=1; x<=100; x++){
				writer.print(x+",");
			}
			writer.println(); //改行

			for(int k=0; k<=step; k++){
				writer.print(k+",");
				for(int x=1; x<=sikou; x++){
					writer.print((double)countSongFollower[x][k]/961+",");
				}
				writer.println();
			}

            System.out.println("CSVファイルが正常に出力されました。");
        } catch (IOException e) {
            System.err.println("CSVファイルの出力中にエラーが発生しました: " + e.getMessage());
        }
	}
}