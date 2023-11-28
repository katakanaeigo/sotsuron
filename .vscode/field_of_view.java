import java.util.Random;

class  field_of_view {

    //視野決定のための関数
    //一定の段階まで常に拡大するver
	static public int fieldOfView(int expantionFrequency, int expantionType, int expantionStage, int reducationSpeed, int sameViewStep, int previousLevel){

		//視野の拡大が起きるか否か、確率expantionFrequency
		boolean expantion = random_tool.generateWithProbability(expantionFrequency);


		//視野レベル
		int level = 0;

		if(expantion){
			if(expantionType==1){
                //type1:固定段階拡大する
                level = previousLevel + expantionStage;
            }else if(expantionType==2){
                //type2:ランダムな段階拡大する
                Random random = new Random();
			    level = previousLevel + random.nextInt(10) + 1;
            }else if(expantionType==3){
                //type3:常に同じ視野まで拡大する
                level = expantionStage;
            }
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