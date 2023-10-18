

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import java.util.ArrayList;

/**
 * ExcelLogWriterクラスで定義された基本操作を利用して，実際の作業を実装するためのクラス．
 *
 * @author miyazaki
 * @version 2008/12/20
 *
 */
public class ExcelLogWriterAdapter extends ExcelLogWriter {
	
	//最終的に出力するエクセルのファイルとなるWritableSheetのインスタンスを生成
	private WritableWorkbook wb[] = new WritableWorkbook[10] ;

	//出力先のエクセルのシート数の数だけWritableSheetのインスタンス生成
	private WritableSheet ws[][] = new WritableSheet[10][100];
	
	Memory memory;
	String date = Lead.date;

	/**
	 * ExcelLogWriterAdapterのコンストラクタ／普通に使用するよう
	 * ファイル名をつけたり、シートを生成する
	 */
	public ExcelLogWriterAdapter(/*ConsPopulation conspopulation*/) {
		memory = new Memory();
	}
	
	/*ブックを作る*/
	public void createBook(String filename, int booknumber){
		wb[booknumber] = generateBook (filename);
	}

	/*シートを作る*/
	public void createSheet(String sheetname,  int booknumber, int numOfSheet){
		ws[booknumber][numOfSheet] = generateSheet(wb[booknumber], sheetname, numOfSheet);
	}
	
	
	public void writeConsum1(int testNumber, int senario, int calibration, int bookNum){
		//最終結果（初週売り上げ）をエクセルへ表示するメソッド（メインの結果・キャリブレーションのターゲット）

		if(calibration == 0 && senario == 0){//各シミュレーションの初回にbookの作成
			createBook(date+"_購買人数記録用",bookNum);
			for(int i = 0; i < Lead.CALIBRATION_NUMBER; i++){
				createSheet("パターン" + i, bookNum, i);
			}
		}
		
		ArrayList<Integer> hairetu1 = memory.getKoubaiLog1();
			
		System.out.println();
		System.out.println((calibration + 1) + "組目の変数/シナリオ" + senario + "/" + testNumber + "試行目の記入");
		//動作確認用
			
		this.setIntLabels(hairetu1, ws[bookNum][calibration], 1, 1 + senario);
		
		if(calibration == Lead.CALIBRATION_NUMBER - 1 && senario == Lead.SENARIO_NUMBER - 1) {
			writeBook(wb[bookNum]);
			closeBook(wb[bookNum]);
		}

	}
		
	
	public void writeConsum2(int test, int senario, int bookNum){
		//各期の口コミ終了段階で何人が購買閾値を超えているかを出力するメソッド
		
		if(senario < 1 && test < 1){//各シミュレーションの初回にbookを作成
			createBook(date + "_閾値超越数記録用" ,bookNum);
			for(int i = 0; i < Lead.SENARIO_NUMBER; i++){
				createSheet("シナリオ" + i ,bookNum ,i);
			}
		}

		ArrayList<Integer> hairetu2 = memory.getKoubaiLog2();
			
		System.out.println();
		System.out.println("シナリオ" + senario + "/" +(test + 1) + "試行目の記録");
		//動作確認用
			
		this.setIntLabels(hairetu2, ws[bookNum][senario], 1, 1 + test);
		
		if(senario == Lead.SENARIO_NUMBER - 1 && test == Lead.TEST_NUMBER - 1) {
			writeBook(wb[bookNum]);
			closeBook(wb[bookNum]);
		}
	}

	public void writeConsParameta(int test, int bookNum){
		//各試行終了時の各消費者の内部モデルを出力するメソッド
		
		if(test < 1){//各シミュレーションの初回にbookを作成
			createBook(date + "_消費者パラメータ_シナリオ" + (bookNum - 2) ,bookNum);
			for(int i = 0; i < Lead.TEST_NUMBER; i++){
				createSheet((i + 1)+ "試行目" ,bookNum ,i);
			}
		}
		
		String[][] header3 = new String [1][27];
		header3[0][0]  = "購買期数";
		header3[0][1]  = "所持_認知";
		header3[0][2]  = "所持_詳細";
		header3[0][3]  = "所持_勧誘";
		header3[0][4]  = "所持_作品";
		header3[0][5]  = "効用_詳細";
		header3[0][6]  = "効用_前作";
		header3[0][7]  = "効用_放縦";
		header3[0][8]  = "詳細効用_ジャンル";
		header3[0][9]  = "詳細効用_スタジオ";
		header3[0][10] = "ウェイト_認知";
		header3[0][11] = "ウェイト_詳細";
		header3[0][12] = "ウェイト_勧誘";
		header3[0][13] = "ウェイト_作品";
		header3[0][14] = "ウェイト_前作";
		header3[0][15] = "ウェイト_放縦";
		header3[0][16] = "詳細ウェイト_ジャンル";
		header3[0][17] = "詳細ウェイト_スタジオ";
		header3[0][18] = "詳細ウェイト_外国";
		header3[0][19] = "情報探索確率";
		header3[0][20] = "口コミ確率";
		header3[0][21] = "印象更新確率";
		header3[0][22] = "勧誘確率";
		header3[0][23] = "印象更新可能性数";
		header3[0][24] = "印象更新回数";
		header3[0][25] = "最終印象更新期";
		header3[0][26] = "参考エージェント";
		
		setMatrix(header3,ws[bookNum][test],0,0);
				
		System.out.println();
		System.out.println((test + 1) + "試行目の記録");
		//動作確認用
			
		for(int i = 0; i < Lead.numberOfConsumer ; i++){
			this.setDoubleLabels(memory.getconsumerLog(i) ,ws[bookNum][test], 1 + i, 0);
		}
		
		if(test == Lead.TEST_NUMBER - 1) {
			writeBook(wb[bookNum]);
			closeBook(wb[bookNum]);
		}
	}
//		createBook("購買記録用",bookNum);
//		createSheet("購買した消費者を記録するシート",bookNum,sheetNum);
//		//int[] kirokuyou1;
//		//kirokuyou1 = new int[numOfConsumer];
//		ArrayList<Integer> kirokuyou1;
//		ArrayList<ArrayList> kirokuyou2;
//		kirokuyou1 = new ArrayList<Integer>();
//		kirokuyou2 = new ArrayList<ArrayList>();
//		memory = new Memory();
//		kirokuyou2 = memory.getMemoryConsumption();
//		for(int i =0; i<100;i++){//kirokuyou2.size(); i++){
//			System.out.println("がんばれ");
//			kirokuyou1 = (kirokuyou2.get(i));
//			this.setIntLabels(kirokuyou1, ws[bookNum][sheetNum], i, 1);
//		}
//		writeBook(wb[bookNum]);
//		closeBook(wb[bookNum]);
//

	public void writeEnd(int booknumber){
		writeBook(wb[booknumber]);
		closeBook(wb[booknumber]);
	}


	/**
	 *シートにデータを書き込んでいくメソッド
	 */

	//松本コードを封印
	/*
	public void writePositiveKnowledge (int booknumber, int numOfSheet, int maxSedai, int line, ConsPopulation conspopulation_) {

		String[][] header = new String [1][5];   //交換確率シナリオなし
//		String[][] header = new String [1][6];	 //交換確率シナリオあり
		header[0][0] = "シナリオ番号";
		header[0][1] = "平均";
		header[0][2] = "ばらつき";
		header[0][3] = "ネットワーク";
		header[0][4] = "広告戦略";
//		header[0][5] = "交換確率";

		setMatrix(header,ws[booknumber][numOfSheet],0,0);

		//世代のセルを作成
		ArrayList<String> sedaicell = new ArrayList<String>();
		sedaicell.add ("初期値");

		for(int a=0; a<maxSedai; a+=n){
			sedaicell.add ("第"+(a+100)+"期");
		}

		setStrLabels(sedaicell, ws[booknumber][numOfSheet], 0,  7) ;
//
//		int[][] retu = new int[96][6];  //★知識交換シナリオありver
		int[][] retu = new int[48][5]; //知識交換シナリオなし

		int count = 0;
		int senario = 1; //シナリオ番号

		for(int a=0; a<2; a++){
			for(int b=0; b<2; b++){
				for(int c=0; c<3; c++){
					for(int d=0; d<4; d++){
//						for(int e=0; e<2; e++){

							retu[count][0] = senario;
							retu[count][1] = a;
							retu[count][2] = b;
							retu[count][3] = c;
							retu[count][4] = d;
//							retu[count][5] = e;
							count++;
							senario++;
//						}
					}
				}
			}
		}
		setMatrix(retu, ws[booknumber][numOfSheet], 1, 0);


		ArrayList<Double> positive2Knowledgelist = new ArrayList<Double>();
		double[] PositiveKnowledge = new double [6];//各期のポジティブな知識量のリスト

		positive2Knowledgelist = conspopulation_.getAverageOfAgentPositiveKnowledgeList();

			PositiveKnowledge[0] = positive2Knowledgelist.get(0);

			for(int a=1; a<6; a++){
				PositiveKnowledge[a] = positive2Knowledgelist.get(a*100);
			}

			setDoubleMatrix(PositiveKnowledge, ws[booknumber][numOfSheet], line, 7);
	}


	public void writeNegativeKnowledge (int booknumber, int numOfSheet, int maxSedai, int line, ConsPopulation conspopulation_) {



		String[][] header = new String [1][5];   //交換確率シナリオなし
//		String[][] header = new String [1][6];  //交換確率シナリオ入れるならば、String [1][6]
		header[0][0] = "シナリオ番号";
		header[0][1] = "平均";
		header[0][2] = "ばらつき";
		header[0][3] = "ネットワーク";
		header[0][4] = "広告戦略";
//		header[0][5] = "交換確率";

		setMatrix(header,ws[booknumber][numOfSheet],0,0);


		//世代のセルを作成
		ArrayList<String> sedaicell = new ArrayList<String>();
		sedaicell.add ("初期値");

		for(int a=0; a<maxSedai; a+=n){
			sedaicell.add ("第"+(a+100)+"期");
		}

		setStrLabels(sedaicell, ws[booknumber][numOfSheet], 0,  7) ;

		//
//		int[][] retu = new int[96][6];  //知識交換シナリオありver
		int[][] retu = new int[48][5]; //知識交換シナリオなし


		int count = 0;
		int senario = 1; //シナリオ番号

		for(int a=0; a<2; a++){  //平均シナリオ
			for(int b=0; b<2; b++){  //ばらつきシナリオ
				for(int c=0; c<3; c++){  //ネットワークシナリオ
					for(int d=0; d<4; d++){  //広告戦略
//						for(int e=0; e<2; e++){  //知識交換確率シナリオ

							retu[count][0] = senario;
							retu[count][1] = a;
							retu[count][2] = b;
							retu[count][3] = c;
							retu[count][4] = d;
//							retu[count][5] = e;
							count++;
							senario++;

//						}
					}
				}
			}
		}
		setMatrix(retu, ws[booknumber][numOfSheet], 1, 0);


		ArrayList<Double> negative2Knowledgelist = new ArrayList<Double>();
		double[] NegativeKnowledge = new double [6];//各期のポジティブな知識量のリスト

		negative2Knowledgelist = conspopulation_.getAverageOfAgentNegativeKnowledgeList();

		NegativeKnowledge[0] = negative2Knowledgelist.get(0);

		for(int a=1; a<6; a++){
			NegativeKnowledge[a] = negative2Knowledgelist.get(a*100);
		}

		setDoubleMatrix(NegativeKnowledge, ws[booknumber][numOfSheet], line, 7);
	}




	/**
	 * 1試行のデータをいくつかのシートに格納してブックとして書き出すメソッド
	 */
	/*
	public void writeBook0(int maxSedai ,int scenarionum, int sikou, ConsPopulation conspopulation, int booknumber) {

		//各シナリオでの知識量（平均）の推移
		writePositiveKnowledge(booknumber, 0, maxSedai, scenarionum, conspopulation);//�u�b�N

		writeNegativeKnowledge(booknumber, 1, maxSedai, scenarionum, conspopulation);

//		writeAgentPositiveKnowledge(memory, elw, s1, sikou,sedai,retu);

		//全シナリオ後、シートへの書き込みが終わったら、ブックに保存して閉じる



		if(scenarionum==48){  //☆★☆交換確率シナリオありverならば、retu==97
			writeBook(wb[booknumber]);

			closeBook(wb[booknumber]);
		}
	}


	public void writeBook1(int sikou, int scenarionum, int ave, int sd, int nt, int ad, int ex, int maxSikou, ConsPopulation conspopulation, int booknumber){

		System.out.println("試行"+sikou+",scenarionum="+scenarionum);

		int numOfSheet = scenarionum-1;

		if(sikou == 1){
			String[][] header1 = new String [1][2];
			String[][] header2 = new String [1][8];
			String[][] header3 = new String [2][1];
			String[][] header4 = new String [2][1];

			header1[0][0] = "シナリオ番号";
			header1[0][1] = Integer.toString(scenarionum);

//			System.out.println("シナリオ番号="+scenarionum);

			header2[0][0] = "平均";
			header2[0][1] = Integer.toString(ave);
			header2[0][2] = "ばらつき";
			header2[0][3] = Integer.toString(sd);
			header2[0][4] = "ネットワーク";
			header2[0][5] = Integer.toString(nt);
			header2[0][6] = "広告戦略";
			header2[0][7] = Integer.toString(ad);
			//header[0][10] = "交換確率";
			//header[0][11] = Integer.toString(ex);


			header3[0][0] = "ポジティブ";
			header3[1][0] = "世代＼試行";

			header4[0][0] = "ネガティブ";
			header4[1][0] = "世代＼試行";

			setMatrix(header1,ws[booknumber][numOfSheet],0,0);
			setMatrix(header2,ws[booknumber][numOfSheet],1,0);
			setMatrix(header3,ws[booknumber][numOfSheet],3,0);
			setMatrix(header4,ws[booknumber][numOfSheet],507,0);

			//世代のセルを作成

			String[][] sedai = new String[501][1]; //知識交換シナリオなし

			sedai[0][0] = "初期値";

			for(int a =1; a<501; a++){
				sedai[a][0] = Integer.toString(a);
			}

			setMatrix(sedai, ws[booknumber][numOfSheet], 5, 0);
			setMatrix(sedai, ws[booknumber][numOfSheet], 509, 0);

			for(int a=0; a<501; a++){
				sumPKnowledge[numOfSheet][a][0] = 0;
				sumNKnowledge[numOfSheet][a][0] = 0;
			}
		}

		String[][] Trial = new String [1][1];
		Trial[0][0] = "���s"+sikou;
		setMatrix(Trial, ws[booknumber][numOfSheet], 4, sikou);



		ArrayList<Double> pKnowledgelist = new ArrayList<Double>();//各期のポジティブな知識量の平均リスト
		ArrayList<Double> nKnowledgelist = new ArrayList<Double>();//各期のネガティブな知識量の平均リスト

		double plist[][] = new double[501][1];
		double nlist[][] = new double[501][1];

		pKnowledgelist = conspopulation.getAverageOfAgentPositiveKnowledgeList();
		nKnowledgelist = conspopulation.getAverageOfAgentNegativeKnowledgeList();


		for(int a=0; a<501; a++){
			plist[a][0] = pKnowledgelist.get(a);
			nlist[a][0] = nKnowledgelist.get(a);
//			System.out.println("plist["+a+"][0]="+plist[a][0]);

			sumPKnowledge[numOfSheet][a][0] += plist[a][0];
			sumNKnowledge[numOfSheet][a][0] += nlist[a][0];

//			System.out.println("sum="+sumPKnowledge[a][0]);

		}

		setMatrix(plist, ws[booknumber][numOfSheet], 5,sikou);
		setMatrix(nlist, ws[booknumber][numOfSheet], 509,sikou);

		if(sikou == 10){

			double avePlist[][] = new double[501][1];
			double aveNlist[][] = new double[501][1];

			for(int a=0; a<501; a++){
				avePlist[a][0] = sumPKnowledge[numOfSheet][a][0] / (double)10;
				aveNlist[a][0] = sumNKnowledge[numOfSheet][a][0] / (double)10;
			}

			setMatrix(avePlist, ws[booknumber][numOfSheet], 5,(sikou+1));
			setMatrix(aveNlist, ws[booknumber][numOfSheet], 509,(sikou+1));
		}

		if(sikou == maxSikou && scenarionum == 48){  //☆★☆交換確率シナリオありverならば、retu==97
			writeBook(wb[booknumber]);

			closeBook(wb[booknumber]);
		}

	}

	public void writeBook2(int sikou, int scenarionum, int ave, int sd, int nt, int ad, int ex, int maxSikou, ConsPopulation conspopulation, int booknumber){

		int numOfSheet = scenarionum-1;

		String[][] header1 = new String [1][2];
		String[][] header2 = new String [1][8];
		String[][] header3 = new String [1][19];

		header1[0][0] = "シナリオ番号";
		header1[0][1] = Integer.toString(scenarionum);
		header2[0][0] = "平均";
		header2[0][1] = Integer.toString(ave);
		header2[0][2] = "ばらつき";
		header2[0][3] = Integer.toString(sd);
		header2[0][4] = "ネットワーク";
		header2[0][5] = Integer.toString(nt);
		header2[0][6] = "広告戦略";
		header2[0][7] = Integer.toString(ad);
		//header[0][10] = "交換確率";
		//header[0][11] = Integer.toString(ex);
		header3[0][0] = "消費者";
		header3[0][1] = "広告";
		header3[0][2] = "次数";
		header3[0][3] = "クラスタ係数";
		header3[0][4] = "態度";
		header3[0][5] = "PorN";
		header3[0][6] = "P初期値";
		header3[0][7] = "P100";
		header3[0][8] = "P200";
		header3[0][9] = "P300";
		header3[0][10] = "P400";
		header3[0][11] = "P500";
		header3[0][12] = "N初期値";
		header3[0][13] = "N100";
		header3[0][14] = "N200";
		header3[0][15] = "N300";
		header3[0][16] = "N400";
		header3[0][17] = "N500";
		header3[0][18] = "スタートアップ";

		setMatrix(header1,ws[booknumber][numOfSheet],0,0);
		setMatrix(header2,ws[booknumber][numOfSheet],1,0);
		setMatrix(header3,ws[booknumber][numOfSheet],3,0);

		//消費者番号
		String consname[][] = new String[500][1];
		for(int i=0; i<500; i++){
			consname[i][0] = "消費者"+i;
		}
		setMatrix(consname,ws[booknumber][numOfSheet],4,0);

		//広告の有無
		String adList[][] = new String[500][1];
		adList = conspopulation.getAdList();
		setMatrix(adList,ws[booknumber][numOfSheet],4,1);

		//次数
		int degreeList[][] = new int[500][1];
		degreeList = conspopulation.getDegreeList();
		setMatrix(degreeList,ws[booknumber][numOfSheet],4,2);

		//クラスタ－係数
		double clusterList[][] = new double[500][1];
		clusterList = conspopulation.getClusterList();
		setMatrix(clusterList,ws[booknumber][numOfSheet],4,3);

		//初期態度
		double attitudeList[][] = new double[500][1];
		attitudeList = conspopulation.getAttitudeList();
		setMatrix(attitudeList,ws[booknumber][numOfSheet],4,4);

		//PorN
		String PNList[][] = new String[500][1];
		PNList = conspopulation.getAttitudePNList();
		setMatrix(PNList,ws[booknumber][numOfSheet],4,5);

		//P値
		double PKnowledgeList[][] = new double[500][6];
		PKnowledgeList = conspopulation.getConsPKList();
		setMatrix(PKnowledgeList,ws[booknumber][numOfSheet],4,6);

		//N値
		double NKnowledgeList[][ ]= new double[500][6];
		NKnowledgeList = conspopulation.getConsNKList();
		setMatrix(NKnowledgeList,ws[booknumber][numOfSheet],4,12);


		//スタートアップの次期
		int startupList[][] = new int[500][1];
		startupList = conspopulation.getStartupList();
		setMatrix(startupList,ws[booknumber][numOfSheet],4,18);


		if(scenarionum == 48){  //☆★☆交換確率シナリオありverならば、retu==97
			writeBook(wb[booknumber]);

			closeBook(wb[booknumber]);
		}


	}
	*/

//		public void writeTrialBook2(Memory memory, int sikou ,int sedai ,int retu) {
//			String currentSikou = Integer.toString(sikou);
//
//			//各シナリオでの知識量(平均)の推移
////			writePositiveKnowledge(memory, elw, s1, sikou,sedai,retu);
//
//			writeNegativeKnowledge(memory, elw, s2, sikou,sedai,retu);
//
////			writeAgentPositiveKnowledge(memory, elw, s1, sikou,sedai,retu);
//
//			//全試行後、シートへの書き込みが終わったら、ブックに保存して閉じる
//			if(retu==97){
//			wb = elw.writeBook(wb);
//
//			wb =elw.closeBook(wb);
//			}
//
//	}



}
