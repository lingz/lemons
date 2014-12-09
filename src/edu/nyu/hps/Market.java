package edu.nyu.hps;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ling on 8/12/14.
 */
public class Market {
    private int N, M, D;
    private int n, m, d;
    private int current_n, current_m = m, current_d = d;
    private boolean[][] connections;
    private boolean[] poisonedAssets;
    private boolean[] poisonedVehicles;

    private Random random = new Random();

    private boolean PRINT_LOCAL_DENSITIES = false;

    public Market(int N, int M, int D, int n, int m, int d) {
        this.N = N;
        this.M = M;
        this.D = D;
        this.n = n;
        this.m = m;
        this.d = d;
        current_n = 0;
        current_m = 0;
        current_d = 0;
        connections = new boolean[N][M];
        poisonedAssets = new boolean[N];
        poisonedVehicles = new boolean[M];
    }

    public void registerAsset(int asset, int portfolio) {
        connections[asset][portfolio] = true;
    }

    private void addPoisonedAsset(int asset) {
        if (!poisonedAssets[asset]) current_n++;
        poisonedAssets[asset] = true;
    }

    private void removePoisonedAsset(int asset) {
        if (poisonedAssets[asset]) current_n--;
        poisonedAssets[asset] = false;
    }

    private void addPoisonedVehicle(int vehicle) {
        if (!poisonedVehicles[vehicle]) current_m++;
        poisonedVehicles[vehicle] = true;
    }

    private void removePoisonedVehicle(int vehicle) {
        if (poisonedVehicles[vehicle]) current_m--;
        poisonedVehicles[vehicle] = false;
    }

    // Generates 20% randomly
    private void seed() {
        current_n = 0;
        current_m = 0;
        current_d = 0;
        poisonedAssets = new boolean[N];
        poisonedVehicles = new boolean[M];

        int suspectAsset;
        int suspectVehicle;
        for (int i = 0; i < n / 5; i++) {
            // Gen a new suspect for poisoned assets
            do {
                suspectAsset = random.nextInt(N);
            } while (poisonedAssets[suspectAsset]);

            addPoisonedAsset(suspectAsset);
        }

        for (int i = 0; i < m / 5; i++) {
            // Gen a new unused vehicle
            do {
                suspectVehicle = random.nextInt(M);
            } while (poisonedAssets[suspectVehicle]);

            addPoisonedVehicle(suspectVehicle);
        }
    }

    private double getPoisonedDensity () {
        int numEdges = 0;
        int local_density = 0;
        for (int i = 0; i < N; i++) {
            if (poisonedAssets[i]) {
                local_density = 0;
                for (int j = 0; j < M; j++) {
                    if (poisonedVehicles[j] && connections[i][j]) {
                        numEdges++;
                        local_density++;
                    }
                }
                if (PRINT_LOCAL_DENSITIES) {
                    System.out.print(local_density + " ");
                }
            }
        }
        return 1.0 * numEdges / (current_n + current_m);
    }

    private ArrayList<Integer> getLocalDensities() {
        ArrayList<Integer> localDensities = new ArrayList<Integer>();
        int localDensity = 0;
        for (int i = 0; i < N; i++) {
            if (poisonedAssets[i]) {
                localDensity = 0;
                for (int j = 0; j < M; j++) {
                    if (poisonedVehicles[j] && connections[i][j]) {
                        localDensity++;
                    }
                }
                localDensities.add(localDensity);
            }
        }
        return localDensities;
    }

    public double getVariance() {
        ArrayList<Integer> localDensities = getLocalDensities();
        int sum = 0;
        for (Integer density : localDensities) {
            sum += density;
        }
        double mean = 1.0 * sum / localDensities.size();

        double variance = 0;

        for (Integer density : localDensities) {
            variance += (density - mean) * (density - mean);
        }

        return variance;
    }

    // greedy climb
    private boolean climb() {
        int bestCandidate;
        double bestDensity, newDensity;

        if (current_n != n) {
            bestCandidate = -1;
            bestDensity = 0;
            if (current_n < n) {
                for (int i = 0; i < N; i++) {
                    if (!poisonedAssets[i]) {
                        addPoisonedAsset(i);
                        newDensity = getPoisonedDensity();
                        if (newDensity > bestDensity) {
                            bestDensity = newDensity;
                            bestCandidate = i;
                        }
                        removePoisonedAsset(i);
                    }
                }
                addPoisonedAsset(bestCandidate);
            } else if (current_n > n) {
                for (int i = 0; i < N; i++) {
                    if (poisonedAssets[i]) {
                        removePoisonedAsset(i);
                        newDensity = getPoisonedDensity();
                        if (newDensity > bestDensity) {
                            bestDensity = newDensity;
                            bestCandidate = i;
                        }
                        addPoisonedAsset(i);
                    }
                }
                removePoisonedAsset(bestCandidate);
            }
        }

        if (current_m != m) {
            bestCandidate = -1;
            bestDensity = 0;
            if (current_m < m) {
                for (int i = 0; i < M; i++) {
                    if (!poisonedVehicles[i]) {
                        addPoisonedVehicle(i);
                        newDensity = getPoisonedDensity();
                        if (newDensity > bestDensity) {
                            bestDensity = newDensity;
                            bestCandidate = i;
                        }
                        removePoisonedVehicle(i);
                    }
                }
                addPoisonedVehicle(bestCandidate);
            } else if (current_m > m) {
                for (int i = 0; i < M; i++) {
                    if (poisonedVehicles[i]) {
                        removePoisonedVehicle(i);
                        newDensity = getPoisonedDensity();
                        if (newDensity > bestDensity) {
                            bestDensity = newDensity;
                            bestCandidate = i;
                        }
                        addPoisonedVehicle(i);
                    }
                }
                removePoisonedVehicle(bestCandidate);
            }
        }

        // stop condition
        return current_m != m || current_n != n;
    }

    public double solve() {
        seed();
        while (climb()) {}
//        PRINT_LOCAL_DENSITIES = true;
        return getPoisonedDensity();
    }

    public String getPoisonedVehiclesString() {
        StringBuilder sb = new StringBuilder(2*M - 1);
        for (int i = 0; i < M; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(poisonedVehicles[i] ? "1" : "0");
        }
        return sb.toString();
    }
}
