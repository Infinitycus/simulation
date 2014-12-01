package models;

/**
 * Модель клиента
 * @author Евгений Мысловец
 * 06.11.2009
 */
public class Client
{
	/**
	 *  онструктор, инициилизирующий клиента
	 * @param id - номер клиента
	 * @param timeArrive - врем¤ по¤влени¤ клиента
	 */
	public Client(int id, double timeArrive)
	{
		name = " лиент " + id;
		this.timeArrive = timeArrive;
	}
	
	/**
	 * ¬озвращает врем¤ по¤влени¤ клиента
	 * @return врем¤ по¤влени¤ клиента
	 */
	public double getTimeArrive()
	{
		return timeArrive;
	}

	/**
	 * «адает врем¤ выхода клиента из системы
	 * @param timeOut - врем¤ выхода клиента из системы
	 */
	public void setTimeOut(double timeOut)
	{
		this.timeOut = timeOut;
	}

	/**
	 * ¬озвращает врем¤ выхода клиента из системы
	 * @return выхода клиента из системы
	 */
	public double getTimeOut()
	{
		return timeOut;
	}

	/**
	 * «адает врем¤, проведенное в очереди
	 * @param timeInQueue - врем¤ в очереди
	 */
	public void setTimeInQueue(double timeInQueue)
	{
		this.timeInQueue = timeInQueue;
	}

	/**
	 * ¬озвращает врем¤, проведенное в очереди
	 * @return врем¤ в очереди
	 */
	public double getTimeInQueue()
	{
		return timeInQueue;
	}

	@Override
	public String toString()
	{	
		return name;
	}

	// врем¤ по¤влени¤ клиента в системе
	private double timeArrive;
	// врем¤ выхода из системы
	private double timeOut;
	// врем¤ в очереди
	private double timeInQueue;
	// им¤ клиента
	private String name;
}
