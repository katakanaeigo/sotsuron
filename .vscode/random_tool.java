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
}