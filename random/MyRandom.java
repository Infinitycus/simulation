package random;

import java.util.Random;

/**
 * Класс для генерации случайных чисел, заданнных
 * нормальным и экспоненциальным распределением
 * @author Евгений Мысловец
 * 06.11.2009
 */
public class MyRandom
{
	/**
	 * Конструктор класса.
	 * Создаем стандартный генератор случайных чисел
	 */
	public MyRandom()
	{
		random = new Random();
		random.setSeed(System.currentTimeMillis());
	}
	
	/**
	 * Генерация случайных чисел, заданнных экспоненциальным распределением
	 * @param lambda - параметр экспоненциального распределения
	 * @return Сгенерированное случайное число, имеющее экспоненциальное распределение с параметром lambda
	 */
	public double rndExp(double lambda)
	{
		double u;
		while ((u = random.nextDouble()) <= 0.00001); // нельзя, чтобы u получилось равное 0
		return -1.0/lambda*Math.log(u);
		
	}
	
	/**
	 * Генерация случайных чисел, заданнных нормальное распределением
	 * @param m - математическое ожидание случайной величины
	 * @param d - дисперсия случайной величины
	 * @return Сгенерированное случайное число, имеющее нормальное распределение с параметрами alpha и sigma
	 */
	public double rndNorm(double alpha, double sigma)
	{
		// получаем случайное число, имеющее стандратное нормальное распределение
		double u = random.nextGaussian(); 
		// приводим его к нормальному распределение с параметрами alpha и sigma
		return alpha + sigma*u;
	}
	
	/**
	 * Генерация случайных чисел, заданнных равномерным распределением
	 * @return Сгенерированное случайное число, имеющее равномерное распределение на отрезке [a,b]
	 */
	public double rnd(double a, double b)
	{
		if (b > a)
			return a + (b -  a)*random.nextDouble();
		else		
			return b + (a -  b)*random.nextDouble();
	}
	
	// стандартный генератор случайных чисел
	private Random random;
}
