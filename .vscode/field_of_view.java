import java.util.Random;

class  field_of_view {

    //視野決定のための関数
    //視野の拡大はexpantionStageだけプラスされるver
	static public int fieldOfView(int expantionFrequency, int expantionStage, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		boolean expantion = random_tool.generateWithProbability(expantionFrequency);


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

    //視野決定のための関数
    //一定の段階まで常に拡大するver
	static public int fieldOfView2(int expantionFrequency, int expantionStage, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		boolean expantion = random_tool.generateWithProbability(expantionFrequency);


		//視野レベル
		int level;

		if(expantion){
			level = expantionStage;
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

    //視野決定のための関数
    //視野の拡大はランダムな数プラスされる
	static public int fieldOfView(int expantionFrequency, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		boolean expantion = random_tool.generateWithProbability(expantionFrequency);


		//視野レベル
		int level;

		if(expantion){
             Random random = new Random();
            int expantionStage = random.nextInt(10) + 1;
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
}