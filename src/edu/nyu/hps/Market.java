package edu.nyu.hps;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ling on 8/12/14.
 */
public class Market {
    public int N, M, D;
    public int min_n, max_n, min_m, max_m, min_d, max_d;
    public boolean[][] connections;
    private Random random = new Random();

    public Market(int N, int M, int D, int min_n, int max_n, int min_m, int max_m, int min_d, int max_d) {
        this.N = N;
        this.M = M;
        this.D = D;
        this.min_n = min_n;
        this.max_n = max_n;
        this.min_m = min_m;
        this.max_m = max_m;
        this.min_d = min_d;
        this.max_d = max_d;
        connections = new boolean[N][M];
    }

    public void registerAsset(int asset, int portfolio) {
        connections[asset][portfolio] = true;
    }



}
