package edu.nyu.hps;

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

    private boolean climb() {
        if (current_n < n) {

        } else if (current_n > n) {

        }
        
        if (current_m < m) {

        } else if (current_m > m) {

        }

        // stop condition
        return current_m == m && current_n == n;
    }

    public void solve() {
        seed();
        while (climb()) {}
    }

    public String poisonedVehiclesString() {
        StringBuilder sb = new StringBuilder(M);
        for (int i = 0; i < M; i++) {
            sb.append(poisonedVehicles[i]);
        }
        return sb.toString();
    }
}
