package models;

import java.util.ArrayList;

/**
 * Модель парома
 * @author Евгений Мысловец
 * 06.11.2009
 */
public class Ferry
{
	/**
	 * Конструктор класса парома
	 */
	public Ferry()
	{
		clients = new ArrayList<Client>();
		countSwim = 0;
		countClients = 0;
	}
	
	/**
	 * Добавление клиента на паром
	 * @param client - клиент, которого необходимо добавить на паром
	 */
	public void addClient(Client client)
	{
		clients.add(client);
	}
	
	/**
	 * Отправление парома на другой берег
	 * @param time - время отправления
	 */
	public void swim(double time)
	{
		// увеличиваем количество переправ
		countSwim++;
		// увеличиваем количество обслуженных клиентов
		countClients += getClients().size();
	}
	
	/**
	 * Возвращает количество обслуженных клиентов
	 * @return количество обслуженных клиентов
	 */
	public int getCountClients()
	{
		return countClients;
	}

	/**
	 * Возвращает количество переправ парома
	 * @return количество переправ парома
	 */
	public int getCountSwim()
	{
		return countSwim;
	}

	/**
	 * Задает время прибытия на левый берег
	 * @param timeNext - время прибытия на левый берег
	 */
	public void setTimeNextLeft(double timeNext)
	{
		this.timeNextLeft = timeNext;
	}

	/**
	 * Возвращает время прибытия на левый берег
	 * @return время прибытия на левый берег
	 */
	public double getTimeNextLeft()
	{
		return timeNextLeft;
	}

	/**
	 * Задает время прибытия на правый берег
	 * @param timeNext - время прибытия на правый берег
	 */
	public void setTimeNextRight(double timeNextRight)
	{
		this.timeNextRight = timeNextRight;
	}

	/**
	 * Возвращает время прибытия на правый берег
	 * @return время прибытия на правый берег
	 */
	public double getTimeNextRight()
	{
		return timeNextRight;
	}
	
	/**
	 * Задает индикатор занятости парома для левого берега
	 * @param busy - индикатор занятости
	 */
	public void setBusyLeft(boolean busy)
	{
		this.busyLeft = busy;
	}

	/**
	 * Проверяет, свободен ли паром на левом берегу
	 * @return индикатор занятости на левом берегу
	 */
	public boolean isBusyLeft()
	{
		return busyLeft;
	}
	
	/**
	 * Задает индикатор занятости парома для правого берега
	 * @param busy - индикатор занятости
	 */
	public void setBusyRight(boolean busy)
	{
		this.busyRight = busy;
	}

	/**
	 * Проверяет, свободен ли паром на правом берегу
	 * @return индикатор занятости на правом берегу
	 */
	public boolean isBusyRight()
	{
		return busyRight;
	}
	
	/**
	 * Возвращает список клиентов, находящихся на пароме
	 * @return список клиентов, находящихся на пароме
	 */
	public ArrayList<Client> getClients()
	{
		return clients;
	}

	// индикатор занятости парома на обоих берегах
	private boolean busyLeft;
	private boolean busyRight;
	// список клиентов, находящихся на пароме
	private ArrayList<Client> clients;
	// время прибытия парома на левый и правый берега
	private double timeNextLeft;
	private double timeNextRight;
	// количество обслуженных клиентов
	private int countClients;
	// количество переправ
	private int countSwim;
	// максимальное время ожидания парома
	public final int TIMEMAX = 20;
	// количество мест на пароме
	public final int MAXNUMBER = 15;
}
