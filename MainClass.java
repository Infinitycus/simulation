import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import kernel.Kernel;

/**
 * @author Евгений Мысловец
 * 06.11.2009
 */
public class MainClass
{
	public static void main(String[] args)
	{
		print("Ќачало моделировани¤");
		// создаем файл отчета и файл статистики
		File fileLog = new File("bin/log.xls");
		File fileStat = new File("bin/stat.xls");
		// создаем потоки записи в файлы
		PrintWriter outLog = null;
		PrintWriter outStat = null;
		try
		{
			outLog = new PrintWriter(fileLog);
			outStat = new PrintWriter(fileStat);
			outLog.println("Ќомер прогона\tћодельное врем¤\tќбъект\t—ообщение");
			outStat.println("ќбщее врем¤\t„исло клиентов на левом берегу\t„исло клиентов на правом берегу\t" +
					"—реднее врем¤ в очереди на левом берегу\t—реднее врем¤ в очереди на правом берегу\t" +
					"—редн¤¤ длина очереди на левом берегу\t—редн¤¤ длина очереди на правом берегу\t" +
					"—реднее врем¤ в системе клиента с левого берега\t—реднее врем¤ в системе клиента с правого берега\t" +
					"—редн¤¤ загрузка парома");
			
			// запускаем модель 10 раз
			for (int i = 1; i <= 10; i++)
			{
				Kernel kernel = new Kernel(i, outLog, outStat);
				kernel.run();
			}

			// закрываем потоки записи
			outLog.close();
			outStat.close();
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("ќшибка открыти¤ файла!");
			ex.printStackTrace();
		}		
	}
	
	private static void print(String msg)
	{
		System.out.println(msg);
	}

}
