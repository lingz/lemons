package edu.nyu.hps;

import java.io.BufferedInputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        int N, M, D, nMin, nMax, mMin, mMax, dMin, dMax;
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



        Market market = new Market(N, M, D, (nMin + nMax)/2, (mMin + mMax)/2, (dMin + dMax)/2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (in.nextInt() == 1) {
                    market.registerAsset(i, j);
                }
            }
        }

        double best = Double.POSITIVE_INFINITY;
        String bestString = "";
        for (int i = 0; i < 15; i++) {;
            double result = market.solve();
            double variance = market.getVariance();
            if (result < best) {
                best = result;
                bestString = market.getPoisonedVehiclesString();
            }
        }

        System.out.println(bestString);
    }
}
