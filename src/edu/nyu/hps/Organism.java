package edu.nyu.hps;


import java.util.*;

/**
 * Created by ling on 25/11/14.
 */
public class Organism {
    public Set<Integer> assets;
    public Set<Integer> vehicles;
    public static Random rand;
    private final double mutationRate = 0.15;
    private final boolean useDensity = false;
    private double density = -1, variance = -1;
    private static Market market;

    public static void setParams(Market market, Random rand) {
        Organism.market = market;
        Organism.rand = rand;
    }

    // Default constructor generates random organisms
    public Organism() {
        assets = new HashSet<Integer>(market.max_n);
        vehicles = new HashSet<Integer>(market.max_m);

        int numAssets = rand.nextInt(market.max_n - market.min_n) + market.min_n + 1;
        int numVehicles = rand.nextInt(market.max_m - market.min_m) + market.min_m + 1;

        // 0 indexed assets and vehicles
        while (assets.size() < numAssets) {
            assets.add(randomAsset());
        }
        while (vehicles.size() < numVehicles) {
            vehicles.add(randomVehicle());
        }
    }

    private int randomAsset() {
        return rand.nextInt(market.N);
    }

    private int randomVehicle() {
        return rand.nextInt(market.M);
    }

    // offspring of parents
    public Organism(Organism a, Organism b) {
        assets = new HashSet<Integer>(market.max_n);
        vehicles = new HashSet<Integer>(market.max_m);

        for (Integer asset : a.assets) {
            if (rand.nextFloat() < 0.5) {
                if (rand.nextFloat() < mutationRate) {
                    assets.add(randomAsset());
                } else {
                    assets.add(asset);
                }
            }
        }
        for (Integer asset: b.assets) {
            if (rand.nextFloat() < 0.5) {
                if (rand.nextFloat() < mutationRate) {
                    assets.add(randomAsset());
                } else {
                    assets.add(asset);
                }
            }
        }

        while (assets.size() < market.min_n) {
            assets.add(randomAsset());
        }

        while (assets.size() > market.max_n) {
            Iterator<Integer> itr = assets.iterator();
            int removeIdx = rand.nextInt(assets.size()) + 1;
            for (int i = 0; i < removeIdx; i++) {itr.next();}
            itr.remove();
        }

        for (Integer vehicle : a.vehicles) {
            if (rand.nextFloat() < 0.5) {
                if (rand.nextFloat() < mutationRate) {
                    vehicles.add(randomVehicle());
                } else {
                    vehicles.add(vehicle);
                }
            }
        }
        for (Integer vehicle: b.vehicles) {
            if (rand.nextFloat() < 0.5) {
                if (rand.nextFloat() < mutationRate) {
                    vehicles.add(randomVehicle());
                } else {
                    vehicles.add(vehicle);
                }
            }
        }

        while (vehicles.size() < market.min_m) {
            vehicles.add(randomVehicle());
        }

        while (vehicles.size() > market.max_m) {
            Iterator<Integer> itr = vehicles.iterator();
            int removeIdx = rand.nextInt(vehicles.size()) + 1;
            for (int i = 0; i < removeIdx; i++) {itr.next();}
            itr.remove();
        }
    }

    private double getDensity() {
        if (density == -1) {
            int numEdges = 0;
            for (Integer asset : assets) {
                for (Integer vehicle : vehicles) {
                    if (market.connections[asset][vehicle]) {
                        numEdges++;
                    }
                }
            }
            density = 1.0 * numEdges / (assets.size() + vehicles.size());
        }
        return density;
    }

    private double getVariance() {
        if (variance == -1) {
            double mean = getDensity() / vehicles.size();
            double totalVariation = 0;

            for (Integer vehicle : vehicles) {
                int localCount = 0;
                for (Integer asset : assets) {
                    if (market.connections[asset][vehicle]) {
                        localCount++;
                    }
                }
                totalVariation += (localCount - mean) * (localCount - mean);
            }

            variance = totalVariation / vehicles.size();
        }
        return variance;
    }

    public String getPoisonedVehiclesString() {
        StringBuilder sb = new StringBuilder(2*market.M - 1);
        for (int i = 0; i < market.M; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(vehicles.contains(i) ? "1" : "0");
        }
        return sb.toString();
    }

    // variance is returned inverted so greater than comparisons are possible
    // and also cumulative distribution works
    public double getMetric() {
        if (useDensity) {
            return getDensity();
        } else {
            return 1 / getVariance();
        }
    }


}
