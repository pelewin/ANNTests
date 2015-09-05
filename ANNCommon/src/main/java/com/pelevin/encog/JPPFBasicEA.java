package com.pelevin.encog;

import org.encog.EncogError;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.ea.train.basic.EAWorker;
import org.encog.ml.genetic.GeneticError;
import org.encog.util.logging.EncogLogging;

/**
 * Created by dmitry on 02.08.15.
 */
public class JPPFBasicEA extends DPBasicEA {

	public JPPFBasicEA(Population thePopulation, CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);
	}

	@Override
	public void iteration() {
		if (this.actualThreadCount == -1) {
			preIteration();
		}

		if (getPopulation().getSpecies().size() == 0) {
			throw new EncogError("Population is empty, there are no species.");
		}

		this.iteration++;

		// Clear new population to just best genome.
		this.newPopulation.clear();
		this.newPopulation.add(this.bestGenome);
		this.oldBestGenome = this.bestGenome;

		// execute species in parallel
		this.threadList.clear();
		for (final Species species : getPopulation().getSpecies()) {
			int numToSpawn = species.getOffspringCount();

			// Add elite genomes directly
			if (species.getMembers().size() > 5) {
				final int idealEliteCount = (int) (species.getMembers().size() * getEliteRate());
				final int eliteCount = Math.min(numToSpawn, idealEliteCount);
				for (int i = 0; i < eliteCount; i++) {
					final Genome eliteGenome = species.getMembers().get(i);
					if (getOldBestGenome() != eliteGenome) {
						numToSpawn--;
						if (!addChild(eliteGenome)) {
							break;
						}
					}
				}
			}

			// now add one task for each offspring that each species is allowed
			while (numToSpawn-- > 0) {
				final EAWorker worker = new EAWorker(this, species);
				this.threadList.add(worker);
			}
		}

		// run all threads and wait for them to finish
		try {
			this.taskExecutor.invokeAll(this.threadList);
		} catch (final InterruptedException e) {
			EncogLogging.log(e);
		}

		// handle any errors that might have happened in the threads
		if (this.reportedError != null && !getShouldIgnoreExceptions()) {
			throw new GeneticError(this.reportedError);
		}

		// validate, if requested
		if (isValidationMode()) {
			if (this.oldBestGenome != null
					&& !this.newPopulation.contains(this.oldBestGenome)) {
				throw new EncogError(
						"The top genome died, this should never happen!!");
			}

			if (this.bestGenome != null
					&& this.oldBestGenome != null
					&& getBestComparator().isBetterThan(this.oldBestGenome,
					this.bestGenome)) {
				throw new EncogError(
						"The best genome's score got worse, this should never happen!! Went from "
								+ this.oldBestGenome.getScore() + " to "
								+ this.bestGenome.getScore());
			}
		}

		this.speciation.performSpeciation(this.newPopulation);

		// purge invalid genomes
		this.population.purgeInvalidGenomes();
	}
}
