package com.peak6.devtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Compares 2 files and outputs the lines and the line numbers where the files differ.
 * <p/>
 * It's implemented as the Longest Common Subsequence (LCS) Problem, with a dynamic programming approach.
 * <p/>
 * See the following article for a full description and example regarding LCS:
 * http://en.wikipedia.org/wiki/Longest_common_subsequence_problem
 * <p/>
 * The set of sequences is given by:
 * lcs[i][j] = 0                              if i = M or j = N
 *           = lcs[i+1][j+1] + 1              if x[i]  = y[j]
 *           = max(lcs[i][j+1], lcs[i+1][j])  if x[i] != y[j]
 * <p/>
 * Then, once we build the LCS matrix, we trace back through it so we can print out the diff types along the way.
 * <p/>
 * User: rmarquez
 * Date: 7/22/2013
 */
public class Diff
{
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String[] x;
    private final String[] y;

    private final int M;
    private final int N;

    private final int[][] lcs;

    /**
     * Usage: java Diff file1 file2
     *
     * @param args file1 and file2
     */
    public static void main(String[] args) throws FileNotFoundException {
        String[] x = getLinesFromFile(new File(args[0]));
        String[] y = getLinesFromFile(new File(args[1]));

        Diff lcsDiff = new Diff(x, y);
        System.out.println(lcsDiff.runDiff());
    }

    private static String[] getLinesFromFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
          lines.add(sc.nextLine());
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Construct Diff with a filled in LCS matrix.
     *
     * @param x all lines for M side of matrix
     * @param y all lines for N side of matrix
     */
    public Diff(String[] x, String[] y) {
        this.x = x;
        this.y = y;

        M = x.length;
        N = y.length;

        lcs = buildLCS();
    }

    /**
     * Fill in the LCS matrix back-to-front (from lcs[M][N] to lcs[0][0]).
     *
     * @return Initialized LCS matrix
     */
    private int[][] buildLCS() {
        int[][] lcs = new int[M+1][N+1];
        for (int i = M-1; i >= 0; i--) {
            for (int j = N-1; j >= 0; j--) {
                if (x[i].equals(y[j])) {
                    lcs[i][j] = lcs[i+1][j+1] + 1;
                }
                else {
                    lcs[i][j] = Math.max(lcs[i][j+1], lcs[i+1][j]);
                }
            }
        }
        return lcs;
    }

    /**
     * Walk the LCS matrix, keeping track of moves toward M and N, which determines which diff type ("a", "c", "d")
     * to use.
     */
    public String runDiff() {

        StringBuilder result = new StringBuilder();

        Queue<String> outQueue = new LinkedBlockingQueue<String>();
        boolean shouldPrintDashes = false;
        List<Integer> moveM = new ArrayList<Integer>(M);
        List<Integer> moveN = new ArrayList<Integer>(N);
        int i = 0;
        int j = 0;
        while (i < M && j < N) {

            // keep moving across M and N until x[i] is the same as y[j]
            if (x[i].equals(y[j])) {

                if (moveN.size() > 0) {
                    if (i == j) {
                        // moved across M and N an equal amount indicates a change ("c")
                        outputChanged(moveM, moveN, result);
                    } else {
                        // moved across N, but not M indicates an add ("a")
                        outputAdded(i, moveN, result);
                    }
                } else if (moveN.size() == 0 && moveM.size() > 0) {
                    // moved across M, but not N indicates an delete ("d")
                    outputDeleted(moveM, j, result);
                }

                outputEntireQueue(outQueue, result);

                // clean-up and move to next diagonal
                moveM.clear();
                moveN.clear();
                shouldPrintDashes = false;
                i++;
                j++;

            } else if (lcs[i+1][j] >= lcs[i][j+1]) {
                // a line was removed or changed
                outQueue.offer("< " + x[i++]);
                moveM.add(i);
                shouldPrintDashes = true;

            } else {
                // a line was added or changed
                if (shouldPrintDashes) {
                    outQueue.offer("---");
                }
                outQueue.offer("> " + y[j++]);
                moveN.add(j);
                shouldPrintDashes = false;
            }
        }

        moveM.clear();
        moveN.clear();

        // If there are left over lines (because M != N), then output the rest as "a" or "d".
        while (i < M || j < N) {
            if (i == M) {
                outQueue.offer("> " + y[j++]);
                moveN.add(j);
            } else if (j == N) {
                outQueue.offer("< " + x[i++]);
                moveM.add(i);
            }
        }

        if (moveM.isEmpty()) {
            outputAdded(i, moveN, result);
        } else {
            outputDeleted(moveM, j, result);
        }
        outputEntireQueue(outQueue, result);

        result = cleanupEnding(result);
        return result.toString();
    }

    /**
     * Output "c", showing the changed line numbers.
     *
     * @param moveM LHS of "c"
     * @param moveN RHS of "c"
     */
    private void outputChanged(List<Integer> moveM, List<Integer> moveN, StringBuilder result) {
        StringBuilder sb = new StringBuilder();
        appendWithCommaDelims(moveM, sb);
        sb.append("c");
        appendWithCommaDelims(moveN, sb);

        result.append(sb).append(NEW_LINE);
    }

    /**
     * Output "a", showing the added line numbers.
     *
     * @param i LHS of "a"
     * @param moveN RHS of "a"
     */
    private void outputAdded(int i, List<Integer> moveN, StringBuilder result) {
        StringBuilder sb = new StringBuilder();
        sb.append(i).append("a");
        appendWithCommaDelims(moveN, sb);

        result.append(sb).append(NEW_LINE);
    }

    /**
     * Output "d", showing the deleted line numbers.
     *
     * @param moveM LHS of "d"
     * @param j LHS of "d"
     */
    private void outputDeleted(List<Integer> moveM, int j, StringBuilder result) {
        StringBuilder sb = new StringBuilder();
        appendWithCommaDelims(moveM, sb);
        sb.append("d").append(j);

        result.append(sb).append(NEW_LINE);
    }

    /**
     * Builds the StringBuilder in this format: firstMove,lastMove
     * For example, if moves=[3,4,5,6], then after this call, sb would be: "3,6"
     *
     * @param moves Subset of moves across M or N.
     * @param sb StringBuilder result.
     */
    private void appendWithCommaDelims(List<Integer> moves, StringBuilder sb) {
        int nMoves = moves.size();
        if (nMoves > 0) {
            sb.append(moves.get(0));
            if (nMoves > 1) {
                sb.append(",").append(moves.get(nMoves-1));
            }
        }
    }

    /**
     * Dequeue from the queue and print each entry out until the queue is empty.
     *
     * @param queue The Queue to dequeue from.
     */
    private void outputEntireQueue(Queue<String> queue, StringBuilder result) {
        while (!queue.isEmpty()) {
            result.append(queue.remove()).append(NEW_LINE);
        }
    }

    /**
     * Handle edge cases for end of result.
     */
    private StringBuilder cleanupEnding(StringBuilder result) {
        if (result.length() < 4) {
            // wipe any residuals out
            result = new StringBuilder();
        } else {
            result = result.deleteCharAt(result.length()-1);
        }
        return result;
    }
}
