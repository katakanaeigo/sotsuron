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

class main {

	public static void main(String[] args) {
		
		//エージェントの数 961
		int agent = 961;

		//ステップ数 30
		int step = 30;

		//曲数 20
		int songs = 20;

		//流行に乗っているか否か
		boolean[][][] followTheTrend = new boolean[step+1][agent+1][songs+1];

        Scanner scan = new Scanner(System.in);

		//内的傾向値
		//平均
        System.out.println("interest_to_trend（流行にどれだけ乗りたいか） 平均値 double型");
		double interestToTrendAve = scan.nextDouble();
		//標準偏差
        System.out.println("interest_to_trend（流行にどれだけ乗りたいか） 標準偏差 double型");
		double interestToTrendSd = scan.nextDouble();
		double[] interestToTrend = new double[agent+1];

		//最初から流行に乗っている人数
        System.out.println("inovator（最初から曲を聴いている人数）の割合(%)　最低値  int型");
		int innovatorFrom = scan.nextInt();
        System.out.println("inovator（最初から曲を聴いている人数）の割合(%)　最高値  int型");
		int innovatorTo = scan.nextInt();

        System.out.println("overdose の最初から聞いている人の割合(%) int型");
        int thisSongInnovator = scan.nextInt();

        //視野レベル
		int[][] fieldOfViewLevel = new int[step+1][agent+1];

		//agentの各値を決定
		for(int k=1; k<=agent; k++){
			//流行への興味　正規分布
			interestToTrend[k] = random_tool.generateRandomGaussian(interestToTrendAve, interestToTrendSd);

			//初期の視野レベルを（1~10）ランダムに設定
			fieldOfViewLevel[0][k] = random_tool.generateRandomNumber(1, 10);

			//overdoseは、最初は28%の人が乗っている　とする
			followTheTrend[0][k][1] = random_tool.generateWithProbability(thisSongInnovator);

			for(int l=2; l<=songs; l++){
				//最初は inovatorFrom~To %の人が流行に乗っている
                //曲ごとにランダムに異なる
				int percent = random_tool.generateRandomNumber(innovatorFrom, innovatorTo);
				followTheTrend[0][k][l] = random_tool.generateWithProbability(percent);
			}
		}

		//視野拡大の頻度、拡大する確率が何パーセントか
        System.out.println("expantion_frequency 視野の拡大が起こる頻度　確率(%)  int型");
		int expantionFrequency = scan.nextInt();

		//視野拡大の時何段階拡大するか
        System.out.println("expantion_stage 視野の拡大が起こるとき何段階拡大するか int型");
		int expantionStage = scan.nextInt();

		//視野縮小の速さ　同じ視野が何ステップ連続するか
         System.out.println("reduction_speed 視野の縮小が起こる速さ  int型");
		int reducationSpeed = scan.nextInt();
		
		//同じ視野が連続しているステップ数　初期値は１
		int[] sameViewStep = new int[agent+1];
		for(int k=1; k<=agent; k++){
			sameViewStep[k] = 1;
		}

		for(int k=1; k<=step; k++){
			for(int l=1; l<=agent; l++){
				//視野の決定
				fieldOfViewLevel[k][l] = field_of_view.fieldOfView(expantionFrequency, expantionStage, reducationSpeed, sameViewStep[l], fieldOfViewLevel[k-1][l]);
				
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
					Map<String, Integer> agentCount = count_follower.countFollower(l, fieldOfViewLevel[k][l], previousFollowTheTrend);
					// 値の取得
					int follower = agentCount.get("follower");
					int notFollower = agentCount.get("notFollower");

					//流行に乗るか判断 kステップ目、lさん、m曲目
					if((interestToTrend[l]*follower) > notFollower){
						followTheTrend[k][l][m] = true;
					}else{
						followTheTrend[k][l][m] = false;
					}
				}
			}
		}

        //kステップ目、m曲目の流行に乗っている人数
		int[][] countSongFollower = new int[step+1][songs+1];
        //kステップ目、全ての曲の聴いている人の合計
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
            writer.println("interest_to_trend 平均値,"+interestToTrendAve);
			writer.println("interest_to_trend 標準偏差,"+interestToTrendSd);
			writer.println("最初から流行に乗る人数,"+innovatorFrom+",~,"+innovatorTo+",%");
            writer.println("注目する曲の最初から流行に乗る人数,"+thisSongInnovator+",%");
			writer.println("reduction_speed,"+reducationSpeed+",step");
			writer.println("expantion_frequency,"+expantionFrequency+",%");
			writer.println("expantion_stage,"+expantionStage+",段階");
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
			writer.println("interest_to_trend 平均値,"+interestToTrendAve);
			writer.println("interest_to_trend 標準偏差,"+interestToTrendSd);
			writer.println("最初から流行に乗る人数,"+innovatorFrom+",~,"+innovatorTo+",%");
            writer.println("注目する曲の最初から流行に乗る人数,"+thisSongInnovator+",%");
			writer.println("reduction_speed,"+reducationSpeed+",step");
			writer.println("expantion_frequency,"+expantionFrequency+",%");
			writer.println("expantion_stage,"+expantionStage+",段階");
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
}