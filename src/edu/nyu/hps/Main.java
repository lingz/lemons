package edu.nyu.hps;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int populationSize = 50;
    private static final int numGenerations= 100;
    private static final int numRestarts = 5;
    private static final boolean weightedRandom = true;
    private static final Random rand = new Random();
    private static Organism bestOrganism = null;
    private static double bestMetric = -1;
    private static String outfile;

    public static void main(String[] args) {
        int N, M, D, nMin, nMax, mMin, mMax, dMin, dMax;
        outfile = args[0];
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        String[] range;
        N = in.nextInt();
        M = in.nextInt();
        D = in.nextInt();
        range = in.next().split("-");
        nMin = Integer.parseInt(range[0]);
        nMax = Integer.parseInt(range[1]);
        range = in.next().split("-");
        mMin = Integer.parseInt(range[0]);
        mMax = Integer.parseInt(range[1]);
        range = in.next().split("-");
        dMin = Integer.parseInt(range[0]);
        dMax = Integer.parseInt(range[1]);

//        System.out.println(N);
//        System.out.println(M);
//        System.out.println(D);
//        System.out.println(nMin);
//        System.out.println(nMax);
//        System.out.println(mMin);
//        System.out.println(mMax);
//        System.out.println(dMin);
//        System.out.println(dMax);

//        System.out.println(String.format("%d %d %d %d %d %d %d %d %d", N, M, D, nMin, nMax, mMin, mMax, dMin, dMax));



        Market market = new Market(N, M, D, nMin, nMax, mMin, mMax, dMin, dMax);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (in.nextInt() == 1) {
                    market.registerAsset(i, j);
                }
            }
        }

        Organism.setParams(market, new Random());

        for (int i = 0; i < numRestarts; i++) {
            List<Organism> population = generatePopulation();
            System.out.println("Restart: " + i);
            for (int j = 0; j < numGenerations; j++) {

                generation(population);
            }
        }

    }

    private static List<Organism> generatePopulation() {
        List<Organism> organisms = new ArrayList<Organism>(populationSize);
        for (int i = 0; i <  populationSize; i++) {
            organisms.add(new Organism());
        }
        return  organisms;
    }

    private static void generation(List<Organism> population) {
        if (!weightedRandom) {
            double bestSeen = Double.NEGATIVE_INFINITY;
            double secondBestSeen = Double.NEGATIVE_INFINITY;
            Organism best = null;
            Organism secondBest = null;
            for (Organism organism : population) {
                double metric = organism.getMetric();
                if (metric > bestSeen) {
                    secondBest = best;
                    secondBestSeen = bestSeen;
                    best = organism;
                    bestSeen = metric;
                } else if (metric > secondBestSeen) {
                    secondBest = organism;
                    secondBestSeen = metric;
                }
            }
            for (int i = 0; i < populationSize; i++) {
                population.set(i, new Organism(best, secondBest));
            }
        } else {
            double maxMetric = Double.NEGATIVE_INFINITY;
            for (Organism organism : population) {
                if (organism.getMetric() > maxMetric) {
                    maxMetric= organism.getMetric();
                }
            }

            List<Double> cumulativeDistance = new ArrayList<Double>(populationSize);

            double totalDistance = 0;
            for (int i = 0; i < populationSize; i++) {
                Organism organism = population.get(i);
                totalDistance += organism.getMetric() / maxMetric;
                cumulativeDistance.add(totalDistance);
            }

            List<Organism> newPopulation = new ArrayList<Organism>(populationSize);

            for (int i = 0; i < populationSize; i++) {
                Organism parentA = population.get(weightedRandom(cumulativeDistance));
                Organism parentB = population.get(weightedRandom(cumulativeDistance));
                newPopulation.add(new Organism(parentA, parentB));
            }

            population = newPopulation;
        }
        // write the output
        double oldBest = bestMetric;
        registerBest(population);
        if (bestMetric != oldBest) {
            System.out.println("New Best: " + bestMetric);
            writeBest();
        }
    }

    private static int weightedRandom(List<Double> cumulativeWeights) {
        double totalWeight = cumulativeWeights.get(cumulativeWeights.size() - 1);
        double randPick = rand.nextDouble() * totalWeight;
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (cumulativeWeights.get(i) > randPick) {
                return i;
            }
        }
        return -1;
    }

    public static Organism registerBest(List<Organism> population) {
        for (Organism organism : population) {
            double metric = organism.getMetric();


            if (bestOrganism == null) {
                bestMetric = metric;
                bestOrganism = organism;
            } else {
                if (metric > bestMetric) {
                    bestOrganism = organism;
                    bestMetric = metric;
                }
            }
        }
        return bestOrganism;
    }

    private static void writeBest() {
        try {
            File file = new File(outfile + ".tmp");

            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(bestOrganism.getPoisonedVehiclesString());
            bw.close();
            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            };
            Files.move(Paths.get(outfile + ".tmp"), Paths.get(outfile), options);
        } catch (IOException e) {
            System.out.println("IO Error");
        }

    }
}
