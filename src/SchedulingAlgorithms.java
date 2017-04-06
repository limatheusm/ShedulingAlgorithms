import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;

public class SchedulingAlgorithms {
	
	public static void main (String[] args) {		
		/*
		 *	Modelo de entrada: 
		 *		
		 *		arrivalTime duration
		 *		arrivalTime duration
		 *		arrivalTime duration 
		 *
		 *	Modelo de saida:
		 *
		 *		FCFS averageReturnTime averageResponseTime averageWaitTime
		 *		SJF averageReturnTime averageResponseTime averageWaitTime
		 *		RR averageReturnTime averageResponseTime averageWaitTime 		
		 **/
		
		try {
			String filePath = "/home/matheus/workspace/AlgoritmosEscalonamento/example/ex1.txt";
	        BufferedReader readFile =
	                  new BufferedReader(new FileReader(filePath));
	        LinkedList<Job> jobs = new LinkedList<Job>();	         
	        String line;
	        String[] arr = new String[2];
	        int PID = 0;
	        //Preenchendo a lista de processos
	        while ((line = readFile.readLine()) != null) {
	        	arr = line.split(" ");        	
	        	jobs.add(new Job(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), PID));
	        	PID++;
	        }
	        readFile.close();
	        
	        //Ordena os processos de acordo com o tempo de chegada	        
	        if (jobs.size() > 0) {
	          Collections.sort(jobs, new Comparator<Job>() {
	              @Override
	              public int compare(final Job object1, final Job object2) {
	                  return object1.getArrivalTime() - object2.getArrivalTime();
	              }
	          });
	        }
	        
	        //Chamada dos metodos de escalonamento
	        FCFS (new LinkedList<Job>(jobs));
			SJF (new LinkedList<Job>(jobs));
			
			int quantum = 2;			
			RR (new LinkedList<Job>(jobs), quantum);

	    } catch (IOException ex) {
	        System.err.print(ex.getMessage());
	    } catch (Exception e) {
	        System.err.print(e.getMessage());
	    }		
			
	}
	
	/**
	 * Scheduling algorithms
	 */
	
	/**
	 * Algoritmo 1 - FCFS - “Primeiro a Entrar, Primeiro a ser Atendido”
	 * @param readyJobs - Fila de processos pronto para serem escalonados
	 * @return printa resultado na tela
	 */
	public static void FCFS(Queue<Job> readyJobs){
		/*Ira simular o tempo do sistema*/
		int systemTime = 0;
		
		/*Quantidade necessária de tempo para executar um processo*/
		float averageReturnTime = 0;
		
		/*quantidade de tempo entre a requisição de execução de um programa e a produção da primeira resposta*/
		float averageResponseTime = 0;
		
		/*quantidade de tempo que um processo aguardou na fila de prontos*/
		float averageWaitTime = 0;
		
		/*Quantidade de processos prontos para serem escalonados*/
		int sizeJobs = readyJobs.size();
		
		Job j = new Job();
		
		// Enquanto existir processos na fila de prontos
		while (readyJobs.peek() != null){
			
		/*
		 * Neste metodo, o primeiro processo a entrar na fila de prontos
		 * sera o primeiro a ser atendido. Portanto:
		 */
			
			/*Verifica se o processo chegou na fila de prontos (tempoDeChegada <= tempoDoSistema)*/
			if (readyJobs.peek().getArrivalTime() <= systemTime){
				
				/*Remove o primeiro elemento da fila de prontos, simulando a entrada do processo no processador.*/
				j = readyJobs.poll();
				
				/*Computa os tempos de Resposta e Espera deste processo*/
				averageResponseTime += systemTime - j.getArrivalTime();
				averageWaitTime += systemTime - j.getArrivalTime();
				
				/*Processo deixa o processador*/				
				
				/*Atualiza do tempo do sistema*/ 
				systemTime += j.getDuration();
				/*Computa o tempo de retorno deste processo*/
				averageReturnTime += systemTime - j.getArrivalTime();
			}
			/*Nao chegou nenhum processo na fila de prontos, atualiza o tempo do sistema*/
			else {
				systemTime = readyJobs.peek().getArrivalTime();
			}
			
			
		}
		
		if (sizeJobs > 0)		
			System.out.printf("FCFS %.1f %.1f %.1f\n",
				(averageReturnTime/sizeJobs), (averageResponseTime/sizeJobs), (averageWaitTime/sizeJobs));		
	}
	
	/**
	 * Algoritmo 2 - SJF - “Small Job First”
	 * @param readyJobs - Fila de processos pronto para serem escalonados
	 * @return printa resultado na tela
	 */
	public static void SJF(Queue<Job> readyJobs){
		
		int systemTime = 0;		
		float averageReturnTime = 0;		
		float averageResponseTime = 0;		
		float averageWaitTime = 0;		
		int sizeJobs = readyJobs.size();		
		Job j = new Job();
		
		/*
		 * Neste metodo, o objetivo eh escalonar o processo com a 
		 * menor duração de pico de CPU.
		 * O SJF é um algoritmo de escalonamento por prioridade onde 
		 * a prioridade é a duração do próximo pico de CPU.
		 */
		
		/*Fila de prioridade de acordo com o tempo de duracao de cada job*/
		LinkedList<Job> priorityQueue = new LinkedList<Job>();
		
		
		/*Executa ate que nao exista processos nas filas*/
		while(readyJobs.peek() != null || priorityQueue.peek() != null){	
			
			/*
			 * Verifica se o tempo do sistema corresponde ao tempo de chegada do processo, 
			 * ou seja, um processo so podera ser posto na fila de prioridades quando ele chegar
			 * na fila de prontos, que esta aqui representado por: 
			 * 				tempoDeChegada <= tempo do sistema
			 * Caso o tempo de chegada do processo esteja coerente ao tempo do sistema, retira-se o
			 * processo da fila de prontos e adiciona na fila de prioridades
			 */
			
			while (readyJobs.peek() != null && readyJobs.peek().getArrivalTime() <= systemTime) 
				priorityQueue.add(readyJobs.poll()); //processo de troca de fila
			
			/*Efetua ordenacao de acordo com o tempo de duracao do job*/
			Collections.sort(priorityQueue, new Comparator<Job>() {
	            @Override
	            public int compare(final Job object1, final Job object2) {
	                return object1.getDuration() - object2.getDuration();
	            }
	        });			
			
			/*ALgum processo chegou na fila de prontos, de acordo com o tempo do sistema,
			 * e foi posto na fila de prioridades do SJF. 
			 */
			if (priorityQueue.peek() != null){
				
				/*Processo sai da fila de prioridade para o processador*/
				j = priorityQueue.poll();
						
				/*Computa os tempos de Resposta e Espera deste processo*/
				averageResponseTime += systemTime - j.getArrivalTime();
				averageWaitTime += systemTime - j.getArrivalTime();
				
				/*Processo deixa o processador*/
					
				/*Atualiza do tempo do sistema*/ 
				systemTime += j.getDuration();
				/*Computa o tempo de retorno deste processo*/
				averageReturnTime += systemTime - j.getArrivalTime();
				
			}
			/*Nao chegou nenhum processo na fila de prontos, atualiza o tempo do sistema*/
			else {
				systemTime = readyJobs.peek().getArrivalTime();
			}						
			
		}//while
		
		if (sizeJobs > 0)		
			System.out.printf("SJF %.1f %.1f %.1f\n",
				(averageReturnTime/sizeJobs), (averageResponseTime/sizeJobs), (averageWaitTime/sizeJobs));		
	}
	
	
	/**
	 * Algoritmo 3 - RR - “Round Robin”
	 * @param readyJobs - Fila de processos pronto para serem escalonados
	 * @return printa resultado na tela
	 */
	public static void RR(Queue<Job> readyJobs, int quantum){
		
		int systemTime = 0;		
		float averageReturnTime = 0;		
		float averageResponseTime = 0;		
		float averageWaitTime = 0;		
		int sizeJobs = readyJobs.size();		
		Job j = new Job();
		
		/*Fila auxiliar para escalonarmos apenas os processos que ja chegaram na fila de prontos*/
		Queue<Job> rrQueue = new LinkedList<Job>();
		
		/*Array que ira verificar se houve a produção da primeira resposta*/
		boolean[] response = new boolean[sizeJobs];
		
		
		/*Executa ate que nao exista processos nas filas*/
		while (readyJobs.peek() != null || rrQueue.peek() != null){
			
			/*	
			 * Vamos preencher a fila rrQueue apenas com
			 * os processos que estao com tempo de chegada coerente com o tempo do sistema 
			 */
			
			while (readyJobs.peek() != null && readyJobs.peek().getArrivalTime() <= systemTime) 
				rrQueue.add(readyJobs.poll());
			
			/*Seguindo a mesma ideia dos algoritmos anteriores*/
			if (rrQueue.peek() != null){
				
				/*Processo sai da fila para o processador*/
				j = rrQueue.poll();				
				averageWaitTime += systemTime - j.getArrivalTime();
				
				/*Verifica se ja houve a producao da primeira resposta deste job. de acordo com seu PID*/
				if (!response[j.getPID()]){ //primeira vez
					averageResponseTime += systemTime - j.getArrivalTime();
					response[j.getPID()] = true;
				}			
				
				/*Verificar se o processo atual terminou sua execucao de acordo com o quantum*/
				if (j.getDuration() < quantum){
					systemTime += j.getDuration();
					j.setDuration(0);							
				}
				/*Caso nao terminou, diminui sua duracao para o proximo escalonamento*/
				else {
					systemTime += quantum; //atualiza o tempo de acordo com o quantum
					j.setDuration(j.getDuration() - quantum);				
				}
				
				/*
				 * Inserir na fila do rr os processos que chegaram da fila de prontos com o decorrer do tempo do sistema
				 * antes de re-inserir os processos incompletos na fila rr, para garantir a ordem correta dos processos.
				 */
				while (readyJobs.peek() != null && readyJobs.peek().getArrivalTime() <= systemTime) 
					rrQueue.add(readyJobs.poll());		
				
				/*Verifica se o processo terminou ou nao*/
				if (j.getDuration() > 0){
					j.setArrivalTime(systemTime);
					rrQueue.add(j);
				}
				else {
					rrQueue.remove(j); //nao volta para a fila
					/*Computa o tempo de retorno deste processo, a partir do primeiro tempo de chegada*/
					averageReturnTime += systemTime - j.getFirstArrivalTime();
				}
				
			}
			/*Nao chegou nenhum processo na fila de prontos, atualiza o tempo do sistema*/
			else {
				systemTime = readyJobs.peek().getArrivalTime();
			}
			
			
		}
		
		if (sizeJobs > 0)		
			System.out.printf("RR %.1f %.1f %.1f\n",
				(averageReturnTime/sizeJobs), (averageResponseTime/sizeJobs), (averageWaitTime/sizeJobs));
		
	}
	
}

















