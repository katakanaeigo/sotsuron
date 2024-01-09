import java.util.Random;

public class random_tool{
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

    //配列のシャッフル
	public static boolean[] shuffleArray(boolean[] array) {
        Random random = new Random();
        boolean[] shuffledArray = array.clone(); // オリジナルの配列を変更しないようにクローンを作成

        for (int i = shuffledArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            // 要素を入れ替え
            boolean temp = shuffledArray[i];
            shuffledArray[i] = shuffledArray[index];
            shuffledArray[index] = temp;
        }

        return shuffledArray;
    }
}