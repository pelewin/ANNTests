package org.pelevin.ann.training;

import org.encog.Encog;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.obj.SerializeObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class GridRunner {

	private static final String FILENAME = "data/lifeNetwork.dp";
	private static final String FILENAME_POPULATION = "data/lifePopulation.eg";

	private static final File pathPopulation = new File(FILENAME_POPULATION);

	private static NEATPopulation pop;
	private static TrainEA train;

	private static final int FILL_FACTOR = 15;
	private static final int NUM_GAMES = 20;
	private static final double TRAIN_RATE = 0.90;
	private static final int NUM_SPECIES = 5000;
	public static final int NUM_THREADS = 2;


	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {

		trainSingleStepNetworkNEAT();
		//org.pelevin.ann.visual.Runner.main(args);
		Encog.getInstance().shutdown();

	}

	private static void trainSingleStepNetworkNEAT() {

		//load population
		pop = (NEATPopulation) EncogDirectoryPersistence.loadObject(pathPopulation);
		pop.setPopulationSize(NUM_SPECIES);

		//newPopulation();

		LifeScore score = new LifeScore(NUM_GAMES, FILL_FACTOR);
		runTraining(score);

		//phase 1
		do {
			Date startDate = new Date();
			train.iteration();
			Date stopDate = new Date();
			long difference = (stopDate.getTime() - startDate.getTime())/1000;

			System.out.println("Ph1.Epoch #" + train.getIteration() + " Error:" + train.getError()
					+ ", Species:" + pop.getSpecies().size()
					+ " time: " + difference + "s");

			/*if (train.getIteration() % 100 == 0){
				saveResults();
			}*/

			if (train.getError() >= (200 + (NUM_GAMES * 100 * TRAIN_RATE))
					|| train.getIteration() % 100 == 0){
				saveResults();
				train.finishTraining();
				runTraining(score);
			}

		} while (true);

	}

	private static void runTraining(LifeScore score) {
		train = NEATUtil.constructNEATTrainer(pop, score);
		train.setThreadCount(NUM_THREADS);
		//OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
		//speciation.setCompatibilityThreshold(1);
		//train.setSpeciation(new OriginalNEATSpeciation());
	}

	private static void newPopulation() {
		pop = new NEATPopulation(25, 2, NUM_SPECIES);
		pop.setActivationCycles(4);
		pop.reset();
	}

	private static void saveResults() {
		EncogDirectoryPersistence.saveObject(pathPopulation, pop);

		//EncogDirectoryPersistence.saveObject(new File(FILENAME), network);
		try {
			SerializeObject.save(new File(FILENAME), (NEATNetwork) train.getCODEC().decode(pop.getBestGenome()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
