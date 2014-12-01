package kernel;

import java.io.PrintWriter;
import java.util.ArrayList;

import models.Client;
import models.Ferry;

import random.MyRandom;

/**
 * Ядро моделирования
 * @author Евгений Мысловец 
 * 06.11.2009
 */
public class Kernel
{

	/**
	 * Конструктор по умолчанию
	 */
	public Kernel(int id, PrintWriter outLog, PrintWriter outStat)
	{
		this.id = id;
		System.out.println("Начало прогона " + id);
		// создаем генератор случайных чисел
		random = new MyRandom();
		if (outLog != null)
			this.outLog = outLog;
		if (outStat != null)
			this.outStat = outStat;
	}
	
	/**
	 * Запуск моделирования
	 */
	public void run()
	{
		// задаем максимальное время моделирования
		timeLimit = 200;

		// подготовительная часть
		prepare();
		// начало моделирования
		modeling();
		// сбор статистики
		stats();
	}
	
	/**
	 * Подготовительная часть моделирования
	 */
	private void prepare()
	{
		// время появления клиента
		double timeArrive = 0;
		// порядковый номер
		int number = 0;
		// список клиентов на левом берегу
		clientsLeft = new ArrayList<Client>();
		// список клиентов на правом берегу
		clientsRight = new ArrayList<Client>();
		// очередь клиентов на левом берегу
		queueLeft = new ArrayList<Client>();
		// очередь клиентов на правом берегу
		queueRight = new ArrayList<Client>();
		// список обслуженных клиентов на левом берегу
		servedLeft = new ArrayList<Client>();
		// список обслуженных клиентов на правом берегу
		servedRight = new ArrayList<Client>();

		// заполняем список клиентов на левом берегу
		do
		{
			clientsLeft.add(new Client(number + 1000, timeArrive));
			number++;
		}
		while ((timeArrive += random.rndExp(lambdaLeft)) < timeLimit);
		
		// заполняем список клиентов на правом берегу
		number = 0; // обнуляем переменные
		timeArrive = 0;
		do
		{
			clientsRight.add(new Client(number + 2000, timeArrive));
			number++;
		}
		while ((timeArrive += random.rndExp(lambdaRight)) < timeLimit);

		// создаем объект парома
		ferry = new Ferry();
		
		// инциализируем переменные моделирования
		timeModeling = 0; // время моделирования
		ferry.setBusyLeft(false); // индикатор занятости паром для левого берега
		ferry.setBusyRight(true); // индикатор занятости паром для правого берега
		ferry.setTimeNextLeft(0); // по умолчанию паром стоит на левом берегу
		ferry.setTimeNextRight(timeLimit); // значит, пока не отправили его на правый, время прибытия на правый не задано
	}
	
	/**
	 * Главный цикл моделирования
	 */
	private void modeling()
	{
		// если еще есть время для моделирования или списки клиентов не пустые
		while (timeModeling <= timeLimit && clientsLeft.size() != 0 && clientsRight.size() != 0)
		{
			// считываем время появления следующего клиента на обоих берегах
			timeNextClientLeft = clientsLeft.get(0).getTimeArrive();
			timeNextClientRight = clientsRight.get(0).getTimeArrive();
			
			// если на левом появится раньше, чем на правом, то работаем с левым берегом, иначе - с правым
			if (timeNextClientLeft <= timeNextClientRight)
			{
				
				// приплыл паром на правый берег
				if (ferry.getTimeNextRight() < timeNextClientLeft && ferry.isBusyRight())
				{
					doReturnFerryRight();
					// сажаем очередь в паром
					if (queueRight.size() != 0)
						doGetFromQueueRight();
				}
				
				// приплыл паром на левый берег
				if (ferry.getTimeNextLeft() < timeNextClientLeft && ferry.isBusyLeft())
				{
					doReturnFerryLeft();
					// сажаем очередь в паром
					if (queueLeft.size() != 0)
						doGetFromQueueLeft();
				}
								
				// отправим паром на левый берег
				if ((ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientRight - ferry.getTimeNextRight() >= ferry.TIMEMAX) && !ferry.isBusyRight())
				{					
					// если время появления клиента на левом берегу меньше, 
					// чем отправление парома, то его надо сначала поставить в очередь
					if (ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientLeft > ferry.getTimeNextRight() + ferry.TIMEMAX)
							doSwimFerryRight();
				}
							
				// отправим паром на правый берег
				if ((ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientLeft - ferry.getTimeNextLeft() >= ferry.TIMEMAX) && !ferry.isBusyLeft())
				{
					// если время появления клиента на правом берегу меньше, 
					// чем отправление парома, то его надо сначала поставить в очередь
					if (ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientRight > ferry.getTimeNextLeft() + ferry.TIMEMAX)
						doSwimFerryLeft();
				}
				
				// поставим в очередь на левом берегу
				if (timeNextClientLeft <= ferry.getTimeNextLeft() && ferry.isBusyLeft())
				{
					doPutIntoQueueLeft();
				}
				
				// посадим в паром на левом берегу
				if (timeNextClientLeft < ferry.getTimeNextLeft() + ferry.TIMEMAX && !ferry.isBusyLeft())
				{
					doPutIntoFerryLeft();
				}								
											
			}
			else
			{			
				// приплыл паром на левый берег
				if (ferry.getTimeNextLeft() < timeNextClientRight && ferry.isBusyLeft())
				{
					doReturnFerryLeft();
					// сажаем очередь в паром
					if (queueLeft.size() != 0)
						doGetFromQueueLeft();
				}
				// приплыл паром на правый берег
				if (ferry.getTimeNextRight() < timeNextClientRight && ferry.isBusyRight())
				{
					doReturnFerryRight();
					// сажаем очередь в паром
					if (queueRight.size() != 0)
						doGetFromQueueRight();
				}				
				
				// отправим паром на левый берег
				if ((ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientRight - ferry.getTimeNextRight() >= ferry.TIMEMAX) && !ferry.isBusyRight())
				{
					// если время появления клиента на левом берегу меньше, 
					// чем отправление парома, то его надо сначала поставить в очередь
					if (ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientLeft > ferry.getTimeNextRight() + ferry.TIMEMAX)
							doSwimFerryRight();
				}				
				
				// отправим паром на правый берег
				if ((ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientLeft - ferry.getTimeNextLeft() >= ferry.TIMEMAX) && !ferry.isBusyLeft())
				{
					// если время появления клиента на правом берегу меньше, 
					// чем отправление парома, то его надо сначала поставить в очередь
					if (ferry.getClients().size() >= ferry.MAXNUMBER || timeNextClientRight > ferry.getTimeNextLeft() + ferry.TIMEMAX)
						doSwimFerryLeft();
				}
				
				// поставим в очередь на правом берегу
				if (timeNextClientRight <= ferry.getTimeNextRight() && ferry.isBusyRight())
				{
					doPutIntoQueueRight();
				}
				
				// посадим на паром на правом берегу
				if (timeNextClientRight < ferry.getTimeNextRight() + ferry.TIMEMAX && !ferry.isBusyRight())
				{
					doPutIntoFerryRight();
				}				
				
			}	
			
		}
		
	}	
	
	/**
	 * Поставить в очередь на правом берегу
	 */
	private void doPutIntoQueueRight()
	{
		// печатаем в лог
		printLog(id, timeNextClientRight, clientsRight.get(0).toString(), "Стал в очередь на правом берегу");
		// ставим в очередь клиента
		queueRight.add(clientsRight.get(0));
		// потом удаляем его из списка клиентов
		clientsRight.remove(0);
		// изменяем время моделирования
		timeModeling = timeNextClientRight;	
	}
	
	/**
	 * Достать из очереди на правом берегу
	 */
	private void doGetFromQueueRight()
	{
		int i = 0;
		// пока в очереди есть клиенты
		while (queueRight.size() > 0)
		{					
			// задаем время, проведенное клиентом в очереди
			queueRight.get(0).setTimeInQueue(timeModeling - queueRight.get(0).getTimeArrive());
			// сажаем их в паром
			ferry.addClient(queueRight.get(0));					
			printLog(id, timeModeling, queueRight.get(0).toString(), "Вышел из очереди на правом берегу и сел в паром");
			// удаляем из очереди
			queueRight.remove(0);
			// если их будет больше 15, то надо бы уже отправить паром
			i++;
			if (i >= ferry.MAXNUMBER)
				break;					
		}		
	}
	
	/**
	 * Посадить в паром на правом берегу
	 */
	private void doPutIntoFerryRight()
	{
		// печатаем в лог
		printLog(id, timeNextClientRight, clientsRight.get(0).toString(), "Сел в паром на правом берегу");
		// сажаем в паром клиента
		ferry.addClient(clientsRight.get(0));
		// удаляем его из списка клиентов
		clientsRight.remove(0);
		// изменяем время моделирования
		timeModeling = timeNextClientRight;	
	}
	
	/**
	 * Отправить паром с правого берега
	 */
	private void doSwimFerryRight()
	{
		// время на переправу парома
		double timeToSwim;
		
		// определяем время, необходимое для переправы парома
		timeToSwim = random.rndNorm(alpha, sigma);
		// показываем, что парома нет на правом берегу
		ferry.setBusyRight(true);
		// определяем причину отправления парома
		if (ferry.getClients().size() < ferry.MAXNUMBER)
		{
			// если прошло 20 минут
			double time = ferry.getTimeNextRight() + ferry.TIMEMAX;
			ferry.setTimeNextLeft(time + timeToSwim);
			ferry.swim(time);
			printLog(id, time, "Паром", "Поплыл с правого берега на левый");
			printLog(id, time, "Паром", "На борту " + ferry.getClients().size() + " человек");
		}
		else
		{
			// если зашли уже 15 клиентов
			ferry.setTimeNextLeft(timeModeling + timeToSwim);
			ferry.swim(timeModeling);
			printLog(id, timeModeling, "Паром", "Поплыл с правого берега на левый");
			printLog(id, timeModeling, "Паром", "На борту " + ferry.getClients().size() + " человек");
		}				
//		print("Будет на левом берегу в " + ferry.getTimeNextLeft());
		// время прибытия обратно не определено
		ferry.setTimeNextRight(timeLimit);
	}
	
	/**
	 * Вернуть паром на правый берег
	 */
	private void doReturnFerryRight()
	{
		// меняем время моделирования
		timeModeling = ferry.getTimeNextRight();
		// печатаем в лог
		printLog(id, timeModeling, "Паром", "Вернулся на правый берег");
		// показываем, что паром свободен на правом берегу
		ferry.setBusyRight(false);
		// записываем время выхода клиентов
		for (Client client : ferry.getClients())
		{
			printLog(id, timeModeling, client.toString(), "Вышел из парома");
			client.setTimeOut(timeModeling);
		}
		// обслужили клиентов
		try
		{
			servedLeft.addAll(ferry.getClients());
		}
		catch (NullPointerException ex)
		{			
			ex.printStackTrace();
		}
		// паром пуст
		ferry.getClients().clear();
	}

	/**
	 * Поставить в очередь на левом берегу
	 */
	private void doPutIntoQueueLeft()
	{
		// печатаем в лог
		printLog(id, timeNextClientLeft, clientsLeft.get(0).toString(), "Стал в очередь на левом берегу");
		// ставим в очередь клиента
		queueLeft.add(clientsLeft.get(0));
		// потом удаляем его из списка клиентов
		clientsLeft.remove(0);
		// изменяем время моделирования
		timeModeling = timeNextClientLeft;
	}
	
	/**
	 * Достать из очереди на левом берегу
	 */
	private void doGetFromQueueLeft()
	{
		// пока в очереди есть клиенты
		int i = 0;
		// сажаем их в паром
		while (queueLeft.size() > 0)
		{					
			// задаем время, проведенное клиентом в очереди
			queueLeft.get(0).setTimeInQueue(timeModeling - queueLeft.get(0).getTimeArrive());
			// сажаем в паром
			ferry.addClient(queueLeft.get(0));
			printLog(id, timeModeling, queueLeft.get(0).toString(), "Вышел из очереди на левом берегу и сел в паром");								
			// удаляем из очереди
			queueLeft.remove(0);
			// если их будет больше 15, то надо бы уже отправить паром
			i++;
			if (i >= ferry.MAXNUMBER)
				break;					
		}	
	}
	
	/**
	 * Посадить в паром на левом берегу
	 */
	private void doPutIntoFerryLeft()
	{
		// печатаем в лог
		printLog(id, timeNextClientLeft, clientsLeft.get(0).toString(), "Сел в паром на левом берегу");
		// сажаем в паром клиента
		ferry.addClient(clientsLeft.get(0));
		// удаляем его из списка клиентов
		clientsLeft.remove(0);
		// изменяем время моделирования
		timeModeling = timeNextClientLeft;
	}
	
	/**
	 * Отправить паром с левого берега
	 */
	private void doSwimFerryLeft()
	{
		// время на переправу парома
		double timeToSwim;
		
		// определяем время, необходимое для переправы парома
		timeToSwim = random.rndNorm(alpha, sigma);
		// показываем, что парома нет на правом берегу
		ferry.setBusyLeft(true);
		// определяем причину отправления парома
		if (ferry.getClients().size() < ferry.MAXNUMBER)
		{
			// если прошло 20 минут
			double time = ferry.getTimeNextLeft() + ferry.TIMEMAX;
			ferry.setTimeNextRight(time + timeToSwim);
			ferry.swim(time);
			printLog(id, time, "Паром", "Поплыл с левого берега на правый");
			printLog(id, time, "Паром", "На борту " + ferry.getClients().size() + " человек");
		}
		else
		{
			// если зашли уже 15 клиентов
			ferry.setTimeNextRight(timeModeling + timeToSwim);
			ferry.swim(timeModeling);
			printLog(id, timeModeling, "Паром", "Поплыл с левого берега на правый");
			printLog(id, timeModeling, "Паром", "На борту " + ferry.getClients().size() + " человек");
		}				
//		print("Будет на правом берегу в " + ferry.getTimeNextRight());
		// время прибытия обратно не определено
		ferry.setTimeNextLeft(250);
	}
		
	/**
	 * Вернуть паром на левый берег
	 */
	private void doReturnFerryLeft()
	{
		// меняем время моделирования
		timeModeling = ferry.getTimeNextLeft();
		// печатаем в лог
		printLog(id, timeModeling, "Паром", "Вернулся на правый берег");
		// показываем, что паром свободен на правом берегу
		ferry.setBusyLeft(false);
		// записываем время выхода клиентов
		for (Client client : ferry.getClients())
		{
			printLog(id, timeModeling, client.toString(), "Вышел из парома");
			client.setTimeOut(timeModeling);
		}
		// обслужили клиентов
		try
		{
			servedRight.addAll(ferry.getClients());
		}
		catch (NullPointerException ex)
		{			
			ex.printStackTrace();
		}
		// паром пуст
		ferry.getClients().clear();
	}
	
	/**
	 * Печать в файл отчета
	 * @param id - номер прогона
	 * @param time - время события
	 * @param name - объект
	 * @param msg - сообщение
	 */
	private void printLog(int id, double time, String name, String msg)
	{
		outLog.println(id + "\t" + time  + "\t" + name + "\t" + msg);
	}
	
	private void stats()
	{
		// общее время
		double time = timeModeling > timeLimit ? timeModeling : timeLimit;
		outStat.print(time + "\t");
		
		// общее количество обслуженных клиентов на левом берегу
		outStat.print(servedLeft.size() + "\t");
		// общее количество обслуженных клиентов на правом берегу
		outStat.print(servedRight.size() + "\t");
		
		// среднее время в очереди на левом берегу
		double timeInQueueTotalLeft = 0;
		for (Client client : servedLeft)
		{
			timeInQueueTotalLeft += client.getTimeInQueue();
		}
		double timeInQueueAvgLeft = 0;
		if (servedLeft.size() != 0)
			timeInQueueAvgLeft = timeInQueueTotalLeft / servedLeft.size();
		outStat.print(timeInQueueAvgLeft + "\t");
		
		// среднее время в очереди на правом берегу
		double timeInQueueTotalRight = 0;
		for (Client client : servedRight)
		{
			timeInQueueTotalRight += client.getTimeInQueue();
		}		
		double timeInQueueAvgRight = 0;
		if (servedRight.size() != 0)
			timeInQueueAvgRight = timeInQueueTotalRight / servedRight.size();
		outStat.print(timeInQueueAvgRight + "\t");
		
		// средняя длина очереди на левом берегу
		double countAvgLeft; 
		countAvgLeft = timeInQueueAvgLeft * lambdaLeft;
		outStat.print(countAvgLeft + "\t");
		
		// средняя длина очереди на правом берегу
		double countAvgRight;
		countAvgRight = timeInQueueAvgRight * lambdaRight;
		outStat.print(countAvgRight + "\t");
		
		// среднее время в системе клиента с левого берега
		double timeInModelLeft = 0;
		for (Client client : servedLeft)
		{
				timeInModelLeft += client.getTimeOut() - client.getTimeArrive();
		}
		double timeInModelAvgLeft = 0;
		if (servedLeft.size() != 0)
			timeInModelAvgLeft = timeInModelLeft / servedLeft.size();
		outStat.print(timeInModelAvgLeft + "\t");
		
		// среднее время в системе клиента с правого берега
		double timeInModelRight = 0;
		for (Client client : servedRight)
		{
				timeInModelRight += client.getTimeOut() - client.getTimeArrive();
		}
		double timeInModelAvgRight = 0;
		if (servedRight.size() != 0)
			timeInModelAvgRight = timeInModelRight / servedRight.size();
		outStat.print(timeInModelAvgRight + "\t");
		
		// средняя загрузка парома
		double inFerryAvg = 0;
		if (ferry.getCountSwim() != 0)
			inFerryAvg = ferry.getCountClients()/ferry.getCountSwim();
		outStat.print(inFerryAvg + "\n");
				
	}
	
	// номер прогона
	private int id;
	// паром
	private Ferry ferry;
	// время прибытия на левый и правый берега
	private double timeNextClientLeft;
	private double timeNextClientRight;
	// время моделирования
	private double timeModeling;
	// генератор случайных чисел
	private MyRandom random;
	// параметры распределения
	private final double lambdaLeft = 0.55;
	private final double lambdaRight = 0.45;
	private final double alpha = 2;
	private final double sigma = 0.3;
	// ограничение времени моделирования
	private int timeLimit;
	// список клиентов на левом и правом берегах
	private ArrayList<Client> clientsLeft;
	private ArrayList<Client> clientsRight;
	// очередь клиентов на левом и правом берегах
	private ArrayList<Client> queueLeft;
	private ArrayList<Client> queueRight;
	// список клиентов для статистики
	private ArrayList<Client> servedLeft;
	private ArrayList<Client> servedRight;
	// поток для записи в файл отчета
	PrintWriter outLog;
	// поток для записи в файл статистики
	PrintWriter outStat;
}